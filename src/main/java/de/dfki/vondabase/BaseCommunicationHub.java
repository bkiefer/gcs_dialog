package de.dfki.vondabase;

import de.dfki.vondabase.RosInterface.ROSClientEwalkerGoal;
import de.dfki.vondabase.RosInterface.ROSClientEwalkerUseElevator;
import de.dfki.vondabase.RosInterface.ROSClientIsInteractive;
import de.dfki.vondabase.RosInterface.ROSClientTaskStatus;
import de.dfki.vondabase.RosInterface.msgs.*;
import de.dfki.vondabase.restapi.caller.RESTCaller;
import de.dfki.vondabase.utils.Listener;
import de.dfki.lt.hfc.WrongFormatException;
import de.dfki.mlt.rosBridge.utils.arm.CommandMessage;
import de.dfki.mlt.rudimant.agent.*;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;


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
  private final List<Listener<CommandMessage>> _cListeners = new ArrayList<>();
  private final List<Listener<DialogueMessage>> _dListeners = new ArrayList<>();
  private final List<Listener<Boolean>> _iListeners = new ArrayList<>();
  private final List<Listener<AvatarMessage>> _aListeners = new ArrayList<>();
  private final List<Listener<TTSMessage>> _ttsListeners = new ArrayList<>();
  private final List<Listener<TaskStatusMessage>> _taStListeners = new ArrayList<>();
  private RESTCaller _restListener;
  private final List<Listener<GoalsMessage>> _egListener = new ArrayList<>();
  private final List<Listener<Integer>> _eeListener = new ArrayList<>();


  private final Random r = new Random();
  private boolean isRunning = true;
  private AbstractAgent _agent;

  // ------------------ init the Communication Hub -----------------------------------------
  public void init(File configDir, Map<String, Object> configs)
          throws IOException, WrongFormatException, TException {
    String robot = (String) configs.get("wrapperClass");
    if (robot.equals("de.dfki.vondabase.BaseAgent")) {
      _agent = new Example();
      _agent.init(configDir, "de", configs);
    //} else if(robot.equals("de.dfki.intuitiv.ArmAgent")) {
    //  _agent = new Arm();
    //  _agent.init(configDir, "de", configs);
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

  public void registerCommandListener(Listener<CommandMessage> listener) {
    _cListeners.add(listener);
  }

  public void registerAvatarListener(Listener<AvatarMessage> listener) {
    _aListeners.add(listener);
  }

  public void registerDialogueListener(Listener<DialogueMessage> listener) {
    _dListeners.add(listener);
  }

  public void registerTTSListener(Listener<TTSMessage> listener) {
    _ttsListeners.add(listener);
  }

  public void registerStatusListener(ROSClientTaskStatus rosClientTaskStatus) {
    _taStListeners.add(rosClientTaskStatus);
  }

  public void registerRESTListener(RESTCaller restCaller) { _restListener = restCaller;}
  public void registerInteractiveListener(ROSClientIsInteractive rosClientIsInteractive) { _iListeners.add(rosClientIsInteractive);}

  public void registerEwalkerGoalListener(ROSClientEwalkerGoal rosClientEwalkerGoal) {
    _egListener.add(rosClientEwalkerGoal);
  }

  public void registerEwalkerUseElevator(ROSClientEwalkerUseElevator rosClientEwalkerUseElevator){
    _eeListener.add(rosClientEwalkerUseElevator);
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
    //sendTTS(MessageFactory.translateBehavior2TTSMessage((ExtendedBehaviour) b));
  }

  public void sendClearAvatar( DialogueMessage dialogueMessage){
    _dListeners.parallelStream().forEach((l) ->l.listen(dialogueMessage) );
  }

  public void sendAvatarUpdate(AvatarMessage avatarMessage){
    _aListeners.parallelStream().forEach((l) -> l.listen(avatarMessage));
  }

  public void sendTaskStatusUpdate(TaskStatusMessage taskStatus){
    _taStListeners.parallelStream().forEach((l) -> l.listen(taskStatus));
  }



  public void sendEwalkerGoal(GoalsMessage msg){
    _egListener.parallelStream().forEach((l) -> l.listen(msg));
  }

  public void sendUseElevator(Integer floorNumber) {
    _eeListener.parallelStream().forEach((l)-> l.listen(floorNumber));
  }

  public void sendTTS(TTSMessage msg){
    _ttsListeners.parallelStream().forEach((l) -> l.listen(msg));
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
    inQueue.push(new Intention(intention, 0.0));
  }

  // Depends on the concrete Event class
  private void sendThis(Object e) {
    if (e instanceof Behaviour)
      sendBehaviour((Behaviour) e);
    else if (e instanceof CommandMessage)
      sendCommand((CommandMessage) e);
    else
      logger.warn("Unknown Object to send: {}", e);
  }

  public void sendCommand(CommandMessage c) {
    _cListeners.parallelStream().forEach((l) -> {
      l.listen(c);
    });
  }

  public void sendIsInteractive(boolean isInteractive) {
    _iListeners.parallelStream().forEach((l) -> l.listen(isInteractive));
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


  public Agent getAgent() {
    return _agent;
  }

  private boolean isRunning() {
    return isRunning;
  }


  /**
   * This method frees the log on the behaviour listener, dialog listener and tts listener, so that new messages can be send.
   * This is important to synchronize the displaying of speech bubbles and tts output.
   *
   * This method is called whenever the @RosHandler class processes a <code>tts_done</code> message.
   */
  public void freeSpeechListener() {
    _listeners.parallelStream().forEach(Listener::free);
    _dListeners.parallelStream().forEach(Listener::free);
    _ttsListeners.parallelStream().forEach(Listener::free);
  }



}
