package de.dfki.vondabase.RosInterface.services;

import de.dfki.vondabase.AbstractAgent;

public class GCSService extends AbstractService{

  private final AbstractAgent _agent;

  private final int _bodyId;


  /**
   * Called by the RosHandler whenever an GSCService.srv call was received
   * @param agent the instance of the used  Agent
   * @param bodyId the bodyID of the "user", body Id must be the same as the one of the corresponding bodyframe
   */
  public GCSService(AbstractAgent agent, int bodyId){
    _agent = agent;
    _bodyId = bodyId;
  }


  @Override
  public void updateIS() throws ServiceException {

      _agent.setActiveServiceCall(this);
      _agent.triggerGCS(_bodyId);

  }


}
