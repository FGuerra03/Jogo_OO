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
    private final int FPS = 40;
    private double averageFPS;
    
    public static Player player;
    public static ArrayList<Bullet> bullets;
    public static ArrayList<Enemy> enemies;
    public static ArrayList<PowerUp> powerups;
    
    private long waveStartTimer;
    private long waveStartTimerDiff;
    private int waveNumber;
    private boolean waveStart;
    private int waveDelay = 2000;
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
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        player = new Player();
        bullets = new ArrayList<>();
        enemies = new ArrayList<>();
        powerups = new ArrayList<>();
        
        waveStartTimer = 0;
        waveStartTimerDiff = 0;
        waveStart = true;
        waveNumber = 0;
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
        
        //
        if (waveStartTimer == 0 && enemies.isEmpty()) {
            waveNumber++;
            waveStart = false;
            waveStartTimer = System.nanoTime();
        }
        else {
            waveStartTimerDiff = (System.nanoTime() - waveStartTimer) / 1000000;
            if(waveStartTimerDiff > waveDelay){
                waveStart = true;
                waveStartTimer = 0;
                waveStartTimerDiff = 0;
            }
        }
        
        //Cria inimigos
        if (waveStart && enemies.isEmpty()){
            createNewEnemies();
        }
        
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
                
                //chance for power up
                double rand = Math.random();
                
                if(rand < 0.001) powerups.add(new(1, e.getx(), e.gety()));
                else if (rand <)
                player.addScore(e.getType() + e.getRank());
                enemies.remove(l);
                l--;
            }
        }
        
        //Colisao inimigo player
        if(!player.isRecovering()){
            int px = player.getx();
            int py = player.gety();
            int pr = player.getr();
            
            for (int m = 0; m < enemies.size(); m++){
                Enemy e = enemies.get(m);
                double ex = e.getx();
                double ey = e.gety();
                double er = e.getr();
                
                double dx = px - ex;
                double dy = py - ey;
                double dist = Math.sqrt(dx * dx + dy * dy);
                
                if (dist < pr +er){
                    player.loseLife();
                }
            }
        }
        
    }

    @SuppressWarnings("empty-statement")
    private void gameRender(){
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        
        /*g.setColor(Color.GREEN);
        g.drawString("FPS: " + averageFPS, 0, 15);*/
        
        //draw player
        player.draw(g);
        
        //draw bullets 
        for (int j = 0; j < bullets.size() ; j++){
            bullets.get(j).draw(g);
        }
        int i;
        
        
        //draw enemy
         for (i = 0; i < enemies.size(); i++){
            enemies.get(i).Make(g);
        }
        
        // draw Wave
        if (waveStartTimer != 0){
            g.setFont(new Font("Sans Serif", Font.PLAIN, 18));
            String s = "-  W A V E " + waveNumber + "   -";
            int length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
            int alpha = (int) (255 * Math.sin(3.14 * waveStartTimerDiff / waveDelay));
            if (alpha > 255) alpha = 255;
            g.setColor(new Color(255, 255, 255, alpha));
            g.drawString(s, WIDTH / 2 - length / 2, HEIGHT /2);
        }
        
        // Draw vida do jogador
        for(int l = 0; l < player.getLives(); l++){
        g.setColor(Color.GREEN);
        g.fillOval(20 + (20 * i), 20, player.getr() * 2, player.getr() * 2 );
        g.setStroke(new BasicStroke(3));
        g.setColor(Color.GREEN.darker());
        g.drawOval(20 + (20 * i), 20, player.getr() * 2, player.getr() * 2 );
        g.setStroke(new BasicStroke(1));
    }
        //Draw Score
        g.setColor(Color.GREEN);
        g.setFont(new Font("Sans Serif", Font.PLAIN, 14));
        g.drawString("Score: " + player.getScore(), WIDTH - 100, 30);
    }
    
    private void gameDraw(){
        Graphics g2 = this.getGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
    }
    
    private void createNewEnemies(){
        
        enemies.clear();
        Enemy e;
        
        if(waveNumber == 1) {
            for(int i = 0; i < 4; i++) {
                enemies.add(new Enemy (1, 1));
            }
        }
        
        if(waveNumber == 2) {
            for(int i = 0; i < 8; i++) {
                enemies.add(new Enemy (1, 1));
            }
        }
        
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

