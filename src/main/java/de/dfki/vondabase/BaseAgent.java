package de.dfki.vondabase;

import static de.dfki.vondabase.Constants.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.ULocale;

import de.dfki.lt.hfc.WrongFormatException;
import de.dfki.lt.hfc.db.HfcDbHandler;
import de.dfki.lt.hfc.db.rdfProxy.Rdf;
import de.dfki.lt.hfc.db.rdfProxy.RdfProxy;
import de.dfki.mlt.rudimant.agent.nlp.Pair;
import de.dfki.mlt.rudimant.agent.Agent;
import de.dfki.mlt.rudimant.agent.Behaviour;
import de.dfki.mlt.rudimant.agent.nlp.DialogueAct;
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
    super.init(configDir, language, proxy, configs, "dom");
    //this.verbose = (boolean) configs.get(IS_VERBOSE);
    //TODO This is again project specific; needs to be adapted
    robot = _proxy.getRdf(ROBOT_URI);
    if (robot == null) {
      System.err.println(_proxy.getClass(ROBOT_CLASS));
      robot = _proxy.getClass(ROBOT_CLASS).newRdf(ROBOT_URI);
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

  //array of weekdays for orientation question check
  @SuppressWarnings("serial")
  static Map<String, Integer> weekdays = new HashMap<String, Integer>() {{
    put("Sonntag", Calendar.SUNDAY);
    put("Montag", Calendar.MONDAY);
    put("Dienstag", Calendar.TUESDAY);
    put("Mittwoch", Calendar.WEDNESDAY);
    put("Donnerstag", Calendar.THURSDAY);
    put("Freitag", Calendar.FRIDAY);
    put("Samstag", Calendar.SATURDAY);
  }};

  //array of months for orientation question check
  @SuppressWarnings("serial")
  static Map<String, Integer> months = new HashMap<String, Integer>() {{
    put("Januar", Calendar.JANUARY);
    put("Februar", Calendar.FEBRUARY);
    put("Maerz", Calendar.MARCH);
    put("April", Calendar.APRIL);
    put("Mai", Calendar.MAY);
    put("Juni", Calendar.JUNE);
    put("Juli", Calendar.JULY);
    put("August", Calendar.AUGUST);
    put("September", Calendar.SEPTEMBER);
    put("Oktober", Calendar.OCTOBER);
    put("November", Calendar.NOVEMBER);
    put("Dezember", Calendar.DECEMBER);
  }};

  public boolean checkAnswer(String answer, String question_type) {
    boolean result = false;
    int index, dow;
    switch(question_type) {
    case "DayOfWeek":
      index = weekdays.get(answer);
      dow = Calendar.getInstance(new ULocale(getLanguage())).get(Calendar.DAY_OF_WEEK);
      result = index == dow;
      break;
    case "Month":
      index = months.get(answer);
      dow = Calendar.getInstance(new ULocale(getLanguage())).get(Calendar.MONTH);
      result = index == dow;
      break;
    }
    return result;
  }
}
