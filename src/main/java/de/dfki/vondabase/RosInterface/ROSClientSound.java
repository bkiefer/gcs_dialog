package de.dfki.vondabase.RosInterface;

import de.dfki.mlt.rosBridge.client.BridgeClient;
import de.dfki.vondabase.RosInterface.msgs.SoundMessage;
import de.dfki.vondabase.RosInterface.msgs.StatusMessage;
import de.dfki.vondabase.utils.Listener;

import java.util.Map;

public class ROSClientSound implements Listener<SoundMessage> {

  private String _ip;
  private int _port;

  public ROSClientSound(Map configs){
    _ip = System.getenv("DIA_IP");;
    _port = (int)((Map<String, Object>) configs.get("ROSBridge")).get("SoundPort");
  }

  @Override
  public void listen(SoundMessage q) {
    System.err.println("Ping pong!");
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
