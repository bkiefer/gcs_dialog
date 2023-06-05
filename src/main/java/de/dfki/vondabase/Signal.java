package de.dfki.vondabase;
import javax.annotation.Nullable;

public class Signal {
  public long timestamp;

  public String user_id;

  public String state;
  @Nullable
  public String arg0;

  /**
  public Signal() {
	  this.timestamp = System.currentTimeMillis();
  }
  */
}