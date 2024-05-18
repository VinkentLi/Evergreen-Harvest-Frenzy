import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;

public class ClickHandler extends MouseAdapter {

  Random rand = new Random();
  public boolean planting, watering, greenButtonPressed, easyPressed, mediumPressed, hardPressed;
  public int score = 0;
  public int[] growthLevels = new int[4];
  public int[] completedLevel = randomizePlants();
  public int gameState = 0; // 0 is mainMenu, 1 means the game is running, 2 is paused, 3 is a win, 4 is a loss, 5 is in-between levels, 6 is after failing a level, 7 is difficulty selector
  public int level = 1;
  public int difficulty = 1;
  public long originalTime = 12L;
  public long time = 12L;
  public int timeErased = 1;
  public int originalLives = 4;
  public int lives = 4;
  public boolean startedPlanting;
  int[][] plants = randomizePlots();
  int[] locationPressed = new int[2];
  
  @Override
  public void mouseClicked(MouseEvent e) {
  }

  @Override
  public void mousePressed(MouseEvent e) {

    int x = e.getX();
    int y = e.getY();
    locationPressed = new int[]{x, y};

    switch (gameState) {
        
      case 0:
      case 5:
      case 6:
        if (x <= 500 && x >= 300 && y >= 175 && y <= 275) {
          greenButtonPressed = true;
        }
      case 7:
        if (x <= 300 && x >= 100 && y >= 175 && y <= 275) {
          easyPressed = true;
        } else if (x <= 500 && x >= 301 && y >= 175 && y <= 275) {
          mediumPressed = true;
        } else if (x <= 700 && x >= 501 && y >= 175 && y <= 275) {
          hardPressed = true;
        }
        
    }
    
    
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    greenButtonPressed = false;
    easyPressed = false;
    mediumPressed = false;
    hardPressed = false;

    int x = locationPressed[0];
    int y = locationPressed[1];

    switch (gameState) {

      case 0: // main menu
        
        if (x <= 500 && x >= 300 && y >= 175 && y <= 275) {
          gameState = 7;
        }
        break;
        
      case 1: // running
        
        if (x <= 700 && x >= 600 && y >= 0 && y <= 100) {
          if (planting) {
            planting = false;
          } else {
            planting = true;
            watering = false;
          }
        }
        else if (x <= 800 && x >= 700 && y >= 0 && y <= 100){
          if (watering) {
            watering = false;
          } else {
            watering = true;
            planting = false;
          }
        }
        else {
          for (int i = 0; i < 4; i++) {
            if (x <= plants[i][0] + 100 && x >= plants[i][0] && y <= plants[i][1] + 100 && y >= plants[i][1]) {
              updatePlant(i);
            } 
          }
        }
        break;
        
      case 5: // between levels
      case 6:
        
        if (x <= 500 && x >= 300 && y >= 175 && y <= 275) {
          plants = randomizePlots();
          completedLevel = randomizePlants();
          gameState = 1;
          startedPlanting = true;
        }
        break;

      case 7: // difficulty selector

        if (x <= 700 && x >= 100 && y >= 175 && y <= 275) {

          if (x <= 300 && x >= 100 && y >= 175 && y <= 275) {
            difficulty = 1;
            originalTime = 30L;
            time = 30L;
            timeErased = 3;
            originalLives = 4;
            lives = 4;
          } else if (x <= 500 && x >= 300 && y >= 175 && y <= 275) {
            difficulty = 2;
            originalTime = 20L;
            time = 20L;
            timeErased = 2;
            originalLives = 3;
            lives = 3;
          } else if (x <= 700 && x >= 500 && y >= 175 && y <= 275) {
            difficulty = 3;
            originalTime = 12L;
            time = 12L;
            timeErased = 1;
            originalLives = 2;
            lives = 2;
          }
          plants = randomizePlots();
          completedLevel = randomizePlants();
          gameState = 1;
          startedPlanting = true;
          
        }
        break;
        
    }
  }

  public int[][] randomizePlots() {
    
    int[] plantCoords1 = new int[]{rand.nextInt(300), rand.nextInt(125)};
    int[] plantCoords2 = new int[]{rand.nextInt(300) + 400, rand.nextInt(25) + 100};
    int[] plantCoords3 = new int[]{rand.nextInt(300), rand.nextInt(125) + 225};
    int[] plantCoords4 = new int[]{rand.nextInt(300) + 400, rand.nextInt(125) + 225};

    int[][] plants = new int[][]{plantCoords1, plantCoords2, plantCoords3, plantCoords4};

    return plants;
    
  }

  public int[] randomizePlants() {

    int[] completedPlantLevels = new int[4];

    for (int plant = 0; plant < 4; plant++) {
      completedPlantLevels[plant] = rand.nextInt(4) + 3;
    }

    return completedPlantLevels;
    
  }

  public void updatePlant(int plantNumber) {

    if (planting && growthLevels[plantNumber] == 0) {
      growthLevels[plantNumber] = 1;
    } else if (watering && growthLevels[plantNumber] < completedLevel[plantNumber] && growthLevels[plantNumber] > 0) {
      growthLevels[plantNumber] += 1;
    } 
    
  }
  
}