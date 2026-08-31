package de.dfki.vondabase;

public interface Constants {

  public static final String CFG_ONTOLOGY_FILE = "ontologyFile";

  public static final String CFG_VISUALISE = "visualise";

  //public static final String USER_CLASS = "<dom:Animate>";

  public static final String ROBOT_CLASS = "<dom:Robot>";

  public static final String ROBOT_URI = "<dom:TakeCare01>";

  // MQTT TOPICS

  public static final String IN_TOPIC = "core/messages";
  public static final String ASR_TOPIC = "voskasr/asrresult";
  public static final String ASR_CTRL_TOPIC = "voskasr/control";

  public static final String OUT_TOPIC = "dialogue/messages";
  public static final String TTS_TOPIC = "tts/behaviour";

  // ASR is suppressed when TTS is active?
  public static final String TTS_STOPS_ASR = "ttsStopsAsr";

}
