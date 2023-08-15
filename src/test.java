import javax.swing.*;
import java.awt.*;

public class test{
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("GIF Example");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Load the GIF ImageIcon
            ImageIcon gifIcon = new ImageIcon("C:\\Users\\hetro\\Downloads\\infinity-loading-5483026-45841-unscreen (1).gif");
            
            JLabel gifLabel = new JLabel(gifIcon); // Create JLabel with ImageIcon

            frame.add(gifLabel, BorderLayout.CENTER);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
