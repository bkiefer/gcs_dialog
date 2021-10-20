package de.dfki.vondabase.RosInterface.msgs;

import java.util.Arrays;
import java.util.List;

public class AsrMessage {

  private String type;
  private String[] transcript;
  private Float[] confidence;

  public AsrMessage(String type, String[] transcript, Float[] confidence){
    this.type = type;
    this.transcript = transcript;
    this.confidence = confidence;

  }

  public String getTranscript(){
    List<Float> confidenceList = Arrays.asList(confidence);
    //int maxIndex = confidenceList.indexOf(Collections.max(confidenceList));
    return transcript[0];
  }

}
