package de.dfki.vondabase;

import com.google.gson.GsonBuilder;
import de.dfki.vondabase.RosInterface.msgs.BodyTrackerMessage;
import de.dfki.vondabase.RosInterface.msgs.PatientStatusMessage;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestPatientStatusMessage {

   @Test
   public  void testFromJson(){
       GsonBuilder builder = new GsonBuilder();
       String json = "{ 'patient_id': 1, 'age': 30, 'gender': 1, 'gesture': 0, 'are_eyes_open': true, 'is_mouth_open': false, 'has_moved': true }";
       PatientStatusMessage patientStatus = builder.create().fromJson(json, PatientStatusMessage.class);
       assertNotNull(patientStatus);
       assertEquals(1, patientStatus.getPatient_id());
       assertEquals(30, patientStatus.getAge());
       assertEquals(0, patientStatus.getGesture());
       json = "{ 'patient_id': 1, 'age': 30, 'gender': 1, 'are_eyes_open': true, 'is_mouth_open': false, 'has_moved': true }";
       patientStatus = builder.create().fromJson(json, PatientStatusMessage.class);
       assertEquals(-1, patientStatus.getGesture());
       json = "{ 'patient_id': 1, 'age': 30, 'gender': 1, 'are_eyes_open': true, 'is_mouth_open': false, 'has_moved': true, 'has_moved_left_arm': 1, 'has_moved_head':0 }";
       patientStatus = builder.create().fromJson(json, PatientStatusMessage.class);
       assertEquals(0, patientStatus.isHas_moved_head());
       assertEquals(1, patientStatus.isHas_moved_left_arm());
       assertEquals(-1, patientStatus.isHas_moved_right_arm());
   }


    
}
