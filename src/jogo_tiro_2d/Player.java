package jogo_tiro_2d;

import java.awt.*;
public class Player extends GamePanel {
    //Atrubutos
    private int x;
    private int y;
    private int r;
    
    private int dx;
    private int dy;
    private int speed;
    
    private boolean left;
    private boolean right;
    private boolean up;
    private boolean down;
    
    private boolean firing;
    private long firingTimer;
    private long firingDelay;
    
    private int lives;
    
    private Color color1;
    private Color color2;

    //Construtor
    public Player() {
        x = GamePanel.WIDTH / 2;
        y = GamePanel.HEIGHT / 2;
        r = 5;
        
        dx = 0;
        dy = 0;
        speed = 5;
        
        lives = 3;

        color1 = Color.green;
        color2 = Color.RED;
        
        firing = false;
        firingTimer = System.nanoTime();
        firingDelay = 200;
    }
    
    //Funcoes
    
    public void setLeft(boolean b) {left = b; }
    public void setRight(boolean b) {right = b; }
    public void setUp(boolean b) {up = b; }
    public void setDown(boolean b) {down = b; }
    
    
    public void update(){
        
        if(left) {
            dx = -speed;
        }
        if(right) {
            dx = speed;
        }
        if (down) {
            dy = speed;
        }
        if (up){
            dy = -speed;
        }
        
        x += dx;
        y += dy;
        
        if (x < r ) x=r;
        if (y < r) y=r;
        if (x > GamePanel.WIDTH - r) x = GamePanel.WIDTH - r;
        if (y > GamePanel.HEIGHT - r) y = GamePanel.HEIGHT - r;
        
        dx = 0;
        dy = 0;
        
        if(firing) {
            long elapsed = System.nanoTime() - firingTimer / 1000000;
            if (elapsed > firingDelay){
                boolean add = GamePanel.bullets.add(new Bullet(270, x, y));
                firingTimer = System.nanoTime();
            }
        }
    }
    
    public void draw(Graphics2D g){
        g.setColor(color1);
        g.fillOval(x - r, y - r, 2 * r, 2 * r);
        
        g.setStroke(new BasicStroke(3));
        g.setColor(color1.darker());
        g.drawOval(x - r, y - r, 2 * r, 2 * r);
        g.setStroke(new BasicStroke(1));
    }

    void setFiring(boolean b) {
        firing = b;
    }
}
