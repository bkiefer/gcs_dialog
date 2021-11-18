package de.dfki.vondabase.restapi.handler;

import com.sun.net.httpserver.HttpExchange;
import de.dfki.vondabase.AbstractAgent;
import de.dfki.vondabase.BaseCommunicationHub;
import de.dfki.vondabase.RosInterface.services.GCS;
import de.dfki.vondabase.RosInterface.services.GCSService;
import de.dfki.vondabase.RosInterface.services.GCSServiceException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


/**
 * summary: Ask for guiding the AGV back home (basically recovery behavior?) - D3
 *       operationId: askForHelp
 *       description: tbd
 */
public class GCSHandler extends AbstractHandler{

  private final AbstractAgent _agent;
  private final static Logger logger = LoggerFactory.getLogger(GCSHandler.class);

  public GCSHandler(BaseCommunicationHub hub){
    super();
    builder.excludeFieldsWithoutExposeAnnotation();
    _agent = (AbstractAgent) hub.getAgent();
  }

  @Override
  protected void handlePostRequest(HttpExchange exchange) throws IOException {
    String json = bodyToString(exchange.getRequestBody());
    JSONObject jsonObject = new JSONObject(json);
    try {
      GCSService service = new GCSService(_agent, jsonObject.getInt("body_id"));
      Future<GCS> result = pool.submit(service);
      if (result.get() != null) {
        System.err.println(result.get().toJson());
        sendResponse(200, exchange, result.get().toJson());
      } else {
        sendResponse(500, exchange, "internal error");
      }
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void handleGetRequest(HttpExchange exchange) {
    //throw new IllegalStateException("Please implement me");
    try {
      sendResponse(400, exchange, "https://http.cat/400");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
