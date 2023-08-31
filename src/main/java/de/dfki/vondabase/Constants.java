package de.dfki.vondabase;

public interface Constants {

  public static final String CFG_ONTOLOGY_FILE = "ontologyFile";

  public static final String CFG_VISUALISE = "visualise";

  //public static final String USER_CLASS = "<dom:Animate>";

  public static final String ROBOT_CLASS = "<dom:Robot>";

  public static final String ROBOT_URI = "<dom:TakeCare01>";

  // MQTT TOPICS

  String IN_TOPIC = "core/messages";
  String ASR_TOPIC = "voskasr/asrresult";

  String OUT_TOPIC = "dialogue/messages";
  String TTS_TOPIC = "tts/behaviour";

}
