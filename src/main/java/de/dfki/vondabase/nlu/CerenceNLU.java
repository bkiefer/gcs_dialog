package de.dfki.vondabase.nlu;

import de.dfki.mlt.nuance_cerence.NuanceWebSocketsJavaApp;
import de.dfki.mlt.nuance_cerence.RequestType;
import de.dfki.mlt.rudimant.agent.DialogueAct;
import de.dfki.mlt.rudimant.agent.nlg.Interpreter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class CerenceNLU extends Interpreter {

  private NuanceWebSocketsJavaApp app;

  private final static Logger logger = LoggerFactory.getLogger(CerenceNLU.class);

  private final ExecutorService executor
          = Executors.newFixedThreadPool(2);

  @Override
  public boolean init(final File file, final String s, final Map map) {
    final Map<String, String> settings = (Map<String, String>) map;
    app = new NuanceWebSocketsJavaApp(settings.get("context_tag"), settings.get("codec"), settings.get("appKey"),
        settings.get("hostname"), settings.get("languageCode"), settings.get("nmaid"));
    app.requestType = RequestType.NLU_TEXT;
    app.setAutoReconnect(false);
    return true;
  }

  @Override
  public DialogueAct analyse(final String s) {
    DialogueAct dia = null;
    final Future<JSONObject> future = executor.submit(new AnalyseCall(s));
    try {
      final JSONObject intent = future.get(30, TimeUnit.SECONDS);
      if(intent != null)
        dia = parseResult(intent);
      else
        dia = getNotParseDia();
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      e.printStackTrace();
    }
    return dia;
  }

  /**
   * remove irrelevant parts from the output
   * 
   * @return a cleaned JSON Object containing only those information relevant to
   *         instantiate the ROS Commands
   */
  public DialogueAct parseResult(final JSONObject in) {
    System.err.println(in);
    final JSONArray json = in.getJSONObject("nlu_interpretation_results").getJSONObject("payload")
        .getJSONArray("interpretations");
    final List<String> values = new ArrayList<>();
    final JSONObject action = json.getJSONObject(0).getJSONObject("action");
    final String intent = action.getJSONObject("intent").getString("value");
    final double confidence = action.getJSONObject("intent").getDouble("confidence");
    if (confidence < 0.5 || intent.equals("NO_MATCH"))
      return getNotParseDia();
    if (intent.equals("Stop") || intent.equals("Continue"))
      return new DialogueAct("Instruct", intent);
    if (intent.startsWith("ControlAGV_")){
      var split = intent.split("_");
      return new DialogueAct("Instruct", split[1]);
    }
    if (intent.equals("Greet")){
      // if the last dialogue act was a initialGreeting uttered by the robot -> ReturnGreeting(Greet);
      // else
      return  new DialogueAct("InitialGreeting", intent);
    }
    if (intent.startsWith("Confirm") || intent.startsWith("Disconfirm"))
      return parseBinaryAnswer(intent);
    if(json.getJSONObject(0).has("concepts")) {
      final JSONObject concepts = json.getJSONObject(0).getJSONObject("concepts");
      values.add("Answer");
      values.add(intent);
      for (final String key : concepts.keySet()) {
        values.add(key);
        switch (key) {
          /**case "Actor":
          case "TargetPos": {
            values.add(((JSONObject) concepts.getJSONArray(key).get(0)).getString("literal"));
            break;
          }
          case "Move": {
            values.add(((JSONObject) concepts.getJSONArray(key).get(0)).getString("value"));
            break;
          }**/
          case "nuance_LOCATION": {
            values.add(((JSONObject) concepts.getJSONArray(key).get(0)).getString("literal"));
            break;
          }
          case "Age": {
            values.add(((JSONObject) concepts.getJSONArray(key).get(0)).getString("literal"));
            break;
          }
          case "nuance_CALENDARX": {
            values.add(((JSONObject) concepts.getJSONArray(key).get(0)).getString("literal"));
            break;
          }
          case "Weekday": {
            values.add(((JSONObject) concepts.getJSONArray(key).get(0)).getString("literal"));
            break;
          }
          case "nuance_ADDRESS": {
            values.add(((JSONObject) concepts.getJSONArray(key).get(0)).getString("literal"));
            break;
          }
          default:
            logger.info("Unsupported key: " + key);
        }
      }
    }
    //if(values.isEmpty() || values.size() % 2 == 0)
    if(values.isEmpty() || values.size() % 2 != 0)
      return getNotParseDia();
    final DialogueAct dia = new DialogueAct(values.toArray(String[]::new));
    dia.setValue("Sender", "User");
    return dia;
  }

  private DialogueAct parseBinaryAnswer(String intent) {
    if (intent.equals("Confirm")|| intent.equals("Disconfirm")){
      return new DialogueAct(intent, intent);
    } else {
      var intent_argument_tuple = intent.split("_");
      if (intent.length()>1) {
        return new DialogueAct(intent_argument_tuple);
      } else {
        return getNotParseDia();
      }
    }

  }

  private DialogueAct getNotParseDia() {
    final DialogueAct dia = new DialogueAct("Disagreement", "NotParse");
    dia.setValue("Sender", "User");
    //return dia;
    return null;
  }

  private class AnalyseCall implements Callable<JSONObject> {

    private final String input;

    AnalyseCall(final String s) {
      this.input = s;
    }

    @Override
    public JSONObject call() throws Exception {
      app.startRequest();
      app.sendNluTextRequest(input);
      int count = 0;
      while (app.getFinalRes() == null && count <= 20) {
        count++;
        Thread.sleep(100);
      }
      final JSONObject finalRes = app.getFinalRes();
      app.clearResults();
      return finalRes;
    }
  }
  }
