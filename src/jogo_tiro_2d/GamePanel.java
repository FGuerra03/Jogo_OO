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
    public static ArrayList<Explosion> explosions;
    public static ArrayList<Text> texts;
    
    private long waveStartTimer;
    private long waveStartTimerDiff;
    private int waveNumber;
    private boolean waveStart;
    private int waveDelay = 2000;
    
    private long slowDownTimer;
    private long slowDownTimerDiff;
    private int slowDownLength = 6000;
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
        explosions = new ArrayList <>();
        texts = new ArrayList <>();
        
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
        
        //Atualizacao powerup
        for(int i = 0; i < powerups.size(); i++){
            boolean remove = powerups.get(i).update();
            if(remove){
                powerups.remove(i);
                i--;
            }
        }
        
        //explosion update
        for (int i = 0; i < explosion.size(); i++){
            boolean remove = explosions.get(i).update();
            if(remove){
                explosions.remove(i);
                i--;
            }
        }
        
        //atualizacao text
        for (int i = 0; i < text.size(); i++){
            boolean remove = texts.get(i).update();
            if(remove){
                texts.remove(i);
                i--;
            }
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
                
                if(rand < 0.001) powerups.add(new PowerUp(1, e.getx(), e.gety()));
                else if (rand < 0.020) powerups.add(new PowerUp(3, e.getx(), e.gety()));
                else if (rand < 0.120) powerups.add(new PowerUp(2, e.getx(), e.gety()));
                else if (rand < 0.130) powerups.add(new PowerUp(4, e.getx(), e.gety()));
                else powerups.add(new PowerUp(4, e.getx(), e.gety()));
                player.addScore(e.getType() + e.getRank());
                enemies.remove(l);
                l--;
                
                e.explode();
                e.explosions.add(new Explosion(e.getx(), e.gety(), e.getr(), e.getr() + 20));
                
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
        
        //Colisao do player com o PowerUp
        int px = player.getx();
        int py = player.gety();
        int pr = player.getr();
        for (int i = 0; i < powerups.size(); i++){
            PowerUp p = powerups.get(i);
            double x = p.getx();
            double y = p.gety();
            double r = p.getr();
            double dx = px - x;
            double dy = py - y;
            double dist = Math.sqrt(dx * dx + dy * dy);
            //Pego o bonus
            if(dist < pr + r) {
                
                int type = p.getType();
                
                if (type == 1){
                    player.gainLife();
                    texts.add(new Text(player.getx(), player.gety(), 2000, "Extra Life"));
                }
                
                if (type == 2) {
                    player.increasePower(1);
                    texts.add(new Text(player.getx(), player.gety(), 2000, "Power"));
                }
                if (type == 3){
                    player.increasePower(2);
                    texts.add(new Text(player.getx(), player.gety(), 2000, "Double Power"));
                }
                if(type == 4) {
                    slowDownTimer = System.nanoTime();
                    for (int j = 0; j < enemies.size(); j++ ){
                        enemies.get(j).setSlow(true);
                    }
                    texts.add(new Text(player.getx(), player.gety(), 2000, "Slow Down"));
                }
                
                powerups.remove(i);
                i--;
            }
        
        }
    
        //PowerUp 4 update
        if(slowDownTimer != 0) {
            slowDownTimerDiff = (System.nanoTime() - slowDownTimer) / 1000000;
            if(slowDonwTimerDiff > slowDownLength){
                slowDownTimer = 0;
                for (int j = 0; j < enemies.size(); j++ ){
                        enemies.get(j).setSlow(false);
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
         for (int i = 0; i < enemies.size(); i++){
            enemies.get(i).draw(g);
        }
        
        //draw powerups
        for (int i = 0; i < powerups.size(); i++){
            powerups.get(i).draw(g);
        }
        
        //draw explosions
        for (int i = 0; i < explosions.size(); i++){
            explosions.get(i).draw(g);
        }
        
        //draw text
        for(int i = 0; i < texts.size(); i++){
            texts.get(i).draw(g);
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
        g.fillOval(20 + (20 * l), 20, player.getr() * 2, player.getr() * 2 );
        g.setStroke(new BasicStroke(3));
        g.setColor(Color.GREEN.darker());
        g.drawOval(20 + (20 * l), 20, player.getr() * 2, player.getr() * 2 );
        g.setStroke(new BasicStroke(1));
    }
        //draw player power
        g.setColor(Color.gray);
        g.fillRect(20,40, player.getPower() * 8,8);
        g.setColor(Color.gray.darker());
        g.setStroke(new BasicStroke (2));
        
        for(int i = 0; i < player.getRequiredPower(); i++ ){
            g.drawRect(20 + 8 * i, 40, 8, 8);
            
        }
        g.setStroke(new BasicStroke (1));
        
        
        //Draw Score
        g.setColor(Color.GREEN);
        g.setFont(new Font("Sans Serif", Font.PLAIN, 14));
        g.drawString("Score: " + player.getScore(), WIDTH - 100, 30);
        
        //Draw barra slowtimer
        if(slowDownTimer != 0){
            g.setColor(Color.WHITE);
            g.drawRect(20, 60, 100, 8);
            g.fillRect(20, 60, (int) ((int) 100 - 100.0 * slowDownTimerDiff / slowDownLength), 8);
            
        }
        
        //draw slowdown screen
        if(slowDownTimer != 0){
            g.setColor(new Color (255, 255, 255, 64));
            g.fillRect(0,0, WIDTH, HEIGHT);
        }
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
            for(int i = 0; i < 4; i++) {
                enemies.add(new Enemy (1, 1));
            }
            enemies.add(new Enemy(1, 2));
            enemies.add(new Enemy(1, 2));
        }
        
        if(waveNumber == 3) {
            enemies.add(new Enemy(1, 3));
            enemies.add(new Enemy(1, 3));
            enemies.add(new Enemy(1, 4));
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

