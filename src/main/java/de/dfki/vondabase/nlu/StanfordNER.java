package de.dfki.vondabase.nlu;

import edu.stanford.nlp.pipeline.*;

import java.util.Properties;

import java.io.File;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.dfki.mlt.rudimant.agent.DialogueAct;
import de.dfki.mlt.rudimant.agent.nlg.Interpreter;

public class StanfordNER extends Interpreter {

    private Properties props;

    private StanfordCoreNLP pipeline;
  
    @Override
    public boolean init(final File file, final String s, final Map map) {
        props = new Properties();
        // set up pipeline properties
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
        // set up pipeline
        pipeline = new StanfordCoreNLP(props);
        return true;
    }

  @Override
  public DialogueAct analyse(String text) {
      // list containing words that Stanford incorrectly parses as PERSON
      // List<String> neg_list = new ArrayList<String>(Arrays.asList("Montag", "Dienstag", "Mittwich", "Donnerstag", "Freitag", "Samstag", "Sonntag", "Ja", "Nein"));
      DialogueAct dia = null;
      // make document out of input text
      CoreDocument doc = new CoreDocument(text);
      // annotate the document
      pipeline.annotate(doc);
      // check if person found
      String name = "";
      //System.out.println(text);
      for (CoreEntityMention em : doc.entityMentions()){
        System.out.println(em.entityType());
          //System.out.println(em.entityType().getClass());
          // check if em is Person but not a word contained in neg list
        if (em.entityType().equals("PERSON")){
         //if (em.entityType().equals("PERSON") && !(neg_list.contains(em.text()))){
            name = em.text();
            System.out.println(name);
            // TODO maximal eine Person?
            // TODO Dialogact pro type?
            dia = new DialogueAct ("Answer","NAME", "Name", name) ;
            break;
          }
      }
      return dia;
  }
}


