package de.dfki.vondabase.RosInterface.msgs;

import de.dfki.mlt.rosBridge.utils.Message;
import de.dfki.mlt.rosBridge.utils.std.Header;

import javax.json.Json;
import javax.json.JsonObject;

public class AvatarMessage extends Message {


  private final Header header;
  private final String status;
  private final String mood;

  public AvatarMessage() {
    this(new Header(), "", "neutral");
  }

  public AvatarMessage(Header header, String status, String mood) {
    super(Json.createObjectBuilder().add("header", header.toJsonObject()).add("status", status).add("mood", mood).build());
    this.header = header;
    this.status = status;
    this.mood = mood;
  }

  public Header getHeader() {
    return this.header;
  }

  public String getStatus() {
    return this.status;
  }

  public String getMood() {
    return this.mood;
  }

  public static AvatarMessage fromJsonString(String jsonString) {
    return fromMessage(new Message(jsonString));
  }

  public static AvatarMessage fromMessage(Message m) {
    return fromJsonObject(m.toJsonObject());
  }

  public static AvatarMessage fromJsonObject(JsonObject jsonObject) {
    Header header = jsonObject.containsKey("header") ? Header.fromJsonObject(jsonObject.getJsonObject("header")) : new Header();
    String status = jsonObject.containsKey("status") ? jsonObject.getString("status") : "";
    String mood = jsonObject.containsKey("mood") ? jsonObject.getString("mood") : "neutral";
    return new AvatarMessage(header, status, mood);
  }
}
