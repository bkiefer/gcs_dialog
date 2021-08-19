package de.dfki.vondabase.RosInterface.msgs;

import de.dfki.mlt.rosBridge.utils.Message;
import javax.json.Json;
import javax.json.JsonObject;

public class ButtonMessage extends Message {

  public static final String FIELD_HEADER = "header";
  public static final String COMMAND_ID = "status";
  public static final String MOVEMENT_TYPE = "mood";
  public static final String MOVEMENT_PARAMETER = "intent";
  private final String title;
  private final String topic; //e.g. /speech_event
  private final String event; //e.g. /Confirm(Instruction)
  private final int color_index;

  public ButtonMessage() {
    this( "", "","", 0);
  }

  public ButtonMessage( String title, String topic, String event, int color_index) {
    super(Json.createObjectBuilder().add("title", title).add("topic", topic).add("button_event", event).add("color_index", color_index).build());
    this.title = title;
    this.topic = topic;
    this.event = event;
    this.color_index = color_index;
  }

  public String getTitle() {
    return this.title;
  }

  public String getTopic() {
    return this.topic;
  }

  public String getEvent() {
    return this.event;
  }

  public int getColor_index() { return this.color_index; }

  public static ButtonMessage fromJsonString(String jsonString) {
    return fromMessage(new Message(jsonString));
  }

  public static ButtonMessage fromMessage(Message m) {
    return fromJsonObject(m.toJsonObject());
  }

  public static ButtonMessage fromJsonObject(JsonObject jsonObject) {
    String title = jsonObject.containsKey("title") ? jsonObject.getString("title") : "";
    String topic = jsonObject.containsKey("topic") ? jsonObject.getString("topic") : "";
    String event = jsonObject.containsKey("event") ? jsonObject.getString("event") : "" ;
    int colorIndex = jsonObject.containsKey("color_index") ? jsonObject.getInt("color_index") : 0;
    return new ButtonMessage( title, topic, event, colorIndex);
  }



}
