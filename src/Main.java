import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class Main {
  
  public static void main(String[] args) throws IOException {
	 
    JFrame window = new JFrame();
    
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    window.setResizable(false);
    window.setTitle("Evergreen Planting Frenzy");
    window.setIconImage(ImageIO.read(new File("res/PlantStages/Carrot/Carrot3.png")));

    GamePanel gamePanel = new GamePanel();
    window.add(gamePanel);

    window.pack();

    window.setLocationRelativeTo(null);
    window.setVisible(true);

    gamePanel.startGameThread();

  }

}
