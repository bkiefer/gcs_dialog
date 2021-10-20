package de.dfki.vondabase.RosInterface.msgs;

import de.dfki.mlt.rosBridge.utils.Message;
import de.dfki.mlt.rosBridge.utils.std.Header;

import javax.json.Json;
import javax.json.JsonObject;

public class GCSMessage extends Message {

    public static final String FIELD_HEADER = "header";
    public static final String EYES = "eyes";
    public static final String AWARENESS = "awareness";
    public static final String MOTORICS = "motorics";
    public static final String SUM = "sum";
    private final Header header;
    private final int eyes;
    private final int awareness;
    private final int motorics;
    private final int sum;

    public GCSMessage(Header header, int eyes, int awareness, int motorics, int sum) {
        super(Json.createObjectBuilder().add("header", header.toJsonObject()).add(EYES, eyes).add(AWARENESS, awareness)
                .add(MOTORICS, motorics).add(SUM, sum).build());
        this.header = header;
        this.eyes = eyes;
        this.awareness = awareness;
        this.motorics = motorics;
        this.sum = sum;
    }

    public int getSum() {
        return sum;
    }

    public int getMotorics() {
        return motorics;
    }

    public int getAwareness() {
        return awareness;
    }

    public Header getHeader() {
        return this.header;
    }

    public int getEyes() {
        return this.eyes;
    }

    public static GCSMessage fromJsonString(String jsonString) {
        return fromMessage(new Message(jsonString));
    }

    public static GCSMessage fromMessage(Message m) {
        return fromJsonObject(m.toJsonObject());
    }

    public static GCSMessage fromJsonObject(JsonObject jsonObject) {
        Header header = jsonObject.containsKey("header") ? Header.fromJsonObject(jsonObject.getJsonObject("header"))
                : new Header();
        int eyes = jsonObject.containsKey(EYES) ? jsonObject.getInt(EYES) : 0;
        int awareness = jsonObject.containsKey(AWARENESS) ? jsonObject.getInt(AWARENESS) : 0;
        int motorics = jsonObject.containsKey(MOTORICS) ? jsonObject.getInt(MOTORICS) : 0;
        int sum = jsonObject.containsKey(SUM) ? jsonObject.getInt(SUM) : 0;
        return new GCSMessage(header, eyes, awareness, motorics, sum);
    }
}
