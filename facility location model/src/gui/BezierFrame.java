//Title:        Geometric modelling tool
//Author:       Ronald Apriliyanto Halim

package gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class BezierFrame extends Frame{
    // shortcut key to close this window/frame
    private static final int kControlX = 88;
    private GreatCircleConnectionApp applet;
  
  public BezierFrame() {
    //set frame's title
    super("Visualization Frame");
    //add menu
    addMenu();
    //add the drawing applet
    addApplet();
    //add windows listener
    this.addWindowListener(new WindowHandler());
    //set frame size
    this.setSize(500, 500);
    //make this frame visible
    this.setVisible(true);
  }

  /**
 * this method adds menu to the bezier frame
 */
private void addMenu()
  {
    //Add menu bar to the frame
    MenuBar menuBar = new MenuBar();
    Menu file = new Menu("File");

    //Add menu items for the file menu
    file.add(new MenuItem("Open")).addActionListener(new WindowHandler());
    file.add(new MenuItem("Save")).addActionListener(new WindowHandler());
    file.add(new MenuItem("Exit Visualization Frame", new MenuShortcut(kControlX))).addActionListener(new WindowHandler());
    
    menuBar.add(file);
    if(this.getMenuBar()==null)
    {
        this.setMenuBar(menuBar);
        }
    }//addMenu()
  
  /**
  This method adds an applet to the bezier frame for drawing the bezier curve
  */
  private void addApplet()
  {
    this.applet = new GreatCircleConnectionApp();
    applet.init();
    this.add(applet); 
  }//end of addApplet();

private void disposeFrame()
{
    this.dispose();
}
  private class WindowHandler extends WindowAdapter implements ActionListener
  {
      
    public void windowClosing(WindowEvent e)
    {
    disposeFrame();
    }

    public void actionPerformed(ActionEvent e)
    {
      //check to see if the action command is equal to exit
      if(e.getActionCommand().equalsIgnoreCase("Exit Visualization Frame"))
      {
       disposeFrame();
      }
      else if(e.getActionCommand().equalsIgnoreCase("Open"))
      {
          JFileChooser jfc = new JFileChooser();
          FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG & GIF Images", "jpg", "gif");
          jfc.setFileFilter(filter);
          int result = jfc.showOpenDialog(BezierFrame.this);
          if(result == JFileChooser.APPROVE_OPTION)
          {
              File file = jfc.getSelectedFile();
          try {
              Image img = ImageIO.read(file);
              applet.repaint();
          } 
          catch (Exception a) {
             JOptionPane.showMessageDialog(BezierFrame.this,a.getMessage(),"File error",JOptionPane.ERROR_MESSAGE);
             }
          }
      }
      
      else if (e.getActionCommand().equalsIgnoreCase("Save"))
      {
          JOptionPane.showMessageDialog(null, "Do not forget to enter the filename extension (.jpeg,.gif)" , "A Geometric Drawing Tool", JOptionPane.PLAIN_MESSAGE);
          JFileChooser jfc = new JFileChooser();
          int result = jfc.showSaveDialog(BezierFrame.this);
          if(result == JFileChooser.APPROVE_OPTION) 
          try {
              BufferedImage image = new BufferedImage(applet.getWidth(), applet.getHeight(), BufferedImage.TYPE_INT_RGB);
              Graphics2D g = image.createGraphics();
              g.setBackground(Color.WHITE);
              applet.paint(g);
              g.dispose();
              File saveLocation =jfc.getSelectedFile();
              ImageIO.write(image, "JPG", saveLocation);
              }
          catch (Exception a) 
          {
             JOptionPane.showMessageDialog(BezierFrame.this, a.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
       }
      }
    }
  }
}


