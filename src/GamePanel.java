import javax.swing.JPanel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.io.FileInputStream;
import java.util.*;

public class GamePanel extends JPanel implements Runnable {

  final int screenWidth = 800; 
  final int screenHeight = 450;
  
  final int FPS = 30;
  
  ClickHandler clickH = new ClickHandler();
  KeyHandler keyH = new KeyHandler(this, clickH);
  Thread gameThread;

  Plant plant1 = new Plant(this, clickH, 0);
  Plant plant2 = new Plant(this, clickH, 1);
  Plant plant3 = new Plant(this, clickH, 2);
  Plant plant4 = new Plant(this, clickH, 3);
  Plant[] plants = new Plant[]{plant1, plant2, plant3, plant4};

  Button buttons = new Button(this, clickH);
  
  int flashOpacity = 0;
  int highScore = 0;
  long plantTimer = 0;
  long plantTime = 0;
  boolean flash = false;
  boolean newHighScore = false;
  boolean[] checkedPlants = new boolean[4];

  public GamePanel() {

    this.setPreferredSize(new Dimension(screenWidth, screenHeight));

    this.setDoubleBuffered(true);
    this.addMouseListener(clickH);
    this.addKeyListener(keyH);
    this.setFocusable(true);
    highScore = 0;

  }

  public void startGameThread() {

    gameThread = new Thread(this);
    gameThread.start();

  }

  @Override
  public void run() {

    double drawInterval = 1000000000 / FPS;
    double delta = 0;
    long lastTime = System.nanoTime();
    long currentTime;
    long timer = 0;
    int drawCount = 0;

    while (gameThread != null) {
      
      currentTime = System.nanoTime();

      // starts the timer
      if (clickH.startedPlanting && clickH.gameState == 1) {
        plantTimer = 0;
        plantTime = System.nanoTime();
        clickH.startedPlanting = false;
      }

      //sets up fps
      delta += (currentTime - lastTime) / drawInterval;
      timer += (currentTime - lastTime);
      lastTime = currentTime;

      if (delta > 1) {
		update();
        repaint();

        // increases the plant timer
        if (plantTime != 0 && clickH.gameState == 1) {
          plantTimer += System.nanoTime() - plantTime;
          plantTime = System.nanoTime();
        } 
        else if (clickH.gameState == 2) {
          // pauses the plant timer when the game is paused
          plantTime = System.nanoTime();
        }

        // resets the game when the time runs out
        if (plantTimer >= clickH.time * 1000000000) {
          clickH.lives -= 1;
          clickH.growthLevels = new int[4];
          clickH.planting = false;
          clickH.watering = false;
          checkedPlants = new boolean[4];
          
          //stops and resets timer
          plantTime = 0;
          plantTimer = 0;
          clickH.startedPlanting = false;
          
          if (clickH.lives <= 0) {
            clickH.gameState = 4; // lose gamestate
            clickH.lives = 5;
            clickH.level = 1;
            
            // checks for new highscore
            if (clickH.score > highScore) {
              highScore = clickH.score;
              newHighScore = true;
            }
            else {
              newHighScore = false;
            }
          } else {
            clickH.gameState = 6; // gamestate after failing a level
          }
          
        }
        
        delta--;
        drawCount++;
      }

      // if (timer > 1000000000) {
      //   System.out.println("FPS: " + drawCount);
      //   drawCount = 0;
      //   timer = 0;
      // }

    }

  }

  public void update() {

    for (int plant = 0; plant < 4; plant++) {
      plants[plant].update();
      if (checkedPlants[plant] == false && plants[plant].isMature()) {
        flash = true;
        checkedPlants[plant] = true;
      }
    }

    if (flash) {
      if (flashOpacity < 125) {
        flashOpacity += 25;
      } 
      else {
        flash = false;
      }
    }

    if (flashOpacity <= 125 && flashOpacity > 0 && flash == false) {
      flashOpacity -= 25;
    }

    // resets the game after you win
    if (clickH.level > 5) {
      
      clickH.gameState = 3;
      clickH.growthLevels = new int[4];
      clickH.planting = false;
      clickH.watering = false;
      checkedPlants = new boolean[4];

      // checks for new highscore
      if (clickH.score > highScore) {
        highScore = clickH.score;
        newHighScore = true;
      } else {
        newHighScore = false;
      }
      clickH.level = 1;
      clickH.lives = 5;
      
    } else if (Arrays.equals(clickH.growthLevels, clickH.completedLevel)) {
      
      //prepares the next level after level completion
      clickH.growthLevels = new int[4];
      clickH.level += 1;
      clickH.gameState = 5;
      clickH.planting = false;
      clickH.watering = false;
      checkedPlants = new boolean[4];

      // determines score
      long hardTime = 15L;
      long otherTime = clickH.originalTime;
      float timeScore = (float) ((((otherTime * 1000000000L - plantTimer) / 1000000000) + 1) / (4 - clickH.difficulty));
      
      if (clickH.difficulty == 3) {
        timeScore = (float) ((((hardTime * 1000000000L - plantTimer) / 1000000000) + 1) / (4 - clickH.difficulty));
      }
      
      clickH.score += (Math.pow(timeScore, 4.2069) * Math.pow((float) clickH.level, 1.5) * Math.pow(1.3, (float) clickH.level) * Math.pow(clickH.difficulty, 1.069)) / (clickH.originalLives - clickH.lives + 1);

      // stops and resets timer
      plantTime = 0;
      plantTimer = 0;
      clickH.time -= clickH.timeErased;
      
    }
    
  }

  public void paintComponent(Graphics g) {

    super.paintComponent(g);

    Graphics2D g2 = (Graphics2D) g;

    String bigText = "";
    String smallText = "";
    String bottomText = "";

    BufferedImage bg = null;
    BufferedImage greenButton = null;
    
    // gets all the images
    try {
      bg = ImageIO.read(new FileInputStream("res/background.png"));
      greenButton = ImageIO.read(new FileInputStream("res/Buttons/greenButton.png"));
    } catch (IOException e) {
      e.printStackTrace();
    }

    g2.drawImage(bg, 0, 0, 800, 450, null);

    switch (clickH.gameState) {

      case 0: // main menu
        
        bigText = "Evergreen Planting Frenzy";
        g2.setColor(new Color(0, 0, 0, 127));
        g2.fillRect(303, 178, 200, 100);
        g2.drawImage(greenButton, 300, 175, 200, 100, null);
        
        break;

      case 1: // running
        
        for (Plant plant : plants) {
          plant.draw(g2);
        }
        buttons.drawRunning(g2);

        Font timeFont = new Font("DejaVu Sans", Font.BOLD, 25);
        g2.setFont(timeFont);
        int timeLeft = (int) ((clickH.time * 1000000000 - plantTimer) / 1000000000) + 1;
        g2.setColor(new Color(0, 0, 0, 127));
        g2.drawString(String.valueOf(timeLeft), 12, 32);
        g2.setColor(new Color(255, 255, 255));
        g2.drawString(String.valueOf(timeLeft), 10, 30);
        
        break;

      case 2: // paused
        
        bigText = "PAUSED";
        smallText = "Press ESC to unpause";
        break;

      case 3: // win

        bigText = "YOU WIN!";
        smallText = "Your score was: " + clickH.score + ". Press ESC to return to main menu.";
        
        if (newHighScore) {
          bottomText = "NEW High Score: " + highScore;
        }
        else {
          bottomText = "High Score: " + highScore;
        }
        clickH.time = clickH.originalTime;
        break;

      case 4: // lose

        bigText = "YOU LOSE!";
        smallText = "Your score was: " + clickH.score + ". Press ESC to return to main menu.";
        
        if (newHighScore) {
          bottomText = "NEW High Score: " + highScore;
        }
        else {
          bottomText = "High Score: " + highScore;
        }
        clickH.time = clickH.originalTime;
        break;

      case 5: // in between levels
        bigText = "LEVEL " + (clickH.level - 1) + " COMPLETE!";
        smallText = "Next Level";
        bottomText = "Your score is: " + clickH.score;

        g2.setColor(new Color(0, 0, 0, 127));
        g2.fillRect(303, 178, 200, 100);
        g2.drawImage(greenButton, 300, 175, 200, 100, null);
        break;

      case 6: // after failing a level
        bigText = "LEVEL " + (clickH.level) + " FAILED!";
        smallText = "Try again";
        if (clickH.lives > 1) {
          bottomText = "You have " + clickH.lives + " lives left!";
        }
        else {
          bottomText = "You have " + clickH.lives + " life left!";
        }

        g2.setColor(new Color(0, 0, 0, 127));
        g2.fillRect(303, 178, 200, 100);
        g2.drawImage(greenButton, 300, 175, 200, 100, null);
        break;

      case 7: // difficulty settings
        bigText = "CHOOSE DIFFICULTY";
        smallText = "MEDIUM";
        
        g2.setColor(new Color(0, 0, 0, 127));
        g2.fillRect(103, 178, 600, 100);
        buttons.drawDifficulty(g2);
        g2.setColor(new Color(255, 255, 255, 127));
        
        if (clickH.easyPressed) {
          g2.fillRect(100, 175, 200, 100);
        }
        else if (clickH.mediumPressed) {
          g2.fillRect(300, 175, 200, 100);
        }
        else if (clickH.hardPressed) {
          g2.fillRect(500, 175, 200, 100);
        }
        break;
        
    }
    
    Font bigFont = new Font("DejaVu Sans", Font.BOLD, 50);
    FontMetrics bigMetrics = g.getFontMetrics(bigFont);

    int x = screenWidth / 2 - bigMetrics.stringWidth(bigText) / 2;
    int y = 100;

    g2.setFont(bigFont);
    g2.setColor(new Color(0, 0, 0, 127));
    g2.drawString(bigText, x + 3, y + 3);
    g2.setColor(new Color(255, 255, 255));
    g2.drawString(bigText, x, y);

    Font smallFont = new Font("DejaVu Sans", Font.BOLD, 25);
    FontMetrics smallMetrics = g.getFontMetrics(smallFont);
    g2.setFont(smallFont);

    x = screenWidth / 2 - smallMetrics.stringWidth(smallText) / 2;
    y = screenHeight / 2 + 9;
    g2.setColor(new Color(0, 0, 0, 127));
    g2.drawString(smallText, x + 3, y + 3);
    g2.setColor(new Color(255, 255, 255));
    g2.drawString(smallText, x, y);

    x = screenWidth / 2 - smallMetrics.stringWidth(bottomText) / 2;
    y = 350;
    g2.setColor(new Color(0, 0, 0, 127));
    g2.drawString(bottomText, x + 3, y + 3);
    g2.setColor(new Color(255, 255, 255));
    g2.drawString(bottomText, x, y);

    if (clickH.gameState == 0) {
      x = screenWidth / 2 - bigMetrics.stringWidth("Play") / 2;
      y = screenHeight / 2 + 18;
      g2.setFont(bigFont);
      g2.setColor(new Color(0, 0, 0, 127));
      g2.drawString("Play", x + 3, y + 3);
      g2.setColor(new Color(255, 255, 255));
      g2.drawString("Play", x, y);
    }
    
    if (clickH.gameState == 1) {
      g2.setColor(new Color(255, 255, 255, 127));
      if (clickH.planting) {
        g2.fillRect(600, 0, 100, 100);
      }
      else if (clickH.watering) {
        g2.fillRect(700, 0, 100, 100);
      }
    } else if (clickH.gameState == 7) {

    	x = 200 - smallMetrics.stringWidth("EASY") / 2;
        y = screenHeight / 2 + 9;
        g2.setColor(new Color(0, 0, 0, 127));
        g2.drawString("EASY", x + 3, y + 3);
        g2.setColor(new Color(255, 255, 255));
        g2.drawString("EASY", x, y);
        
        x = 600 - smallMetrics.stringWidth("HARD") / 2;
        g2.setColor(new Color(0, 0, 0, 127));
        g2.drawString("HARD", x + 3, y + 3);
        g2.setColor(new Color(255, 255, 255));
        g2.drawString("HARD", x, y);
    	
    }

    if (clickH.greenButtonPressed) {
      g2.setColor(new Color(255, 255, 255, 127));
      g2.fillRect(300, 175, 200, 100);
    }

    g2.setColor(new Color(255, 255, 255, flashOpacity));
    g2.fillRect(0, 0, 800, 450);
    g2.setColor(new Color(255, 255, 0, flashOpacity * 2));
    g2.setFont(bigFont);
    x = screenWidth / 2 - bigMetrics.stringWidth("MATURE") / 2;
    y = screenHeight / 2 + 18;
    g2.drawString("MATURE", x, y);
    
    Font credits = new Font("DejaVu Sans", Font.BOLD, 15);
    g2.setColor(new Color(255, 255, 255));
    g2.setFont(credits);
    g2.drawString("Vincent Li 2023                                                                                         images from Pixabay and edited by Vincent Li", 5, 445);

    g2.dispose();
    
  }

}