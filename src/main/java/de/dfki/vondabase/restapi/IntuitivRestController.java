package de.dfki.vondabase.restapi;

import com.sun.net.httpserver.HttpServer;
import de.dfki.vondabase.BaseCommunicationHub;
import de.dfki.vondabase.restapi.handler.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;

public class IntuitivRestController {


  /**
   * The <code>IntuiticCommunicationHub</code> provides (indirect) access to the agents and other system components
   */
  private final BaseCommunicationHub hub;

  /**
   * The HttpServer used to server the endpoints
   */
  protected final HttpServer _server;

  /**
   * path to the static resources used to display the API when callen the /dialogue/API endpoint
   */
  private final String _dataLoc;


  private final static Logger logger = LoggerFactory.getLogger(IntuitivRestController.class);

  public IntuitivRestController(BaseCommunicationHub hub, Map<String, Object> configs, int i) throws IOException {
    this.hub = hub;
    int port = (int) configs.get("Port");
    _dataLoc = (String) configs.get("DataLoc");
    _server = HttpServer.create(new InetSocketAddress(port), 50);
  }

  /**
   * Initialize the endpoints.
   * This should be done before starting the server
   */
  public void initEndpoints(){
    logger.debug("Creating new Endpoints");
    _server.createContext("/dialog/api", new StaticFileHandler(_dataLoc));
    GCSHandler gcsHandler = new GCSHandler(hub);
    _server.createContext("/dialog/gcs", gcsHandler);
    EyesHandler eyesOpen = new EyesHandler(hub);
    _server.createContext("/dialog/eyes",eyesOpen);
    MovementHandler movement = new MovementHandler(hub);
    _server.createContext("/dialog/move",movement);
    DemoHandler demoHandler = new DemoHandler(hub);
    _server.createContext("/dialog/demo",demoHandler);

  }

  /**
   * start serving the endpoints using the <code>HttpServer</code>
   */
  public void start() {
    _server.start();
  }

  /**
   * stop serving the endpoints by killing the <code>HttpServer</code>
   */
  public void shutdown() {
    _server.stop(0);
  }
}
