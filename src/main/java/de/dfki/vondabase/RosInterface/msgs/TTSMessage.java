package de.dfki.vondabase.RosInterface.msgs;

import de.dfki.mlt.rosBridge.utils.Message;
import de.dfki.mlt.rosBridge.utils.std.Header;

import javax.json.Json;
import javax.json.JsonObject;

public class TTSMessage extends Message{

  public static final String FIELD_HEADER = "header";
  public static final String COMMAND_ID = "status";
  public static final String MOVEMENT_TYPE = "mood";
  public static final String MOVEMENT_PARAMETER = "intent";
  private final Header header;
  private final String message;

  public TTSMessage() {
    this(new Header(),"" );
  }

  public TTSMessage(Header header, String message) {
    super(Json.createObjectBuilder().add("header", header.toJsonObject()).add("message", message).build());
    this.header = header;
    this.message = message;
  }

  public Header getHeader() {
    return this.header;
  }

  public String getMessage() {
    return this.message;
  }


  public static TTSMessage fromJsonString(String jsonString) {
    return fromMessage(new Message(jsonString));
  }

  public static TTSMessage fromMessage(Message m) {
    return fromJsonObject(m.toJsonObject());
  }

  public static TTSMessage fromJsonObject(JsonObject jsonObject) {
    Header header = jsonObject.containsKey("header") ? Header.fromJsonObject(jsonObject.getJsonObject("header")) : new Header();
    String status = jsonObject.containsKey("message") ? jsonObject.getString("message") : "";
    return new TTSMessage(header, status);
  }
}
