package de.dfki.vondabase.RosInterface.msgs;

import de.dfki.mlt.rosBridge.utils.Message;
import de.dfki.mlt.rosBridge.utils.geometry.Point;

import javax.json.Json;

import static java.lang.Math.abs;

public class PatientStatusMessage extends Message {

    private int patient_id;
    private int age;
    private int gender;
    private int gesture;

    private boolean are_eyes_open;
    private boolean is_mouth_open;



    private boolean has_moved;
    private boolean has_moved_left_arm;
    private boolean has_moved_left_hand;
    private boolean has_moved_left_leg;
    private boolean has_moved_right_arm;
    private boolean has_moved_right_hand;
    private boolean has_moved_right_leg;
    private boolean has_moved_head;

    public PatientStatusMessage(int patient_id, int age, int gender, int gesture,boolean are_eyes_open,
        boolean is_mouth_open,
        boolean has_moved,
        boolean has_moved_left_arm,
        boolean has_moved_left_hand,
        boolean has_moved_left_leg,
        boolean has_moved_right_arm,
        boolean has_moved_right_hand,
        boolean has_moved_right_leg ,
        boolean has_moved_head ) {
        super(Json.createObjectBuilder().add("patient_id", patient_id).add("age", age).add("gender", gender)
                .add("gesture", gesture).add("has_moved", has_moved).build());
        this.patient_id = patient_id;
        this.age = age;
        this.gender = gender;
        this.gesture = gesture;
        this.are_eyes_open = are_eyes_open;
        this.is_mouth_open = is_mouth_open;
        this.has_moved = has_moved;
        this.has_moved_left_arm = has_moved_left_arm;
        this.has_moved_left_hand = has_moved_left_hand;
        this.has_moved_left_leg = has_moved_left_leg;
        this.has_moved_right_arm = has_moved_right_arm;
        this.has_moved_right_hand = has_moved_right_hand;
        this.has_moved_right_leg = has_moved_right_leg;
    }

    public String getHRGender() {
        // TODO validate these assignments
        System.err.println("Gender Value: " + gender);
        if (gender == 0)
            return "unknown";
        else if (gender == 1)
            return "male";
        else
            return "female";
    }

    public int getPatient_id() {
        return patient_id;
    }

    public int getAge() {
        return age;
    }

    public int getGender() {
        return gender;
    }

    public int getGesture() {
        return gesture;
    }

    public boolean isAre_eyes_open() {
        return are_eyes_open;
    }

    public boolean isIs_mouth_open() {
        return is_mouth_open;
    }

    public boolean isHas_moved() {
        return has_moved;
    }

    public boolean isHas_moved_left_arm() {
        return has_moved_left_arm;
    }

    public boolean isHas_moved_left_hand() {
        return has_moved_left_hand;
    }

    public boolean isHas_moved_left_leg() {
        return has_moved_left_leg;
    }

    public boolean isHas_moved_right_arm() {
        return has_moved_right_arm;
    }

    public boolean isHas_moved_right_hand() {
        return has_moved_right_hand;
    }

    public boolean isHas_moved_right_leg() {
        return has_moved_right_leg;
    }

    public Object isHas_moved_head() {
        return has_moved_head;
    }
}
