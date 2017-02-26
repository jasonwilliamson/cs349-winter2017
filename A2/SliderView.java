import java.io.*;
//import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.border.*;
import javax.swing.event.*;
import java.lang.Math;

public class SliderView extends JComponent implements Observer {
    private Model model;
    private JButton playBtn;
    private JButton startBtn;
    private JButton endBtn;
    private JSlider undoSlider;
    private int count;
    private Boolean animating = false;

    public SliderView(Model aModel) {
        super();

        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        playBtn = new JButton("Play");
        playBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                animateDrawing();
            }
        });
        this.add(playBtn);
    
        this.undoSlider = new JSlider();
        this.add(undoSlider);
        this.undoSlider.setEnabled(false);
        this.undoSlider.addChangeListener(new UndoController());
    
        startBtn = new JButton("Start");
        this.add(startBtn);
        startBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                undoSlider.setValue(0);
            }
        });

        endBtn = new JButton("End");
        this.add(endBtn);
        endBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                undoSlider.setValue(undoSlider.getMaximum());
                model.forceRepaint();
                 System.out.println("@@@");
            }
        });

        playBtn.setEnabled(false);
        startBtn.setEnabled(false);
        endBtn.setEnabled(false);

        this.model = aModel;
        model.addObserver(this);
    }

    private void animateDrawing(){
        //for each line animated its drawing , make one second long...
        undoSlider.setValue(0);
        int valPerLine = (100/model.getLineCount());
        int tick = 1000/ valPerLine;
        Timer animation = new Timer(tick, new ActionListener(){
            public void actionPerformed(ActionEvent e){
                if(undoSlider.getMaximum() != count){
                     ++count;
                     undoSlider.setValue(undoSlider.getValue() + 1);
                } else {
                    ((Timer) e.getSource()).stop();
                }
            }
        });
        animation.start();
        count = 0;
    }

    public void update(Object observable) {
        // XXX Fill this in with the logic for updating the view when the model
        // changes.
        System.out.println("update slider!");
        int lineCount = this.model.getLineCount();
        System.out.println("lineCount: " + lineCount);

        if(0 == lineCount){
            playBtn.setEnabled(false);
            startBtn.setEnabled(false);
            endBtn.setEnabled(false);
            undoSlider.setEnabled(false);
            this.undoSlider.setPaintTicks(false);
        }else{
            this.undoSlider.setMajorTickSpacing(100/lineCount);
            this.undoSlider.setMinorTickSpacing((100/lineCount)/10);
            playBtn.setEnabled(true);
            startBtn.setEnabled(true);
            endBtn.setEnabled(true);
        }

        if (lineCount > 0 && !undoSlider.isEnabled()){
            this.undoSlider.setEnabled(true);
            this.undoSlider.setMinimum(0);
            this.undoSlider.setMaximum(100);
            this.undoSlider.setPaintTicks(true);
            this.undoSlider.setMajorTickSpacing(100/lineCount);
            this.undoSlider.setMinorTickSpacing((100/lineCount)/10);
            this.undoSlider.setValue(100);
        }else if(model.isSliderAdjusted()){
            this.undoSlider.setMinimum(0);
            this.undoSlider.setMaximum(100);
            this.undoSlider.setPaintTicks(true);
            this.undoSlider.setMajorTickSpacing(100/lineCount);
            this.undoSlider.setMinorTickSpacing((100/lineCount)/10);
            this.undoSlider.setValue(100);
            model.resetSliderAdjusted();
        }
    }

    private class UndoController implements ChangeListener {
        public void stateChanged(ChangeEvent e) {

            double value = undoSlider.getValue();
            //System.out.println("VALUE: " + value);
            //System.out.println("undoController:");
            int valPerLine = (100/model.getLineCount());
            //System.out.println("valPerLine: " + valPerLine);
            double minorVal = value % valPerLine;
            double framePercent = minorVal / valPerLine;
            double line = Math.floor(value / valPerLine);

            //make sure line is not line.size()
            if(line == model.getLineCount()){ 
                model.resetMax();
                return;
            }
            int pntCount = model.getLineAt((int)line).getPointCount();
            double upToPnt = Math.floor( pntCount * framePercent);
            //System.out.println("slider value: " + value);
            model.setMaxLinePoint((int)line, (int)upToPnt);
		}
    }
}