package de.dfki.vondabase.RosInterface.services;

import com.google.gson.Gson;

public class GCS {

  private final int eyes;
  private final int awareness;
  private final int motorics;
  private final int sum;


  public GCS(int eyes, int awareness, int motorics) {
    this.eyes = eyes;
    this.awareness = awareness;
    this.motorics = motorics;
    this.sum = eyes+awareness+motorics;
  }

  public String toString(){
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("GCS\n");
    stringBuilder.append("    eyes:"+ eyes+ "\n");
    stringBuilder.append("    awareness:"+ awareness+ "\n");
    stringBuilder.append("    motorics:"+ motorics+ "\n");
    stringBuilder.append("    sum:"+ sum+ "\n");
    return stringBuilder.toString();
  }

  public String toJson(){
    Gson gson = new Gson();
    String json = gson.toJson(this);
    return json;
  }
}
