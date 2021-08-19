package de.dfki.vondabase.RosInterface;

import de.dfki.vondabase.RosInterface.msgs.TaskStatusMessage;
import de.dfki.vondabase.utils.Listener;
import de.dfki.mlt.rosBridge.client.BridgeClient;

import java.util.Map;

public class ROSClientTaskStatus implements Listener<TaskStatusMessage> {

  private String _ip;
  private int _port;

  public ROSClientTaskStatus(Map configs){
    _ip = System.getenv("DIA_IP");;
    _port = (int)((Map<String, Object>) configs.get("ROSBridge")).get("TaskStatusPort");
  }

  @Override
  public void listen(TaskStatusMessage q) {
    BridgeClient client = new BridgeClient(q.toString(), _ip, _port);
    try {
      client.worker();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void free() {
    //nothing to do here
  }

}
