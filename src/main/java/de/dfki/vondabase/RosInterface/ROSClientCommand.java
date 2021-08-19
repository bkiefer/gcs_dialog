package de.dfki.vondabase.RosInterface;

import de.dfki.mlt.rosBridge.client.BridgeClient;
import de.dfki.mlt.rosBridge.utils.arm.CommandMessage;
import de.dfki.vondabase.utils.Listener;

import java.util.Map;

public class ROSClientCommand implements  Listener<CommandMessage> {

    private String _ip;
    private int _port;

    public ROSClientCommand(Map configs){
        //_ip = (String) ((Map<String, Object>) configs.get("ROSBridge")).get("Ip");
        _ip = System.getenv("DIA_IP");
        _port = (int) ((Map<String, Object>) configs.get("ROSBridge")).get("CommandPort");
    }

    @Override
    public void listen(CommandMessage q) {
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
