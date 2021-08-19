package de.dfki.vondabase.RosInterface;

import de.dfki.vondabase.utils.Listener;
import de.dfki.mlt.rosBridge.client.BridgeClient;

import java.util.Map;

public class ROSClientEwalkerUseElevator implements Listener<Integer> {

  private String _ip;
  private int _port;

  public ROSClientEwalkerUseElevator(Map configs){
    _ip = System.getenv("DIA_IP");;
    _port = (int)((Map<String, Object>) configs.get("ROSBridge")).get("EwalkerUseElevator");
  }

  @Override
  public void listen(Integer q) {
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
