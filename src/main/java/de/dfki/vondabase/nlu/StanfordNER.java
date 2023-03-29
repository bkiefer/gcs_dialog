package de.dfki.vondabase.nlu;

import java.io.File;
import java.util.Map;
import java.util.Properties;

import de.dfki.mlt.rudimant.agent.nlp.DialogueAct;
import de.dfki.mlt.rudimant.agent.nlp.Interpreter;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

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
      for (CoreEntityMention em : doc.entityMentions()){
        System.out.println(em.entityType());
        // check if em is Person
        if (em.entityType().equals("PERSON")){
         //if (em.entityType().equals("PERSON") && !(neg_list.contains(em.text()))){
            name = em.text();
            System.out.println(name);
            // TODO maximal eine Person?
            dia = new DialogueAct ("Answer","NAME", "Name", name) ;
            break;
          }
      }
      return dia;
  }
}


