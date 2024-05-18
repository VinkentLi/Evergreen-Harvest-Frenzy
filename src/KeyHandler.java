import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

  ClickHandler clickH;
  GamePanel gp;
  
  public KeyHandler(GamePanel gp, ClickHandler clickH) {

    this.gp = gp;
    this.clickH = clickH;
    
  }
  
	@Override
	public void keyTyped(KeyEvent e) {
    
	}

	@Override
	public void keyPressed(KeyEvent e) {
    
	}

	@Override
	public void keyReleased(KeyEvent e) {
    int code = e.getKeyCode();

    if (code == KeyEvent.VK_ESCAPE) {
      
      switch (clickH.gameState) {

        case 0:
          break;
        case 1:
          clickH.gameState = 2;
          break;
        case 2:
          clickH.gameState = 1;
          break;
        case 3:
        case 4:
        case 7:
          clickH.gameState = 0;
          clickH.score = 0;
          break;
        
      }
		
	}
  
	}
  
}