package de.dfki.vondabase;

import junit.framework.TestCase;

public class DialogueAgentTest extends TestCase {

  public void testGetDayTime() {
    BaseAgent agent = new BaseAgent() {
      @Override
      public int process() {
        return 0;
      }
    };
    assertNotNull(agent.getDayTime());
  }
}