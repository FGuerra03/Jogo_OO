package jogo_tiro_2d;
import java.awt. *;

public class Bullet extends GamePanel {
    //Atributos
    private double x;
    private double y;
    private int r; 
    
    private double dx;
    private double dy;
    private double rad;
    private double speed;
    
    private Color color1; 

    Bullet(double angle, int x, int y) {
        this.y = y;
        r = 2;
        
        rad = Math.toRadians(angle);
        speed = 15;
        dx = Math.cos(rad) * speed;
        dy = Math.sin(rad) * speed;
         
        
        color1 = Color.BLUE;
    }
    

    public boolean update (){
        x += dx;
        y += dy;
        
        return x < -r || x > GamePanel.WIDTH + r||
                y < -r || y > GamePanel.HEIGHT +r;
    }
    
    public void draw(Graphics2D g) {
        g.setColor(color1);
        g.fillOval((int)(x - r),(int) (y - r), 2*r, 2*r);
    }
}