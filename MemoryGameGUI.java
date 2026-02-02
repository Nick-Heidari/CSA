/*
 * Project 3.1.5
 
 * A Memory Game GUI
 * 
 * Creates buttons for displaying strings and provides a UI for
 * gathering user input
 *
 * V1.0
 * 6/1/2019
 * Copyright(c) 2019 PLTW to present. All rights reserved
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Class to play Memory Game.
 */
public class MemoryGameGUI extends JFrame 
{
  // used internally by the GUI
  private static final long serialVersionUID = 100L;

  // instance variables
  private JButton gameButtons[];
  private Boolean playRandom;

  /**
   * Class constructor for creating a GUI for the Memory Game. 
   * Create a game board with the createBoard method and play the game by
   * providing a sequence of strings to playSequence.
   */
  public MemoryGameGUI()
  {
    super();
    setResizable(false);
    setFocusable(true);
  }
 
  /**
   * Create a game board of buttons that will display strings from a given sequence.
   * After you create the board, you can play the game by calling the playSequence method.
   * <P>
   * Initial condition: The number of buttons should be positive and should not exceed the width of the screen
   * 
   * @param numButtons Number of buttons to display on a horizontal board
   * @param playRandom If true, during play, the buttons will display text in a random order,
   *          if false, they will show from left to right.
   */
  public void createBoard(int numButtons, Boolean playRandom)
  {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(500, 400);

    // create buttons
    gameButtons = new JButton[numButtons];
    JPanel panel1 = new JPanel();
    panel1.setLayout(new FlowLayout());
    for (int i = 0; i < numButtons; i++) {
      gameButtons[i] = new JButton();
      gameButtons[i].setPreferredSize(new Dimension(100,100));
      gameButtons[i].setBackground(Color.GRAY);
      panel1.add(gameButtons[i]);
    }

    // organize and size the GUI
    Container pane = getContentPane();
    pane.add(panel1,  BorderLayout.PAGE_START);
    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    setLocation(dim.width/2 - 100, dim.height/2 - 100);
    pack();
    setVisible(true);

    // specify random or not
    this.playRandom = playRandom;

    JOptionPane.showMessageDialog(this, "Welcome to the Memory Game", "Welcome", JOptionPane.PLAIN_MESSAGE);
  }

  /**
   * Play the game by showing buttons with the text from a randomly ordered sequcence
   * of strings. The button text may appear in order from left to right
   * or may appear on random buttons throughout the board.
   * <P>
   * Initial condition: sequence should be not empty/null and already radmondized 
   * 
   * @param sequence an array of strings to show as individual button text
   * @param delay how long the text will show, measured in tenths of a second
   * @return a string containing the users guess, or null if the user cancels
   */
  public String playSequence(String[] sequence, double delay)
  {
    delay *= 1000;
    JOptionPane.showMessageDialog(this, "Click when ready!", "Ready to Play?", JOptionPane.PLAIN_MESSAGE);

    // show each memory strings on a botton
    int buttonNum = -1; 
    for (int i = 0; i < sequence.length; i++ )
    {
      if (playRandom)
      {
        buttonNum = (int)(Math.random() * gameButtons.length);
      }
      else
      {
        buttonNum += 1;
        if (buttonNum > gameButtons.length-1)
        buttonNum = 0;
      }

      // wait a bit
      // a try is like an if statement, "throwing" an error if the body of the try fails
      try
      {
        Thread.sleep((int)delay);
      } catch (InterruptedException ie) { /* do nothing */ }

      // change button color and show the memory string
      gameButtons[buttonNum].setBackground(new Color(230, 204, 255));
      gameButtons[buttonNum].setText(sequence[i]);

      // wait a bit
      try
      {
        Thread.sleep((int)delay);
      } catch (InterruptedException ie) { /* do nothing */ }

      // show button as gray again and clear the memory string from it
      gameButtons[buttonNum].setBackground(Color.GRAY);
      gameButtons[buttonNum].setText("");
    }
    
    JFrame frame = new JFrame();
    String seq = JOptionPane.showInputDialog(frame, "What is the sequence?");

    return seq;
  }

  /**
   * Shows a message that the user matched the sequence
   */
  public void matched()
  { 
    JOptionPane.showMessageDialog(this, "You matched!", "Congratulations", JOptionPane.PLAIN_MESSAGE);
  }
    
  /**
   * Asks the user if they want to play another round of the game.
   * 
   * @return true to continue playing, false to quit 
   */
  public boolean playAgain()
  {
    int n = JOptionPane.showConfirmDialog(this, "Do you want to play again?", "Memory Game", JOptionPane.YES_NO_OPTION);
    if (n == JOptionPane.YES_OPTION )
      return true;
    return false;
  }

  /**
   * Show the current score and the number of times the game was played.
   * 
   * @param score number of correct guesses
   * @param rounds number of times game was played
   */
  public void showScore(int score, int rounds)
  {
    JOptionPane.showMessageDialog(this, "You scored " + score + " out of " + rounds, "Score Summary", JOptionPane.PLAIN_MESSAGE);
  }
  
  /**
   * Shows a "try again" message.
   */
  public void tryAgain()
  { 
    JOptionPane.showMessageDialog(this, "Try again...", "Oops", JOptionPane.PLAIN_MESSAGE);
  }

  /**
   * Close the board, ending the game.
   */
  public void quit()
  {
    this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
  }

}
