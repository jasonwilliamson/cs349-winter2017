import java.io.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.border.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

public class Canvas extends JPanel implements Observer {

    private Point lastPoint;
    private Model model;

    public Canvas(Model aModel) {

        this.model = aModel;
        model.addObserver(this);
        this.setSize(640, 480);        
        //System.out.println("size: " + this.getSize().height + " " + this.getSize().width);
       
        /**
        * Track initial MousePress
        */
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                System.out.println("size: " + getSize().height + " " + getSize().width);
                double height = getSize().height;
                double width = getSize().width;
                lastPoint = new Point(e.getX(), e.getY());
                model.beginLine(lastPoint);
                model.getCurrentLine().setHeight(height);
                model.getCurrentLine().setWidth(width);
                model.getCurrentLine().setStrokeSize(model.getStrokeSize());
                model.getCurrentLine().setColour(model.getColour());
            }
        });

        /**
        * drawline while MouseDown
        */
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Graphics g = getGraphics();
                Graphics2D g2 = (Graphics2D) g;
                g2.setStroke(new BasicStroke(model.getCurrentLine().getStrokeSize()));
                g2.setColor(model.getColour());
                g2.drawLine(lastPoint.x, lastPoint.y, e.getX(), e.getY());
                lastPoint = new Point(e.getX(), e.getY());
                model.getCurrentLine().addPoint(lastPoint);
                g2.dispose();
            }
        });

        setVisible(true);
        

    }

    /**
    * Handle redraws
    */
    protected void paintComponent(Graphics g){
        double h = this.getSize().height;
        double w = this.getSize().width;
        //System.out.println("w and h: " + w + " " + h);
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        AffineTransform oldXForm = g2.getTransform();
        int lineMax = model.getLineCount() -1;
        // Handle is doodle is drawn when slider has undon lines
        if(this.model.isMaxSet()){ lineMax = model.getMaxLine();}
        for(int i = 0; i <= lineMax; ++i){
            LinesObject tmp = model.getLineAt(i);
            int pointMax = tmp.pointsArr.size() - 1;
            if(this.model.isMaxSet()){ 
                if(i == lineMax){
                    pointMax = model.getMaxPoint();
                }
            }

            double sX = (double)(w/tmp.getWidth());
            double sY = (double)(h/tmp.getHeight());
            //System.out.println("Scale: " + sX + " " + sY);
            g2.scale(sX, sY);
            for(int j = 0; (j + 1) <= pointMax; ++j){
                Point p1 = tmp.getPoint(j);
                Point p2 = tmp.getPoint(j+1);
                //System.out.println("drawline " + p1.x + " " + p1.y + " " + p2.x+ " " + p2.y);
                g2.setStroke(new BasicStroke(tmp.getStrokeSize()));
                g2.setColor(tmp.getColour());
                g2.drawLine(p1.x, p1.y, p2.x, p2.y);    
            }
            g2.setTransform(oldXForm);
        }
        
    }

    public void update(Object observable) {
        // XXX Fill this in with the logic for updating the view when the model
        // changes.
        if(this.model.isMaxSet())
        { 
            repaint();
        }else if(this.model.shouldRepaint()){
            this.model.toggleRepaint();
            //System.out.println("REPAINT!!");
            repaint();
        }
        //System.out.println("Canvas model change");
    }
}