import java.io.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.border.*;

public class LeftView extends JComponent implements Observer {
    private Model model;
    private Color[] colors;
    private final int COLOR_COUNT = 12;
    private JPanel gridPane;
    private JPanel currentColorPane;
    private StrokeBtn smallStrokeBtn;
    private StrokeBtn medStrokeBtn;
    private StrokeBtn lrgStrokeBtn;
    private JButton colorPickerBtn;

    public LeftView(Model aModel) {
        super();
        this.setLayout(new BoxLayout(this, 1));
        this.initColors();
        
        //Setup colour picker
        gridPane = new JPanel();
        GridLayout grid = new GridLayout(6, 2);
        gridPane.setLayout(grid);
        this.initColorGrid();
        
        //Setup colour indicator
        Box vertBox = Box.createVerticalBox();
        vertBox.add(gridPane);
        vertBox.add(Box.createVerticalStrut(5));
        currentColorPane = new JPanel();
		currentColorPane.setSize(50,50);
		currentColorPane.setBackground(aModel.getColour());
		vertBox.add(currentColorPane);
        this.add(vertBox);
        this.add(Box.createVerticalGlue());
        this.add(Box.createVerticalStrut(10));

        //Setup stroke thickness buttons
        Box boxStroke = Box.createVerticalBox();
        smallStrokeBtn = new StrokeBtn("small");
        smallStrokeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        boxStroke.add(smallStrokeBtn);
        boxStroke.add(Box.createVerticalStrut(5));

        medStrokeBtn = new StrokeBtn("medium");
        medStrokeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        boxStroke.add(medStrokeBtn);
        boxStroke.add(Box.createVerticalStrut(5));

        lrgStrokeBtn = new StrokeBtn("large");
        lrgStrokeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        boxStroke.add(lrgStrokeBtn);
        this.add(boxStroke);

        StrokeBtn[] strokeBtns = new StrokeBtn[3];
        strokeBtns[0] = smallStrokeBtn;
        strokeBtns[1] = medStrokeBtn;
        strokeBtns[2] = lrgStrokeBtn;

        for(int i = 0; i < strokeBtns.length; ++i){
            strokeBtns[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Object source = e.getSource();
                    StrokeBtn tmp = (StrokeBtn) source;
                    model.setStroke(tmp.getStrokeSize());
                }
            });
        }

        //Setup up colorPicker
        this.add(Box.createVerticalStrut(10));
        colorPickerBtn = new JButton("Colour Picker");
        colorPickerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(colorPickerBtn);
        colorPickerBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Color newColour = JColorChooser.showDialog(
                    null, "A2 ColourChooser", null);
                if ( newColour != null){
                    model.setColour(newColour);
                }
            }
        });

        // Hook up this observer so that it will be notified when the model
        // changes.
        this.model = aModel;
        model.addObserver(this);
        //System.out.println("LeftView ready!");
        setVisible(true);
    }

    public void update(Object observable) {
        // XXX Fill this in with the logic for updating the view when the model
        // changes.
        this.currentColorPane.setBackground(this.model.getColour());
        System.out.println("LeftView Model changed!");
    }

    private void initColors() {
        colors = new Color[12];
        colors[0] = Color.black;
        colors[1] = Color.blue;
        colors[2] = Color.cyan;
        colors[3] = Color.darkGray;
        colors[4] = Color.gray;
        colors[5] = Color.green;
        colors[6] = Color.yellow;
        colors[7] = Color.lightGray;
        colors[8] = Color.magenta;
        colors[9] = Color.orange;
        colors[10] = Color.pink;
        colors[11] = Color.red;
    }

    private void initColorGrid() {  
        ColourBtn tmpBtn;
        for(int i = 0; i < COLOR_COUNT; ++i) {
            tmpBtn = new ColourBtn(colors[i]);
            gridPane.add(tmpBtn);
            tmpBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Object source = e.getSource();
                    ColourBtn tmp = (ColourBtn) source;
                    model.setColour(tmp.getColour());
                }
            });
        }
    }
}
