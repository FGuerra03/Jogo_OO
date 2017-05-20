package jogo_tiro_2d;
import javax.swing.JFrame;
public class Jogo_Tiro_2D {
    public static void main(String[] args) {
        
        JFrame window = new JFrame("Teste 001");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        window.setContentPane(new GamePanel());
        
        window.pack();
        window.setVisible(true);
        
    }
    
}
