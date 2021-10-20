package de.dfki.vondabase.RosInterface.msgs;

import de.dfki.mlt.rosBridge.utils.Message;
import de.dfki.mlt.rosBridge.utils.geometry.Point;

/**
 * std_msgs/Header header uint32 seq time stamp string frame_id
 * body_tracker_msgs/BodyTracker[] detected_list + int32 body_id + int32
 * tracking_status + int32 gesture + bool face_found + int32 face_left + int32
 * face_top + int32 face_width + int32 face_height + int32 age + int32 gender +
 * string name + string angry + string surprise + string happy + string neutral
 * string left_eye_x string left_eye_y string right_eye_x string right_eye_y +
 * geometry_msgs/Point32 position2d float32 x float32 y float32 z +
 * geometry_msgs/Point32 position3d float32 x float32 y float32 z +
 * geometry_msgs/Point32 face_center float32 x float32 y float32 z
 */
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

    private String name;
    private String angry;
    private String surprise;
    private String happy;
    private String neutral;

    private String left_eye_x;
    private String left_eye_y;
    private String right_eye_x;
    private String right_eye_y;

    private Point position2d;
    private Point position3d;
    private Point face_center;

    /**
     *    GESTURE_WAVING = 0, GESTURE_SWIPE_LEFT = 1, GESTURE_SWIPE_RIGHT = 2,GESTURE_SWIPE_UP = 3, GESTURE_SWIPE_DOWN = 4, GESTURE_PUSH = 5,
     */
    public static String idToGesture(int gestureId) {
        String gesture;
        switch (gestureId) {
            case 0:
                gesture = "waving";
                break;
            case 1:
                gesture = "swipe_left";
                break;
            case 2:
                gesture = "swipe_right";
                break;
            case 3:
                gesture = "swipe_up";
                break;
            case 4:
                gesture = "swipe_down";
                break;
            case 5:
                gesture = "push";
                break;
            default:
                gesture = "none";
        }
        return gesture;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;

        BodyTrackerMessage that = (BodyTrackerMessage) o;

        if (body_id != that.body_id)
            return false;
        if (tracking_status != that.tracking_status)
            return false;
        if (gesture != that.gesture)
            return false;
        if (face_found != that.face_found)
            return false;
        if (face_left != that.face_left)
            return false;
        if (face_top != that.face_top)
            return false;
        if (face_width != that.face_width)
            return false;
        if (face_height != that.face_height)
            return false;
        if (age != that.age)
            return false;
        if (gender != that.gender)
            return false;
        if (name != null ? !name.equals(that.name) : that.name != null)
            return false;
        if (angry != null ? !angry.equals(that.angry) : that.angry != null)
            return false;
        if (surprise != null ? !surprise.equals(that.surprise) : that.surprise != null)
            return false;
        if (happy != null ? !happy.equals(that.happy) : that.happy != null)
            return false;
        if (neutral != null ? !neutral.equals(that.neutral) : that.neutral != null)
            return false;
        if (left_eye_x != null ? !left_eye_x.equals(that.left_eye_x) : that.left_eye_x != null)
            return false;
        if (left_eye_y != null ? !left_eye_y.equals(that.left_eye_y) : that.left_eye_y != null)
            return false;
        if (right_eye_x != null ? !right_eye_x.equals(that.right_eye_x) : that.right_eye_x != null)
            return false;
        if (right_eye_y != null ? !right_eye_y.equals(that.right_eye_y) : that.right_eye_y != null)
            return false;
        if (position2d != null ? !position2d.equals(that.position2d) : that.position2d != null)
            return false;
        if (position3d != null ? !position3d.equals(that.position3d) : that.position3d != null)
            return false;
        return face_center != null ? face_center.equals(that.face_center) : that.face_center == null;
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
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (angry != null ? angry.hashCode() : 0);
        result = 31 * result + (surprise != null ? surprise.hashCode() : 0);
        result = 31 * result + (happy != null ? happy.hashCode() : 0);
        result = 31 * result + (neutral != null ? neutral.hashCode() : 0);
        result = 31 * result + (left_eye_x != null ? left_eye_x.hashCode() : 0);
        result = 31 * result + (left_eye_y != null ? left_eye_y.hashCode() : 0);
        result = 31 * result + (right_eye_x != null ? right_eye_x.hashCode() : 0);
        result = 31 * result + (right_eye_y != null ? right_eye_y.hashCode() : 0);
        result = 31 * result + (position2d != null ? position2d.hashCode() : 0);
        result = 31 * result + (position3d != null ? position3d.hashCode() : 0);
        result = 31 * result + (face_center != null ? face_center.hashCode() : 0);
        return result;
    }

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

    /**
     GESTURE_WAVING = 0, GESTURE_SWIPE_LEFT = 1, GESTURE_SWIPE_RIGHT = 2,GESTURE_SWIPE_UP = 3, GESTURE_SWIPE_DOWN = 4, GESTURE_PUSH = 5,
     */
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

    public void setHappy(String v) {
         this.happy = v;
    }

    public void setSurprise(String v) {
        this.surprise = v;
    }


    public void setAngry(String v) {
        this.angry = v;
    }

    public void setNeutral(String v) {
        this.neutral = v;
    }


    /**
     * @return the status of the user encoded as an array of type double (angry,
     *         surprise, happy, neutral)
     * 
     */
    public double[] getEmotions() {
        var result = new double[4];
        result[0] = Double.parseDouble(angry);
        result[1] = Double.parseDouble(surprise);
        result[2] = Double.parseDouble(happy);
        result[3] = Double.parseDouble(neutral);
        return result;
    }

    public String getHRGender() {
        // TODO validate these assignments
        System.err.println("Gender Value: " + gender);
        if (gender == -1)
            return "unknown";
        else if (gender == 0)
            return "female";
        else
            return "male";
    }
}
