// ====================================================
// Jason Williamson (20552360)
// CS 349 Winter 2017
// Assignment 04
// File: Canvas.java
// ====================================================
//

import java.io.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.JScrollPane;
import java.util.List;
public class Canvas extends JPanel implements Observer{

    public ModelYouTube model;
    static final private String STAR_1 = "star_1";
    static final private String STAR_2 = "star_2";
    static final private String STAR_3 = "star_3";
    static final private String STAR_4 = "star_4";
    static final private String STAR_5 = "star_5";
    public int filter = 0;
    public Boolean gridView = true;
    public List<VideoPanel>videoItems;

    public Canvas(ModelYouTube model){
        this.model = model;
        model.addObserver(this);
        videoItems = new ArrayList<VideoPanel>();
    }

   public void updateView(){
        clearDisplay();
        if(gridView){
            displayGridView();
        }else{
            displayListView();
        }
    }

    //display Video entries in a list order...
    public void displayListView(){
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        JPanel listViewWrapper = new JPanel();
        listViewWrapper.setLayout(new BoxLayout(listViewWrapper, BoxLayout.Y_AXIS));
        
            System.out.println("videoItems.isEmpty(): " + videoItems.isEmpty());
            if(videoItems.isEmpty()){
                for(int i = 0; i < 25; i++){
                    VideoPanel vp = new VideoPanel(model, i);
                    videoItems.add(vp);
                }
            }
            for(int i = 0; i < 25; i++){
                VideoPanel vp = (VideoPanel) videoItems.get(i);
                if(filter != 0){
                    System.out.println("ratings: vp: " + vp.starRating + " filter: " + filter);
                    if( vp.starRating < filter){
                        continue;
                    }
                }
                listViewWrapper.add(vp);
            }
        this.add(listViewWrapper);
        revalidate();
        repaint();
    }

    public void displayGridView(){
            System.out.println("videoItems.isEmpty(): " + videoItems.isEmpty());
            if(videoItems.isEmpty()){ //new search so ratings will be zero...
                for(int i = 0; i < 25; i++){
                    VideoPanel vp = new VideoPanel(model, i);
                    videoItems.add(vp);
                }
            }

            for(int i = 0; i < 25; i++){
                VideoPanel vp = (VideoPanel) videoItems.get(i);
                if(filter != 0){
                    System.out.println("ratings: vp: " + vp.starRating + " filter: " + filter);
                    if( vp.starRating < filter){
                        continue;
                    }
                }
                JPanel panelHolder = new JPanel();
                panelHolder.add(vp);
                this.add(panelHolder);
                System.out.println("adding vp: " + i);
            }
    }


    public void clearDisplay(){
        this.removeAll();
        repaint();
    }

    public void clearVideoData(){
        this.videoItems.clear();
    }

    public void setModel(ModelYouTube model){
        clearVideoData();
        this.model = model;
        updateView();
        System.out.println("Canvas model set new");
    }


    public void update(Object observable) {
        this.model = (ModelYouTube) observable;
        System.out.println("Canvas model change");
        updateView();
    }


}