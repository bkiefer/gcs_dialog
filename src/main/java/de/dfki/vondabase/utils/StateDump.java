package de.dfki.vondabase.utils;

import de.dfki.vondabase.AbstractAgent;
import de.dfki.lt.hfc.db.rdfProxy.Rdf;

public class StateDump {

  public String getState() {
    return _state;
  }

  public Rdf getActivity() {
    return _activity;
  }

  public String getInternalState() {
    return _internalState;
  }

  private final String _state;

  private final Rdf _activity;

  private final String _internalState;

  public StateDump(AbstractAgent abstractAgent) {
    _state = abstractAgent.state;
    _activity = abstractAgent.robot.getRdf("<dom:hasActivity>");
    _internalState = abstractAgent.robot.getString("<dom:hasInternalState>");

  }
}
