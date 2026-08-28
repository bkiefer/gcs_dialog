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
/*
 * {'confidence': 140.32074,
 *  'result': [{'end': 72.15, 'start': 71.55, 'word': 'ja'},
 *             {'end': 72.48, 'start': 72.18, 'word': 'wäre'},
 *             {'end': 72.84, 'start': 72.48, 'word': 'fünfzehn'},
 *             {'end': 73.17, 'start': 72.84, 'word': 'dreißig'},
 *             {'end': 73.71, 'start': 73.17, 'word': 'okay'}],
 *   'text': 'ja wäre fünfzehn dreißig okay',
 *   'start': 1787918604730,
 *   'end': 1787918435576,
 *   'source': 'microphone'}
 */
public class AsrResult {

  public static class Word {
    public double conf;
    public double start;
    public double end;
    public String word;
  }

  public List<Word> result;
  public String text;
  public long id;
  public long start;
  public long end;
  public double confidence;
  public String source;

  public String getText() {
    return text;
  }
}
