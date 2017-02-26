
import java.io.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.util.List;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.border.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.ObjectOutputStream;
import javax.swing.filechooser.FileFilter;

public class View extends JFrame implements Observer {

    private Model model;

    //Menu
    private JMenuBar mb = new JMenuBar();
	private JMenu mnuFile = new JMenu("File");
    private JMenuItem mnuItemNew = new JMenuItem("New");
    private JMenuItem mnuItemSave = new JMenuItem("Save");
    private JMenuItem mnuItemLoad = new JMenuItem("Load");
	private JMenuItem mnuItemExit = new JMenuItem("Exit");
	private JMenu mnuView = new JMenu("View");
	private JMenuItem mnuItemResize = new JMenuItem("Auto Resize");

    private static JFileChooser chooser;

    /**
     * Create a new View.
     */
    public View(Model model) {

        // Set up the window.
        this.setTitle("A2 Doodle, by Jason Williamson 205 52 360");
        this.setMinimumSize(new Dimension(400, 300));
        this.setSize(800, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set layout
        this.setLayout(new BorderLayout());
	    
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

        // Setup menu
        this.setJMenuBar(mb);
        mnuFile.add(mnuItemNew);
        mnuItemNew.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if(0 < model.getLineCount()){
                        int save = JOptionPane.showConfirmDialog(null, "Save changes to this file before overwriting?",
                        "Save", JOptionPane.YES_NO_OPTION);
                        if (save == JOptionPane.YES_OPTION){
                            saveFile();
                        }
                        model.clearLines();
                    }
                }
            });

        mnuFile.add(mnuItemSave);
        mnuItemSave.setEnabled(false);
        mnuItemSave.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    saveFile();
                }
            });

        mnuFile.add(mnuItemLoad);
        mnuItemLoad.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if(0 < model.getLineCount()){
                        int save = JOptionPane.showConfirmDialog(null, "Save changes to this file before overwriting?",
                        "Save", JOptionPane.YES_NO_OPTION);
                        if (save == JOptionPane.YES_OPTION){
                            saveFile();
                        }
                    }
                    loadFile();
                }
            });

        mnuFile.add(mnuItemExit);
        mnuItemExit.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if(0 < model.getLineCount()){
                        int save = JOptionPane.showConfirmDialog(null, "Save changes to this file before exiting?",
                        "Save", JOptionPane.YES_NO_OPTION);
                        if (save == JOptionPane.YES_OPTION){
                             saveFile();
                        }
                    }
                    System.exit(0);
                }
            });

		mnuView.add(mnuItemResize);
		mb.add(mnuFile);
		//mb.add(mnuView);

        //Set SliderView
        this.add(new SliderView(model), BorderLayout.SOUTH);
		
        //Set LeftView (color picker)
        this.add(new LeftView(model), BorderLayout.WEST);

        //Set Canvas
        Canvas canvas = new Canvas(model);
        canvas.setBackground(Color.WHITE);
        this.add(canvas, BorderLayout.CENTER);

        // Hook up this observer so that it will be notified when the model
        // changes.
        this.model = model;
        model.addObserver(this);

        setVisible(true);
        System.out.println("View ready!");
    }

    /**
     * Update with data from the model.
     */
    public void update(Object observable) {
        System.out.println("Model changed!");
        if(0 < this.model.getLineCount()){
            mnuItemSave.setEnabled(true);
        }else{
            mnuItemSave.setEnabled(false);
        }
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
               oos.writeObject(model.linesArr);
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
            List<LinesObject> linesArr = new ArrayList<LinesObject>();
            try {
                FileInputStream fin = new FileInputStream(file);
                ObjectInputStream oin = new ObjectInputStream(fin);
                linesArr = (List) oin.readObject();
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
            model.linesArr = linesArr;
            model.forceRepaint();
         }
    }
}
