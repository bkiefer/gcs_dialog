package de.dfki.vondabase.restapi.handler;

import com.sun.net.httpserver.HttpExchange;
import de.dfki.vondabase.AbstractAgent;
import de.dfki.vondabase.BaseCommunicationHub;
import de.dfki.vondabase.utils.MessageFactory;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


/**
 * summary: Ask for guiding the AGV back home (basically recovery behavior?) - D3
 *       operationId: askForHelp
 *       description: tbd
 */
public class PainHandler extends AbstractHandler{

  private final AbstractAgent _agent;
  private final BaseCommunicationHub _hub;
  private final static Logger logger = LoggerFactory.getLogger(PainHandler.class);

  public PainHandler(BaseCommunicationHub hub){
    super();
    builder.excludeFieldsWithoutExposeAnnotation();
    _agent = (AbstractAgent) hub.getAgent();
    _hub = hub;
  }

  @Override
  protected void handlePostRequest(HttpExchange exchange) throws IOException {
    String json = bodyToString(exchange.getRequestBody());
    JSONObject jsonObject = new JSONObject(json);
    if (_hub.hasSoundOutputListener()){
      _hub.sendSound(MessageFactory.soundMessageFromText("pain.wav"));
      sendResponse(200, exchange, "triggered pain");
    } else {
      sendResponse(500, exchange, "No sound output possible");
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
