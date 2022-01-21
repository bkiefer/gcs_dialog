package de.dfki.vondabase.restapi.handler;

import com.sun.net.httpserver.HttpExchange;
import de.dfki.vondabase.AbstractAgent;
import de.dfki.vondabase.BaseCommunicationHub;
import de.dfki.vondabase.RosInterface.services.GCS;
import de.dfki.vondabase.RosInterface.services.GCSService;
import de.dfki.vondabase.utils.MessageFactory;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Deprecated
/**
 * @deprecated for UZL testing only
 *
 * Triggers the first phase of the GCS computation, i.e. the dialogue where the user is asked to open his/her eyes,
 * and returns the result for this phase.
 * It does create a new instance of user with eyes initially closes. This part is equivalent to calling {@link GCSHandler}
 *
 */
public class GCSPhaseOneHandler extends AbstractHandler {

    private final AbstractAgent _agent;
    private final BaseCommunicationHub _hub;
    private final static Logger logger = LoggerFactory.getLogger(PainHandler.class);

    public GCSPhaseOneHandler(BaseCommunicationHub hub) {
        super();
        builder.excludeFieldsWithoutExposeAnnotation();
        _agent = (AbstractAgent) hub.getAgent();
        _hub = hub;
    }

    @Override
    protected void handlePostRequest(HttpExchange exchange) throws IOException {
        String json = bodyToString(exchange.getRequestBody());
        JSONObject jsonObject = new JSONObject(json);
        try {
            GCSService service = new GCSService(_agent, jsonObject.getInt("body_id"), 1);
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
