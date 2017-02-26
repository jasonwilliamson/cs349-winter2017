import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

class StrokeWidthIcon implements Icon {
    private int height;
    private final String SMALL = "small";
    private final String MEDIUM = "medium";
    private final String LARGE = "large";
    private int borderWidth;

    StrokeWidthIcon(String size) {
        switch(size){
            case SMALL:
                this.height = 5;
                break;
            case MEDIUM:
                this.height = 10;
                break;
            case LARGE:
                this.height = 15;
                break;
        }
    }

    public int getIconWidth() {
        return 75;
    }

    public int getIconHeight() {
        return this.height;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.setColor(Color.black);
        g.fillRect(x, y, getIconWidth(), getIconHeight());
    }
}