// ====================================================
// Jason Williamson (20552360)
// CS 349 Winter 2017
// Assignment 04
// File: VideoPanel.java
// ====================================================
//

import java.io.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.JScrollPane;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.Calendar;
import java.net.URL;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URI;
import java.io.IOException;
public class VideoPanel extends JPanel implements ActionListener, Observer{

    static final private String STAR_1 = "star_1";
    static final private String STAR_2 = "star_2";
    static final private String STAR_3 = "star_3";
    static final private String STAR_4 = "star_4";
    static final private String STAR_5 = "star_5";
    static final private String VIDEO_CLICK = "video_click";
    private VideoDataType vdt;
    private JToggleButton starOneBtn;
    private JToggleButton starTwoBtn;
    private JToggleButton starThreeBtn;
    private JToggleButton starFourBtn;
    private JToggleButton starFiveBtn;
    public int starRating = 0;
    private ModelYouTube model;
    private int selfIndex; //cheap workaround 
    private String defualtImage = "images/default.jpg";
    private ImageIcon icon;
    private JButton videoBtn;

    public VideoPanel(ModelYouTube model, int index){
        this.model = model;
        this.vdt = model.vidList.get(index);
        this.selfIndex = index;
        init();

        // (format http://youtube.com/watch?v=/<VIDEO_ID>/) 
    }

    public VideoPanel init(){
        JPanel panelWrapper = new JPanel();
        panelWrapper.setMaximumSize(new Dimension(400,130));
        panelWrapper.setMinimumSize(new Dimension(400,130));
        panelWrapper.setPreferredSize(new Dimension(400,130));
        panelWrapper.setLayout(new BoxLayout(panelWrapper, BoxLayout.Y_AXIS));

        JPanel ratingWrapper = new JPanel();
        ratingWrapper.setLayout(new BoxLayout(ratingWrapper, BoxLayout.X_AXIS));

        starOneBtn = makeStarButton("star","star_gold",STAR_1);
        ratingWrapper.add(starOneBtn);
        starTwoBtn = makeStarButton("star","star_gold",STAR_2);
        ratingWrapper.add(starTwoBtn);
        starThreeBtn = makeStarButton("star","star_gold",STAR_3);
        ratingWrapper.add(starThreeBtn);
        starFourBtn = makeStarButton("star","star_gold",STAR_4);
        ratingWrapper.add(starFourBtn);
        starFiveBtn = makeStarButton("star","star_gold",STAR_5);
        ratingWrapper.add(starFiveBtn);

        panelWrapper.add(ratingWrapper);

        JPanel videoWrapper = new JPanel();
        videoWrapper.setLayout(new BoxLayout(videoWrapper, BoxLayout.X_AXIS));

        icon = createImageIcon(defualtImage);
        videoBtn = new JButton(icon);
        videoBtn.setActionCommand(VIDEO_CLICK);
        videoBtn.addActionListener(this);
        String iconURL = this.vdt.thumbnail;
        loadIconImage(icon, iconURL);
        

        JPanel videoInfoWrapper = new JPanel();
        videoInfoWrapper.setLayout(new BoxLayout(videoInfoWrapper, BoxLayout.Y_AXIS));

        //VIDEO INFO
        String title = this.vdt.title;
        JLabel videoTitle = new JLabel(title);
        videoTitle.setMinimumSize(new Dimension(200,24));
        videoTitle.setMaximumSize(new Dimension(200,24));
        videoTitle.setPreferredSize(new Dimension(200,24));
        videoTitle.setHorizontalTextPosition(SwingConstants.LEFT);

        String date = getPublishedDate(this.vdt.getPublishedSeconds());
        JLabel videoDate = new JLabel(date);
        videoDate.setHorizontalTextPosition(SwingConstants.LEFT);
        videoInfoWrapper.add(videoTitle);
        videoInfoWrapper.add(videoDate);

        //set star rating here...
        setStarRating(this.vdt.starRating);

        videoWrapper.add(videoBtn);
        videoWrapper.add(videoInfoWrapper);
        panelWrapper.add(videoWrapper);

        this.add(panelWrapper);
        return this;
    }

    private String getPublishedDate(long publishMillis){
        long nowMillis = System.currentTimeMillis();
        long timeMillis = nowMillis - publishMillis;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeMillis);
        int year = calendar.get(Calendar.YEAR);
        year = year - 1970;
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        String formatedTime;
        if(year > 0){
            if(1 == year){
                formatedTime = year + " year ago";
            }else{
                formatedTime = year + " years ago";
            }
        }else if(month > 0){
            if(1 == month){
                formatedTime = month + " month ago";
            }else{
                formatedTime = month + " months ago";
            }
        }else{
            if(1 == day){
                formatedTime = day + " day ago";
            }else{
                formatedTime = day + " days ago";
            }
        }
        return formatedTime;
    }
    /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path) {
        
        if (path != null) {
            return new ImageIcon(path);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    private void loadIconImage(ImageIcon icon, String path){
        SwingUtilities.invokeLater(new Runnable() {
            public void run(){
                try{
                    URL url = new URL(path);
                    BufferedImage img = ImageIO.read(url);
                    icon.setImage(img);
                    videoBtn.setIcon(icon);
                    videoBtn.updateUI();
                } catch (MalformedURLException e) {
                  e.printStackTrace();
                } catch (IOException e) {
                  e.printStackTrace();
                } 
            }
        });
    }

    private JToggleButton makeStarButton(String unselectedImageName, 
                                           String selectedImageName,
                                           String actionCommand){

        String imgUnSelectedLocation = "icons/" + unselectedImageName + ".png";
        String imgSelectedLocation = "icons/" + selectedImageName + ".png";
        JToggleButton button = new JToggleButton();
        button.setBorder(null);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        
        button.setActionCommand(actionCommand);
        button.addActionListener(this);

        if((imgUnSelectedLocation != null) && (imgSelectedLocation != null)){
            button.setIcon(new ImageIcon(imgUnSelectedLocation));
            button.setSelectedIcon(new ImageIcon(imgSelectedLocation));
        }else{
            System.out.println("Icons not found " + imgUnSelectedLocation + " " + imgSelectedLocation);
        }
        return button;
    }

    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        System.out.println("actionPerformed: " + cmd);
        if(STAR_1.equals(cmd)){
            if(!starOneBtn.isSelected()){
                starOneBtn.setSelected(false);
                starTwoBtn.setSelected(false);
                starThreeBtn.setSelected(false);
                starFourBtn.setSelected(false);
                starFiveBtn.setSelected(false);
                starRating = 0; //zero
            }else{
                starOneBtn.setSelected(true);
                starRating = 1; //one
            }
            vdt.setStarRating(starRating);
        }else if(STAR_2.equals(cmd)){
            if(!starTwoBtn.isSelected()){
                starTwoBtn.setSelected(true);
                starThreeBtn.setSelected(false);
                starFourBtn.setSelected(false);
                starFiveBtn.setSelected(false);
                starRating = 2; //two
            }else{
                starOneBtn.setSelected(true);
                starTwoBtn.setSelected(true);
                starRating = 2; //two
            }
            vdt.setStarRating(starRating);
        }else if(STAR_3.equals(cmd)){
            if(!starThreeBtn.isSelected()){
                starThreeBtn.setSelected(true);
                starFourBtn.setSelected(false);
                starFiveBtn.setSelected(false);
                starRating = 3; //three
            }else{
                starOneBtn.setSelected(true);
                starTwoBtn.setSelected(true);
                starThreeBtn.setSelected(true);
                starRating = 3; //three
            }
            vdt.setStarRating(starRating);
        }else if(STAR_4.equals(cmd)){
            if(!starFourBtn.isSelected()){
                starFourBtn.setSelected(true);
                starFiveBtn.setSelected(false);
                starRating = 4; //four
            }else{
                starOneBtn.setSelected(true);
                starTwoBtn.setSelected(true);
                starThreeBtn.setSelected(true);
                starFourBtn.setSelected(true);
                starRating = 4; //four
            }
            vdt.setStarRating(starRating);
        }else if(STAR_5.equals(cmd)){
            if(!starFiveBtn.isSelected()){
                starFiveBtn.setSelected(true);
                starRating = 5; //five
            }else{
                starOneBtn.setSelected(true);
                starTwoBtn.setSelected(true);
                starThreeBtn.setSelected(true);
                starFourBtn.setSelected(true);
                starFiveBtn.setSelected(true);
                starRating = 5; //five
            }
            vdt.setStarRating(starRating);
        }else if(VIDEO_CLICK.equals(cmd)){
            String url =  "http://youtube.com/watch?v=" + vdt.videoId;
            if(Desktop.isDesktopSupported()){
                Desktop desktop = Desktop.getDesktop();
                try {
                    desktop.browse(new URI(url));
                } catch (IOException | URISyntaxException err) {
                    err.printStackTrace();
                }
            }else{
                Runtime runtime = Runtime.getRuntime();
                try {
                    runtime.exec("xdg-open " + url);
                } catch (IOException err) {
                    err.printStackTrace();
                }
            }
        }
       
        
    }

    private void setStarRating(int rating){
        starRating = rating;
        if(rating != 0){
            if(1 == rating){
                starOneBtn.setSelected(true);
            }else if(2 == rating){
                starOneBtn.setSelected(true);
                starTwoBtn.setSelected(true);
            }else if(3 == rating){
                starOneBtn.setSelected(true);
                starTwoBtn.setSelected(true);
                starThreeBtn.setSelected(true);
            }else if(4 == rating){
                starOneBtn.setSelected(true);
                starTwoBtn.setSelected(true);
                starThreeBtn.setSelected(true);
                starFourBtn.setSelected(true);
            }else{
                starOneBtn.setSelected(true);
                starTwoBtn.setSelected(true);
                starThreeBtn.setSelected(true);
                starFourBtn.setSelected(true);
                starFiveBtn.setSelected(true);
            }
        }
    }

    public void update(Object observable) {
        System.out.println("VideoPanel model change");
        //clearDisplay();
    }
}