import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Ellipse2D;
import java.awt.Point;

/**
 * Ellipse sprite 
 * Extended from rectangular sprite.
 * Michael Terry & Jeff Avery
 */

 public class EllipseSprite extends Sprite {
    public int width = 0;
    public int height = 0;
    public EllipseSprite parentSprite = null;
    private Ellipse2D elli = null;
    private Ellipse2D pin = null;
    private Ellipse2D fixed = null;
    private String bodyPart;
    private Point rotationPoint;
    public Double hyp = 1.0; //Defualt
    public Double rotationValue = 0.0;
    public Double translatedX = 0.0; //MUST BE SET IF ULA,URA,ULL,URL,H
    public Double translatedY = 0.0;

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

     /**
     * Creates a rectangle based at the origin with the specified
     * width and height
     */
    public EllipseSprite(int width, int height) {
        super();
        this.initialize(width, height);
        this.width = width;
        this.height = height;
    }

    /**
     * Creates a rectangle based at the origin with the specified
     * width, height, and parent
     */
    public EllipseSprite(int width, int height,String bodyPart, EllipseSprite parentSprite) {
        super(parentSprite);
        this.width = width;
        this.height = height;
        this.parentSprite = parentSprite;
        setBodyPart(bodyPart);
        this.initialize(width, height);
    }

    /**
     * Creates a rectangle based at the origin with the specified
     * width and height
     */
    public EllipseSprite(int width, int height, String bodyPart) {
        super();
        this.width = width;
        this.height = height;
        setBodyPart(bodyPart);
        this.initialize(width, height);
    }
    

    private void initialize(int width, int height) {
        elli = new Ellipse2D.Double(0, 0, width, height);

        //Set pin orientation base on what bodyPart object represents
        switch (getBodyPart()) {
            case HEAD:
                translatedX = 30.0;
                translatedY = -95.0;
                //System.out.println("HEAD");
                pin = new Ellipse2D.Double(width/2, height, 2, 2);
                setRotationPoint(new Point(width/2, height));
                break;
            case RH:
            case LRA:
            case URA:
                translatedX = -130.0;
                translatedY = 25.0;
                pin = new Ellipse2D.Double(width, height/2, 2, 2);
                setRotationPoint(new Point(width, height/2));
                hyp = 1.0 * width;
                rotationValue = -180.0;
                break;
            case LH:
            case LLA:
            case ULA: 
                translatedX = 130.0;
                translatedY = 25.0; 
                pin = new Ellipse2D.Double(0, height/2 , 2, 2);
                setRotationPoint(new Point(0, height/2));
                hyp = 1.0 * width;
                rotationValue = 0.0;
                break;
            case URL: //UPPER RIGHT LEG
                hyp = 1.0 * width;
                translatedX = 40.0;
                translatedY = 250.0; 
                pin = new Ellipse2D.Double(0, height/2 , 2, 2);
                setRotationPoint(new Point(0, height/2));
                rotationValue = 90.0;
                break;
            case LRL:
                pin = new Ellipse2D.Double(0, height/2 , 2, 2);
                setRotationPoint(new Point(0, height/2));
                hyp = 1.0 * width;
                rotationValue = 90.0;
                break;
            case RF:
                pin = new Ellipse2D.Double(0, height/2 , 2, 2);
                setRotationPoint(new Point(0, height/2));
                rotationValue = 180.0;
                break;
            case ULL:
                hyp = 1.0 * width;
                translatedX = 112.0;
                translatedY = 250.0;
                setRotationPoint(new Point(0, height/2));
                rotationValue = 90.0;
                pin = new Ellipse2D.Double(0, height/2 , 2, 2);
                break;
            case LLL:
                pin = new Ellipse2D.Double(0, height/2 , 2, 2);
                setRotationPoint(new Point(0, height/2));
                hyp = 1.0 * width;
                rotationValue = 90.0;
                break;
            case LF:
                pin = new Ellipse2D.Double(0, height/2 , 2, 2);
                setRotationPoint(new Point(0, height/2));
                rotationValue = -90.0;
                break;
            default:
                //BODY 
                pin = new Ellipse2D.Double(width/2, height/2 , 2, 2);
                rotationValue = 0.0;
                break;
        }
    }

    private void pinLeftArm(){
        pin = new Ellipse2D.Double(width, height/2, 2, 2);
    }
    
    /**
     * Test if our rectangle contains the point specified.
     */
    public boolean pointInside(Point2D p) {
        AffineTransform fullTransform = this.getFullTransform();
        AffineTransform inverseTransform = null;
        try {
            inverseTransform = fullTransform.createInverse();
        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
        }
        Point2D newPoint = (Point2D)p.clone();
        inverseTransform.transform(newPoint, newPoint);
        return elli.contains(newPoint);
    }

    protected void drawSprite(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.draw(elli);
        g.draw(pin);
    }
    
    public String toString() {
        return "EllipseSprite: " + elli;
    }

    public Point getPin(){
        return new Point(0, this.height/2);
    }

    public void setBodyPart(String part){
        bodyPart = part;
    }

    public String getBodyPart(){
        return bodyPart;
    }

    public void setRotationPoint(Point p){
        rotationPoint = p;
    }

    public Point getRotationPoint(){
        return rotationPoint;
    }

    public void setFixedPoint(Double fixedX, Double fixedY){
        fixed = new Ellipse2D.Double(fixedX, fixedY, 55, 55);
    }

    public void setRotationValue(Double val){
        rotationValue = val;
    }

    public Double getRotationValue(){
        return rotationValue;
    }

    
 }