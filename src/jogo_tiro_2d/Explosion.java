package jogo_tiro_2d;
import java.awt.*;

public class Explosion {
	
    //Atributos
    private double x;
    private double y;
    private int r;
    private int maxRadius;
	
    // CONSTRUCTOR
    public Explosion(double x, double y, int r, int max) {
	this.x = x;
	this.y = y;
	this.r = r;
	maxRadius = max;
    }
	
    public boolean update() {
	r += 2;
	if(r >= maxRadius) {
            return true;
	}
	return false;
    }
	
    public void draw(Graphics2D g) {
	//Tomate
        g.setColor(new Color(255, 99, 71));
	g.setStroke(new BasicStroke(3));
	g.drawOval((int) (x - r), (int) (y - r), 2 * r, 2 * r);
	g.setStroke(new BasicStroke(1));
    }
	
}