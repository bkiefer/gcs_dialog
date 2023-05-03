package de.dfki.vondabase;

import static de.dfki.vondabase.Constants.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;

import org.apache.tools.ant.taskdefs.email.Header;

import com.google.common.util.concurrent.AbstractService;

import de.dfki.lt.hfc.WrongFormatException;
import de.dfki.lt.hfc.db.HfcDbHandler;
import de.dfki.lt.hfc.db.rdfProxy.Rdf;
import de.dfki.lt.hfc.db.rdfProxy.RdfProxy;
import de.dfki.mlt.rudimant.agent.Agent;
import de.dfki.mlt.rudimant.agent.Behaviour;
import de.dfki.mlt.rudimant.agent.nlp.DialogueAct;
import de.dfki.mlt.rudimant.agent.nlp.Pair;
import de.dfki.vondabase.utils.DayTime;
import de.dfki.vondabase.utils.ExtendedBehaviour;
import de.dfki.vondabase.utils.StateDump;
import de.dfki.vondabase.utils.Triple;

public abstract class AbstractAgent extends Agent {

  public String state = "initial";
  /**
   * Some RDF Objects representing robots and user. Need to be adapted according to project
   */
  public Rdf robot;
  public Rdf user;
  public boolean isUZLTest;
  //public SkeletonMessage _userSkeleton;
  public DayTime dTime = DayTime.day;
  protected AbstractService _activeServiceCall;
  private int userID = -1;
  private boolean verbose;
  private StateDump stateDump;
  /* ===== Core Workings =================================================== */
  private HfcDbHandler handler;
  //private HfcDbServer server;
  //private boolean _ignoreRos = false;

  private RdfProxy startClient(File configDir, Map<String, Object> configs)
          throws IOException, WrongFormatException {
    String ontoFileName = (String) configs.get(CFG_ONTOLOGY_FILE);
    if (ontoFileName == null) {
      throw new IOException("Ontology file is missing.");
    }
    /*
    server = new HfcDbServer(new File(configDir, ontoFileName).getPath());
    if (configs.containsKey(CFG_SERVER_PORT)) {
      server.runServer((int) configs.get(CFG_SERVER_PORT));
    }
    handler = server.getHandler();
    */
    HfcDbHandler h = new HfcDbHandler(ontoFileName);
    handler = h;
    RdfProxy proxy = new RdfProxy(handler);
    handler.registerStreamingClient(proxy);
    return proxy;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public void init(File configDir, Map configs, String language)
          throws IOException, WrongFormatException {
    RdfProxy proxy = startClient(configDir, configs);
    super.init(configDir, language, proxy, configs, "dom");
    this.verbose = (boolean) configs.get(IS_VERBOSE);
    //TODO This is again project specific; needs to be adapted
    robot = _proxy.getRdf(ROBOT_URI);
    if (robot == null) {
      System.err.println(_proxy.getClass(ROBOT_CLASS));
      robot = _proxy.getClass(ROBOT_CLASS).newRdf(ROBOT_URI);
    }
  }

  @Override
  public void shutdown() {
    handler.shutdown();
    //if (server != null) server.shutdown();
    super.shutdown();
  }

  public DayTime getDayTime() {
    LocalDateTime date = LocalDateTime.now(); //https://www.w3schools.com/java/java_date.asp
    int hours = date.getHour();

    if (hours > 5 && hours < 11) {
      return DayTime.morning;
    } else if (hours >= 11 && hours < 16) {
      return DayTime.day;
    } else if (hours >= 16 && hours <= 22) {
      return DayTime.evening;
    } else {
      return DayTime.night;
    }
  }

  @Override
  protected Behaviour createBehaviour(int delay, DialogueAct da) {
    return createExtendedBehaviour(delay, da);
  }

  private Behaviour createExtendedBehaviour(int delay, DialogueAct da) {
    Pair<String, String> toSay = this.langServices.generate(da.getDag());
    return new ExtendedBehaviour(this.generateId(), toSay.second, toSay.first, delay, da);
  }


  public final DialogueAct emitDAWB(int delay, DialogueAct da, Triple... choices) {
    logger.debug("Calling emitDAWB with choices: " + Arrays.toString(choices));
    ExtendedBehaviour extendedBehaviour = (ExtendedBehaviour) createExtendedBehaviour(delay, da);
    extendedBehaviour.setButtonChoices(choices);
    emitBehaviour(extendedBehaviour);
    return this.addToMyDA(da);
  }

  public final DialogueAct emitDAWB(DialogueAct da, Triple... choices) {
    return this.emitDAWB(Behaviour.DEFAULT_DELAY, da, choices);
  }

  public void storeState() {
    this.stateDump = new StateDump(this);
  }

  public void recoverState() {
    this.state = stateDump.getState();
    robot.setValue("<dom:hasInternalState>", stateDump.getInternalState());
    this.stateDump = null;
  }

  public void resetAgent() {
    this.reset();
    this.state = "initial";
    // TODO add more project specific details
  }


  public void setActiveServiceCall(AbstractService service) {
    _activeServiceCall = service;
  }

  public void triggerGCS(int bodyId, int phase) {
    if (user != null) {
      throw new IllegalStateException("Can't talk to two people at once");
    } else {
      initUser(bodyId);
      // added trigger for specific phases to simplify testing by UZL
      switch (phase){
        case 1:
          state = "gcs_phase1";
          isUZLTest = true;
          break;
        case 2:
          state = "gcs_phase2";
          isUZLTest = true;
          break;
        case 3:
          state = "gcs_phase3";
          isUZLTest = true;
          break;
        default:
          state = "gcs_init";
          isUZLTest = false;
      }
      newData();
    }
  }

  /**
   * TOOD extend with bodyId etc
   *
   * @param bodyId
   * @return
   */
  public void initUser(int bodyId) {
    System.err.println("New user for body_id " + bodyId);
    this.userID = bodyId;
    user = _proxy.getClass(USER_CLASS).getNewInstance(DOMAIN_NS);
    user.setValue("<dom:areEyesOpen>", false);
    user.setValue("<dom:isMouthOpen>", false);
    user.setValue("<dom:isHandOpen>", false);

    user.setValue("<dom:hasMovedLeftArm>", 0);
    user.setValue("<dom:hasMovedRightArm>", -1);
    user.setValue("<dom:hasMovedRightLeg>", -1);
    user.setValue("<dom:hasMovedLeftLeg>", -1);
    user.setValue("<dom:hasMovedRightHand>", -1);
    user.setValue("<dom:hasMovedLeftHand>", 0);

    user.setValue("<dom:rightHandConfidence>",1.0);
    user.setValue("<dom:leftHandConfidence>",1.0);
    user.setValue("<dom:rightArmConfidence>",1.0);
    user.setValue("<dom:leftArmConfidence>",1.0);
    user.setValue("<dom:rightLegConfidence>",1.0);
    user.setValue("<dom:leftLegConfidence>",1.0);

    user.setValue("<dom:gcs_phase1>", 0);
    user.setValue("<dom:gcs_phase2>", 0);
    user.setValue("<dom:gcs_phase3>", 0);
    // add gesture
    user.setValue("<dom:hasGender>", "unknown");
    user.setValue("<dom:hasAge>", -1);
  }

  public void resetUser() {
    this.userID = -1;
    user = null;
    //_userSkeleton = null;
  }
/**
  public void updateUserTrack(BodyTrackerMessage track) {
    if (!_ignoreRos) {
      // TODO use bodyId to initialize user rdf with corresponding values, e.g. areEyesOpen
      // angry, surprise, happy, neutral
      double[] emotions = track.getEmotions();
      boolean eyesOpen = emotions[1] >= 0.75d;
      boolean mouthOpen = emotions[0] >= 0.75d;
      user.setValue("<dom:areEyesOpen>", eyesOpen);
      user.setValue("<dom:isMouthOpen>", mouthOpen);
      // add gesture
      user.setValue("<dom:hasGender>", track.getHRGender());
      user.setValue("<dom:hasAge>", track.getAge());
      user.setValue("<dom:performsGesture>", BodyTrackerMessage.idToGesture(track.getGesture()));
    }
  }
 */


  public int getUserID() {
    return userID;
  }

  public void moveRightArm(int bodyId, int armMoved) {

    if (userID == bodyId) {
      user.setValue("<dom:hasMovedRightArm>", armMoved);
      System.err.println("User id " + bodyId + " rightArmMoved: " + armMoved );
      newData();
    }
  }

  public void moveLeftArm(int bodyId, int armMoved) {

    if (userID == bodyId) {
      user.setValue("<dom:hasMovedLeftArm>", armMoved);
      System.err.println("User id " + bodyId + " leftArmMoved: " + armMoved );
      newData();
    }
  }

  public void moveLeftLeg(int bodyId, int legMoved) {

    if (userID == bodyId) {
      user.setValue("<dom:hasMovedLeftLeg>", legMoved);
      System.err.println("User id " + bodyId + " leftLegMoved: " + legMoved );
      newData();
    }
  }

  public void moveRightLeg(int bodyId, int legMoved) {

    if (userID == bodyId) {
      user.setValue("<dom:hasMovedRightLeg>", legMoved);
      System.err.println("User id " + bodyId + " rightlegMoved: " + legMoved );
      newData();
    }
  }


  public void moveLeftHand(int bodyId, int handMoved) {

    if (userID == bodyId) {
      user.setValue("<dom:hasMovedLeftHand>", handMoved);
      System.err.println("User id " + bodyId + " leftHandMoved: " + handMoved );
      newData();
    }
  }

  public void moveRightHand(int bodyId, int handMoved) {

    if (userID == bodyId) {
      user.setValue("<dom:hasMovedRightHand>", handMoved);
      System.err.println("User id " + bodyId + " rightHandMoved: " + handMoved );
      newData();
    }
  }

  public void handOpen(int bodyId, boolean handOpen) {

    if (userID == bodyId) {
      user.setValue("<dom:isHandOpen>", handOpen);
      System.err.println("User id " + bodyId + " isHandOpen: " + handOpen );
      newData();
    }
  }
}
