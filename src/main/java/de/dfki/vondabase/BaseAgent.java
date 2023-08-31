package de.dfki.vondabase;

import static de.dfki.vondabase.Constants.CFG_ONTOLOGY_FILE;
import static de.dfki.vondabase.Constants.ROBOT_CLASS;
import static de.dfki.vondabase.Constants.ROBOT_URI;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.ULocale;

import de.dfki.lt.hfc.WrongFormatException;
import de.dfki.lt.hfc.db.HfcDbHandler;
import de.dfki.lt.hfc.db.rdfProxy.Rdf;
import de.dfki.lt.hfc.db.rdfProxy.RdfProxy;
import de.dfki.mlt.rudimant.agent.Agent;
import de.dfki.mlt.rudimant.agent.Behaviour;
import de.dfki.mlt.rudimant.agent.nlp.DialogueAct;
import de.dfki.mlt.rudimant.agent.nlp.Pair;
import de.dfki.vondabase.data.Command;
import de.dfki.vondabase.utils.ExtendedBehaviour;

public abstract class BaseAgent extends Agent {
  /**
   * Some RDF Objects representing robots and user. Need to be adapted according
   * to project
   */
  public Rdf robot;
  public Rdf user;

  /** Store external incoming signals from the sensors here */
  private final Deque<Command> cmdQueue = new ArrayDeque<>();

  private HfcDbHandler handler = null;

  private RdfProxy startClient(File configDir, Map<String, Object> configs)
          throws IOException, WrongFormatException {
    String ontoFileName = (String) configs.get(CFG_ONTOLOGY_FILE);
    if (ontoFileName == null) {
      throw new IOException("Ontology file is missing.");
    }
    handler = new HfcDbHandler(ontoFileName);
    //handler = h;
    RdfProxy proxy = new RdfProxy(handler);
    handler.registerStreamingClient(proxy);
    return proxy;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public void init(File configDir, Map configs, String language)
          throws IOException, WrongFormatException {
    RdfProxy proxy = startClient(configDir, configs);
    init(configDir, language, proxy, configs, "dom");
    //this.verbose = (boolean) configs.get(IS_VERBOSE);
    //TODO This is again project specific; needs to be adapted
    robot = _proxy.getRdf(ROBOT_URI);
    if (robot == null) {
      //System.err.println(_proxy.getClass(ROBOT_CLASS));
      robot = _proxy.getClass(ROBOT_CLASS).newRdf(ROBOT_URI);
    }
    if (configs.containsKey("filterRules")) {
      ruleLogger.filterUnchangedRules = (Boolean)configs.get("filterRules");
    }
    logAllRules();
  }

  @Override
  public void shutdown() {
    handler.shutdown();
    //if (server != null) server.shutdown();
    super.shutdown();
  }

  @Override
  protected Behaviour createBehaviour(int delay, DialogueAct da) {
    return createExtendedBehaviour(delay, da);
  }

  private Behaviour createExtendedBehaviour(int delay, DialogueAct da) {
    Pair<String, String> toSay = langServices.generate(da.getDag());
    if (toSay == null) {
      return new ExtendedBehaviour(generateId(), "error", "error", 0, da);
    }
    return new ExtendedBehaviour(generateId(), toSay.second, toSay.first, delay, da);
  }

  /* ===== Support Functions =============================================== */

  /**
   * retrieve information from informationstate
   * @param user
   * @return
   */
  public List<Object> getAllSessions(Rdf user) {
    // TODO: have a special Rdf.getAll(prop) method??
    return _proxy.query(
        "select ?sess where {} <dom:hasSession> ?sess ?_", user.getURI());
  }

  /** Add incoming command to the command queue */
  void addCommand(Command c) {
    cmdQueue.offer(c);
    newData();
  }

  /** return and remove the last command */
  Command removeLastCommand() {
    return cmdQueue.poll();
  }

  /** return received command message, if any, without removing it from queue */
  public Command getCommand(){
    return cmdQueue.peek();
  }

  public Rdf getUser(String id) {
    // query db for user with id and return, or return null
    List<Object> result =
        query("select ?u where ?u <rdf:type> <dom:User> ?_ & ?u <dom:id> \"{}\" ?_");

    return result.isEmpty() ? null : (Rdf)result.get(0);
  }

  public Rdf getRandomItem (String type_uri) {
	String query = String.format(
        "select ?a where ?a <rdf:type> %s ?_", type_uri);
	List<Object> items = _proxy.query(query);
	Random rand = new Random();
	return items.isEmpty() ? null : (Rdf)items.get(rand.nextInt(items.size()));
  }

  public String[] splitLimb(String limbspec) {
    return limbspec.split("_");
  }

  String getName(String what) {
    ULocale locale = new ULocale(getLanguage());
    Calendar calendar = Calendar.getInstance(locale);
    SimpleDateFormat df2 = new SimpleDateFormat (what, locale);
    String monthname = df2.format(calendar.getTime());
    return monthname;
  }

  public boolean checkAnswer(String answer, String question_type) {
    if (answer == null || question_type == null) return false;
    boolean result = false;
    switch(question_type) {
    case "DayOfWeek":
      result = answer.equalsIgnoreCase(getName(SimpleDateFormat.WEEKDAY));
      break;
    case "Month":
      result = answer.equalsIgnoreCase(getName(SimpleDateFormat.MONTH));
      break;
    }
    return result;
  }
}
