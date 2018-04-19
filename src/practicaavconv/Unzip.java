package practicaavconv;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;



/**
 *
 * @author Ivan Toro and David Muntal
 */
public class Unzip
{
    List<String> fileList;
    
    private boolean neg = false;
    private boolean sep = false; 
    private boolean bn = false;
    private int bin = 0;
    private int ave = 0;

    public boolean unZipIt(String zipFile, String outputFolder, int fps){

        byte[] buffer = new byte[1024];
        
        ArrayList<BufferedImage> images = new ArrayList<>();

        try{    		
            //get the zip file content
            ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
            //get the zipped file list entry
            ZipEntry ze = zis.getNextEntry();

            //create output directory if not exists
            File folder = new File(outputFolder);
            if(!folder.exists()){
                    folder.mkdir();
            }
            
            JFrame jframe = new JFrame();
            
             //unzip the files  
            while(ze!=null){

                String current = ze.getName();

                String[] name = current.split("[.]");  
                System.out.println(ze.getName());
                String fileName = name[0]; 
                String extension = name[1]; 

                File newFile = new File(outputFolder + File.separator + current);
                new File(newFile.getParent()).mkdirs();

                FileOutputStream fos = new FileOutputStream(newFile);       

                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }

                fos.close();   

              //if extension not jpg, convert

              if (extension.equals("png") || extension.equals("gif") || extension.equals("bmp") || extension.equals("jpeg") || extension.equals("jpg")){

                    BufferedImage bufferedImage = ImageIO.read(new File(outputFolder + File.separator + current));

                    // create a blank, RGB, same width and height, and a white background
                    BufferedImage newBufferedImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
                    newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0, Color.WHITE, null);

                    // get filtered image
                    BufferedImage filteredImage = this.getFilteredImage(newBufferedImage);
                 
                    images.add(filteredImage);
                 
                    // write to jpeg file
                    ImageIO.write(filteredImage, "jpeg", new File(outputFolder + File.separator + fileName+".jpeg"));

                    // delete the original file unziped
                    newFile.delete();
                 }

                ze = zis.getNextEntry();
            }
    	
            zis.closeEntry();
            zis.close();

            playList(images, fps);

            System.out.println("Done");
            
            return true;
    		
        }catch(IOException ex){
            return false;
        }
    }    
    
    private void playList(ArrayList<BufferedImage> images, int fps){
    
        
        JFrame frame = new JFrame();
        JLabel jlabel = new JLabel();
        
        frame.setVisible(true);
        
        for(BufferedImage img : images){

                long time_before = System.currentTimeMillis();
                
                jlabel.setIcon(new ImageIcon(img));
                frame.getContentPane().add(jlabel);
                frame.pack();
                
                /*long time_after = System.currentTimeMillis();
                int ms_sleep = (1000/fps) - (int) (time_after - time_before);
                try {
                    if(ms_sleep > 0)
                        Thread.sleep(ms_sleep);
                } catch(InterruptedException e) {
                    
                }*/
        }
    }
    
    private BufferedImage getFilteredImage(BufferedImage newBufferedImage) {
            
            Filtros filter = new Filtros();
            BufferedImage imgFiltered = newBufferedImage;
            
            if(isNeg())
                imgFiltered = filter.negFilter(imgFiltered);
            if(isSep())
                imgFiltered = filter.sepFilter(imgFiltered);
            if(isBN())
                imgFiltered = filter.BNFilter(imgFiltered);            
            if(getBin()!= 0)
                imgFiltered = filter.binFilter(imgFiltered, getBin());
            if(getAve()!= 0)
                imgFiltered = filter.aveFilter(imgFiltered, getAve());            
            
            return imgFiltered;
    }
    
    public boolean isNeg() {
        return neg;
    }

    public void setNeg(boolean neg) {
        this.neg = neg;
    }

    public boolean isSep() {
        return sep;
    }

    public void setSep(boolean sep) {
        this.sep = sep;
    }

    public boolean isBN() {
        return bn;
    }

    public void setBN(boolean bn) {
        this.bn = bn;
    }
    
    public int getBin() {
        return bin;
    }

    public void setBin(int bin) {
        this.bin = bin;
    }

    public int getAve() {
        return ave;
    }

    public void setAve(int ave) {
        this.ave = ave;
    }
}
