package de.dfki.vondabase.RosInterface.msgs;

import de.dfki.mlt.rosBridge.utils.Message;
import de.dfki.mlt.rosBridge.utils.geometry.Point;
import de.dfki.mlt.rosBridge.utils.std.Header;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * Example from Intuitiv Project.
 * TODO adapt to work with Luebeck msgs
 */
public class StatusMessage extends Message {

  public static final String FIELD_HEADER = "header";
  public static final String STATUS = "status";
  public static final int STATUS_INTRODUCTION = 0;
  public static final int STATUS_EYES = 1;
  public static final int STATUS_AWARENESS = 2;
  public static final int STATUS_MOTORICS = 3;
  public static final int STATUS_DONE = 4;

  private final Header header;
  private final int status;

  public StatusMessage() {
    this( new Header(), 0);
  }

  public StatusMessage( Header header, int status1) {
    super(Json.createObjectBuilder()
            .add(FIELD_HEADER, header.toJsonObject())
            .add(STATUS, status1).build(), "intuitiv_msgs/TaskStatus");
    this.header = header;
    this.status = status1;
  }

  public static StatusMessage fromJsonString(String jsonString) {
    return fromMessage(new Message(jsonString));
  }

  public static StatusMessage fromMessage(Message m) {
    return fromJsonObject(m.toJsonObject());
  }

  public static StatusMessage fromJsonObject(JsonObject jsonObject) {
    Header header = jsonObject.containsKey(FIELD_HEADER) ? Header.fromJsonObject(jsonObject.getJsonObject(FIELD_HEADER)) : new Header();
    int status = jsonObject.containsKey(STATUS) ? jsonObject.getInt(STATUS): 0;
    return new StatusMessage( header, status);
  }

}
