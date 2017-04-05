// ====================================================
// Jason Williamson (20552360)
// CS 349 Winter 2017
// Assignment 04
// File: View.java
// ====================================================
//
import java.io.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

public class View extends JFrame implements Observer, ActionListener, AdjustmentListener {

    private ModelYouTube model;

    static final private String GRID_LAYOUT = "grid_layout";
    static final private String LIST_LAYOUT = "list_layout";
    static final private String SEARCH = "search";
    static final private String LOAD = "load";
    static final private String SAVE = "save";
    static final private String STAR_1 = "star_1";
    static final private String STAR_2 = "star_2";
    static final private String STAR_3 = "star_3";
    static final private String STAR_4 = "star_4";
    static final private String STAR_5 = "star_5";
    static final private String STAR_TIP = "Star Rating";
    static final private String STAR_ALT = "Star";

    //ToolBar button
    private JButton searchBtn;
    private JButton saveBtn;
    private JButton loadBtn;
    private JToggleButton gridViewBtn;
    private JToggleButton listViewBtn;
    private TextField searchTxt;
    private JToggleButton starOneBtn;
    private JToggleButton starTwoBtn;
    private JToggleButton starThreeBtn;
    private JToggleButton starFourBtn;
    private JToggleButton starFiveBtn;
    private Canvas canvas;
    private Boolean isGridView = true;
    private JScrollPane scrollBar;
    private int ratingsFilter = 0;
    private static JFileChooser chooser;
    private Boolean refresh = false;
    /**
     * Create a new View.
     */
    public View(ModelYouTube model) {

        // Set up the window.
        this.setTitle("A4 YouTube Video Gallery, by Jason Williamson 205 52 360");
        this.setMinimumSize(new Dimension(650, 300));
        this.setSize(1280, 800);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //JFileChooser
        chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("."));
        chooser.setFileFilter(new javax.swing.filechooser.FileFilter()
        {
            public boolean accept(File f)
            {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".ser");
            }

            public String getDescription()
            {
                return "SER Binary files";
            }
        });

        // Set layout
        this.setLayout(new BorderLayout());

        JToolBar toolBar = new JToolBar();
        System.out.println("ToolBar " + toolBar.toString());
        addButtons(toolBar);
        toolBar.setFloatable(false);

        this.model = model;
        model.addObserver(this);
        //Set Canvas
        canvas = new Canvas(model);

        this.add(toolBar, BorderLayout.PAGE_START); 
        setVisible(true);
        System.out.println("View ready!");        
    }

    public void adjustmentValueChanged(AdjustmentEvent e) {
      System.out.println("New Value is " + e.getValue() + "      ");
      repaint();
    }

    private void addButtons(JToolBar toolBar) {
        System.out.println("addButtons ");
        toolBar.addSeparator();

        //Grid View Button
        gridViewBtn = makeToggleButton("grid_unselected", "grid_selected",GRID_LAYOUT,
                                        "Grid layout", "Grid layout");
        gridViewBtn.setSelected(true);
        toolBar.add(gridViewBtn);

        toolBar.addSeparator();

        //List View Button
        listViewBtn = makeToggleButton("list_unselected", "list_selected",LIST_LAYOUT,
                                        "List layout", "List layout");
        toolBar.add(listViewBtn);

        toolBar.addSeparator();

        searchTxt = new TextField("", 20);
        toolBar.add(searchTxt);

        toolBar.addSeparator();

        searchBtn = makeNavigationButton("search",SEARCH, "Perform search", "Search");
        toolBar.add(searchBtn);

        toolBar.addSeparator();

        loadBtn = new JButton("Load");
        loadBtn.setActionCommand(LOAD);
        loadBtn.addActionListener(this);
        toolBar.add(loadBtn);

        saveBtn = new JButton("Save");
        saveBtn.setActionCommand(SAVE);
        saveBtn.addActionListener(this);
        toolBar.add(saveBtn);

        toolBar.addSeparator();

        starOneBtn = makeToggleButton("star","star_gold",STAR_1, STAR_TIP, STAR_ALT);
        toolBar.add(starOneBtn);
        starTwoBtn = makeToggleButton("star","star_gold",STAR_2, STAR_TIP, STAR_ALT);
        toolBar.add(starTwoBtn);
        starThreeBtn = makeToggleButton("star","star_gold",STAR_3, STAR_TIP, STAR_ALT);
        toolBar.add(starThreeBtn);
        starFourBtn = makeToggleButton("star","star_gold",STAR_4, STAR_TIP, STAR_ALT);
        toolBar.add(starFourBtn);
        starFiveBtn = makeToggleButton("star","star_gold",STAR_5, STAR_TIP, STAR_ALT);
        toolBar.add(starFiveBtn);
    }

    private JToggleButton makeToggleButton(String unselectedImageName, 
                                           String selectedImageName,
                                           String actionCommand,
                                           String toolTipText,
                                           String altText ){

        String imgUnSelectedLocation = "icons/" + unselectedImageName + ".png";
        String imgSelectedLocation = "icons/" + selectedImageName + ".png";
        JToggleButton button = new JToggleButton();
        
        button.setActionCommand(actionCommand);
        button.setToolTipText(toolTipText);
        button.setBorder(null);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.addActionListener(this);

        if((imgUnSelectedLocation != null) && (imgSelectedLocation != null)){
            button.setIcon(new ImageIcon(imgUnSelectedLocation, altText));
            button.setSelectedIcon(new ImageIcon(imgSelectedLocation, altText));
        }else{
            System.out.println("Icons not found " + imgUnSelectedLocation + " " + imgSelectedLocation);
        }

        return button;
    }

    private JButton makeNavigationButton(String imageName,
                                         String actionCommand,
                                         String toolTipText,
                                         String altText){
        
        String imgLocation = "icons/" + imageName + ".png";
        JButton button = new JButton();
        button.setActionCommand(actionCommand);
        button.setToolTipText(toolTipText);
        button.addActionListener(this);

        if(imgLocation != null){
            button.setIcon(new ImageIcon(imgLocation, altText));
        }else{
            System.out.println("Icons not found " + imgLocation);
        }
    
        return button;
    }

    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        System.out.println("actionPerformed: " + cmd);
        //Handle button events
        if(GRID_LAYOUT.equals(cmd)){
            if(!isGridView){
                isGridView = true;
                canvas.gridView = true;
                gridViewBtn.setSelected(true);
                listViewBtn.setSelected(false);
                System.out.println("Display GridView");                
                updateView();
            }else{
                gridViewBtn.setSelected(true);
            }
        }else if(LIST_LAYOUT.equals(cmd)){
            if(isGridView){
                isGridView = false;
                canvas.gridView = false;
                gridViewBtn.setSelected(false);
                listViewBtn.setSelected(true);
                System.out.println("Display ListView");
                updateView();
            }else{
                listViewBtn.setSelected(true);

            }
        }else if(SEARCH.equals(cmd)){
            if(!searchTxt.getText().trim().isEmpty()){
                

                if(!canvas.videoItems.isEmpty()){
                    canvas.clearVideoData();
                    System.out.println("PREVIOUS SEARCH RESULTS "); 
                }
                resetRatingFilter();
                System.out.println("Searching for: " + searchTxt.getText());
                model.searchVideos(searchTxt.getText());
            }
        }else if(STAR_1.equals(cmd)){
            if(!starOneBtn.isSelected()){
                starOneBtn.setSelected(false);
                starTwoBtn.setSelected(false);
                starThreeBtn.setSelected(false);
                starFourBtn.setSelected(false);
                starFiveBtn.setSelected(false);
                setRatingFilter(0);
            }else{
                starOneBtn.setSelected(true);
                setRatingFilter(1);
            }
            updateView();

        }else if(STAR_2.equals(cmd)){
            if(!starTwoBtn.isSelected()){
                starTwoBtn.setSelected(true);
                starThreeBtn.setSelected(false);
                starFourBtn.setSelected(false);
                starFiveBtn.setSelected(false);
                setRatingFilter(2);
            }else{
                starOneBtn.setSelected(true);
                starTwoBtn.setSelected(true);
                setRatingFilter(2);
            }
            updateView();

        }else if(STAR_3.equals(cmd)){
            if(!starThreeBtn.isSelected()){
                starThreeBtn.setSelected(true);
                starFourBtn.setSelected(false);
                starFiveBtn.setSelected(false);
                setRatingFilter(3);
            }else{
                starOneBtn.setSelected(true);
                starTwoBtn.setSelected(true);
                starThreeBtn.setSelected(true);
                setRatingFilter(3);
            }
            updateView();

        }else if(STAR_4.equals(cmd)){
            if(!starFourBtn.isSelected()){
                starFourBtn.setSelected(true);
                starFiveBtn.setSelected(false);
                //four
                setRatingFilter(4);
            }else{
                starOneBtn.setSelected(true);
                starTwoBtn.setSelected(true);
                starThreeBtn.setSelected(true);
                starFourBtn.setSelected(true);
                //four
                setRatingFilter(4);
            }
            updateView();

        }else if(STAR_5.equals(cmd)){
            if(!starFiveBtn.isSelected()){
                starFiveBtn.setSelected(true);
                //five
                setRatingFilter(5);
            }else{
                starOneBtn.setSelected(true);
                starTwoBtn.setSelected(true);
                starThreeBtn.setSelected(true);
                starFourBtn.setSelected(true);
                starFiveBtn.setSelected(true);
                //five
                setRatingFilter(5);
            }
            updateView();
        }else if(SAVE.equals(cmd)){
            saveFile();
        }else if(LOAD.equals(cmd)){
            loadFile();
        }
    }

    private void setRatingFilter(int rating){
        this.ratingsFilter = rating;
        canvas.filter = rating;
    }

    public void saveFile(){
        System.out.println("Save File");
        
        if(chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION){
           File file = chooser.getSelectedFile();
           FileOutputStream fout = null;
           ObjectOutputStream oos = null;
           try{
               fout = new FileOutputStream(file);
           } catch (FileNotFoundException e){
               e.printStackTrace();
           }
           try {
               oos = new ObjectOutputStream(fout);
               oos.writeObject(model.vidList);
               oos.close();
               fout.close();
           }catch (IOException e){
              e.printStackTrace();
              System.exit(1);
           }
        }
    }

    public void loadFile(){
         System.out.println("Load File"); 
         if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
            File file = chooser.getSelectedFile();
            List<VideoDataType> vidList = new ArrayList<VideoDataType>();
            try {
                model.vidList.clear(); //clear old remaining data
                FileInputStream fin = new FileInputStream(file);
                ObjectInputStream oin = new ObjectInputStream(fin);
                vidList = (List) oin.readObject();
                oin.close();
                fin.close();
            }catch(IOException e){
                e.printStackTrace();
                return;
            }catch(ClassNotFoundException c){
                System.out.println("Class not found");
                c.printStackTrace();
                return;
            }
            VideoDataType vdt = (VideoDataType) vidList.get(0);
            searchTxt.setText(vdt.query);

            model.vidList = vidList; ///modify to model class
           System.out.println("inside LOAD file: Model size: " + model.vidList.size());

           System.out.println("inside LOAD file: Model title: " + vdt.title);
           System.out.println("inside LOAD file: Model  rating:" + vdt.starRating);

           canvas.clearVideoData();
           model.notifyObservers();
         }
    }

    private void updateRatings(){
        if(ratingsFilter != canvas.filter){
            canvas.filter = ratingsFilter;
            updateView();
        }
    }

    private void resetRatingFilter(){
        starOneBtn.setSelected(false);
        starTwoBtn.setSelected(false);
        starThreeBtn.setSelected(false);
        starFourBtn.setSelected(false);
        starFiveBtn.setSelected(false);
        ratingsFilter = 0;
        canvas.filter = 0;
    }

    private void updateView(){
        if(isGridView){
            canvas.clearDisplay();
            if(scrollBar != null){
                scrollBar.removeAll();
                this.remove(scrollBar);
            }
            
            MyFlowLayout flow = new MyFlowLayout();
            canvas.setBackground(Color.WHITE);
            canvas.setLayout(flow);
            flow.setAlignment(FlowLayout.LEADING);

        
            scrollBar = new JScrollPane(canvas,
                                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

            this.add(scrollBar, BorderLayout.CENTER);
            canvas.displayGridView();

            revalidate();
            repaint();
            System.out.println("update Gridview");
        }else{
            canvas.clearDisplay();
            canvas.displayListView();
            System.out.println("update ListView");
        }
    }


    /**
     * Update with data from the model.
     */
    public void update(Object observable) {
        this.model = (ModelYouTube) observable;

/////////
    if(!model.vidList.isEmpty()){
         
        VideoDataType vdt = (VideoDataType) model.vidList.get(0);
            
           System.out.println("LOADED: Model size: " + model.vidList.size());

           System.out.println("LOADED: Model title: " + vdt.title);
           System.out.println("LOADED: Model  rating:" + vdt.starRating);
    }
//////////

        System.out.println("Model veiw changed!");
        updateView();
    }
} 