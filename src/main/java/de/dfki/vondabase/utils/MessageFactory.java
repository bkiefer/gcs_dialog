package de.dfki.vondabase.utils;

import de.dfki.vondabase.RosInterface.msgs.*;
import de.dfki.mlt.rosBridge.utils.geometry.Point;
import de.dfki.mlt.rosBridge.utils.geometry.Pose;
import de.dfki.mlt.rosBridge.utils.geometry.PoseStamped;
import de.dfki.mlt.rosBridge.utils.geometry.Quaternion;
import de.dfki.mlt.rosBridge.utils.primitives.Time;
import de.dfki.mlt.rosBridge.utils.std.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import java.util.List;

public class MessageFactory {

  private static final Quaternion DEFAULTQUATERNION = new Quaternion();
  static int aSeq = 0;
  static int sSeq = 0;

  private final static Logger logger = LoggerFactory.getLogger(MessageFactory.class);

  private static PoseStamped getPoseStamped(double x, double y, int z, List<Double> quat) {
    Header header = new Header(aSeq++,new Time(System.currentTimeMillis()),"map");
    Quaternion quaternion = DEFAULTQUATERNION;
    if (!quat.isEmpty()){
      quaternion = new Quaternion(quat.get(0), quat.get(1), quat.get(2),quat.get(3));
    }
    Pose pose = new Pose( new Point(x,y, z), quaternion);
    return new PoseStamped(header, pose);
  }

  public static TTSMessage translateBehavior2TTSMessage(ExtendedBehaviour behaviour){
    Header header = new Header(0, new Time(System.currentTimeMillis()), "");
    return new TTSMessage(header, behaviour.getText());
  }

  public static TTSMessage ttsMessageFromText(String text){
    Header header = new Header(0, new Time(System.currentTimeMillis()), "");
    return new TTSMessage(header, text);
  }

  public static SoundMessage soundMessageFromText(String sound){
    Header header = new Header(sSeq++, new Time(System.currentTimeMillis()), "");
    return new SoundMessage(header, sound);
  }

}
