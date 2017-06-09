package jogo_tiro_2d;

import java.awt.EventQueue;
import javax.swing.JFrame;


public class Jogo_Tiro_2D extends JFrame {
     public GamePanel game;
     public Menu menu;
     
    public Jogo_Tiro_2D() {
       
        
        startMenu();

        setSize(400,400);
        
        setTitle("Space Combat Game");
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
    
    public void startMenu(){
        menu = new Menu();
        add(menu);
    }
    
    public static void main(String[] args) {
        
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Jogo_Tiro_2D app = new Jogo_Tiro_2D();
                app.setVisible(true);
                app.setLocationRelativeTo(null);
            }
        });
    }
}