package de.dfki.vondabase;

import static de.dfki.vondabase.Constants.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dfki.lt.hfc.WrongFormatException;
import de.dfki.mlt.mqtt.JsonMarshaller;
import de.dfki.mlt.mqtt.MqttHandler;
import de.dfki.mlt.rudimant.agent.Behaviour;
import de.dfki.mlt.rudimant.agent.CommunicationHub;
import de.dfki.mlt.rudimant.agent.Intention;
import de.dfki.mlt.rudimant.agent.nlp.DialogueAct;
import de.dfki.vondabase.data.AsrResult;
import de.dfki.vondabase.data.Command;
import de.dfki.vondabase.data.Signal;
import de.dfki.vondabase.utils.Listener;


public class BaseCommunicationHub implements CommunicationHub {

  private final static Logger logger = LoggerFactory.getLogger(BaseCommunicationHub.class);

  /**
   * How much time in milliseconds must pass between two behaviours, if
   * no message came back that the previous behaviour was finished.
   */
  public static long MIN_TIME_BETWEEN_BEHAVIOURS = 10000;
  private final Deque<Object> inQueue = new ArrayDeque<>();
  private final Deque<Object> itemsToSend = new ArrayDeque<>();

  private final Deque<Object> pendingEvents = new ArrayDeque<>();
  // Define a set of EventListener -> these are used to trigger audio output, update the avatar and so on
  private final List<Listener<Behaviour>> _listeners = new ArrayList<>();

  private final Random r = new Random();
  private boolean isRunning = true;
  private BaseAgent _agent;

  private MqttHandler client;
  private JsonMarshaller mapper;

  private boolean receiveAsr(byte[] b) {
    Optional<AsrResult> cmd;
    (cmd = mapper.unmarshal(b, AsrResult.class)).ifPresent(this::sendEvent);
    return cmd.isPresent();
  }

  private boolean receiveMqtt(byte[] b) {
    Optional<Command> cmd;
    (cmd = mapper.unmarshal(b, Command.class)).ifPresent(_agent::addCommand);
    return ! cmd.isEmpty();
  }

  private void initMqtt(Map<String, Object> configs) throws MqttException {
    ///////////////////////////////////////
    mapper = new JsonMarshaller();
    client = new MqttHandler(configs);
    client.register(IN_TOPIC, this::receiveMqtt);
    client.register(ASR_TOPIC + "/de", this::receiveAsr);
    // do I need to subscribe to publish? NO!
    //client.register(OUT_TOPIC);
    ////////////////////////////////////////
  }

  // ------------------ init the Communication Hub -----------------------------------------
  @SuppressWarnings("unchecked")
  public void init(File configDir, Map<String, Object> configs)
          throws IOException, WrongFormatException, MqttException {
    // check that we got the right config
    String checkConfig = (String) configs.get("agentBase");
    if (checkConfig.equals("de.dfki.vondabase.BaseAgent")) {
      _agent = new DialogAgent();
      _agent.init(configDir, configs, "de_DE");
      initMqtt((Map<String, Object>)configs.get("mqtt"));
    } else {
      throw new IllegalArgumentException("unknown config " + checkConfig);
    }
    registerBehaviourListener(new Listener<Behaviour>() {

      @Override
      public void listen(Behaviour q) {
        Optional<String> out = mapper.marshal(q);
        if (out.isPresent()) {
          client.sendMessage(TTS_TOPIC, out.get());
        } else {
          logger.error("Could not serialize Behaviour: {}", q);
        }
      }

      @Override
      public void free() { }

    });
    _agent.setCommunicationHub(this);
  }

  // --------------------- start/shutdown -----------------------------------
  public void startListening() {
    Thread listenToClient = new Thread() {
      @Override
      public void run() {
        runReceiveSendCycle();
      }
    };
    listenToClient.setName("ListenToEvents");
    listenToClient.setDaemon(true);
    listenToClient.start();
  }

  public void shutdown() {
    // disconnect from communication infrastructure
    try {
      client.disconnect();
    } catch (MqttException e) {
      logger.error("Error disconnecting MQTT: {}", e);
    }
    isRunning = false;
  }

  // --------- register new Listener ----------------------------------------
  public void registerBehaviourListener(Listener<Behaviour> listener) {
    _listeners.add(listener);
  }

  /************ Send messages / signals **********/
  public void sendSignal(Signal signal) {
    mapper.marshal(signal).ifPresent(json -> {
      System.out.println(json);
      client.sendMessage(OUT_TOPIC, json);
    });
  }

  // ------------ publish new Events (Behavior, Dia, RosMessage -----------------------
  public void sendEvent(Object in) {
    inQueue.push(in);
  }

  // depends on the concrete Event class
  private void onEvent(Object evt) {
    logger.debug("on event ...");
    if (evt instanceof Intention) {
      _agent.executeProposal((Intention) evt);
    } else if (evt instanceof DialogueAct) {
      logger.debug("Dia {}", evt);
      _agent.addLastDA((DialogueAct) evt);
      _agent.newData();
    } else if (evt instanceof String) {
      logger.debug("String {}", evt);
      DialogueAct da = _agent.analyse((String) evt);
      sendEvent(da);
    } else if (evt instanceof AsrResult) {
      _agent.speechInput = 2;
      String text = ((AsrResult)evt).getText();
      logger.debug("AsrResult {}" + text);
      DialogueAct da = _agent.analyse(text);
      if (_agent.user != null) {
        da.setValue("sender", _agent.user.toString());
      }
      sendEvent(da);
    } else {
      logger.warn("Unknown incoming object: {}", evt);
    }
  }

  private void runReceiveSendCycle() {
    while (isRunning()) {
      boolean emptyRun = true;
      while (!inQueue.isEmpty()) {
        Object event = inQueue.pollFirst();
        onEvent(event);
      }
      // if a proposal was executed, handle pending events now
      if (!_agent.waitForIntention()) {
        // handle any pending events
        while (!pendingEvents.isEmpty()) {
          onEvent(pendingEvents.removeLast());
        }
        _agent.processRules();
      }
      synchronized (itemsToSend) {
        Object c = itemsToSend.peekFirst();
        if (c != null && (c instanceof Behaviour)
                && _agent.waitForBehaviours((Behaviour) c)) {
          c = null;
        }
        if (c != null) {
          itemsToSend.removeFirst();
          logger.debug("<-- {}", c);
          sendThis(c);
          emptyRun = false;
        }
      }
      if (emptyRun) {
        try {
          Thread.sleep(100);
        } catch (InterruptedException ex) {
          // shut down?
        }
      }
    }
    _agent.shutdown();
  }

  @Override
  public void sendBehaviour(Behaviour b) {
    _listeners.parallelStream().forEach((l) -> {
      l.listen(b);
    });
  }

  // select one of a set of intentions
  @Override
  public void sendIntentions(Set<String> intentions) {
    if (intentions.isEmpty()) return;
    // The following is a stub "statistical" component which randomly selects
    // one intention
    int rand = r.nextInt(intentions.size());
    String intention = null;
    Iterator<String> it = intentions.iterator();
    for (int i = 0; i <= rand; ++i) {
      intention = it.next();
    }
    sendEvent(new Intention(intention, 0.0));
  }

  // Depends on the concrete Event class
  private void sendThis(Object e) {
    if (e instanceof Behaviour)
      sendBehaviour((Behaviour) e);
    else
      logger.warn("Unknown Object to send: {}", e);
  }

  public BaseAgent getAgent() {
    return _agent;
  }

  private boolean isRunning() {
    return isRunning;
  }

}
