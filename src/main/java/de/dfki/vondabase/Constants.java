package de.dfki.vondabase;

public interface Constants {

  public static final String CFG_ONTOLOGY_FILE = "ontologyFile";

  public static final String CFG_VISUALISE = "visualise";

  //public static final String USER_CLASS = "<dom:Animate>";

  public static final String ROBOT_CLASS = "<dom:Inanimate>";

  public static final String ROBOT_URI = "<dom:Rolli01>";

  // MQTT TOPICS

  String IN_TOPIC = "core/messages";
  String ASR_TOPIC = "voskasr/asr";
  String OUT_TOPIC = "dialogue/messages";

}
