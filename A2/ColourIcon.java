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

class ColourIcon implements Icon {
    private Color colour;

    private int borderWidth;

    ColourIcon(Color colour) {
        this.colour = colour;
    }

    public int getIconWidth() {
        return 25;
    }

    public int getIconHeight() {
        return 25;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.setColor(this.colour);
        g.fillRect(x, y, getIconWidth(), getIconHeight());
    }
}