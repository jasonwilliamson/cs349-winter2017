// ====================================================
// Jason Williamson (20552360)
// CS 349 Winter 2017
// Assignment 04
// File: MyFlowLayout.java
// ====================================================
//
import java.awt.*;

public class MyFlowLayout extends FlowLayout {
    public MyFlowLayout() {
        super();
    }

    public Dimension preferredLayoutSize(Container target){
    return getSize(target);
}

private Dimension getSize(Container target){
    
    //horizontal and vert gap between the components and the borders of the Container
    int hgap = getHgap();
    int vgap = getVgap();

    //current width of this component.
    int width = target.getWidth();

    //Determines the insets of this container, which indicate the size of the container's border.
    Insets insets = target.getInsets();

    int totalWidth = width - (insets.left + insets.right + hgap * 2);


    int calculatedWidth = 0;
    int calculatedHeight = 0;
    
    int maxwidth = width - (insets.left + insets.right + hgap * 2);

    //Gets the number of components in this panel.
    int compCount = target.getComponentCount();

    //int y = insets.top + vgap;
    int rowHeight = insets.top + vgap;
    int rowWidth = 0;
    int compHeight = 0;

    //add up width and height of components 
    for (int i = 0; i < compCount; i++) {
    Component comp = target.getComponent(i);
        if (comp.isVisible()) {
            Dimension dim = comp.getPreferredSize();
            if ((rowWidth + dim.width) <= maxwidth) {
                rowWidth += (hgap + dim.width);
                compHeight = dim.height;
            }
            else {
                rowWidth = dim.width;
                rowHeight += vgap + dim.height;;
            }
            calculatedWidth = rowWidth;
        }
    }
        
        rowHeight += (compHeight + insets.bottom);
        calculatedHeight = rowHeight;

        //Dimension(int width, int height)
        calculatedWidth = (calculatedWidth + insets.left + insets.right);
        return new Dimension( calculatedWidth, calculatedHeight);
    }
}

