package de.dfki.vondabase.RosInterface;

import com.google.gson.GsonBuilder;

import de.dfki.vondabase.RosInterface.msgs.PoiMessage;
import de.dfki.vondabase.RosInterface.msgs.AsrMessage;
import de.dfki.mlt.rudimant.agent.DialogueAct;
import de.dfki.vondabase.BaseCommunicationHub;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Simple handler implementation:
 * receives Position and Velocity information from ROS
 * and adds them to the Ontology using the RDFProxy.
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
            switch (type){
                case ("move_base_status"):
                    _stub.sendEvent(new PoiMessage());
                    break;
                case ("tts_done"):
                    _stub.freeSpeechListener();
                    break;
                case ("SpeechRecognitionCandidates"):
                    AsrMessage asrMessage = builder.create().fromJson(message, AsrMessage.class);
                    _stub.asrInput(asrMessage.getTranscript());
                    //_stub.sendEvent(asrMessage.getTranscript());
                    break;
                case ("Utterance"):
                    _stub.asrInput(jsonObject.getString("text"));
                    break;
                /* case ("Detection"):
                    DetectionMessage detection = builder.create().fromJson(message, DetectionMessage.class);
                    _stub.peopleDetected(detection);
                    break; */
                case("SpeechEvent"):
                    String speech_event = jsonObject.getString("speech_event");
                    DialogueAct speechEvent = SpeechEventFactory.translateEvent2Dia(speech_event);
                    _stub.sendEvent(speechEvent);
                    break;
              default:
                  throw new IllegalStateException("Unknown Message type " + type);
            }
        } catch (IOException  e) {System.out.println("IOException, Handler-run");}
        finally {
            out.println(sb);
            if ( !_client.isClosed() ) {
                try {
                    _client.close();
                } catch ( IOException e ) { }
            }
        }
    }

    private String getMessage() throws IOException {
        BufferedReader bufferedReader =
                new BufferedReader(
                        new InputStreamReader(
                                _client.getInputStream()));
        char[] buffer = new char[512];
        int dataLength = bufferedReader.read(buffer, 0, 512); // blocking until a message was received
        return new String(buffer, 0, dataLength);
    }

}
