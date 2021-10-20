package de.dfki.vondabase;

import de.dfki.vondabase.RosInterface.msgs.BodyTrackerMessage;
import org.junit.Assert;
import org.junit.Test;

public class TestBodyTrackerMessage {

   @Test
   public  void testGetEmotions(){
       BodyTrackerMessage msg = new BodyTrackerMessage();
       msg.setAngry("1.0");
       msg.setSurprise("0.4");
       msg.setHappy("0.130");
       msg.setNeutral("0.09");
       Assert.assertEquals(msg.getEmotions()[0], 1.0d, 0.0);
       Assert.assertEquals(msg.getEmotions()[1], 0.4d, 0.0);
       Assert.assertEquals(msg.getEmotions()[2], 0.130d, 0.0);
       Assert.assertEquals(msg.getEmotions()[3], 0.09d, 0.0);
    }

    @Test
    public void testGestures(){
       Assert.assertEquals("none", BodyTrackerMessage.idToGesture(-1));
       Assert.assertEquals("none", BodyTrackerMessage.idToGesture(6));
       Assert.assertEquals("push", BodyTrackerMessage.idToGesture(5));
    }

    
}
