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

public class ColourBtn extends JButton {
    private Color colour;

    ColourBtn(Color colour){
        super.setIcon(new ColourIcon(colour));
        setColour(colour);
    }

    public void setColour(Color colour) {
        this.colour = colour;
    }

    public Color getColour() {
        return this.colour;
    }
}