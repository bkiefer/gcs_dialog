package de.dfki.vondabase;

import static de.dfki.vondabase.Constants.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import de.dfki.vondabase.RosInterface.IntuitivBridgeServer;
import de.dfki.vondabase.restapi.IntuitivRestController;
import de.dfki.vondabase.restapi.caller.RESTCaller;
import de.dfki.vondabase.ui.GUI;
import org.apache.thrift.TException;
import org.yaml.snakeyaml.Yaml;

import de.dfki.vondabase.ui.Reaction;
import de.dfki.lt.hfc.WrongFormatException;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

/**
 * The main class to start the Rolli environment
 * This class will also start a Java based TCP server, allowing connection to the ros de.dfki.intuitiv.system
 */
public class App {
  public static Map<String, Object> configs;
  public static File confDir;

  final static Object [][] defaults = {
      { CFG_VISUALISE, false , "v" },
      { CFG_ONTOLOGY_FILE, "src/main/resources/ontology/Intuitiv/data/Ontology/chatcat.ini", "o" },
  };

  public static Map<String, Object> defaultConfig() {
    configs = new LinkedHashMap<String, Object>();
    for (Object[] pair : defaults) {
      configs.put((String)pair[0], pair[1]);
    }
    return configs;
  }

  static Object getDefault(String key) {
    Object result = null;
    for (Object[] pair : defaults) {
      if (key.equals(pair[0])) {
        result = pair[1];
        break;
      }
    }
    return result;
  }

  static void setDefault(String key, Map<String, Object> configs) {
    if (! configs.containsKey(key)) {
      configs.put(key, getDefault(key));
    }
  }

  private static void interactive(final BaseCommunicationHub client, boolean userInterface) throws IOException {
    if (userInterface) {
      javax.swing.SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          final GUI qw = new GUI("Intuitiv Test");
          qw.initializeComponents();
          // connect to client
          qw._react = new Reaction(client, qw._chat, qw._statusbar);
          qw._react.execute();
        }
      });
    } else {
      System.err.println("Add commandline reader here");
    }
  }


  @SuppressWarnings("unchecked")
  public static Map<String, Object> readConfig(String confname)
          throws IOException {
    Yaml yaml = new Yaml();
    File confFile = new File(confname).getCanonicalFile();
    confDir = confFile.getParentFile();
    return (Map<String, Object>) yaml.load(new FileReader(confFile));
  }

  private static void connectToRos(BaseCommunicationHub stub, Map<String, Object> configs) throws IOException {
    IntuitivBridgeServer bridgeServer = new IntuitivBridgeServer( stub, configs, -1);
    Thread rosBridge = new Thread(bridgeServer.getNetworkService());
    rosBridge.setDaemon(true);
    rosBridge.start();
  }

  private static void startRestEndpoints(BaseCommunicationHub hub, Map<String, Object> configs) throws  IOException {
    hub.registerRESTListener(new RESTCaller(configs));
    IntuitivRestController restService = new IntuitivRestController(hub, configs, -1);
    // start server and publish endpoints
    restService.initEndpoints();
    restService.start();
  }

  private static void usage(String message) {
    System.out.println(message);
    System.out.println("[-c confFile]");
  }

  public static void main(String[] args)
          throws TException, IOException, WrongFormatException, InterruptedException {
    //BasicConfigurator.configure();

    OptionParser parser = new OptionParser("c:");
    // parser.accepts("help");
    OptionSet options = null;

    //List files = null;
    confDir = new File(".");
    String confName = "config.yml";

    try {
      options = parser.parse(args);
      //files = options.nonOptionArguments();
      if (options.has("c")) {
        confName = (String)options.valueOf("c");
      }
      configs = (confName != null) ? readConfig(confName) : defaultConfig();
    } catch (OptionException ex) {
      usage("Error parsing options: " + ex.getMessage());
    }
    BaseCommunicationHub stub = new BaseCommunicationHub();
    stub.init(confDir, configs);
    stub.startListening();
    if((boolean)configs.get("GUI_enabled"))
      interactive(stub, true);
    if((boolean) configs.get("REST_enabled")) {
      Map<String, Object> restConfig = (Map<String, Object>) configs.get("RestAPI");
      startRestEndpoints(stub, restConfig);
    }
    connectToRos(stub, configs);
    /*  connectToRos and start RestEndpoints is deactivated if system not deployed with ROS Core environment
     *     Map<String, Object> restConfig = (Map<String, Object>) configs.get("RestAPI");
     *     startRestEndpoints(stub, restConfig);
     */
  }

}
