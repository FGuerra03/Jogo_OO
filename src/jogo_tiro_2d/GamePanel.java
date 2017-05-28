package jogo_tiro_2d;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util. *;


public class GamePanel extends JPanel implements Runnable, KeyListener{
    
    //Atributos
    public static int WIDTH = 400;
    public static int HEIGHT = 400;
    private Thread thread;
    private boolean running;
    private BufferedImage image;
    private Graphics2D g;
    private int FPS = 40;
    private double averageFPS;
    
    public static Player player;
    public static ArrayList<Bullet> bullets;
    public static ArrayList<Enemy> enemies;
    
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
        addKeyListener(this);
    }
    
    @Override
    public void run(){
        running = true;
        
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        g = (Graphics2D) image.getGraphics();
        
        player = new Player();
        bullets = new ArrayList<Bullet>();
        enemies = new ArrayList<Enemy>();
        for (int k = 0; k < 5; k++){
            enemies.add(new Enemy(1, 1));
        }
        
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
        
        //Atualizacao do player
        player.update();
        
        //Atualizacao das balas
        for (int i = 0; i < bullets.size(); i++){
            boolean remove = bullets.get(i).update();
            if (remove) {
                bullets.remove(i);
                i--;
            }
        }
        int z;
        int i;
        
        //Atualizacao inimigos
        for (i = 0; i < enemies.size(); i++){
            enemies.get(i).update();
        }
        
        //Colisao bullet-enemy
        for (int k = 0; k < bullets.size(); k++){
            
            Bullet b = bullets.get(k);
            double bx = b.getx();
            double by = b.gety();
            double br = b.getr();
            
            for (int j = 0; j < enemies.size(); j++){
                Enemy e = enemies.get(j);
                double ex = e.getx();
                double ey = e.gety();
                double er = e.getr();
                
                double dx = bx - ex;
                double dy = by - ey;
                double dist = Math.sqrt(dx * dx + dy * dy);
                
                if (dist < br + er){
                    e.hit();
                    bullets.remove(k);
                    k--;
                    break;
                }
            }
        }
        
        //Checar inimigos
        for (int l = 0; l < enemies.size(); l++){
            if (enemies.get(l).isDead()) {
                enemies.remove(l);
                l--;
            }
        }
        
    }

    private void gameRender(){
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(Color.GREEN);
        g.drawString("FPS: " + averageFPS, 0, 15);
        
        //drar player
        player.draw(g);
        int i = 0;
        
        //draw bullets 
        for (int j = 0; j < bullets.size() ; j++){
            bullets.get(j).draw(g);
        }
        
        
        //draw enemy
         for (i = 0; i < enemies.size(); i++){
            enemies.get(i).draw(g);
        }
    }
    
    private void gameDraw(){
        Graphics g2 = this.getGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
    }
    
    @Override
    public void keyTyped(KeyEvent key){
        
    }
    
    @Override
    public void keyPressed(KeyEvent key){
        int keyCode;
        keyCode = key.getKeyCode();
        if(keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_A ){
            player.setLeft(true);
        }
        if(keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_D){
            player.setRight(true);
        }
        if(keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_W){
            player.setUp(true);
        }
        if(keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_S){
            player.setDown(true);
        }
        if(keyCode == KeyEvent.VK_Z || keyCode == KeyEvent.VK_SPACE || keyCode == KeyEvent.VK_ENTER){
            player.setFiring(true);
        }   
    }

    @Override
    public void keyReleased(KeyEvent key) {
        int keyCode;
        keyCode = key.getKeyCode();
        if(keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_A){
            player.setLeft(false);
        }
        if(keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_D){
            player.setRight(false);
        }
        if(keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_W){
            player.setUp(false);
        }
        if(keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_S){
            player.setDown(false);
        }
         if(keyCode == KeyEvent.VK_Z || keyCode == KeyEvent.VK_SPACE || keyCode == KeyEvent.VK_ENTER){
            player.setFiring(false);
        }
    }
}

