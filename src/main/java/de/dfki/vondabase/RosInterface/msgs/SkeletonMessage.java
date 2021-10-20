package de.dfki.vondabase.RosInterface.msgs;

import de.dfki.mlt.rosBridge.utils.geometry.Point;
import de.dfki.mlt.rosBridge.utils.Message;

import javax.json.Json;
import javax.json.JsonObject;

import static java.lang.Math.abs;

public class SkeletonMessage extends Message {

    private int body_id;
    private int tracking_status;
    private int gesture;

    private Point position2D;
    private Point centerOfMass;

    private Point joint_position_head;
    private Point joint_position_neck;
    private Point joint_position_shoulder;
    private Point joint_position_spine_top;
    private Point joint_position_spine_mid;
    private Point joint_position_spine_bottom;

    private Point joint_position_left_shoulder;
    private Point joint_position_left_elbow;
    private Point joint_position_left_hand;

    private Point joint_position_right_shoulder;
    private Point joint_position_right_elbow;
    private Point joint_position_right_hand;

    public SkeletonMessage(int body_id, int tracking_status, int gesture, Point position2D, Point centerOfMass,
            Point joint_position_head, Point joint_position_neck, Point joint_position_shoulder,
            Point joint_position_spine_top, Point joint_position_spine_bottom, Point joint_position_spine_mid,
            Point joint_position_left_shoulder, Point joint_position_left_elbow, Point joint_position_left_hand,
            Point joint_position_right_shoulder, Point joint_position_right_elbow, Point joint_position_right_hand) {
        super(Json.createObjectBuilder().add("body_id", body_id).add("tracking_status", tracking_status)
                .add("gesture", gesture).add("position2D", position2D.toJsonObject())
                .add("centerOfMass", centerOfMass.toJsonObject()).build());
        this.body_id = body_id;
        this.tracking_status = tracking_status;
        this.gesture = gesture;
        this.position2D = position2D;
        this.centerOfMass = centerOfMass;
        this.joint_position_head = joint_position_head;
        this.joint_position_neck = joint_position_neck;
        this.joint_position_shoulder = joint_position_shoulder;
        this.joint_position_spine_top = joint_position_spine_top;
        this.joint_position_spine_bottom = joint_position_spine_bottom;
        this.joint_position_spine_mid = joint_position_spine_mid;
        this.joint_position_left_shoulder = joint_position_left_shoulder;
        this.joint_position_left_elbow = joint_position_left_elbow;
        this.joint_position_left_hand = joint_position_left_hand;
        this.joint_position_right_shoulder = joint_position_right_shoulder;
        this.joint_position_right_elbow = joint_position_right_elbow;
        this.joint_position_right_hand = joint_position_right_hand;
    }

    public Point getJoint_position_right_hand() {
        return joint_position_right_hand;
    }

    public Point getJoint_position_right_elbow() {
        return joint_position_right_elbow;
    }

    public Point getJoint_position_right_shoulder() {
        return joint_position_right_shoulder;
    }

    public Point getJoint_position_left_hand() {
        return joint_position_left_hand;
    }

    public Point getJoint_position_left_elbow() {
        return joint_position_left_elbow;
    }

    public Point getJoint_position_left_shoulder() {
        return joint_position_left_shoulder;
    }

    public Point getJoint_position_spine_bottom() {
        return joint_position_spine_bottom;
    }

    public Point getJoint_position_spine_mid() {
        return joint_position_spine_mid;
    }

    public Point getJoint_position_spine_top() {
        return joint_position_spine_top;
    }

    public Point getJoint_position_shoulder() {
        return joint_position_shoulder;
    }

    public Point getJoint_position_neck() {
        return joint_position_neck;
    }

    public Point getJoint_position_head() {
        return joint_position_head;
    }

    public Point getCenterOfMass() {
        return centerOfMass;
    }

    public Point getPosition2D() {
        return position2D;
    }

    public int getGesture() {
        return gesture;
    }

    public int getTracking_status() {
        return tracking_status;
    }

    public int getBody_id() {
        return body_id;
    }

    public static double delta(Point oldPoint, Point newPoint){
        var difX = abs(oldPoint.getX() - newPoint.getX());
        var difY = abs(oldPoint.getY() - newPoint.getY());
        var difZ = abs(oldPoint.getZ() - newPoint.getZ());
      return difX + difY + difZ;
    }

}
