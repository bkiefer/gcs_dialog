package de.dfki.vondabase.data;

import java.util.List;

/** Vosk server JSON result as Java object */
// {'result': [
//     {'conf': 1.0, 'end': 10.32, 'start': 9.96, 'word': 'hallo'},
//     {'conf': 1.0, 'end': 11.07, 'start': 10.38, 'word': 'computer'}
//   ],
//   'text': 'hallo computer',
//   'id': 1688468962493
// }
public class AsrResult {

  public class Word {
    double conf;
    double start;
    double end;
    String word;
  }

  public List<Word> words;
  public String text;
  public long id;

  public String getText() {
    return text;
  }
}
