package de.dfki.vondabase;


import de.dfki.lt.hfc.WrongFormatException;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static de.dfki.vondabase.App.readConfig;

public class AbstractIntuitivAgentTest {

  private static File CONFIGDIR = new File(".");;
  private static String LANGUAGE = "de";
  private static String ROLLICONF = CONFIGDIR + "/src/test/resources/RolliConfig.yml";
  private static String KOFFICONF = CONFIGDIR + "/src/test/resources/KoffiConfig.yml";




  @Test
  public void testInit() throws IOException, WrongFormatException {
    // load Rolli
    Map config = readConfig(ROLLICONF);
    AbstractAgent agent = new BaseAgent() {
      @Override
      public int process() {
        return 0;
      }
    };
    agent.init(CONFIGDIR, LANGUAGE,config );
    // TODO test something
  }

}