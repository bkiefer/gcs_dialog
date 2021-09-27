package de.dfki.vondabase.RosInterface;

import de.dfki.vondabase.RosInterface.msgs.TTSMessage;
import de.dfki.vondabase.utils.Listener;
import de.dfki.mlt.rosBridge.client.BridgeClient;
import de.dfki.mlt.rosBridge.client.ClientHandler;

import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.FutureTask;

public class ROSClientTTS implements Listener<TTSMessage> {

  private String _ip;
  private int _port;
  private LinkedList<TTSMessage> messagequeue = new LinkedList<>();
  private boolean locked = false;
  private final boolean _blocking;

  public ROSClientTTS(Map configs){
    //_ip = (String) ((Map<String, Object>) configs.get("ROSBridge")).get("Ip");
    _ip = System.getenv("DIA_IP");
    _port = (int)((Map<String, Object>) configs.get("ROSBridge")).get("TTSPort");
    _blocking = (boolean) configs.get("tts_blocking");
  }

  @Override
  public void listen(TTSMessage q) {
    System.err.println("Calling TTS Service with message " + q + " to " + _ip + ":" + _port);
    System.err.println("locked =" + locked);
    System.err.println("blocking =" + _blocking);
    if(!this.locked || !_blocking ){
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


   class MyBridgeClient extends BridgeClient {
    private final String _value;
    private final String _ip;
    private final int _port;

    public MyBridgeClient(String value, String ip, int port) {
      super(value, ip, port);
      this._ip = ip;
      this._port = port;
      this._value = value;
    }

    @Override
    public void worker() throws Exception {
      ClientHandler ch = new ClientHandler(this._value, this._ip, this._port);
      FutureTask<String> ft = new FutureTask(ch);
      Thread tft = new Thread(ft);
      tft.start();
    }
  }

