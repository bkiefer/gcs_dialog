package de.dfki.vondabase.restapi.caller;

public class RESTMessage {

  public final String endpoint; 
  public final String arg;
  public final String body;

  @Override
  public String toString() {
    return "RESTMessage{" +
            "endpoint='" + endpoint + '\'' +
            ", arg='" + arg + '\'' +
            ", body='" + body + '\'' +
            '}';
  }

  public RESTMessage( String endpoint, String arg, String body) {
    this.endpoint = endpoint;
    if(arg != null)
      this.arg = arg.replace("<dom:", "").replace(">", "");
    else
      this.arg = arg;
    if (body != null){
      this.body = "{\"lead_memorized_person\": " + body + "}";
    } else {
      this.body = body;
    }
  }


}
