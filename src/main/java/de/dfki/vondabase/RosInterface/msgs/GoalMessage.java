package de.dfki.vondabase.RosInterface.msgs;

import de.dfki.mlt.rosBridge.utils.Message;
import de.dfki.mlt.rosBridge.utils.geometry.PoseStamped;

import javax.json.Json;
import javax.json.JsonObject;

public class GoalMessage extends Message {

  private final PoseStamped goal;
  private final boolean is_elevator;

  public GoalMessage(PoseStamped pose, boolean is_elevator) {
    super(Json.createObjectBuilder().add("goal", pose.toJsonObject()).add("is_elevator", is_elevator).build());
    this.goal = pose;
    this.is_elevator = is_elevator;
  }

  public PoseStamped getGoal() {
    return this.goal;
  }

  public boolean getIsElevator() {
    return this.is_elevator;
  }

  public static GoalMessage fromJsonString(String jsonString) {
    return fromMessage(new Message(jsonString));
  }

  public static GoalMessage fromMessage(Message m) {
    return fromJsonObject(m.toJsonObject());
  }

  public static GoalMessage fromJsonObject(JsonObject jsonObject) {
    PoseStamped goal = jsonObject.containsKey("goal") ? PoseStamped.fromJsonObject(jsonObject.getJsonObject("goal")) : new PoseStamped();
    boolean is_elevator = jsonObject.containsKey("is_elevator") ? jsonObject.getBoolean("is_elevator") : false;
    return new GoalMessage(goal, is_elevator);
  }


}
