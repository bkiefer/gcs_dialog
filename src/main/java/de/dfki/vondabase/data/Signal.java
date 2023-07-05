package de.dfki.vondabase.data;
import javax.annotation.Nullable;

public class Signal {
  public long timestamp;

  public String user_id;

  public String state;
  @Nullable
  public String arg0;


  public Signal() { }

  public Signal(String uid, String state) {
    this.user_id = uid;
    this.state = state;
    this.timestamp = System.currentTimeMillis();
  }

  public Signal(String uid, String state, String arg) {
    this(uid, state);
    this.arg0 = arg;
  }
}