package de.dfki.vondabase.RosInterface;

import com.google.gson.GsonBuilder;

import de.dfki.vondabase.AbstractAgent;
import de.dfki.vondabase.RosInterface.msgs.AsrMessage;
import de.dfki.vondabase.RosInterface.msgs.BodyTrackerMessage;
import de.dfki.vondabase.RosInterface.msgs.SkeletonMessage;
import de.dfki.mlt.rudimant.agent.DialogueAct;
import de.dfki.vondabase.BaseCommunicationHub;
import de.dfki.vondabase.RosInterface.services.GCS;
import de.dfki.vondabase.RosInterface.services.GCSService;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Simple handler implementation: receives Position and Velocity information
 * from ROS and adds them to the Ontology using the RDFProxy.
 *
 */
class RosHandler implements Runnable {
    private final Socket _client;
    private final ServerSocket _serverSocket;
    protected final GsonBuilder builder = new GsonBuilder();
    private final BaseCommunicationHub _stub;
    private ExecutorService pool = Executors.newFixedThreadPool(4);

    RosHandler(ServerSocket serverSocket, Socket client, BaseCommunicationHub baseCommunicationHub) {
        _client = client;
        _serverSocket = serverSocket;
        _stub = baseCommunicationHub;
    }

    public void run() {
        StringBuffer sb = new StringBuffer();
        PrintWriter out = null;
        try {
            out = new PrintWriter(_client.getOutputStream(), true);
            String message = getMessage();
            JSONObject jsonObject = new JSONObject(message);
            String type = jsonObject.getString("type");
            switch (type) {
                // ToDo something like this might be useful in the signs of life project
                /**
                 * case ("move_base_status"): _stub.sendEvent(new PoiMessage()); break;
                 **/
                case ("tts_done"):
                    _stub.freeSpeechListener();
                    break;
                case ("SpeechRecognitionCandidates"):
                    AsrMessage asrMessage = builder.create().fromJson(message, AsrMessage.class);
                    _stub.asrInput(asrMessage.getTranscript());
                    // _stub.sendEvent(asrMessage.getTranscript());
                    break;
                case ("Utterance"):
                    _stub.asrInput(jsonObject.getString("text"));
                    break;
                case ("Skeleton"):
                    System.err.println("message: " + message);
                    if(_stub.getAgent().getUserID() == jsonObject.getInt("body_id")) {
                        SkeletonMessage skeleton = builder.create().fromJson(message, SkeletonMessage.class);
                        _stub.updateSkeleton(skeleton);
                    }
                    break;
                case ("BodyTrack"):
                    System.err.println("message: " + message);
                    //System.err.println("detected_list: " + jsonObject.getJSONArray("detected_list"));
                    List<BodyTrackerMessage> tracks = new ArrayList<>();
                    if(_stub.getAgent().user != null) {
                            tracks.add(builder.create().fromJson(jsonObject.toString(), BodyTrackerMessage.class));
                        _stub.updateTracks(tracks);
                    }
                    break;
                case ("GCSService"):
                    _client.setKeepAlive(true);
                    // create corresponding service call and wait for result (how? add logic to rule (a method is called that changes a volatile field in this service instance? Or this service is monitoring the information state)
                    // this starts a dialogue, which will hopefully resolve the given situation
                    // result = ...
                    GCSService service = new GCSService( (AbstractAgent) _stub.getAgent(), jsonObject.getInt("body_id"));
                    Future<GCS> result = pool.submit(service);
                    // send result back to the caller
                    sb.append(result.get().toRos().toString());
                    break;
                default:
                    throw new IllegalStateException("Unknown Message type " + type);
            }
        } catch (IOException e) {
            System.out.println("IOException, Handler-run");
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            out.println(sb);
            if (!_client.isClosed()) {
                try {
                    _client.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private String getMessage() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(_client.getInputStream()));
        char[] buffer = new char[512];
        int dataLength = bufferedReader.read(buffer, 0, 512); // blocking until a message was received
        return new String(buffer, 0, dataLength);
    }

}
