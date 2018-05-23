package practicaf1;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 *
 * @author Ivan Toro and David Muntal
 */
public class Unzip
{    
    private boolean neg;
    private boolean sep; 
    private boolean bn;
    private int bin;
    private int ave;
    private int gop;
    private int seek;
    private int size_x;
    private int size_y;
    private int quality;
    boolean batch;

    public Unzip(){
        this.neg = false;
        this.sep = false; 
        this.bn = false;
        this.bin = 0;
        this.ave = 0;
        this.gop = 0;
        this.seek = 0;
        this.size_x = 0;
        this.size_y = 0;
        this.quality = 0;
        this.batch = false;
    }
    
    public boolean unZipIt(String zipFile, String outputFolder, int fps, boolean encode, boolean decode){

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
                        
             //unzip the files  
            while(ze!=null){

                String current = ze.getName();

                String[] name = current.split("[.]");  
                //System.out.println(ze.getName());
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

            //Reprodueix les imatges amb un fps concret
            playList(images, fps);
            
            if(encode && !decode){
                this.encode(images);
            } else if(!encode && decode){
                this.decode(images);
            } else if(!encode && !decode){
                this.encodedecode(images);
            }
            
            //System.out.println("Done");
          
            return true;
    		
        }catch(IOException ex){
            return false;
        }
    }    
    
    public void encode(ArrayList<BufferedImage> images){
        
    }
     
    public void decode(ArrayList<BufferedImage> images){
    
    }
    
    public void encodedecode(ArrayList<BufferedImage> images){
    
    }
        
    /**
     * Reprodueix les imatges en un Jframe
     * 
     * @param images
     * @param fps 
     */
    private void playList(ArrayList<BufferedImage> images, int fps){
    
        
        JFrame frame = new JFrame();
        JLabel jlabel = new JLabel();
        
        frame.setVisible(true);
        for(BufferedImage img : images){
                
                long t1 = System.currentTimeMillis();
                
                jlabel.setIcon(new ImageIcon(img));
                frame.getContentPane().add(jlabel);
                frame.pack();
             
                long t2 = System.currentTimeMillis();
                int miliseconds = (1000/fps) - (int) (t2 - t1);
                try {
                    if(miliseconds > 0)
                        Thread.sleep(miliseconds);
                } catch(InterruptedException e) {
                    
                }
        }
    }
    
    /**
     * Devuelve las imagenes filtradas segun los parametros
     * 
     * @param newBufferedImage
     * @return 
     */
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
    
    public boolean isBn() {
        return bn;
    }

    public void setBn(boolean bn) {
        this.bn = bn;
    }

    public int getGop() {
        return gop;
    }

    public void setGop(int gop) {
        this.gop = gop;
    }

    public int getSeek() {
        return seek;
    }

    public void setSeek(int seek) {
        this.seek = seek;
    }

    public int getSize_x() {
        return size_x;
    }

    public void setSize_x(int size_x) {
        this.size_x = size_x;
    }

    public int getSize_y() {
        return size_y;
    }

    public void setSize_y(int size_y) {
        this.size_y = size_y;
    }

    public int getQuality() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    public boolean isBatch() {
        return batch;
    }

    public void setBatch(boolean batch) {
        this.batch = batch;
    }
}
