package de.dfki.vondabase.RosInterface.msgs;

import de.dfki.mlt.rosBridge.utils.Message;
import de.dfki.mlt.rosBridge.utils.std.Header;

import javax.json.Json;
import javax.json.JsonObject;

public class SoundMessage extends Message{


  private final Header header;
  private final String sound;

  public SoundMessage() {
    this(new Header(),"" );
  }

  public SoundMessage(Header header, String message) {
    super(Json.createObjectBuilder().add("header", header.toJsonObject()).add("sound", message).build());
    this.header = header;
    this.sound = message;
  }

  public Header getHeader() {
    return this.header;
  }

  public String getMessage() {
    return this.sound;
  }


  public static SoundMessage fromJsonString(String jsonString) {
    return fromMessage(new Message(jsonString));
  }

  public static SoundMessage fromMessage(Message m) {
    return fromJsonObject(m.toJsonObject());
  }

  public static SoundMessage fromJsonObject(JsonObject jsonObject) {
    Header header = jsonObject.containsKey("header") ? Header.fromJsonObject(jsonObject.getJsonObject("header")) : new Header();
    String status = jsonObject.containsKey("sound") ? jsonObject.getString("sound") : "";
    return new SoundMessage(header, status);
  }
}
