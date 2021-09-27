package de.dfki.vondabase;

import de.dfki.mlt.rosBridge.utils.std.Header;
import de.dfki.vondabase.RosInterface.msgs.GCSMessage;
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
  protected String DEFNS = "dom";
  public DayTime dTime = DayTime.day;
  private boolean verbose;
  private StateDump stateDump;

  /* ===== Core Workings =================================================== */
  private HfcDbHandler handler;
  private HfcDbServer server;
  protected AbstractService _activeServiceCall;

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

  public DayTime getDayTime(){
    LocalDateTime date = LocalDateTime.now(); //https://www.w3schools.com/java/java_date.asp
    int hours = date.getHour();

    if (hours > 5 && hours < 11){
      return DayTime.morning;
    }
    else if (hours >= 11 && hours < 16){
      return DayTime.day;
    }
    else if (hours >= 16 && hours <= 22){
      return DayTime.evening;
    }
    else {
      return DayTime.night;
    }
  }

  @Override
  protected Behaviour createBehaviour(int delay, DialogueAct da) {
    return createExtendedBehaviour(delay, da);
  }

  private Behaviour createExtendedBehaviour(int delay, DialogueAct da){
    Pair<String, String> toSay = this.asr.generate(da.getDag());
    return new ExtendedBehaviour(this.generateId(), (String) toSay.second, (String) toSay.first, delay, da);
  }


  protected Rdf internalize(String endPOI){
    Rdf poi =  _proxy.getRdf("<"+DOMAIN_NS+""+endPOI+">");
    if (poi != null)
      return poi;
    throw new IllegalArgumentException("Unknown POI");
  }


  public final DialogueAct emitDAWB(int delay, DialogueAct da, Triple ... choices) {
    logger.debug("Calling emitDAWB with choices: " +Arrays.toString(choices) );
    ExtendedBehaviour extendedBehaviour = (ExtendedBehaviour) createExtendedBehaviour(delay, da);
    extendedBehaviour.setButtonChoices(choices);
    emitBehaviour(extendedBehaviour);
    return this.addToMyDA(da);
  }

  public final DialogueAct emitDAWB(DialogueAct da, Triple ... choices) {
    return this.emitDAWB(Behaviour.DEFAULT_DELAY, da, choices);
  }

  public final void emitStatus(int status){
    StatusMessage msg = new StatusMessage(new Header(), status);
    emitStatus(msg);
  }

  public final void emitGCS(int eyes, int awareness, int motions){
    int sum = eyes + awareness + motions;
    GCSMessage message = new GCSMessage(new Header(), eyes, awareness, motions, sum);
    ((BaseCommunicationHub)_hub).sendGCS(message);
  }

  public final void emitStatus(StatusMessage msg){
    ((BaseCommunicationHub)_hub).sendStatus(msg);
  }

  public void storeState() {
    this.stateDump = new StateDump(this);
  }

  public void recoverState(){
    this.state = stateDump.getState();
    robot.setValue("<dom:hasInternalState>", stateDump.getInternalState());
    this.stateDump = null;
  }

  public void resetAgent(){
    this.reset();
    this.state = "initial";
    // TODO add more project specific details
  }


  public void setActiveServiceCall(AbstractService service){
    _activeServiceCall = service;
  }

  public void triggerGCS(int bodyId){
    if(user != null){
      throw new IllegalStateException("Can't talk to two people at once");
    } else {
      user = initUser(bodyId);
      state = "gcs_init";
      newData();
    }
  }

  /**
   * TOOD extend with bodyId etc
   * @param bodyId
   * @return
   */
  public Rdf initUser(int bodyId){
    return _proxy.getClass(ROBOT_CLASS).getNewInstance(DOMAIN_NS);
  }
}
