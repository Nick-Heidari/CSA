/*
* Project 4.1.5: Escape Room Revisited
* 
* V1.0
* Copyright(c) 2024 PLTW to present. All rights reserved
*/
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import javax.imageio.ImageIO;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

/**
 * A game where a player maneuvers around a gameboard to answer
 * riddles or questions, collecting prizes with correct answers.
 */
public class GameGUI extends JComponent implements KeyListener
{
  static final long serialVersionUID = 415L;

  // constants for gameboard config
  private static final int WIDTH = 510;
  private static final int HEIGHT = 360;
  private static final int SPACE_SIZE = 60;
  private static final int GRID_W = 8;
  private static final int GRID_H = 5;
  private static final int MOVE = 60;

  // project constants
  private static final int MAX_LEVEL = 5;
  private static final String LEVEL_FILE = "level.txt";
  private static final String QUIZ_FILE = "quiz.txt";
  private static final String DELIMITER = "\\|";

  // frame and images for gameboard
  private JFrame frame;
  private Image bgImage;
  private Image prizeImage;
  private Image player;
  private Image playerQ;

  // player config
  private int currX = 15; 
  private int currY = 15;
  private boolean atPrize;
  private Point playerLoc;
  private int playerSteps;

  // walls, player level, and prizes
  private int numWalls = 8;
  private int playerLevel = 1;
  private Rectangle[] walls; 
  private Rectangle[] prizes;

  // quiz data
  private ArrayList<String[]> quizItems;

  // scores, sometimes awarded as (negative) penalties
  private int goodMove = 1;
  private int offGridVal = 5; // penalty 
  private int hitWallVal = 5;  // penalty 
  private int correctAns = 10;
  private int wrongAns = 7; // penalty 
  private int score = 0; 

  /**
   * Constructor for the GameGUI class.
   * 
   * Gets the player level and the questions/answers for the game 
   * from two files on disk. Creates the gameboard with a background image,
   * walls, prizes, and a player.
   */
  public GameGUI() throws IOException
  {
    newPlayerLevel();
    createQuiz();
    createBoard();
  }

  /**
   * Create array of questions and answers from the quiz.txt file.
   * 
   * @precondition: The TXT file contains at least playerLevel number of questions.
   * 
   * @postcondition: An ArrayList is populated with one question and one answer per entry.
   */
  private void createQuiz() 
  {
    quizItems = new ArrayList<String[]>();

    try
    {
      File quizFile = new File(QUIZ_FILE);
      Scanner input = new Scanner(quizFile);

      while (input.hasNextLine())
      {
        String line = input.nextLine().trim();
        if (line.length() > 0)
        {
          String[] parts = line.split(DELIMITER);
          if (parts.length >= 2)
          {
            String question = parts[0].trim();
            String answer = parts[1].trim();
            quizItems.add(new String[] {question, answer});
          }
        }
      }

      input.close();
      Collections.shuffle(quizItems); // customization: randomized questions without repeats
    }
    catch (IOException e)
    {
      System.err.println("Could not read quiz file.");
    }
  }

  /**
   * Update the instance variables playerLevel and numWalls
   * based on user level stored in the level.txt file.
   * 
   * @precondition: The TXT file must contain a level of at least 1.
   * @throws IOException
   */
  private void newPlayerLevel() 
  {
    try
    {
      File levelFile = new File(LEVEL_FILE);
      Scanner input = new Scanner(levelFile);

      if (input.hasNextInt())
      {
        playerLevel = input.nextInt();
      }
      input.close();
    }
    catch (IOException e)
    {
      playerLevel = 1;
      System.err.println("Could not read level file. Starting at level 1.");
    }

    if (playerLevel < 1)
    {
      playerLevel = 1;
    }
    if (playerLevel > MAX_LEVEL)
    {
      playerLevel = MAX_LEVEL;
    }

    numWalls = 8 + (playerLevel - 1) * 2;
  }

  /**
   * Manage the input from the keyboard: arrow keys, wasd keys, p, q, and h keys.
   * Key input is not case sensitive.
   * 
   * @param the key that was pressed
   */
  @Override
  public void keyPressed(KeyEvent e)
  {
    // P Key: If player is at a prize, ask a question and check for correct answer.
    // If correct, pickup prize and add correctAns to score, otherwise deduct from score.
    if (e.getKeyCode() == KeyEvent.VK_P )
    {
      if (atPrize)
      {
        int prizeIndex = getPrizeIndexAtPlayer();

        if (prizeIndex != -1 && prizeIndex < quizItems.size())
        {
          String question = quizItems.get(prizeIndex)[0];
          String correctAnswer = quizItems.get(prizeIndex)[1];

          String userAnswer = askQuestion(question);

          if (userAnswer != null && userAnswer.trim().equalsIgnoreCase(correctAnswer))
          {
            pickupPrize();
            score += correctAns;
            showMessage("Correct! You collected the coin.");
          }
          else
          {
            score -= wrongAns;
            showMessage("Incorrect. The correct answer was: " + correctAnswer);
          }
        }
      }
      else
      {
        showMessage("There is no coin here to pick up.");
      }
    }

    // Q key: quit game if all questions have been answered
    if (e.getKeyCode() == KeyEvent.VK_Q)
    {
      if (allPrizesCollected())
      {
        int finalScore = score - playerSteps;

        if (finalScore > 0 && playerLevel < MAX_LEVEL)
        {
          playerLevel++;
          showMessage("You escaped the room!\nFinal score: " + finalScore +
                      "\nYou leveled up to level " + playerLevel + "!");
        }
        else
        {
          showMessage("You escaped the room!\nFinal score: " + finalScore);
        }

        score = finalScore;
        endGame();
      }
      else
      {
        showMessage("You can only quit after collecting all coins.");
      }
    }

    // H key: help
    if (e.getKeyCode() == KeyEvent.VK_H)
    {
      String msg = "Move player: arrows or WASD keys\n" + 
      "Pickup prize: p\n" +
      "Quit: q\n" +
      "Help: h\n" +
      "Answer questions correctly to collect all coins!";
      showMessage(msg);
    }
    
    // Arrow and WASD keys: move down, up, left or right
    if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S )
    {
      score += movePlayer(0, MOVE);
    }
    if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W)
    {
      score += movePlayer(0, -MOVE);
    }
    if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A)
    {
      score += movePlayer(-MOVE, 0);
    }
    if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D)
    {
      score += movePlayer(MOVE, 0);
    }
  } 

  /**
   * Manage the key release, checking if the player is at a prize.
   * 
   * @param the key that was pressed
   */
  @Override
  public void keyReleased(KeyEvent e) 
  { 
    checkForPrize();
  }

  /* override necessary but no action */
  @Override
  public void keyTyped(KeyEvent e) { }

  /**
  * Add player, prizes, and walls to the gameboard.
  */
  private void createBoard() throws IOException
  {    
    prizes = new Rectangle[playerLevel];
    createPrizes();

    walls = new Rectangle[numWalls];
    createWalls();

    bgImage = ImageIO.read(new File("grid.png"));
    prizeImage = ImageIO.read(new File("coin.png"));
    player = ImageIO.read(new File("player.png")); 
    playerQ = ImageIO.read(new File("playerQ.png")); 
    
    // save player location
    playerLoc = new Point(currX, currY);

    // create the game frame
    frame = new JFrame();
    frame.setTitle("EscapeRoom");
    frame.setSize(WIDTH, HEIGHT);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.add(this);
    frame.setVisible(true);
    frame.setResizable(false); 
    frame.addKeyListener(this);

    checkForPrize();

    showMessage("Welcome to the Escape Room.\nLevel: " + playerLevel +
                "\nCollect all " + playerLevel + " coin(s) to escape.\nPress h for help.");
  }

  /**
   * Increment/decrement the player location by the amount designated.
   * This method checks for bumping into walls and going off the grid,
   * both of which result in a penalty.
   * 
   * @param incrx amount to move player in x direction
   * @param incry amount to move player in y direction
   * 
   * @return penalty for hitting a wall or trying to go off the grid, goodMove otherwise
   */
  private int movePlayer(int incrx, int incry)
  {
      int newX = currX + incrx;
      int newY = currY + incry;

      // check if off grid horizontally and vertically
      if ( (newX < 0 || newX > WIDTH-SPACE_SIZE) || (newY < 0 || newY > HEIGHT-SPACE_SIZE) )
      {
        showMessage("You have tried to go off the grid!");
        return -offGridVal;
      }

      // determine if a wall is in the way
      for (Rectangle r: walls)
      {
        int startX =  (int)r.getX();
        int endX  =  (int)r.getX() + (int)r.getWidth();
        int startY =  (int)r.getY();
        int endY = (int) r.getY() + (int)r.getHeight();

        if ((incrx > 0) && (currX <= startX) && (startX <= newX) && (currY >= startY) && (currY <= endY))
        {
          showMessage("A wall is in the way.");
          return -hitWallVal;
        }
        else if ((incrx < 0) && (currX >= startX) && (startX >= newX) && (currY >= startY) && (currY <= endY))
        {
          showMessage("A wall is in the way.");
          return -hitWallVal;
        }
        else if ((incry > 0) && (currY <= startY && startY <= newY && currX >= startX && currX <= endX))
        {
          showMessage("A wall is in the way.");
          return -hitWallVal;
        }
        else if ((incry < 0) && (currY >= startY) && (startY >= newY) && (currX >= startX) && (currX <= endX))
        {
          showMessage("A wall is in the way.");
          return -hitWallVal;
        }     
      }

      playerSteps++;
      currX += incrx;
      currY += incry;
      repaint();   
      return goodMove;
  }

  /**
   * Displays a dialog with a simple message and an OK button
   * 
   * @param str the message to show
   */
  private void showMessage(String str)
  {
    JOptionPane.showMessageDialog(frame, str);
  }

  /**
   * Display a dialog that asks a question and waits for an answer
   *
   * @param the question to display
   *
   * @return the text the user entered, null otherwise
   */
  private String askQuestion(String q)
  {
    return JOptionPane.showInputDialog(q.replace("\\n","\n"), JOptionPane.OK_OPTION);
  }

  /**
   * If there's a prize at the location, set atPrize to true and change player image
   */
  private void checkForPrize()
  {
    double px = playerLoc.getX();
    double py = playerLoc.getY();

    for (Rectangle r: prizes)
    {
      if (r.contains(px, py))
      {
        atPrize = true;
        repaint();
        return;
      }
    }
    atPrize = false;
  }

  /**
   * Pickup a prize.
   */
  private void pickupPrize()
  {
    double px = playerLoc.getX();
    double py = playerLoc.getY();

    for (Rectangle p: prizes)
    {
      if (p.getWidth() > 0 && p.contains(px, py))
      {
        p.setSize(0,0);
        atPrize = false;
        repaint();
      }
    }
  }

  /**
   * Return the index of the prize the player is currently on.
   * 
   * @return prize index or -1 if no prize is there
   */
  private int getPrizeIndexAtPlayer()
  {
    double px = playerLoc.getX();
    double py = playerLoc.getY();

    for (int i = 0; i < prizes.length; i++)
    {
      if (prizes[i].getWidth() > 0 && prizes[i].contains(px, py))
      {
        return i;
      }
    }
    return -1;
  }

  /**
   * Check if all prizes have been collected.
   * 
   * @return true if all prizes are gone, false otherwise
   */
  private boolean allPrizesCollected()
  {
    for (Rectangle p : prizes)
    {
      if (p.getWidth() > 0)
      {
        return false;
      }
    }
    return true;
  }

  /**
   * End the game, update and save the player level.
   */
  private void endGame() 
  {
    try
    {
      FileWriter fw = new FileWriter(LEVEL_FILE);
      fw.write(playerLevel + "\n");
      fw.close();
    }
    catch (IOException e)
    {
      System.err.println("Could not save level.");
    }
  
    setVisible(false);
    frame.dispose();
  }

  /**
   * Add randomly placed prizes to be picked up.
   */
  private void createPrizes()
  {
    int s = SPACE_SIZE; 
    Random rand = new Random();
    for (int numPrizes = 0; numPrizes < playerLevel; numPrizes++)
    {
      int h = rand.nextInt(GRID_H);
      int w = rand.nextInt(GRID_W);
      Rectangle r = new Rectangle((w*s + 15),(h*s + 15), 15, 15);

      for (Rectangle p : prizes)
      {
        while (p != null && p.equals(r))
        {
          h = rand.nextInt(GRID_H);
          w = rand.nextInt(GRID_W);
          r = new Rectangle((w*s + 15),(h*s + 15), 15, 15);
        }
      }
      prizes[numPrizes] = r;
    }
  }

  /**
   * Add walls to the board in random locations.
   */
  private void createWalls()
  {
     int s = SPACE_SIZE; 
     Random rand = new Random();

     for (int n = 0; n < numWalls; n++)
     {
      int h = rand.nextInt(GRID_H);
      int w = rand.nextInt(GRID_W);

      Rectangle r;
      if (rand.nextInt(2) == 0) 
      {
        r = new Rectangle((w*s + s - 5), h*s, 8, s);
      }
      else
      {
        r = new Rectangle(w*s, (h*s + s - 5), s, 8);
      }

      walls[n] = r;
    }
  }

  /* 
   * Manage board elements with graphics buffer g.
   * For internal use - do not call directly, use repaint instead.
   */
  public void paintComponent(Graphics g)
  {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D)g;

    // draw grid
    g.drawImage(bgImage, 0, 0, null);

    // add prizes
    for (Rectangle p : prizes)
    {
      if (p.getWidth() > 0) 
      {
        int px = (int)p.getX();
        int py = (int)p.getY();
        g.drawImage(prizeImage, px, py, null);
      }
    }

    // add walls
    for (Rectangle r : walls) 
    {
      g2.setPaint(Color.BLACK);
      g2.fill(r);
    }
   
    // draw player, saving its location
    if (atPrize)
    {
      g.drawImage(playerQ, currX, currY, 40, 40, null);
    }
    else
    {
      g.drawImage(player, currX, currY, 40, 40, null);
    }
    playerLoc.setLocation(currX, currY);
  }
}