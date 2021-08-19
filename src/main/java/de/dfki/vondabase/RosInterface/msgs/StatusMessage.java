package de.dfki.vondabase.RosInterface.msgs;

import de.dfki.mlt.rosBridge.utils.Message;
import de.dfki.mlt.rosBridge.utils.geometry.Point;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * Example from Intuitiv Project.
 * TODO adapt to work with Luebeck msgs
 */
public class StatusMessage extends Message {

  public static final String FIELD_HEADER = "header";
  public static final String START = "start";
  public static final String DESTINATION = "destination";
  public static final String WITHPATIENT = "withClient";
  public static final String NEXTELEVATOR = "nextElevator";

  private final Point start;
  private final Point destination; //e.g. /speech_event
  private final boolean withPatient; //e.g. /Confirm(Instruction)
  private final Point nextElevator;

  public StatusMessage() {
    this( null, null,false, null);
  }

  public StatusMessage( Point start, Point destination, boolean withPatient, Point nextElevator) {
    super(Json.createObjectBuilder()
            .add(START, start.toJsonObject())
            .add(DESTINATION, destination.toJsonObject())
            .add(WITHPATIENT, withPatient)
            .add(NEXTELEVATOR, nextElevator.toJsonObject())
            .build(), "intuitiv_msgs/TaskStatus");
    this.start = start;
    this.destination = destination;
    this.withPatient = withPatient;
    this.nextElevator = nextElevator;
  }

  public Point getStart() {
    return start;
  }

  public Point getDestination() {
    return destination;
  }

  public boolean isWithPatient() {
    return withPatient;
  }

  public Point getNextElevator() {
    return nextElevator;
  }

  public static StatusMessage fromJsonString(String jsonString) {
    return fromMessage(new Message(jsonString));
  }

  public static StatusMessage fromMessage(Message m) {
    return fromJsonObject(m.toJsonObject());
  }

  public static StatusMessage fromJsonObject(JsonObject jsonObject) {
    Point start = jsonObject.containsKey(START) ? Point.fromJsonObject(jsonObject.getJsonObject(START)) : null;
    Point destination = jsonObject.containsKey(DESTINATION) ? Point.fromJsonObject(jsonObject.getJsonObject(DESTINATION)) : null;
    Point nextElevator = jsonObject.containsKey(NEXTELEVATOR) ? Point.fromJsonObject(jsonObject.getJsonObject(NEXTELEVATOR)) : null;
    boolean withPatient = jsonObject.containsKey(WITHPATIENT) ? jsonObject.getBoolean("color_index") : false;
    return new StatusMessage( start, destination, withPatient, nextElevator);
  }

}
