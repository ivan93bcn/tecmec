package practicaf1;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 *
 * @author Ivan Toro and David Muntal
 */
public class ImgContainer
{
  private BufferedImage bufImage;
  String nomImatge;
  private int x0;
  private int y0;
  private boolean modified = false;
  
  public ImgContainer(BufferedImage x, String y)
  {
    this.bufImage = x;
    this.nomImatge = y;
    this.x0 = 0;
    this.y0 = 0;
  }
  
  public void setBufImg(BufferedImage bi)
  {
    this.bufImage = bi;
  }
  
  public BufferedImage getBufImg()
  {
    return this.bufImage;
  }
  
  public int getX0()
  {
    return this.x0;
  }
  
  public int getY0()
  {
    return this.y0;
  }
  
  public void setX0(int value)
  {
    this.x0 = value;
  }
  
  public void setY0(int value)
  {
    this.y0 = value;
  }
  
  public String getName()
  {
    return this.nomImatge;
  }
  
  public static BufferedImage copia(BufferedImage original)
  {
    return new BufferedImage(original.getColorModel(), (WritableRaster)original.getData(), 
      original.getColorModel().isAlphaPremultiplied(), null);
  }
  
  public boolean isModified()
  {
    return this.modified;
  }
  
  public void setModified(boolean modified)
  {
    this.modified = modified;
  }
}

