package de.dfki.vondabase.RosInterface.msgs;

import de.dfki.mlt.rosBridge.utils.Message;
import de.dfki.mlt.rosBridge.utils.std.Header;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;

public class DialogueMessage extends Message {


  private final Header header;
  private final String text;
  private final JsonArray buttons;

  public DialogueMessage() {
    this(new Header(), "", Json.createArrayBuilder().build());
  }

  public DialogueMessage(Header header, String text, JsonArray buttons) {
    super(Json.createObjectBuilder().add("header", header.toJsonObject()).add("text", text).add("buttons", buttons).build());
    this.header = header;
    this.text = text;
    this.buttons = buttons;
  }

  public Header getHeader() {
    return this.header;
  }

  public String getText() {
    return this.text;
  }

  public JsonArray getButtons() {
    return this.buttons;
  }

  public static DialogueMessage fromJsonString(String jsonString) {
    return fromMessage(new Message(jsonString));
  }

  public static DialogueMessage fromMessage(Message m) {
    return fromJsonObject(m.toJsonObject());
  }

  public static DialogueMessage fromJsonObject(JsonObject jsonObject) {
    Header header = jsonObject.containsKey("header") ? Header.fromJsonObject(jsonObject.getJsonObject("header")) : new Header();
    String text = jsonObject.containsKey("text") ? jsonObject.getString("text") : "";
    JsonArray buttons = jsonObject.containsKey("buttons") ? jsonObject.getJsonArray("buttons") : Json.createArrayBuilder().build();
    return new DialogueMessage(header, text, buttons);
  }
}
