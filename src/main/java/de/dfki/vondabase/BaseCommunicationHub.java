package de.dfki.vondabase;

import static de.dfki.vondabase.Constants.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.dfki.lt.hfc.WrongFormatException;
import de.dfki.mlt.rudimant.agent.Behaviour;
import de.dfki.mlt.rudimant.agent.CommunicationHub;
import de.dfki.mlt.rudimant.agent.Intention;
import de.dfki.mlt.rudimant.agent.nlp.DialogueAct;
import de.dfki.vondabase.utils.Listener;
import de.dfki.vondabase.utils.MqttUtil;

import org.eclipse.paho.client.mqttv3.MqttException;


public class BaseCommunicationHub implements CommunicationHub {

  private final static Logger logger = LoggerFactory.getLogger(BaseCommunicationHub.class);

  /**
   * How much time in milliseconds must pass between two behaviours, if
   * no message came back that the previous behaviour was finished.
   */
  public static long MIN_TIME_BETWEEN_BEHAVIOURS = 10000;
  private final Deque<Object> inQueue = new ArrayDeque<>();
  private final Deque<Object> itemsToSend = new ArrayDeque<>();
  private final Deque<Object> myItemsToSend = new ArrayDeque<>();
  private final Deque<Object> pendingEvents = new ArrayDeque<>();
  // Define a set of EventListener -> these are used to trigger audio output, update the avatar and so on
  private final List<Listener<Behaviour>> _listeners = new ArrayList<>();
//  private final List<Listener<TTSMessage>> _ttsListeners = new ArrayList<>();
//  private final List<Listener<StatusMessage>> _statusListeners = new ArrayList<>();
//  private final List<Listener<GCSMessage>> _gcsListeners = new ArrayList<>();
//  private final List<Listener<SoundMessage>> _soundListeners = new ArrayList<>();
//  private RESTCaller _restListener;



  private final Random r = new Random();
  private boolean isRunning = true;
  private AbstractAgent _agent;
  
  private MqttUtil client;
  //private JsonMarshaller mapper;
  private ObjectMapper mapper;
  
  // ------------------ init the Communication Hub -----------------------------------------
  public void init(File configDir, Map<String, Object> configs)
          throws IOException, WrongFormatException, MqttException {
    String robot = (String) configs.get("agentBase");
    if (robot.equals("de.dfki.vondabase.BaseAgent")) {
      _agent = new DialogAgent();
      _agent.init(configDir, configs, "deu");
      //} else if(robot.equals("de.dfki.intuitiv.ArmAgent")) {
    //  _agent = new Arm();
    //  _agent.init(configDir, "de", configs);
      
      ///////////////////////////////////////
      //mapper = new JsonMarshaller();
      mapper = new ObjectMapper();
      client = new MqttUtil((Map<String, Object>)configs.get("mqtt"));
      client.register(IN_TOPIC);
      // TODO do I need to subscribe to publish?
      //client.register(OUT_TOPIC);
      ////////////////////////////////////////
      
    } else {
      throw new IllegalArgumentException("unknown input " + robot);
    }
    _agent.setCommunicationHub(this);
  }

  // --------------------- start/shutdown -------------------------------------------
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
    // eventually disconnect from communication infrastructure
    // _communicationChannel.disconnect();
    isRunning = false;
  }

  // --------- register new Listener ------------------------------------------------------
  public void registerBehaviourListener(Listener<Behaviour> listener) {
    _listeners.add(listener);
  }

//  public void registerTTSListener(Listener<TTSMessage> listener) {
//    _ttsListeners.add(listener);
//  }
//
//  public void registerStatusListener(ROSClientStatus rosClientTaskStatus) {
//    _statusListeners.add(rosClientTaskStatus);
//  }
//
//  public void registerGCSListener(ROSClientGCS rosClientGCS) {
//    _gcsListeners.add(rosClientGCS);
//  }
//
//  public void registerRESTListener(RESTCaller restCaller) { _restListener = restCaller;}
//
//  public void registerSoundListener(ROSClientSound rosSoundClient) {
//    _soundListeners.add(rosSoundClient);
//  }
  
  /************ Send and receive messages / signals **********/
  public void sendSignal(HashMap<String, String> signal) {
    try {
        String json = mapper.writeValueAsString(signal);
        client.sendMessage(OUT_TOPIC, json);
      } catch (JsonProcessingException e) {
        logger.error("{}", e);
      }
  }
  
  // TODO Read and decode last received message
  public Map<String, String> readSignal(){
	  return this.decodeSignal(client.getLastMessage());
  }

  
	private Map<String, String> decodeSignal(byte[] payload) {
	    try {
	    	//Map<String, String> map = mapper.readValue(json, Map.class);
	    	return mapper.readValue(new InputStreamReader(new ByteArrayInputStream(payload),Charset.forName("UTF-8")),new TypeReference<>(){});
	    	//return mapper.readValue(payload, new TypeReference<>(){});
	      }
	      catch (Exception ex) {
	        logger.error("{}", ex);
	        return null;
	      }
	  }


  // ------------ process incoming transcription -------------------------------------
  public DialogueAct analyse(String in) {
    return _agent.analyse(in);
  }


  // ------------ publish new Events (Behavior, Dia, RosMessage -----------------------
  public void sendEvent(Object in) {
    inQueue.push(in);
  }

  // depends on the concrete Event class
  private void onEvent(Object evt) {
    System.err.println("on event ...");
    if (evt instanceof Intention) {
      _agent.executeProposal((Intention) evt);
    } else if (evt instanceof DialogueAct) {
      System.err.println("Dia " + evt);
      _agent.addLastDA((DialogueAct) evt);
      _agent.newData();
    } else if (evt instanceof String) {
      System.err.println("String " + evt);
      DialogueAct da = _agent.analyse((String) evt);
      inQueue.add(da);
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
    // TODO from old project - kept it as an example
    //_dListeners.parallelStream().forEach((l) ->l.listen(MessageFactory.translateBehavior2DialogueMessage((ExtendedBehaviour) b)) );
    // TODO: sendTTS(b);
    //MessageFactory.translateBehavior2TTSMessage((ExtendedBehaviour) b));

  }

//  public void sendStatus(StatusMessage message){
//    _statusListeners.parallelStream().forEach((l) -> {
//      l.listen(message);
//    });
//  }

//  public void sendGCS(GCSMessage message){
//    _gcsListeners.parallelStream().forEach((l) -> {l.listen(message);});
//  }
//
//  public void sendStatusUpdate(StatusMessage statusMessage){
//    _statusListeners.parallelStream().forEach((l) -> l.listen(statusMessage));
//  }
//
//  public void sendSound(SoundMessage sound){
//    System.err.println("Playing Sound: " + sound);
//    _soundListeners.parallelStream().forEach((l) -> l.listen(sound));
//  }
//
//  public void sendTTS(TTSMessage msg){
//    _ttsListeners.parallelStream().forEach((l) -> l.listen(msg));
//  }

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
    inQueue.push(new Intention(intention, 0.0));
  }

  // Depends on the concrete Event class
  private void sendThis(Object e) {
    if (e instanceof Behaviour)
      sendBehaviour((Behaviour) e);
    else
      logger.warn("Unknown Object to send: {}", e);
  }


  /**
   * TODO redundant can also be done by calling sendEvent(asr);
   * @param asr
   */
  public void asrInput(String asr) {
    // optional -> show ast in UI
    Behaviour behaviour = new Behaviour(asr, "input");
    _listeners.parallelStream().forEach((l) -> l.listen(behaviour));
    // this is all we need to make this work properly
    sendEvent(asr);
  }


  public BaseAgent getAgent() {
    return (BaseAgent) _agent;
  }

  private boolean isRunning() {
    return isRunning;
  }

/**
  public void updateSkeleton(SkeletonMessage skeleton) {
    _agent.updateUserSkeleton(skeleton);
  }

  public void updateTracks(List<BodyTrackerMessage> tracks) {
    if (_agent.getUserID() != -1){
      for (BodyTrackerMessage track : tracks){
        if (_agent.getUserID() == track.getBody_id()){
          _agent.updateUserTrack(track);
        }
      }
    }

    throw new IllegalStateException("Please implement me!");
  }
 */

//  public void updateStatus(PatientStatusMessage status){
//    if(_agent.getUserID() != -1){
//      _agent.updatePatientStatus(status);
//    }
//  }

  /**
   * This method frees the log on the behaviour listener, dialog listener and tts listener, so that new messages can be send.
   * This is important to synchronize the displaying of speech bubbles and tts output.
   *
   * This method is called whenever the @RosHandler class processes a <code>tts_done</code> message.
   *
  public void freeSpeechListener() {
    for (Listener<TTSMessage> l : _ttsListeners)
      l.free();
    //_listeners.parallelStream().forEach(Listener::free);
    //_dListeners.parallelStream().forEach(Listener::free);
    //_ttsListeners.parallelStream().forEach(Listener::free);
  }

  public boolean hasSoundOutputListener() {
   return !_soundListeners.isEmpty();
  }
  */
}
