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

public class StrokeBtn extends JButton {
    private int strokeSize;
    private final String SMALL = "small";
    private final String MEDIUM = "medium";
    private final String LARGE = "large";

    public StrokeBtn(String size){

        int strokeSize = 0;
        switch(size){
            case SMALL:
                strokeSize = 1;
                break;
            case MEDIUM:
                strokeSize = 3;
                break;
            case LARGE:
                strokeSize = 7;
                break;
        }
        setStrokeSize(strokeSize);
        super.setIcon(new StrokeWidthIcon(size));
    }

    public void setStrokeSize(int strokeSize){
        this.strokeSize = strokeSize;
    }

    public int getStrokeSize(){
        return this.strokeSize;
    }
}