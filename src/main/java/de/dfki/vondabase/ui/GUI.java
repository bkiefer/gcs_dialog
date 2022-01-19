package de.dfki.vondabase.ui;


import de.dfki.vondabase.utils.Listener;
import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Christophe Biwer, christophe.biwer@dfki.de
 * @author Christian Willms, christian.willms@dfki.de
 * @
 */


public class GUI extends JFrame {
  private static final long serialVersionUID = 1L;
  final GUI mainFrame;
  private final List<Listener<String>> _listeners = new ArrayList<>();
  public JTextField queryInput;
  public ChatProtocol _chat;
  public JTable chatprotocol;
  public JLabel _statusbar;
  public Reaction _react;

  public GUI(final String name) {
    super(name);

    mainFrame = this;

    // set handler for closing operations
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }

  private static JButton getIconButton(final String name) {
    final URL url = GUI.class.getClassLoader().getResource("icons/" + name + ".png");
    final Icon icon = new ImageIcon(url);
    final JButton button = new JButton(icon);
    button.setPreferredSize(new Dimension(32, 32));
    button.setBorderPainted(false);
    button.setBorder(null);
    button.setMargin(new Insets(0, 0, 0, 0));
    button.setContentAreaFilled(false);
    return button;
  }

  private static void setDefaultFont(final int size) {
    final FontUIResource font = new FontUIResource("'DejaVu Sans Mono", Font.PLAIN, size);
    for (final Map.Entry<Object, Object> e : UIManager.getLookAndFeelDefaults().entrySet()) {
      try {
        final String key = (String) e.getKey();
        if (key.endsWith(".font")) {
          UIManager.put(key, font);
        }
      } catch (final ClassCastException ex) {
        /* so what. */
      }
    }
  }

  private JTextField createQueryInput(final ActionListener execute) {
    final JTextField queryInput = new JTextField();
    queryInput.setText("");
    queryInput.addActionListener(execute);
    return queryInput;
  }

  protected void errorDialog(final String string) {
    JOptionPane.showMessageDialog(this, string, "Error", JOptionPane.ERROR_MESSAGE);
  }

  public void initializeComponents() {
    this.setLocationByPlatform(true);
    // set preferred size
    this.setPreferredSize(new Dimension(800, 600));
    setDefaultFont(18);
    /*
     * try { ASRServiceFactory.init(null); } catch (IOException ex) {}
     */

    // create content panel and add it to the frame
    final JPanel contentPane = new JPanel(new BorderLayout());
    this.setContentPane(contentPane);

    final ActionListener execute = new ActionListener() {
      @Override
      public void actionPerformed(final ActionEvent e) {
        synchronized (queryInput) {
          final String timestamp = Utilities.getTimeStamp();
          final String input = queryInput.getText();
          // save the current output to a file specified in brackets
          // TODO adapt according to own project
          if (input.trim().equals("lastDA()")) {
            _statusbar.setText("LastDA:" + _react.getLastDA());//Frage, was macht lastDa()?
            _chat.sendMessage(timestamp + " " + input);
            queryInput.setText("");
          } else if (input.trim().equals("clear()")) {  // clear history aka chat
            _chat.resetMessages();
            queryInput.setText("");
            _statusbar.setText("History has been cleared.");
          } else if (input.trim().equals("reset()")) {
            _react.reset();
            queryInput.setText("");
            _chat.sendMessage(timestamp + " " + input);
          } else if (input.trim().equals("testDias()")) {
            _react.testDias();
            queryInput.setText("");
            _chat.sendMessage(timestamp + " " + input);
          } else if (input.trim().equals("testDiaState()")) {
            _react.testDiaState();
            queryInput.setText("");
            _chat.sendMessage(timestamp + " " + input);
          } else if (input.trim().equals("createUser()")) {
            _react.triggerGCS(0);
            queryInput.setText("");
            _chat.sendMessage(timestamp + " " + input);
          } else if (input.trim().equals("clearUser()")) {
            _react.clearUser();
            queryInput.setText("");
            _chat.sendMessage(timestamp + " " + input);
          } else if (input.trim().equals("phase1()")) {
            _react.triggerGCS(1);
            queryInput.setText("");
            _chat.sendMessage(timestamp + " " + input);
          } else if (input.trim().equals("phase2()")) {
            _react.triggerGCS(2);
            queryInput.setText("");
            _chat.sendMessage(timestamp + " " + input);
          } else if (input.trim().equals("phase3()")) {
            _react.triggerGCS(3);
            queryInput.setText("");
            _chat.sendMessage(timestamp + " " + input);
          } else if (input.trim().equals("eyesOpen()")) {
            _react.eyesOpen();
            queryInput.setText("");
            _chat.sendMessage(timestamp + " " + input);
          } else if (input.trim().equals("testEyesOpen()")) {
            _react.testEyesOpen();
            queryInput.setText("");
            _chat.sendMessage(timestamp + " " + input);
          } else if (input.trim().equals("eyesClosed()")) {
            _react.eyesClosed();
            queryInput.setText("");
            _chat.sendMessage(timestamp + " " + input);
          } else if (input.trim().equals("rightArmMove()")) {
            _react.rightArmMoved();
            queryInput.setText("");
            _chat.sendMessage(timestamp + " " + input);
          } else if (input.trim().equals("leftArmMove()")) {
            _react.leftArmMoved();
            queryInput.setText("");
            _chat.sendMessage(timestamp + " " + input);
          } else if (input.trim().equals("rightLegMove()")) {
            _react.rightLegMoved();
            queryInput.setText("");
            _chat.sendMessage(timestamp + " " + input);
          } else if (input.trim().equals("leftLegMove()")) {
            _react.leftLegMoved();
            queryInput.setText("");
            _chat.sendMessage(timestamp + " " + input);
          } else if (input.trim().equals("rightHandMove()")) {
            _react.rightHandMoved();
            queryInput.setText("");
            _chat.sendMessage(timestamp + " " + input);
          } else if (input.trim().equals("leftHandMove()")) {
            _react.leftHandMoved();
            queryInput.setText("");
            _chat.sendMessage(timestamp + " " + input);
          } else if (input.trim().equals("handClench()")) {
            _react.handClenched();
            queryInput.setText("");
            _chat.sendMessage(timestamp + " " + input);
          } else if (input.trim().equals("handOpen()")) {
            _react.handOpened();
            queryInput.setText("");
            _chat.sendMessage(timestamp + " " + input);
          } else if (input.trim().equals("testComaScore()")) {
            _react.testDiaScore();
            queryInput.setText("");
            _chat.sendMessage(timestamp + " " + input);
          } else if (input.trim().equals("playSound()")) {
            _react.playSound();
            queryInput.setText("");
            _chat.sendMessage(timestamp + " " + input);
          }else if (input.contains("getState(")) {
          _chat.sendMessage(timestamp + " " + input);
          _statusbar.setText(_react.getState());
          queryInput.setText("");
          }else if (input.trim().equals("")) {
            //do nothing
          } else {
            _chat.sendMessage(timestamp + " " + input);
            _react.sendMessageToQueue(input);
            queryInput.setText("");
          }
        }
      }
    };

    final JPanel south = new JPanel();
    south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));

    final JPanel south_buttons = new JPanel();
    south_buttons.setLayout(new BoxLayout(south_buttons, BoxLayout.X_AXIS));

    JButton jb = getIconButton("info");
    jb.setToolTipText("show help");
    south_buttons.add(jb);

    south_buttons.add(queryInput = createQueryInput(execute));

    jb = getIconButton("gtk-apply");
    jb.setToolTipText("execute query");
    south_buttons.add(jb);
    jb.addActionListener(execute);

    jb = getIconButton("gtk-clear");
    jb.setToolTipText("clear input field");
    south_buttons.add(jb);
    jb.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(final ActionEvent e) {
        queryInput.setText("");
      }
    });

    south.add(south_buttons);

    final JPanel south_status = new JPanel();
    south_status.setLayout(new FlowLayout(FlowLayout.LEFT));
    _statusbar = new JLabel("Welcome", SwingConstants.LEFT);
    _statusbar.setForeground(new Color(168, 168, 168));
    south_status.add(_statusbar);

    _chat = new ChatProtocol(mainFrame, new Listener<String>() {
      @Override
      public void listen(final String q) {
        queryInput.setText(q);
      }

      @Override
      public void free() {
        //noting to do here
      }
    }, execute, _statusbar);

    south.add(south_status);
    contentPane.add(_chat);
    contentPane.add(south, BorderLayout.SOUTH);

    // display the frame
    this.pack();
    this.setLocationRelativeTo(null);
    this.setVisible(true);
  }
}
