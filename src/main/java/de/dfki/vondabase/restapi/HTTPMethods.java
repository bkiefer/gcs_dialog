package de.dfki.vondabase.restapi;

public enum HTTPMethods {
  GET("GET"),
  POST("POST"),
  PUT("PUT"),
  PATCH("PATCH"),
  DELETE("DELETE");

  private String method;

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  HTTPMethods(String method) {
    this.method = method  ;
  }



}