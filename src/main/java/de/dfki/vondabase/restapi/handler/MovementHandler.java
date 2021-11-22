package de.dfki.vondabase.restapi.handler;

import com.sun.net.httpserver.HttpExchange;
import de.dfki.vondabase.AbstractAgent;
import de.dfki.vondabase.BaseCommunicationHub;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


/**
 * summary: Ask for guiding the AGV back home (basically recovery behavior?) - D3
 *       operationId: askForHelp
 *       description: tbd
 */
public class MovementHandler extends AbstractHandler{

  private final AbstractAgent _agent;
  private final static Logger logger = LoggerFactory.getLogger(MovementHandler.class);

  public MovementHandler(BaseCommunicationHub hub){
    super();
    builder.excludeFieldsWithoutExposeAnnotation();
    _agent = (AbstractAgent) hub.getAgent();
  }

  @Override
  protected void handlePostRequest(HttpExchange exchange) throws IOException {
    String json = bodyToString(exchange.getRequestBody());
    JSONObject jsonObject = new JSONObject(json);
    boolean isOpen =  jsonObject.getBoolean("hasMoved");
    if (_agent.user != null){
      _agent.hasMoved(_agent.getUserID(),isOpen);
      sendResponse(200, exchange, "changed moved status");

    } else {
        sendResponse(500, exchange, "no user ");
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
