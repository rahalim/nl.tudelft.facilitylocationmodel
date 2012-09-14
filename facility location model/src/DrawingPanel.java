//Title:        Geometric modelling tool
//Author:       Ronald Apriliyanto Halim



import java.awt.Canvas;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.util.ArrayList;

import nl.tudelft.simulation.dsol.animation.D2.GisRenderable2D;
import nl.tudelft.simulation.language.io.URLResource;

public class DrawingPanel extends Canvas
{
// array to store maximum 20 basic shapes
  private Shape[] cshape =new Shape[1000];
  
  private ArrayList graphshape = new ArrayList();
  
// array to store maximum 20 filled shapes
  private Shape[] dshape= new Shape[20];
// counter for the basic shape
  private int counter1=0;
// counter for the filled shape
  private int counter2=0;
// indicator for which array is being used
  private String chooser="normal";
//colors for the filled shape
  private Color color1 = Color.RED;
  private Color color2= Color.WHITE;
  private Color def_color= Color.BLACK;
//coordinate for the filled shape
  private double CoordX_Filled=0;
  private double CoordY_Filled=0;
//coordinate for the basic shapes
  private double CoordX=0;
  private double CoordY=0;
// boolean variable to check whether the user commands a logical operation or not
  private boolean logical=false;
//area for logical operations  
  private Area a1;
  private Area a2;
  private Area a;
  Rectangle2D.Double rectangle;
  private Ellipse2D.Double circle;
//variable that stores the loaded image
  private Image img=null;
  
  double scale=1;
  
  private Image worldMap = Toolkit.getDefaultToolkit().getImage("./data/eqiworld.png");
  
  int initw=1000;

 

  /**
 * @param shape
 * function to draw the basic shapes
 */
public void drawShape(Shape shape)
  {
      if(getChooser()=="filled")
      {
          this.getCshape()[counter1]=shape;
          setChooser("normal");
          counter1++;
      }
      else
      {
          setChooser("normal");
          this.getCshape()[counter1]=shape;
          counter1++;
      }
      RectangularShape rect=(RectangularShape) shape;
      this.CoordX=rect.getCenterX();
      this.CoordY=rect.getCenterY();
  }
  
  /**
 * @param shape
 * function to draw the line
 */
public void drawLine(Shape shape)
{
        this.getCshape()[counter1]=shape;
        counter1++;
        Line2D.Double line= (Line2D.Double) shape;
        this.CoordX=line.getX1();
        this.CoordY=line.getY1();   
}
  /**
 * @param shape
 * function to draw the filled shapes
 */
public void drawFilledShape(RectangularShape shape)
  {
      RectangularShape rect=(RectangularShape) shape;
      if(getChooser()=="normal")
      {
          this.getDshape()[counter2]=shape;
          setChooser("filled");
          counter2++;
      }
      else
      {
    	  setChooser("filled");
    	  this.getDshape()[counter2]=shape;
    	  counter2++;
      }
      this.CoordX_Filled=rect.getCenterX();
      this.CoordY_Filled=rect.getCenterY();      
  }
  
//  override panel paint method to draw shapes
  public void paint(Graphics g)
  {
      super.paint(g);
      //downcast of object of type graphic (g) to graphic2D
      Graphics2D g2 = (Graphics2D) g;
      g2.drawImage(this.getImg(), 0, 0, this);
      g2.drawImage(worldMap,0,(int) (63*scale),(int) (initw*scale),(int) (initw*scale*1930/4000),null);
      
      
      
      for(int j=0; j<counter2; j++)
      {
        GradientPaint redtowhite = new GradientPaint(0,0, color1, 700,700,color2);
        g2.setPaint(redtowhite);
        g2.fill(getDshape()[j]);
      }
      
      for(int k=0; k<counter1; k++)
      {
        //logic to distinguish the color based on the types of shapes
    	  g2.setPaint(def_color);
          if(getCshape()[k] instanceof Ellipse2D.Double)
        	  g2.setPaint(Color.blue);
          if (getCshape()[k] instanceof Rectangle2D.Double)
        	  g2.setPaint(Color.red);
          if (getCshape()[k] instanceof Line2D.Double)
        	  g2.setPaint(Color.black);
          g2.draw(getCshape()[k]);
      }
    //should only be called if logical operation function is called
      if(logical)
      {
    	  g2.draw(a1);
    	  g2.draw(a2);
    	  g2.fill(a);
      }
   }
  

  /**
 * @param angle
 * function to rotate a shape
 */
public void rotateShape(double angle) 
  {
      AffineTransform tx = new AffineTransform();
      double theta=angle/180*Math.PI;      
      if (chooser=="normal")
      {        
          tx.rotate(theta,CoordX,CoordY);
          Shape result = tx.createTransformedShape(getCshape()[counter1-1]);
          cshape[counter1-1]=result;
      }
      else if (chooser=="filled")
      {
          tx.rotate(theta,CoordX_Filled,CoordY_Filled);
          Shape result2 =  tx.createTransformedShape(getDshape()[counter2-1]);
          dshape[counter2-1]= result2;
      }
  }
  
  /**
 * @param coordX
 * @param coordY
 * function to translate a shape
 */
public void translateShape(double coordX, double coordY)
  {
      AffineTransform tx = new AffineTransform();
      tx.translate(coordX, coordY);
      if (chooser=="normal")
      {
          Shape result = tx.createTransformedShape(getCshape()[counter1-1]);
          cshape[counter1-1]=result;
      }
      else if (chooser=="filled")
      {
        
          Shape result2 =  tx.createTransformedShape(getDshape()[counter2-1]);
          dshape[counter2-1]= result2;  
      }
  }

  /**
 * function to make an area on which logical operations can be performed
 */
public void AreaPanel()
  {
      this.logical=true;
      if (chooser=="normal")
      {
      a1=new Area(getCshape()[counter1-1]);
      a2=new Area(getCshape()[counter1-2]);
      addAreas();
      }
      else if (chooser=="filled")
      {
      a1=new Area(getDshape()[counter2-1]);
      a2=new Area(getDshape()[counter2-2]);
      }
  }
  
  /**
 * function for union operation
 */
public void addAreas() {
      a = new Area();
      a.add(a1);
      a.add(a2);
    }
  /**
 * function for difference operation
 */
public void subtractAreas() {
      a = new Area();
      a.add(a1);
      a.subtract(a2);
    }
  /**
 * function for intersect operation
 */
public void intersectAreas() {
      a = new Area();
      a.add(a1);
      a.intersect(a2);
    }

/**
 * function for exclusiving or area
 */
public void exclusiveOrAreas() {
      a = new Area();
      a.add(a1);
      a.exclusiveOr(a2);
  
    }
  
/**
 * function to clear the canvas/drawing panel
 */
public void clearShape()
{
    for(int j=0; j<getCshape().length; j++)
    {
        getCshape()[j]=null;
       
    }
    for(int k=0; k<getDshape().length; k++)
    {
        getDshape()[k]=null;
    }
    counter1=0;
    counter2=0;
    logical=false;
    setImg(null);
}

public void setCshape(Shape[] cshape)
{
    this.cshape = cshape;
}

public Shape[] getCshape()
{
    return cshape;
}

public void setDshape(RectangularShape[] dshape)
{
    this.dshape = dshape;
}

public Shape[] getDshape()
{
    return dshape;
}

public void setChooser(String chooser)
{
    this.chooser = chooser;
}

public String getChooser()
{
    return chooser;
}

public void setImg(Image img)
{
    this.img = img;
}

public Image getImg()
{
    return img;
}
}//DrawingPanel


