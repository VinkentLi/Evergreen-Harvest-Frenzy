import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.io.FileInputStream;

public class Button {
  
  public BufferedImage norm, difficultyButtons;
  GamePanel gp;
  ClickHandler clickH;

  public Button(GamePanel gp, ClickHandler clickH) {

    this.gp = gp;
    this.clickH = clickH;
    
    try {

      norm = ImageIO.read(new FileInputStream("res/Buttons/buttons.png"));
      difficultyButtons = ImageIO.read(new FileInputStream("res/Buttons/difficultyButtons.png"));
      
    } catch (IOException e) {
      e.printStackTrace();
    }
    
  }

  public void drawRunning(Graphics2D g2) {
    
    g2.drawImage(norm, 0, 0, 800, 450, null);
    
  }

  public void drawDifficulty(Graphics2D g2) {

    g2.drawImage(difficultyButtons, 0, 0, 800, 450, null);
    
  }

}