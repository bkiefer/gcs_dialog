package de.dfki.vondabase.data;

import jakarta.annotation.Nullable;

public class Command {
  public long timestamp;

  public String user_id;

  public String cmd;
  @Nullable
  public String arg0;

  @Override
  public String toString() {
    return cmd + "(" + (arg0 == null ? "" : arg0) + ") " + user_id;
  }
}