package de.dfki.vondabase.RosInterface;

import com.google.gson.GsonBuilder;
import de.dfki.lt.hfc.WrongFormatException;
import de.dfki.vondabase.AbstractAgent;
import de.dfki.vondabase.App;
import de.dfki.vondabase.BaseCommunicationHub;
import de.dfki.vondabase.RosInterface.msgs.AsrMessage;
import de.dfki.vondabase.RosInterface.msgs.PatientStatusMessage;
import de.dfki.vondabase.RosInterface.services.GCS;
import de.dfki.vondabase.RosInterface.services.GCSService;
import org.apache.thrift.TException;
import org.json.JSONObject;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Future;

import static org.junit.Assert.*;

public class RosHandlerTest {


    public static File confDir = new File(".");
    public static Map<String, Object> configs;

    static {
        try {
            configs = App.readConfig("config.yml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testHandlePatientStatusValid() throws TException, IOException, WrongFormatException {
        BaseCommunicationHub stub = new BaseCommunicationHub();
        stub.init(confDir, configs);
        stub.getAgent().initUser(1);
        GsonBuilder builder = new GsonBuilder();
        String message = "{'type': 'PatientStatus', 'patient_id': 1, 'age': 30, 'gender': 1, 'gesture': 0, 'are_eyes_open': true, 'is_mouth_open': false, 'has_moved': true }";
        JSONObject jsonObject = new JSONObject(message);
        String type = jsonObject.getString("type");
        switch (type) {
            case ("tts_done"):
                stub.freeSpeechListener();
                break;
            case ("SpeechRecognitionCandidates"):
                AsrMessage asrMessage = builder.create().fromJson(message, AsrMessage.class);
                stub.asrInput(asrMessage.getTranscript());
                // _stub.sendEvent(asrMessage.getTranscript());
                break;
            case ("Utterance"):
                stub.asrInput(jsonObject.getString("text"));
                break;
            /**
             case ("Skeleton"):
             System.err.println("message: " + message);
             if(_stub.getAgent().getUserID() == jsonObject.getInt("body_id")) {
             SkeletonMessage skeleton = builder.create().fromJson(message, SkeletonMessage.class);
             _stub.updateSkeleton(skeleton);
             }
             break;
             case ("BodyTrack"):
             System.err.println("message: " + message);
             //System.err.println("detected_list: " + jsonObject.getJSONArray("detected_list"));
             List<BodyTrackerMessage> tracks = new ArrayList<>();
             if(_stub.getAgent().user != null) {
             tracks.add(builder.create().fromJson(jsonObject.toString(), BodyTrackerMessage.class));
             _stub.updateTracks(tracks);
             }
             break;
             **/
            case ("PatientStatus"):
                System.err.println("message: " + message);
                if(stub.getAgent().getUserID() == jsonObject.getInt("patient_id")) {
                    PatientStatusMessage patientStatus = builder.create().fromJson(message, PatientStatusMessage.class);
                    assertNotNull(patientStatus);
                    assertEquals( 1, patientStatus.getPatient_id());
                    stub.updateStatus(patientStatus);
                    int age = stub.getAgent().user.getInteger("<dom:hasAge>");
                    assertEquals(30,age);
                    boolean areEyesOpen = stub.getAgent().user.getBoolean("<dom:areEyesOpen>");
                    assertTrue(areEyesOpen);
                } else {
                    fail();
                }
                break;
            default:
                throw new IllegalStateException("Unknown Message type " + type);
        }
    }

    @Test
    public void testHandlePatientStatusInvalid() throws TException, IOException, WrongFormatException {
        BaseCommunicationHub stub = new BaseCommunicationHub();
        stub.init(confDir, configs);
        GsonBuilder builder = new GsonBuilder();
        String message = "{'type': 'PatientStatus', 'patient_id': 1, 'age': 30, 'gender': 1, 'gesture': 0, 'are_eyes_open': true, 'is_mouth_open': false, 'has_moved': true }";
        JSONObject jsonObject = new JSONObject(message);
        String type = jsonObject.getString("type");
        switch (type) {
            case ("tts_done"):
                stub.freeSpeechListener();
                break;
            case ("SpeechRecognitionCandidates"):
                AsrMessage asrMessage = builder.create().fromJson(message, AsrMessage.class);
                stub.asrInput(asrMessage.getTranscript());
                // _stub.sendEvent(asrMessage.getTranscript());
                break;
            case ("Utterance"):
                stub.asrInput(jsonObject.getString("text"));
                break;
            /**
             case ("Skeleton"):
             System.err.println("message: " + message);
             if(_stub.getAgent().getUserID() == jsonObject.getInt("body_id")) {
             SkeletonMessage skeleton = builder.create().fromJson(message, SkeletonMessage.class);
             _stub.updateSkeleton(skeleton);
             }
             break;
             case ("BodyTrack"):
             System.err.println("message: " + message);
             //System.err.println("detected_list: " + jsonObject.getJSONArray("detected_list"));
             List<BodyTrackerMessage> tracks = new ArrayList<>();
             if(_stub.getAgent().user != null) {
             tracks.add(builder.create().fromJson(jsonObject.toString(), BodyTrackerMessage.class));
             _stub.updateTracks(tracks);
             }
             break;
             **/
            case ("PatientStatus"):
                System.err.println("message: " + message);
                if(stub.getAgent().getUserID() == jsonObject.getInt("patient_id")) {
                    fail();
                }
                break;


            default:
                throw new IllegalStateException("Unknown Message type " + type);
        }
    }
}