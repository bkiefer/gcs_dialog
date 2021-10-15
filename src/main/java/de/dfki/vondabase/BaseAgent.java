package de.dfki.vondabase;

import java.util.List;

import de.dfki.lt.hfc.db.rdfProxy.Rdf;

public abstract class BaseAgent extends AbstractAgent implements Constants {



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
