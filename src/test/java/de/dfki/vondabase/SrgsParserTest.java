package de.dfki.vondabase;


import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;

import org.json.JSONObject;
import org.junit.Test;
import org.jvoicexml.processor.srgs.ChartGrammarChecker;
import org.jvoicexml.processor.srgs.JVoiceXmlGrammarManager;
import org.jvoicexml.processor.srgs.grammar.Grammar;
import org.jvoicexml.processor.srgs.grammar.GrammarException;
import org.jvoicexml.processor.srgs.grammar.GrammarManager;

import de.dfki.mlt.srgsparser.JSInterpreter;

public class SrgsParserTest {

  @Test
  public void parserTest() throws GrammarException, IOException, URISyntaxException {
    String resdir = "src/main/resources/grammars/srgs/";

    final GrammarManager manager = new JVoiceXmlGrammarManager();
    final Grammar ruleGrammar = manager.loadGrammar(
        new File(resdir, "rolli.gram").toURI());

    final ChartGrammarChecker checker = new ChartGrammarChecker(manager);
    final JSInterpreter walker = new JSInterpreter(checker);

    final int[] i = { 1 };
    Stream<String> in = Files.lines(Paths.get(resdir + "testfile.txt"));

    File outFile = Paths.get(resdir + "testfile.out").toFile();
    if (outFile.exists()) outFile.delete();
    FileWriter out = new FileWriter(outFile);
    in.forEach((s) -> {
      String[] tokens = s.split(" +");
      ChartGrammarChecker.ChartNode validRule;
      try {
        System.err.println(Arrays.toString(tokens));
        validRule = checker.parse(ruleGrammar, tokens);
        assertNotNull("parse line " + i[0], validRule);
        JSONObject object = walker.evaluate(validRule);
        assertNotNull("eval line " + i[0], object);
        out.append(object.toString()).append('\n');
        ++i[0];
      } catch (GrammarException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        assertTrue("Grammar not correct", false);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    });
    out.close();
    in.close();
  }
}
