package de.dfki.vondabase.RosInterface.msgs;

import de.dfki.mlt.rosBridge.utils.Message;
import de.dfki.mlt.rosBridge.utils.geometry.Point;

public class BodyTrackerMessage extends Message {

    private int body_id;
    private int tracking_status;
    private int gesture;
    private boolean face_found;

    private int face_left;
    private int face_top;
    private int face_width;
    private int face_height;
    private int age;
    private int gender;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        BodyTrackerMessage that = (BodyTrackerMessage) o;

        if (body_id != that.body_id) return false;
        if (tracking_status != that.tracking_status) return false;
        if (gesture != that.gesture) return false;
        if (face_found != that.face_found) return false;
        if (face_left != that.face_left) return false;
        if (face_top != that.face_top) return false;
        if (face_width != that.face_width) return false;
        if (face_height != that.face_height) return false;
        if (age != that.age) return false;
        if (gender != that.gender) return false;
        if (!name.equals(that.name)) return false;
        if (!position2d.equals(that.position2d)) return false;
        if (!position3d.equals(that.position3d)) return false;
        return face_center.equals(that.face_center);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + body_id;
        result = 31 * result + tracking_status;
        result = 31 * result + gesture;
        result = 31 * result + (face_found ? 1 : 0);
        result = 31 * result + face_left;
        result = 31 * result + face_top;
        result = 31 * result + face_width;
        result = 31 * result + face_height;
        result = 31 * result + age;
        result = 31 * result + gender;
        result = 31 * result + name.hashCode();
        result = 31 * result + position2d.hashCode();
        result = 31 * result + position3d.hashCode();
        result = 31 * result + face_center.hashCode();
        return result;
    }

    private String name;

    private Point position2d;
    private Point position3d;
    private Point face_center;

    public int getBody_id() {
        return body_id;
    }

    public Point getFace_center() {
        return face_center;
    }

    public void setFace_center(Point face_center) {
        this.face_center = face_center;
    }

    public Point getPosition3d() {
        return position3d;
    }

    public void setPosition3d(Point position3d) {
        this.position3d = position3d;
    }

    public Point getPosition2d() {
        return position2d;
    }

    public void setPosition2d(Point position2d) {
        this.position2d = position2d;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getFace_height() {
        return face_height;
    }

    public void setFace_height(int face_height) {
        this.face_height = face_height;
    }

    public int getFace_width() {
        return face_width;
    }

    public void setFace_width(int face_width) {
        this.face_width = face_width;
    }

    public int getFace_top() {
        return face_top;
    }

    public void setFace_top(int face_top) {
        this.face_top = face_top;
    }

    public int getFace_left() {
        return face_left;
    }

    public void setFace_left(int face_left) {
        this.face_left = face_left;
    }

    public boolean isFace_found() {
        return face_found;
    }

    public void setFace_found(boolean face_found) {
        this.face_found = face_found;
    }

    public int getGesture() {
        return gesture;
    }

    public void setGesture(int gesture) {
        this.gesture = gesture;
    }

    public int getTracking_status() {
        return tracking_status;
    }

    public void setTracking_status(int tracking_status) {
        this.tracking_status = tracking_status;
    }

    public void setBody_id(int body_id) {
        this.body_id = body_id;
    }

}
