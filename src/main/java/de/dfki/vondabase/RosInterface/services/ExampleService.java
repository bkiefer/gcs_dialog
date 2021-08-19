package de.dfki.vondabase.RosInterface.services;

import de.dfki.vondabase.AbstractAgent;

public class ExampleService extends AbstractService{

  private final AbstractAgent _agent;

  /**
   * Called by the RosHandler whenever an ElevatorDialogue.srv call or a corresponding REST API call was received
   * @param agent the instance of the used Intuitiv Agent (specific for Rolli, or Koffi)
   */
  public ExampleService(AbstractAgent agent){
    _agent = agent;
  }

  @Override
  public void updateIS() {
    //TODO use agent object to update information state
  }
}
