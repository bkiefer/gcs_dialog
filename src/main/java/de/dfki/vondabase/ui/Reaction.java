/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dfki.vondabase.ui;

import de.dfki.vondabase.AbstractAgent;
import de.dfki.vondabase.BaseAgent;
import de.dfki.vondabase.BaseCommunicationHub;
import de.dfki.vondabase.utils.Listener;
import de.dfki.mlt.rudimant.agent.Behaviour;
import de.dfki.mlt.rudimant.agent.DialogueAct;

import javax.swing.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Christophe Biwer, christophe.biwer@dfki.de
 */
public class Reaction implements Runnable, Listener<Behaviour> {

  private BaseCommunicationHub _stub;
  private ChatProtocol _chat;
  private BlockingQueue<String> _q;
  private JLabel _statusbar;

  private boolean compute = true;

  public Reaction(BaseCommunicationHub client, ChatProtocol chat,
                  JLabel statusbar) {
    _chat = chat;
    _stub = client;
    client.registerBehaviourListener(this);
    _q = new LinkedBlockingQueue<String>();
    _statusbar = statusbar;
  }

  public void execute() {
    Thread t = new Thread(this);
    t.setName("Reaction");
    t.setDaemon(true);
    t.start();
  }

  public void shutdown() {
    compute = false;
  }

  public void sendMessageToQueue(String mes) {
    _q.add(mes);
  }

  /*
  This is where the input will be processed by our dialog management.
  */
  public void processInputMessage(String in) {
    DialogueAct da = _stub.analyse(in);
    _statusbar.setText(da == null ? "No dialogue act" : da.toString());
    if (da != null) {
      _stub.sendEvent(da);
    }
  }

  public void run() {
    while (compute) {
      String s;
      try {
        if ((s = _q.take()) != null) {
          // _chat.sendMessage("Got: " + s);
          processInputMessage(s);
        }
      } catch (InterruptedException e) {
        compute = false;
      }
    }
  }

  @Override
  public void listen(Behaviour q) {
    if (q.getMotion().equals("input"))
      _chat.sendMessage(Utilities.getTimeStamp() + " " + q.getText());
    else
      _chat.sendMessage(Utilities.getTimeStamp() + " >>> " + q.getText());
  }

  @Override
  public void free() {
    //nothing to do here
  }


  public String getLastDA() {
    return _stub.getAgent().lastDA().toString();
  }


  public String getState() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(((BaseAgent) _stub.getAgent()).state);
    stringBuilder.append(" - ");
    stringBuilder.append(((BaseAgent) _stub.getAgent()).robot.getString("<dom:hasInternalState>"));
    return stringBuilder.toString();
  }


  public void reset() {
    ((BaseAgent) _stub.getAgent()).resetAgent();
  }


  public void testDias() {
    ((AbstractAgent) _stub.getAgent()).state = "test_dia";
    ((AbstractAgent) _stub.getAgent()).newData();
  }

  public void testDiaState() {
    ((AbstractAgent) _stub.getAgent()).state = "test_dia_state";
    ((AbstractAgent) _stub.getAgent()).newData();
  }

  public void testDiaScore() {
    ((AbstractAgent) _stub.getAgent()).state = "test_dia_score";
    ((AbstractAgent) _stub.getAgent()).newData();
  }

    public void createUser() {
      ((AbstractAgent) _stub.getAgent()).initUser(1);
    }

  public void clearUser() {
    ((AbstractAgent) _stub.getAgent()).resetUser();
  }

  public void eyesOpen() {
    ((AbstractAgent) _stub.getAgent()).eyesOpen(1, true);
  }

  public void eyesClosed() {
    ((AbstractAgent) _stub.getAgent()).eyesOpen(1, false);
  }
}
