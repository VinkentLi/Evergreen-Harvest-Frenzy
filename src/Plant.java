import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.io.FileInputStream;

public class Plant {

  GamePanel gp;
  ClickHandler clickH;
  int x;
  int y;
  int plantX;
  int plantY;
  int plantNumber;
  BufferedImage img;
  BufferedImage plot;

  public Plant(GamePanel gp, ClickHandler clickH, int plantNumber) {

    this.gp = gp;
    this.clickH = clickH;

    x = clickH.plants[plantNumber][0];
    y = clickH.plants[plantNumber][1];
    
    this.plantNumber = plantNumber;
    getPlantImage();
    
  }

  public void getPlantImage() {

    try {
      plot = ImageIO.read(new FileInputStream("res/plot.png"));
      // img = ImageIO.read(getClass().getResourceAsStream("res/PlantStages/plant.png"));  
    } catch (IOException e) {
      e.printStackTrace();      
    }
    
  }

  public boolean isMature() {

    if (clickH.growthLevels[plantNumber] == clickH.completedLevel[plantNumber]) {
      return true;
    }
    return false;
    
  }

  public void update() {

    String plant = null;

    switch (clickH.completedLevel[plantNumber]) {

      case 3:
        plant = "Carrot";
        break;
      case 4:
        plant = "Moss";
        break;
      case 5:
        plant = "Potato";
        break;
      case 6:
        plant = "Bush";
        break;
        
    }

    try {
      img = ImageIO.read(new FileInputStream("res/PlantStages/" + plant +"/"+ plant + clickH.growthLevels[plantNumber] + ".png"));
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    x = clickH.plants[plantNumber][0];
    y = clickH.plants[plantNumber][1];
    
  }

  public void draw(Graphics2D g2) {

    g2.drawImage(plot, x, y, 100, 100, null);
    switch (clickH.completedLevel[plantNumber]) {

      case 3:
      case 5:
        plantX = clickH.plants[plantNumber][0];
        plantY = clickH.plants[plantNumber][1];
        break;
      case 4:
      case 6:
        plantX = clickH.plants[plantNumber][0];
        plantY = clickH.plants[plantNumber][1] - 25;
        break;
        
    }
    g2.drawImage(img, plantX, plantY, 100, 100, null);
    
  }
  
}