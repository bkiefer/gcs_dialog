package de.dfki.vondabase;

import de.dfki.mlt.rosBridge.utils.std.Header;
import de.dfki.vondabase.RosInterface.msgs.BodyTrackerMessage;
import de.dfki.vondabase.RosInterface.msgs.GCSMessage;
import de.dfki.vondabase.RosInterface.msgs.SkeletonMessage;
import de.dfki.vondabase.RosInterface.msgs.StatusMessage;
import de.dfki.vondabase.RosInterface.services.AbstractService;
import de.dfki.vondabase.RosInterface.services.GCSService;
import de.dfki.vondabase.utils.*;
import de.dfki.lt.hfc.WrongFormatException;
import de.dfki.lt.hfc.db.rdfProxy.Rdf;
import de.dfki.lt.hfc.db.rdfProxy.RdfProxy;
import de.dfki.lt.hfc.db.server.HandlerFactory;
import de.dfki.lt.hfc.db.server.HfcDbHandler;
import de.dfki.lt.hfc.db.server.HfcDbServer;
import de.dfki.mlt.rudimant.agent.Agent;
import de.dfki.mlt.rudimant.agent.Behaviour;
import de.dfki.mlt.rudimant.agent.DialogueAct;
import de.dfki.mlt.rudimant.agent.nlg.Pair;

import java.time.LocalDateTime;

import java.io.File;
import java.io.IOException;
import java.util.*;

public abstract class AbstractAgent extends Agent implements Constants {

  public String state = "initial";
  /**
   * Some RDF Objects representing robots and user. Need to be adapted according to project
   */
  public Rdf robot;
  public Rdf user;
  public SkeletonMessage _userSkeleton;
  public DayTime dTime = DayTime.day;
  protected String DEFNS = "dom";
  protected AbstractService _activeServiceCall;
  private int userID = -1;
  private boolean verbose;
  private StateDump stateDump;
  /* ===== Core Workings =================================================== */
  private HfcDbHandler handler;
  private HfcDbServer server;

  private RdfProxy startClient(File configDir, Map<String, Object> configs)
          throws IOException, WrongFormatException {
    String ontoFileName = (String) configs.get(CFG_ONTOLOGY_FILE);
    if (ontoFileName == null) {
      throw new IOException("Ontology file is missing.");
    }
    if (configs.containsKey(CFG_SERVER_PORT)) {
      server = new HfcDbServer();
      server.readConfig(new File(configDir, ontoFileName));
      server.runServer((int) configs.get(CFG_SERVER_PORT));
      handler = server.getHandler();
    } else {
      handler = HandlerFactory.getHandler();
      handler.readConfig(new File(configDir, ontoFileName));
    }
    RdfProxy proxy = new RdfProxy(handler);
    handler.registerStreamingClient(proxy);
    return proxy;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public void init(File configDir, String language, Map configs)
          throws IOException, WrongFormatException {
    RdfProxy proxy = startClient(configDir, configs);
    super.init(configDir, language, proxy, configs);
    this.verbose = (boolean) configs.get(IS_VERBOSE);
    //TODO This is again project specific; needs to be adapted
    robot = _proxy.getRdf(ROBOT_URI);
    if (robot == null) {
      System.err.println(_proxy.getClass(ROBOT_CLASS));
      robot = _proxy.getClass(ROBOT_CLASS).newRdf(ROBOT_URI);
    }
  }

  public void shutdown() {
    handler.shutdown();
    if (server != null) server.shutdown();
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
    Pair<String, String> toSay = this.asr.generate(da.getDag());
    return new ExtendedBehaviour(this.generateId(), (String) toSay.second, (String) toSay.first, delay, da);
  }


  protected Rdf internalize(String endPOI) {
    Rdf poi = _proxy.getRdf("<" + DOMAIN_NS + "" + endPOI + ">");
    if (poi != null)
      return poi;
    throw new IllegalArgumentException("Unknown POI");
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

  public final void emitStatus(int status) {
    StatusMessage msg = new StatusMessage(new Header(), status);
    emitStatus(msg);
  }

  public final void emitGCS(int eyes, int awareness, int motions) {
    int sum = eyes + awareness + motions;
    GCSMessage message = new GCSMessage(new Header(), eyes, awareness, motions, sum);
    ((BaseCommunicationHub) _hub).sendGCS(message);
  }

  public final void emitStatus(StatusMessage msg) {
    ((BaseCommunicationHub) _hub).sendStatus(msg);
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

  public void triggerGCS(int bodyId) {
    if (user != null) {
      throw new IllegalStateException("Can't talk to two people at once");
    } else {
      initUser(bodyId);
      state = "gcs_init";
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
    this.userID = bodyId;
    user = _proxy.getClass(ROBOT_CLASS).getNewInstance(DOMAIN_NS);
  }

  public void resetUser() {
    this.userID = -1;
    user = null;
    _userSkeleton = null;
  }

  public void updateUserTrack(BodyTrackerMessage track) {
    // TODO use bodyId to initialize user rdf with corresponding values, e.g. areEyesOpen
    boolean eyesOpen = Float.parseFloat(track.getHappy()) >= 75.0f;
    user.setValue("<dom:areEyesOpen>", eyesOpen);
    // add gesture
    user.setValue("<dom:hasGender>", track.getHRGender());
    user.setValue("<dom:hasAge>", track.getAge());
  }

  public void updateUserSkeleton(SkeletonMessage skeletonMessage) {
    var hasMovedHead = false;
    var hasMovedLeftHand = false;
    var hasMovedRightHand = false;
    var hasMovedLeftArm = false;
    var hasMovedRightArm = false;
    if (_userSkeleton == null) {
      _userSkeleton = skeletonMessage;
    } else {
      if (skeletonMessage != _userSkeleton) {
        hasMovedHead = SkeletonMessage.delta(skeletonMessage.getJoint_position_head(), _userSkeleton.getJoint_position_head()) >= 1;
        hasMovedLeftHand = SkeletonMessage.delta(skeletonMessage.getJoint_position_left_hand(), _userSkeleton.getJoint_position_left_hand()) >= 1;
        hasMovedRightHand = SkeletonMessage.delta(skeletonMessage.getJoint_position_right_hand(), _userSkeleton.getJoint_position_right_hand()) >= 1;
        hasMovedLeftArm = SkeletonMessage.delta(skeletonMessage.getJoint_position_left_elbow(), _userSkeleton.getJoint_position_left_elbow()) >= 1;
        hasMovedRightArm = SkeletonMessage.delta(skeletonMessage.getJoint_position_right_elbow(), _userSkeleton.getJoint_position_right_elbow()) >= 1;
        //var hasMovedLeftLeg = SkeletonMessage.delta(skeletonMessage.getJoint_position, _userSkeleton.getJoint_position_head()) >= 1;
        //var hasMovedRightLeg = SkeletonMessage.delta(skeletonMessage.getJoint_position_head(), _userSkeleton.getJoint_position_head()) >= 1;
      }
    }
    user.setValue("<dom:hasMoved>", (hasMovedHead || hasMovedLeftHand || hasMovedRightHand || hasMovedLeftArm || hasMovedRightArm));
    user.setValue("<dom:hasMovedHead>", hasMovedHead );
    user.setValue("<dom:hasMovedLeftHand>", hasMovedLeftHand );
    user.setValue("<dom:hasMovedLeftArm>", hasMovedLeftArm );
    user.setValue("<dom:hasMovedRightArm>", hasMovedRightArm );
    user.setValue("<dom:hasMovedRightHand>", hasMovedRightHand );
  }

  public int getUserID() {
    return userID;
  }
}
