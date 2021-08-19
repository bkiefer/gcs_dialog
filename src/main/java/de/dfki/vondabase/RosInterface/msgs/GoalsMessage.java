package de.dfki.vondabase.RosInterface.msgs;

import de.dfki.mlt.rosBridge.utils.Message;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;

public class GoalsMessage extends Message {

  private final JsonArray goals;

  public GoalsMessage(JsonArray goals) {
    super(Json.createObjectBuilder().add("goals", goals).build());
    this.goals = goals;
  }

  public JsonArray getGoals() {
    return this.goals;
  }

  public static GoalsMessage fromJsonString(String jsonString) {
    return fromMessage(new Message(jsonString));
  }

  public static GoalsMessage fromMessage(Message m) {
    return fromJsonObject(m.toJsonObject());
  }

  public static GoalsMessage fromJsonObject(JsonObject jsonObject) {
    JsonArray goals = jsonObject.containsKey("goals") ? jsonObject.getJsonArray("buttons") : Json.createArrayBuilder().build();
    return new GoalsMessage(goals);
  }

}
