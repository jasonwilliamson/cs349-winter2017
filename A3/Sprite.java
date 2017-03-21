
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Vector;
import java.lang.Math;
import java.awt.Point;
import java.util.*;

/**
 * A building block for creating your own shapes that can be
 * transformed and that can respond to input. This class is
 * provided as an example; you will likely need to modify it
 * to meet the assignment requirements.
 * 
 * Michael Terry & Jeff Avery
 */
public abstract class Sprite {
    
    /**
     * Tracks our current interaction mode after a mouse-down
     */
    protected enum InteractionMode {
        IDLE,
        DRAGGING,
        SCALING,
        ROTATING
    }

    private final String HEAD = "head";
    private final String BODY = "body";
    private final String URA = "ura";     //UpperRightArm
    private final String LRA = "lra";     //LowerRightArm
    private final String RH = "rh";       //RightHand
    private final String ULA = "ula";     //UpperLeftArm
    private final String LLA = "lla";     //LowerLeftArm
    private final String LH = "lh";       //LeftHand
    private final String URL = "url";     //UpperRightLeg
    private final String LRL = "lrl";     //LowerRightLeg
    private final String RF = "rf";       //RightFoot
    private final String ULL = "ull";     //UpperLeftLeg
    private final String LLL = "lll";     //LowerLeftLeg
    private final String LF = "lf";       //LeftFoot

    public Sprite parent = null;                               // Pointer to our parent
    public Vector<Sprite> children = new Vector<Sprite>();     // Holds all of our children
    private AffineTransform transform = new AffineTransform();  // Our transformation matrix
    protected Point2D lastPoint = null;                         // Last mouse point
    protected InteractionMode interactionMode = InteractionMode.IDLE;    // current state
    private double lastTheta = 0;
    private double lastDegree = 0;
    private double translateX;
    private double translateY;
    private double fixedX;
    private double fixedY;
    private boolean dampen = true;
    private double last_td = 0.0;
    private final double SCALE_MAX = 1.33;
    private final double SCALE_MIN = .67;
    private double lastTotalDiff = 0.0;
    private boolean increasing = true;
    

    public Sprite() {
    }
    
    public Sprite(Sprite parent) {
        if (parent != null) {
            parent.addChild(this);
        }
    }

    public void addChild(Sprite s) {
        children.add(s);
        s.setParent(this);
    }
    public Sprite getParent() {
        return parent;
    }
    private void setParent(Sprite s) {
        this.parent = s;
    }

    /**
     * Test whether a point, in world coordinates, is within our sprite.
     */
    public abstract boolean pointInside(Point2D p);

    /**
     * Handles a mouse down event, assuming that the event has already
     * been tested to ensure the mouse point is within our sprite.
     */
    protected void handleMouseDownEvent(MouseEvent e) {

        lastPoint = e.getPoint();
        if (e.getButton() == MouseEvent.BUTTON1) {
            EllipseSprite el = (EllipseSprite) this; // Interaction based on what object selected 
            //System.out.println("HELLO: " + el.getBodyPart());
            if(BODY == el.getBodyPart()){
                //System.out.println("DRAG");
                interactionMode = InteractionMode.DRAGGING;  
            }else{
                last_td = 0.0;
                //System.out.println("000000000000");
                dampen = true;
                AffineTransform at = el.getLocalTransform();
                double r = Math.atan2(at.getShearY(), at.getScaleY());   //current rotation
                double lastDegree = Math.toDegrees(r);

                //arrange grandparent -> parent -> child order..for adjusting the translation
                Stack lifo = new Stack();
                lifo.push(el);
                EllipseSprite eParent = el.parentSprite;
                while(eParent != null){ //Push parents onto stack
                    lifo.push( eParent );
                    el = eParent;
                    //System.out.println("CREATE STACK: " + el.getBodyPart());
                    eParent = el.parentSprite;
                    
                }
                double degreeCounter = 0.0; //reset to zero
                while( !lifo.empty() ){
                    el = (EllipseSprite) lifo.pop();
                    //System.out.println("Current part " + el.getBodyPart());
                    AffineTransform lt = el.getLocalTransform();
                    if(el.getBodyPart() == BODY){ //BODY NEVER ROTATES .. add direct translation always
                        translateX += lt.getTranslateX();
                        translateY += lt.getTranslateY();
                        //System.out.println("body x:" + lt.getTranslateX() + " y: " + lt.getTranslateY());
                    }else if((el.getBodyPart() == ULA) || (el.getBodyPart() == URA) || (el.getBodyPart() == URL) ||
                            (el.getBodyPart() == ULL) || (el.getBodyPart() == HEAD)){
                        //Just add straight translation from body and rotaion point .. no need to consider rotation element
                        Point rotationPoint = el.getRotationPoint();
                        //System.out.println("Calculating rotation of " + el.getBodyPart() + " : " + el.getRotationValue());
                        //System.out.println("CALC TRANS " + el.getBodyPart() + " x: " + el.translatedX + " y: " + el.translatedY);
                        translateX += el.translatedX + rotationPoint.x;
                        translateY += el.translatedY + rotationPoint.y;
                    }else{
                        //System.out.println("Calculating rotation of " + el.getBodyPart() + " : " + el.getRotationValue());
                        EllipseSprite par = (EllipseSprite) el.parent;
                        double curDegrees = par.getRotationValue();
                        double rotRadians = Math.toRadians(curDegrees);
                        //System.out.println(par.getBodyPart() + " hyp: " + par.hyp);
                        double xtrans =  (Math.cos(rotRadians) * par.hyp);
                        double ytrans =  (Math.sin(rotRadians) * par.hyp);

                        //System.out.println(el.getBodyPart()+ " x: " + xtrans + " y: " + ytrans);
                        translateX += xtrans;
                        translateY += ytrans;
  
                        }
                    }

                    fixedX = translateX;
                    fixedY = translateY;
                    translateX = translateY = 0;
            
                    //System.out.println("fixedX " + fixedX + " fixedY " + fixedY);
                    interactionMode = InteractionMode.ROTATING;
                }
                
        }
    
        // Handle rotation, scaling mode depending on input
    }

    /**
     * Handle mouse drag event, with the assumption that we have already
     * been "selected" as the sprite to interact with.
     * This is a very simple method that only works because we
     * assume that the coordinate system has not been modified
     * by scales or rotations. You will need to modify this method
     * appropriately so it can handle arbitrary transformations.
     */
    protected void handleMouseDragEvent(MouseEvent e) {
        
        Point2D oldPoint = lastPoint;
        Point2D newPoint = e.getPoint();
         
        switch (interactionMode) {
            case IDLE:
                ; // no-op (shouldn't get here)
                break;
            case DRAGGING:
                //System.out.println("DRAGGING ");
                double x_diff = newPoint.getX() - oldPoint.getX();
                double y_diff = newPoint.getY() - oldPoint.getY();
                transform.translate(x_diff, y_diff);
                break;
            case ROTATING:
                //System.out.println("-------------------------");
                EllipseSprite el = (EllipseSprite) this;
                Point rotationPoint = el.getRotationPoint(); //objects rotation point or pivot point ..
                
                AffineTransform at = getLocalTransform();
                
                double dx = newPoint.getX() - fixedX; //colculate distance between rotation point and mouse 
                double dy = newPoint.getY() - fixedY;
                double newDX = Math.abs(dx);
                double newDY = Math.abs(dy);
                double totalDiff = newDX + newDY;

                //System.out.println("totalDiff: " + totalDiff + " lastTotalDiff " + lastTotalDiff);
                if((totalDiff - lastTotalDiff) > 0){
                    increasing = true;
                }else{
                    increasing = false;
                    //System.out.println("decresing ");
                }
                lastTotalDiff = totalDiff;
                
                


                //System.out.println("mouse: x: " + newPoint.getX() + " y: " + newPoint.getY());
                //System.out.println("FixedPoint: x: " + fixedX + " y: " + fixedY);
                //System.out.println("dx: " + dx + " dy: " + dy); 
                double theta = Math.atan2(dy,dx); //Distance here is effecting the rotation values from atan2
                                                  //Rotation on child lim is over exagerated 
                double degrees = Math.toDegrees(theta); //Degrees becomes too large on child objects with transforms on parents 
                //System.out.println("??Determined Degree: " + degrees);
                //System.out.println("el.getRotationValue() " + el.getRotationValue());
               

                double difference = degrees - el.getRotationValue();
                if(difference > 180.0){
                    difference = 1; 
                }else if(difference < -180.0){
                    difference = -1;
                }
               
                double total_rotation = Math.toDegrees(Math.atan2(at.getShearY(), at.getScaleY()));
                //System.out.println("total rotation " + total_rotation);

                
                //System.out.println("increment by degree " + difference);
                if (dampen){ 
                    difference *= .01; 
                    dampen = false;
                } 

                String part = el.getBodyPart();
                double t_d = total_rotation + difference;
                
                
                if((HEAD == part) && ((50.0 < t_d) || (-50.0 > t_d))){
                   break;
                }else if(((LLA == part) || (LRA == part)) && ((-135.0 > t_d) || (135.0 < t_d))){
                   break;
                }else if(((ULL == part) || ( URL== part)) && ((0.0 > t_d) || (180.0 < t_d))){
                   break;
                }else if(((LLL == part) || (LRL == part)) && ((-90.0 > t_d) || (90.0 < t_d))){
                  break;
                }else if(((LH == part) || (RH == part)) && (( 35.0 < t_d) || (-35.0 > t_d))){
                  break;
                }else if((RF == part) && ((125.0 < t_d) || (55.0 > t_d))){
                   break;
                }else if((LF == part) && ((-125.0 > t_d) || (-55.0 < t_d))){
                   break;
                }

                el.setRotationValue(degrees);
                theta = Math.toRadians(difference); //new angle of rotation 
                
                //System.out.println("-------------------------");
                
                if((LLL == part) || (LRL == part)){
                    if(increasing){
                        if(!isMaxScaled(el)){
                            transform.scale(1.003, 1.003);
                            EllipseSprite cel = (EllipseSprite) el.children.get(0);
                            cel.transform(AffineTransform.getScaleInstance(.997,.997));
                        }
                    }else{
                        if(!isMinScaled(el)){
                            // System.out.println("isMIn: " + isMinScaled(el));
                            transform.scale(.997,.997);
                            EllipseSprite cel = (EllipseSprite) el.children.get(0);
                            cel.transform(AffineTransform.getScaleInstance(1.003, 1.003));
                        }
                    }
                }else if((ULL == part) || ( URL== part)){
                    if(increasing){
                        if(!isMaxScaled(el)){
                            transform.scale(1.003, 1.003);
                            EllipseSprite cel = (EllipseSprite) el.children.get(0); //LLL or LRL
                            EllipseSprite ccel = (EllipseSprite) cel.children.get(0);
                            ccel.transform(AffineTransform.getScaleInstance(.997,.997));
                        }
                    }else{
                        //System.out.println("Scale: x: " + at.getScaleX() + " y: " + at.getScaleY()); 
                        //System.out.println("isMIn: " + isMinScaled(el));
                        if(!isMinScaled(el)){
                            
                            transform.scale(.997,.997);
                            EllipseSprite cel = (EllipseSprite) el.children.get(0); //LLL or LRL
                            EllipseSprite ccel = (EllipseSprite) cel.children.get(0);
                            ccel.transform(AffineTransform.getScaleInstance(1.003, 1.003));
                        }
                    }
                    
                }
                
                //System.out.println("Scale: x: " + at.getScaleX() + " y: " + at.getScaleY()); 
                transform.rotate(theta, rotationPoint.x, rotationPoint.y);
                
                break;
                
        }
        // Save our last point, if it's needed next time around
        lastPoint = e.getPoint();
    }

    private boolean isMaxScaled(EllipseSprite el){
        AffineTransform at = el.getLocalTransform();
        if(SCALE_MAX > Math.abs(at.getScaleX())){
            return false;
        }
        return true;
    }

    private boolean isMinScaled(EllipseSprite el){
        AffineTransform at = el.getLocalTransform();
        if(SCALE_MIN < Math.abs(at.getScaleX())){
            return false;
        }
        return true;
    }
    
    protected void handleMouseUp(MouseEvent e) {
        interactionMode = InteractionMode.IDLE;
        // Do any other interaction handling necessary here
    }
    
    /**
     * Locates the sprite that was hit by the given event.
     * You *may* need to modify this method, depending on
     * how you modify other parts of the class.
     * 
     * @return The sprite that was hit, or null if no sprite was hit
     */
    public Sprite getSpriteHit(MouseEvent e) {
        for (Sprite sprite : children) {
            Sprite s = sprite.getSpriteHit(e);
            if (s != null) {
                return s;
            }
        }
        if (this.pointInside(e.getPoint())) {
            return this;
        }
        return null;
    }
    
    /*
     * Important note: How transforms are handled here are only an example. You will
     * likely need to modify this code for it to work for your assignment.
     */
    
    /**
     * Returns the full transform to this object from the root
     */
    public AffineTransform getFullTransform() {
        AffineTransform returnTransform = new AffineTransform();
        Sprite curSprite = this;
        while (curSprite != null) {
            returnTransform.preConcatenate(curSprite.getLocalTransform());
            curSprite = curSprite.getParent();
        }
        return returnTransform;
    }

    /**
     * Returns our local transform
     */
    public AffineTransform getLocalTransform() {
        return (AffineTransform)transform.clone();
    }
    
    /**
     * Performs an arbitrary transform on this sprite
     */
    public void transform(AffineTransform t) {
        //System.out.println("CONCAT");
        transform.concatenate(t);
    }

    /**
     * Draws the sprite. This method will call drawSprite after
     * the transform has been set up for this sprite.
     */
    public void draw(Graphics2D g) {
        AffineTransform oldTransform = g.getTransform();

        // Set to our transform
        g.setTransform(this.getFullTransform());
        
        // Draw the sprite (delegated to sub-classes)
        this.drawSprite(g);
        
        // Restore original transform
        g.setTransform(oldTransform);

        // Draw children
        for (Sprite sprite : children) {
            sprite.draw(g);
        }
        
    }
    
    /**
     * The method that actually does the sprite drawing. This method
     * is called after the transform has been set up in the draw() method.
     * Sub-classes should override this method to perform the drawing.
     */
    protected abstract void drawSprite(Graphics2D g);
}
