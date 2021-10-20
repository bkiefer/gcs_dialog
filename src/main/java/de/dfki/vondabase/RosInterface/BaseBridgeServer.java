package de.dfki.vondabase.RosInterface;

import de.dfki.mlt.rosBridge.server.BridgeServer;
import de.dfki.mlt.rosBridge.server.NetworkService;
import de.dfki.vondabase.BaseCommunicationHub;

import java.io.IOException;
import java.util.Map;

public class BaseBridgeServer extends BridgeServer {

    private final BaseCommunicationHub _stub;
    private  IntuitivNS _networkService;

    public BaseBridgeServer(BaseCommunicationHub baseCommunicationHub, Map config, int poolSize) throws IOException {
        super((Integer)config.get("serversocket"), poolSize);
        _stub = baseCommunicationHub;
        _stub.registerTTSListener(new ROSClientTTS(config));
        _stub.registerStatusListener(new ROSClientStatus(config));
    }


    @Override
    public NetworkService getNetworkService() {
        if (_networkService == null)
            _networkService = new IntuitivNS(super._pool, super._serverSocket, _stub);
        return _networkService;
    }
}
