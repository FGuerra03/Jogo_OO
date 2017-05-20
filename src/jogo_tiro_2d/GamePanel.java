package jogo_tiro_2d;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.image.*;

public class GamePanel extends JPanel implements Runnable {
    
    //Dimensoes
    public static int WIDTH = 400;
    public static int HEIGHT = 400;
    private Thread thread;
    private boolean running;
    private BufferedImage image;
    private Graphics2D g;
    private int FPS = 40;
    private double averageFPS;
    
    //Construtor
    public GamePanel(){
        super();
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        requestFocus();
    }
    
    //Funcoes 
    @Override
    public void addNotify(){
        super.addNotify();
        if(thread == null){
            thread = new Thread (this);
            thread.start();
        }
    }
    
    @Override
    public void run(){
        running = true;
        
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        g = (Graphics2D) image.getGraphics();
        
        long startTime;
        long URDTimeMills;
        long waitTime;
        long totalTime = 0;
        
        int frameCount = 0;
        int maxFrameCount = 40;
        
        long targetTime = 1000/FPS;
        
        //LOOP
        while(running) {
            
            startTime = System.nanoTime();
            
            gameUpdate();
            gameRender();
            gameDraw();
        
            URDTimeMills = (System.nanoTime() - startTime) / 1000000;
            waitTime = targetTime - URDTimeMills;
            
            try {
                Thread.sleep(waitTime);
            } catch (Exception e) {
            }
              totalTime += System.nanoTime() - startTime;
              frameCount ++;
              if(frameCount == maxFrameCount){
                  averageFPS = 1000.0 / ((totalTime / frameCount) / 1000000);
                  frameCount = 0;
                  totalTime = 0;
              }
        }
    }
    
    private void gameUpdate() {
    
    }

    private void gameRender(){
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(Color.GREEN);
        g.drawString("FPS: " + averageFPS, 20, 20);
    }
    
    private void gameDraw(){
        Graphics g2 = this.getGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
    }
}

