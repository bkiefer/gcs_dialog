package de.dfki.vondabase.data;

import javax.annotation.Nullable;

public class Command {
  public long timestamp;

  public String user_id;

  public String cmd;
  @Nullable
  public String arg0;
}