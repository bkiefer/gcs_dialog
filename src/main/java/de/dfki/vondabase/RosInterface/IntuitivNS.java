package de.dfki.vondabase.RosInterface;

import de.dfki.mlt.rosBridge.server.NetworkService;
import de.dfki.vondabase.BaseCommunicationHub;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

//Thread bzw. Runnable zur Entgegennahme der Client-Anforderungen
class IntuitivNS extends NetworkService {
    //oder extends Thread
    private final ServerSocket _serverSocket;
    private final ExecutorService _pool;
    private final BaseCommunicationHub _stub;

    public IntuitivNS(ExecutorService pool,
                      ServerSocket serverSocket, BaseCommunicationHub stub) {
        super(pool, serverSocket);
        _serverSocket = serverSocket;
        _pool = pool;
        _stub = stub;
    }
    public void run() { // run the service
        try {
            while ( true ) {
                Socket cs = _serverSocket.accept();  //warten auf Client-Anforderung
                //starte den Handler-Thread zur Realisierung der Client-Anforderung
                _pool.execute(new RosHandler(_serverSocket,cs, _stub));
            }
        } catch (IOException ex) {
            System.out.println("--- Interrupt NetworkService-run");
        }
        finally {
            _pool.shutdown();  //keine Annahme von neuen Anforderungen
            try {
                //warte maximal 4 Sekunden auf Beendigung aller Anforderungen
                _pool.awaitTermination(4L, TimeUnit.SECONDS);
                if ( !_serverSocket.isClosed() ) {
                    _serverSocket.close();
                }
            } catch ( IOException | InterruptedException ignored) { }
        }
    }

}

