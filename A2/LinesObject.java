//import javax.swing.*;
import java.util.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.border.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.Color;
import java.awt.Point;
import java.io.Serializable;

 /**
 * LinesObject, represents a single line in the doodle
 */
public class LinesObject implements Serializable{

    private Color colour;
    private int strokeSize;
    public List<Point> pointsArr;
    private double height;
    private double width;


    public LinesObject() {
        pointsArr = new ArrayList<Point>();
    }

    public void addPoint(Point p){
        pointsArr.add(p);
    }

    public Point getPoint(int i){
        return pointsArr.get(i);
    }

    public void setColour(Color colour){
        this.colour = colour;
    }

    public Color getColour(){
        return this.colour;
    }

    public void setStrokeSize(int size){
        this.strokeSize = size;
    }

    public int getStrokeSize(){
        return this.strokeSize;
    }

    public void setHeight(double h){
        this.height = h;
    }

    public void setWidth(double w){
        this.width = w;
    }

    public double getHeight(){
        return this.height;
    }

    public double getWidth(){
        return this.width;
    }

    public int getPointCount(){
        return this.pointsArr.size();
    }

}