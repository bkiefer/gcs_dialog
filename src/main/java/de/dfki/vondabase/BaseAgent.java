package de.dfki.vondabase;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import de.dfki.lt.hfc.db.rdfProxy.Rdf;

public abstract class BaseAgent extends AbstractAgent implements Constants {

  private final Deque<Command> cmdQueue = new ArrayDeque<>();

  /** This tells us if we gave the first or second nav instruction already (for
   *  the activeTrigger)
   */
  public int instructionGiven;


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

  //////////////////////////////////// Method connected to TestSuite -

  /**
   * Example for adding information to ontology (information state)
   * @param x
   * @param y
   * @param z
   */
  public void addDestination(double x, double y, int z){
    if (robot.getValue("<dom:hasDestination>").isEmpty()){
      Rdf user = _proxy.getClass("<dom:Animate>").getNewInstance("dom");
      user.setValue("<dom:forename>", "Johannes");
      user.setValue("<dom:surname>", "Müller");
      user.setValue("<dom:hasGender>", "male");
      user.setValue("<dom:hasTitle>", "Herr");
      user.setValue("<dom:secondTriggerTime>", 1.0);
      user.setValue("<dom:firstTriggerTime>", 2.0);
      this.user = user;
      newData();
    }
  }

}
