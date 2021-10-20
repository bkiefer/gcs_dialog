package de.dfki.vondabase.nlu;


import de.dfki.mlt.rudimant.agent.DialogueAct;
import de.dfki.mlt.rudimant.agent.nlg.Interpreter;

import java.io.File;
import java.util.Map;

/**
 * This interpreter class combines the gramma based srgs parser and the Cerence NLU parser
 * It first tries to find a interpretation using the srgs parser, which is really fast, and only uses the Cerence NLU
 * if the SRGS Parser had no succecss
 */
public class CombinedNLU extends Interpreter {

  private SrgsParser _srgsParser;
  private CerenceNLU _cerenceNLU;

  @Override
  public boolean init(File file, String s, Map map) {
    _srgsParser = new SrgsParser();
    _cerenceNLU = new CerenceNLU();
    boolean srgsIsInit = _srgsParser.init(file, s, map);
    boolean cerenceIsInit = _cerenceNLU.init(file, s, map);
    return srgsIsInit && cerenceIsInit;
  }

  private DialogueAct getNotParseDia() {
    final DialogueAct dia = new DialogueAct("Disagreement", "NotParse");
    dia.setValue("Sender", "User");
    return dia;
  }

  @Override
  public DialogueAct analyse(String s) {
    // first try srgsParser
    try {
      DialogueAct result = _srgsParser.analyse(s);
      if (result == null)
        result = _cerenceNLU.analyse(s);
      return result;
    } catch (Exception e){
      e.printStackTrace();
      return getNotParseDia();
      }
    }
}
