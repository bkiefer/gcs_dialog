package de.dfki.vondabase.RosInterface;

import de.dfki.vondabase.RosInterface.msgs.DialogueMessage;
import de.dfki.vondabase.utils.Listener;
import de.dfki.mlt.rosBridge.client.BridgeClient;

import java.util.LinkedList;
import java.util.Map;

public class ROSClientDialogue implements Listener<DialogueMessage> {

  private String _ip;
  private int _port;
  private LinkedList<DialogueMessage> messagequeue = new LinkedList<>();
  private boolean locked = false;
  private final boolean _blocking;

  public ROSClientDialogue(Map configs){
    //_ip = (String) ((Map<String, Object>) configs.get("ROSBridge")).get("Ip");
    _ip = System.getenv("DIA_IP");
    _port = (int)((Map<String, Object>) configs.get("ROSBridge")).get("DialogPort");
    _blocking = (boolean) configs.get("tts_blocking");
  }

  @Override
  public void listen(DialogueMessage q) {
    if(!this.locked || !_blocking){
      BridgeClient client = new MyBridgeClient(q.toString(), _ip, _port);
      try {
        locked = true;
        client.worker();
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      messagequeue.add(q);
    }

  }
  public void free() {
    locked = false;
    if (!messagequeue.isEmpty())
      listen(messagequeue.pop());
  }

}
