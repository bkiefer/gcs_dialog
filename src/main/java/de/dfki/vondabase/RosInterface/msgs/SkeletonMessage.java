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

    private Point p_head;

    private Point p_l_s;
    private Point p_l_e;
    private Point p_l_h;

    private Point p_r_s;
    private Point p_r_e;
    private Point p_r_h;

    public SkeletonMessage(int body_id, int tracking_status, int gesture, Point position2D, Point centerOfMass,
            Point joint_position_head, Point joint_position_left_shoulder, Point joint_position_left_elbow, Point joint_position_left_hand,
            Point joint_position_right_shoulder, Point joint_position_right_elbow, Point joint_position_right_hand) {
        super(Json.createObjectBuilder().add("body_id", body_id).add("tracking_status", tracking_status)
                .add("gesture", gesture).add("position2D", position2D.toJsonObject())
                .add("centerOfMass", centerOfMass.toJsonObject()).build());
        this.body_id = body_id;
        this.tracking_status = tracking_status;
        this.gesture = gesture;
        this.position2D = position2D;
        this.centerOfMass = centerOfMass;
        this.p_head = joint_position_head;
        this.p_l_s = joint_position_left_shoulder;
        this.p_l_e = joint_position_left_elbow;
        this.p_l_h = joint_position_left_hand;
        this.p_r_s = joint_position_right_shoulder;
        this.p_r_e = joint_position_right_elbow;
        this.p_r_h = joint_position_right_hand;
    }

    public Point getJoint_position_right_hand() {
        return p_r_h;
    }

    public Point getJoint_position_right_elbow() {
        return p_r_e;
    }

    public Point getJoint_position_right_shoulder() {
        return p_r_s;
    }

    public Point getJoint_position_left_hand() {
        return p_l_h;
    }

    public Point getJoint_position_left_elbow() {
        return p_l_e;
    }

    public Point getJoint_position_left_shoulder() {
        return p_l_s;
    }


    public Point getJoint_position_head() {
        return p_head;
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
