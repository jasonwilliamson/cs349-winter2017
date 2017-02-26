
import java.util.*;
import java.awt.Color;
import java.awt.Point;
import java.io.Serializable;
//import java.awt.List;
//import java.awt.*;

public class Model implements Serializable{
    /** The observers that are watching this model for changes. */
    private List<Observer> observers;
    private Color colour;
    private int strokeSize;
    public List<LinesObject> linesArr;
    private int lineCount = 0;
    private int maxLine;
    private int maxPoint;
    private Boolean maxSet = false;
    private Boolean sliderAdjusted = false;
    private Boolean repaint = false;

    /**
     * Create a new model.
     */
    public Model() {
        this.observers = new ArrayList();
        setColour(Color.black);
        linesArr = new ArrayList<LinesObject>();
    }

    /**
     * Add an observer to be notified when this model changes.
     */
    public void addObserver(Observer observer) {
        this.observers.add(observer);
    }

    /**
     * Remove an observer from this model.
     */
    public void removeObserver(Observer observer) {
        this.observers.remove(observer);
    }

    /**
     * Notify all observers that the model has changed.
     */
    public void notifyObservers() {
        for (Observer observer: this.observers) {
            observer.update(this);
        }
    }

    public void setColour(Color colour){
        //To set the color
       this.colour = colour;
        notifyObservers();
    }

    public Color getColour() {
        return this.colour;
    }

    public void setStroke(int strokeSize){
        this.strokeSize = strokeSize;
        notifyObservers();
    }

    public int getStrokeSize(){
        return this.strokeSize;
    }

    public int getLineCount(){
        return this.linesArr.size();
    }

    public void setLineCount(int val){
        this.lineCount = val;
    }

    public LinesObject getCurrentLine(){
        int index = this.linesArr.size() -1;
        return this.linesArr.get(index);
    }

    /**
     * Add new a line to the doodle
     */
    public void beginLine(Point p){
        LinesObject newLine = new LinesObject();
        newLine.setColour(getColour());
        newLine.addPoint(p);

        if(isMaxSet()){
            //get line, check if points to erase match it's length
            // if so erase whole line
            // otherwise erase points
            LinesObject lobj = linesArr.get(maxLine);
            if(maxPoint == (lobj.pointsArr.size() -1)){
                //System.out.println("remove the LINE! ");
                for(int i = linesArr.size() -1; i >= maxLine; --i){
                    linesArr.remove(i);
                }
            }else{
                for(int i = linesArr.size() -1; i >= maxLine; --i){
                    if(maxLine == i){
                        //System.out.println("maxLine == i: ");
                        lobj = linesArr.get(i);
                        for (int j = lobj.pointsArr.size() -1; j >= maxPoint; --j){
                            linesArr.get(i).pointsArr.remove(j);
                        }
                    }else{
                        linesArr.remove(i);
                    }
                }
            }
            resetMax();
            sliderAdjusted = true;
        }
        
        this.linesArr.add(newLine);
        notifyObservers();
    }

    public LinesObject getLineAt(int index){
        return this.linesArr.get(index);
    }

    public void setMaxLinePoint(int l, int p){
        this.maxLine = l;
        this.maxPoint = p;
        this.maxSet = true;
        notifyObservers();
    }

    public void resetMax(){
        this.maxSet = false;
    }

    public int getMaxLine(){
        return this.maxLine;
    }

    public int getMaxPoint(){
        return this.maxPoint;
    }

    public Boolean isMaxSet(){
        return maxSet;
    }

    public Boolean isSliderAdjusted(){
        return sliderAdjusted;
    }

    public void resetSliderAdjusted(){
        sliderAdjusted = false;
    }

    public void forceRepaint(){
        repaint = true;
        notifyObservers();
        System.out.println("forceRepaint");
    }

    public void toggleRepaint(){
        repaint =! repaint;
    }

    public Boolean shouldRepaint(){
        //System.out.println("shouldRepaint: YES");
        return this.repaint;
    }

    public void clearLines(){
        linesArr.clear();
        this.forceRepaint();
    }

    
}
