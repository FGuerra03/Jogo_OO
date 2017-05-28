package jogo_tiro_2d;

import java.awt. *;
public class Enemy extends GamePanel {
    
    //Atributos
    private double x;
    private double y;
    private int r;
    
    private double dx;
    private double dy;
    private double rad;
    private double speed;
    
    private int health;
    private int type;
    private int rank;
    
    private Color color1;
    
    private boolean ready;
    private boolean dead;
    
    //Construtor
    public Enemy (int type, int rank){
        this.type = type;
        this.rank = rank;
        
        //Inimigo padrao
        if (type == 1){
            color1 = Color.BLUE;
            if (rank == 1){
                speed = 2;
                 rad = 5;
                health = 1;
            }
        }
        
        x = Math.random() * GamePanel.WIDTH /2 + GamePanel.WIDTH / 4;
        y = -r;
        
        double angle = Math.random() * 140 + 20;
        rad = Math.toRadians(angle);
        
        dx = Math.cos(rad) * speed;
        dy = Math.sin(rad) * speed;
        
        ready = false;
        dead = false;
    }
    
    //Funcoes 
    public double getx() { return x; }
    public double gety() { return y; }
    public double getr() { return r; }
    public boolean isDead(){return dead;}
    public void hit () {
        health --;
        if (health <= 0){
            dead = true;
        }
    }
    
    public void update(){
        x += dx;
        y += dy;
        
        if(!ready){
            if(x > r && r < GamePanel.WIDTH - r && y > r && y < GamePanel.HEIGHT -r){
                ready = true;
            }
            
            if(x < r && dx < 0) dx = -dx;
            if(y < r && dy < 0) dy = -dy;
            if(x > GamePanel.WIDTH - r && dx > 0) dx = -dx;
            if(y > GamePanel.HEIGHT - r && dy > 0) dy = -dy;
            
        }
        
    }
  
    public void draw(Graphics2D h){
        
        h.setColor(color1);
        h.fillOval((int) (x-r), (int) (y - r), 2 * r, 2 * r);
        
        h.setStroke(new BasicStroke(3));
        h.setColor(color1.darker());
        h.drawOval((int) (x-r), (int) (y - r), 2 * r, 2 * r);
        h.setStroke(new BasicStroke(1));
    }
}
