package de.dfki.vondabase.RosInterface.msgs;

import de.dfki.mlt.rosBridge.utils.Message;
import de.dfki.mlt.rosBridge.utils.geometry.Point;

import javax.json.Json;

import static java.lang.Math.abs;

public class PatientStatusMessage extends Message {

    private int patient_id;
    private int age ;
    private int gender ;
    private int gesture ;

    private boolean are_eyes_open;
    private boolean is_mouth_open;



    private boolean has_moved;
    /**
     * -1 no measurement or not visible
     * 0 did not move
     * 1 did move
     */
    private int has_moved_left_arm;
    private int has_moved_left_hand;
    private int has_moved_left_leg;
    private int has_moved_right_arm;
    private int has_moved_right_hand;
    private int has_moved_right_leg;
    private int has_moved_head;

    public PatientStatusMessage(int patient_id, int age, int gender, int gesture,boolean are_eyes_open,
        boolean is_mouth_open,
        boolean has_moved,
        int has_moved_left_arm,
        int has_moved_left_hand,
        int has_moved_left_leg,
        int has_moved_right_arm,
        int has_moved_right_hand,
        int has_moved_right_leg ,
        int has_moved_head ) {
        super(Json.createObjectBuilder().add("patient_id", patient_id).add("age", age).add("gender", gender)
                .add("gesture", gesture).add("has_moved", has_moved).build());
        this.patient_id = patient_id;
        this.age = age;
        this.gender = gender;
        this.gesture = gesture;
        this.are_eyes_open = are_eyes_open;
        this.is_mouth_open = is_mouth_open;
        this.has_moved = has_moved;
        this.has_moved_head = has_moved_head;
        this.has_moved_left_arm = has_moved_left_arm;
        this.has_moved_left_hand = has_moved_left_hand;
        this.has_moved_left_leg = has_moved_left_leg;
        this.has_moved_right_arm = has_moved_right_arm;
        this.has_moved_right_hand = has_moved_right_hand;
        this.has_moved_right_leg = has_moved_right_leg;
    }

    public PatientStatusMessage() {
        super(Json.createObjectBuilder().add("patient_id", 0).add("age", -1).add("gender", 0)
                .add("gesture", -1).add("has_moved", true).build());
        this.age = -1;
        this.gender = 0;
        this.gesture = -1;
        this.has_moved_head = -1;
        this.has_moved_left_arm = -1;
        this.has_moved_left_hand = -1;
        this.has_moved_left_leg = -1;
        this.has_moved_right_arm = -1;
        this.has_moved_right_hand = -1;
        this.has_moved_right_leg = -1;
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

    public int isHas_moved_left_arm() {
        return has_moved_left_arm;
    }

    public int isHas_moved_left_hand() {
        return has_moved_left_hand;
    }

    public int isHas_moved_left_leg() {
        return has_moved_left_leg;
    }

    public int isHas_moved_right_arm() {
        return has_moved_right_arm;
    }

    public int isHas_moved_right_hand() {
        return has_moved_right_hand;
    }

    public int isHas_moved_right_leg() {
        return has_moved_right_leg;
    }

    public int isHas_moved_head() {
        return has_moved_head;
    }
}
