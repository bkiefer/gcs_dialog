package de.dfki.vondabase.RosInterface;

import de.dfki.mlt.rudimant.agent.DialogueAct;

public class SpeechEventFactory {

  public static DialogueAct translateEvent2Dia(String speech_event) {
    return new DialogueAct(speech_event);
  }

}
