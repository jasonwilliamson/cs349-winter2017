
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import javax.swing.JFrame;
import java.lang.Math;
import javax.swing.*;
import java.awt.event.KeyEvent;

public class Main {
	private static final String HEAD = "head";
 	private static final String BODY = "body";
 	private static final String URA = "ura";     //UpperRightArm
 	private static final String LRA = "lra";     //LowerRightArm
 	private static final String RH = "rh";       //RightHand
 	private static final String ULA = "ula";     //UpperLeftArm
 	private static final String LLA = "lla";     //LowerLeftArm
 	private static final String LH = "lh";       //LeftHand
 	private static final String URL = "url";     //UpperRightLeg
 	private static final String LRL = "lrl";     //LowerRightLeg
 	private static final String RF = "rf";       //RightFoot
 	private static final String ULL = "ull";     //UpperLeftLeg
 	private static final String LLL = "lll";     //LowerLeftLeg
 	private static final String LF = "lf";       //LeftFoot

	public static void main(String[] args) {		


		// create a frame to hold it
		JFrame f = new JFrame();
		f.setTitle("A3 Paper Dolls, by Jason Williamson 205 52 360");
		f.setSize(1280, 800);
		f.setResizable(false);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// add scene graph to the canvas
		Canvas canvas = new Canvas();
		

		JMenuBar mb = new JMenuBar();
		JMenu mnuFile = new JMenu("File");
	    //JMenuItem mnuItemReset = new JMenuItem("Reset", KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK);
		JMenuItem mnuItemReset = new JMenuItem("Reset");
		Action resetAction = new AbstractAction("Reset") {
			@Override
			public void actionPerformed(ActionEvent e){
				System.out.println("Reseting..");
				canvas.sprites.clear();
				canvas.repaint();
				canvas.addSprite(Main.makeSprite());
			}
		};
		resetAction.putValue(Action.ACCELERATOR_KEY, 
			KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK));
		mnuItemReset.setAction(resetAction);

        JMenuItem mnuItemQuit = new JMenuItem("Quit");
		Action quitAction = new AbstractAction("Quit") {
			@Override
			public void actionPerformed(ActionEvent e){
				System.out.println("Quiting..");
				System.exit(0);
			}
		};
		quitAction.putValue(Action.ACCELERATOR_KEY,
			KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK));
		mnuItemQuit.setAction(quitAction);

		f.setJMenuBar(mb);
		mb.add(mnuFile);
		mnuFile.add(mnuItemReset);
		mnuFile.addSeparator();
		mnuFile.add(mnuItemQuit);
		mb.add(mnuFile);
		
		//f.getContentPane().add(canvas);
		f.add(canvas);
		f.setLayout(new GridLayout(1, 1));
		//f.getContentPane().setLayout(new GridLayout(1, 1));
		canvas.addSprite(Main.makeSprite());
		f.setVisible(true);
		f.setFocusable(true);
		canvas.repaint();
		
	}
	
	/* Make sample scene graph for testing purposes. */
	private static EllipseSprite makeSprite() {

		//Create different parts
		EllipseSprite bodySprite = new EllipseSprite(150, 300, BODY);
		EllipseSprite headSprite = new EllipseSprite(80, 95, HEAD, bodySprite);
		EllipseSprite upperRightArm = new EllipseSprite(150, 45, URA, bodySprite);
		EllipseSprite lowerRightArm = new EllipseSprite(100, 35, LRA, upperRightArm);
		EllipseSprite rightHand = new EllipseSprite(55, 25, RH, lowerRightArm);
		EllipseSprite upperLeftArm = new EllipseSprite(150, 45, ULA, bodySprite);
		EllipseSprite lowerLeftArm = new EllipseSprite(100, 35, LLA, upperLeftArm);
		EllipseSprite leftHand = new EllipseSprite(55, 25, LH, lowerLeftArm);
		EllipseSprite upperRightLeg = new EllipseSprite(150, 55, URL, bodySprite);
		EllipseSprite lowerRightLeg = new EllipseSprite(110, 45, LRL, upperRightLeg);
		EllipseSprite rightFoot = new EllipseSprite(75, 35, RF, lowerRightLeg);
		EllipseSprite upperLeftLeg = new EllipseSprite(150, 55, ULL, bodySprite);
		EllipseSprite lowerLeftLeg = new EllipseSprite(110, 45, LLL, upperLeftLeg);
		EllipseSprite leftFoot = new EllipseSprite(75, 35, LF, lowerLeftLeg);
		

		bodySprite.transform(AffineTransform.getTranslateInstance((1280/2) - 75, 150));
		headSprite.transform(AffineTransform.getTranslateInstance( 30, -95));
		upperRightArm.transform(AffineTransform.getTranslateInstance( -130, 25));
		lowerRightArm.transform(AffineTransform.getTranslateInstance( -97, 5));
		rightHand.transform(AffineTransform.getTranslateInstance(-53, 3));

		//
		upperLeftArm.transform(AffineTransform.getTranslateInstance( 130, 25));
		lowerLeftArm.transform(AffineTransform.getTranslateInstance( 147, 5));
		leftHand.transform(AffineTransform.getTranslateInstance(100, 3));
		
		upperRightLeg.transform(AffineTransform.getTranslateInstance(40,250));
		lowerRightLeg.transform(AffineTransform.getTranslateInstance(148, 5));
		rightFoot.transform(AffineTransform.getTranslateInstance(108, 5));

		upperLeftLeg.transform(AffineTransform.getTranslateInstance(112,250));
		lowerLeftLeg.transform(AffineTransform.getTranslateInstance(147, 5));
		leftFoot.transform(AffineTransform.getTranslateInstance(108, 5));
		
		
		// build scene graph
		bodySprite.addChild(headSprite);
		bodySprite.addChild(upperRightArm);
		upperRightArm.addChild(lowerRightArm);
		lowerRightArm.addChild(rightHand);
		bodySprite.addChild(upperLeftArm);
		upperLeftArm.addChild(lowerLeftArm);
		lowerLeftArm.addChild(leftHand);
		bodySprite.addChild(upperRightLeg);
		upperRightLeg.addChild(lowerRightLeg);
		lowerRightLeg.addChild(rightFoot);
		bodySprite.addChild(upperLeftLeg);
		upperLeftLeg.addChild(lowerLeftLeg);
		lowerLeftLeg.addChild(leftFoot);

		upperRightLeg.transform(AffineTransform.getRotateInstance(Math.toRadians(90.0), 0, upperRightLeg.height / 2));
		rightFoot.transform(AffineTransform.getRotateInstance(Math.toRadians(90.0), 0, rightFoot.height / 2));

		upperLeftLeg.transform(AffineTransform.getRotateInstance(Math.toRadians(90.0), 0, upperLeftLeg.height / 2));
		leftFoot.transform(AffineTransform.getRotateInstance(Math.toRadians(-90.0), 0, leftFoot.height / 2));
		// return root of the tree
	return bodySprite;
	}

}
