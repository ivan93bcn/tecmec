package practicaavconv;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 *
 * @author Ivan Toro and David Muntal
 */
public class Filtros {

    /**
     * Funcion que devuelve la imagen pasada con el filtro de blanco y negro
     * 
     * @param img
     * @return 
     */
    public BufferedImage BNFilter(BufferedImage img) {
        
        int width = img.getWidth();
        int height = img.getHeight();
        BufferedImage res = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        int r, g, b;
        for(int	i = 0; i < width; i++) {
            for(int j = 0; j < height; j++){
                Color c = new Color(img.getRGB(i, j));
                
                r = (int)(c.getRed() * 0.299);
                g = (int)(c.getGreen() * 0.587);
                b = (int)(c.getBlue() * 0.114);
                
                Color newColor = new Color(r+g+b, r+g+b, r+g+b);
                
                res.setRGB(i, j, newColor.getRGB());
            }
        }    
        return res;
    }
    
    /**
     * Funcion que devuelve la imagen pasada con el filtro de binarizacion
     * 
     * @param img
     * @param mid
     * @return 
     */
    public BufferedImage binFilter(BufferedImage img, int mid) {
        
        int width = img.getWidth();
        int height = img.getHeight();
        BufferedImage res = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        BufferedImage imgBN = this.BNFilter(img);
        
        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                Color c = new Color(imgBN.getRGB(i, j));
                Color newColor;
                
                if(c.getRed() < mid) {
                    newColor = new Color(0, 0, 0);
                } else {
                    newColor = new Color(255, 255, 255);
                }             
                res.setRGB(i, j, newColor.getRGB());
            }
        }
        return res;
    }
    
    /**
     * Funcion que devuelve la imagen pasada con el filtro de sepia
     * 
     * @param img
     * @return 
     */
    public BufferedImage sepFilter(BufferedImage img) {
        
        int width = img.getWidth();
        int height = img.getHeight();
        BufferedImage res = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        int r, g, b;
        
        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                Color c	= new Color(img.getRGB(i, j));
                
                r = (int) (((int) (c.getRed()) * 0.393)+((int) (c.getGreen()) * 0.769)+((int) (c.getBlue()) * 0.189));
                g = (int) (((int) (c.getRed()) * 0.349)+((int) (c.getGreen()) * 0.686)+((int) (c.getBlue()) * 0.168));
                b = (int) (((int) (c.getRed()) * 0.272)+((int) (c.getGreen()) * 0.534)+((int) (c.getBlue()) * 0.131));
                if (r > 255)  
                    r = 255;
                if (g > 255)  
                    g = 255;
                if (b > 255)  
                    b = 255;
                
                Color newColor = new Color(r,g,b);
                res.setRGB(i, j,newColor.getRGB());
            }
        }
        return res;
    }
    
    /**
     * Funcion que devuelve la imagen pasada con el filtro de negativos
     * 
     * @param img
     * @return 
     */
    public BufferedImage negFilter(BufferedImage img) {
        
        int width = img.getWidth();
        int height = img.getHeight();
        BufferedImage res = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        int r, g, b;
        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                Color c = new Color(img.getRGB(i, j));
                
                r = 255 - c.getRed();
                g = 255 - c.getGreen();
                b = 255 - c.getBlue();
                
                Color newColor = new Color(r, g, b);
                
                res.setRGB(i, j, newColor.getRGB());
            }
        }       
        return res;
    }
    
    /**
     * Funcion que devuelve la imagen pasada con el filtro de averaging
     * 
     * @param img
     * @param valor
     * @return 
     */
    public BufferedImage aveFilter(BufferedImage img, int valor) {
        int[][] myFilter = new int[valor][valor];

        if (valor%2 == 0){ //si es par, lo dejo en impar
            valor-=1;
        }
        
        for (int x=0; x< valor; x++){ //creo el filtro con el tamaño que me pasan
            for(int y= 0; y<valor; y++){
                myFilter[x][y]=1;                
            }
        }
        
        int distancia = valor/2;
        int width = img.getWidth();
        int height = img.getHeight();
        BufferedImage res = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        
        for(int i = distancia; i + distancia< height; i++) {
            for(int j = distancia; j +distancia < width; j++) {
                Color tempColor = getAverageValue(img, i, j, myFilter, distancia);
                    res.setRGB(j, i, tempColor.getRGB());
            }
        }
        return res;
    }
    
   
    private Color getAverageValue(BufferedImage img, int y, int x, int[][] filter, int dis) {
        int r = 0, g = 0, b = 0;
        for (int j = -dis; j <= dis; j++) {
            for (int k = -dis; k <= dis; k++) {
                Color cr = new Color(img.getRGB(x + k, y + j));
                     int crr   =cr.getRed();
                r += (filter[dis + j][dis + k] * (crr));
                g += (filter[dis + j][dis + k] * (new Color(img.getRGB(x + k, y + j))).getGreen());
                b += (filter[dis + j][dis + k] * (new Color(img.getRGB(x + k, y + j))).getBlue());
            }

        }
        r = r / sum(filter);
        g = g / sum(filter);
        b = b / sum(filter);
        return new Color(r, g, b);
    }

    private int sum(int[][] filter) {
        int sum = 0;
        for (int y = 0; y < filter.length; y++) {
            for (int x = 0; x < filter[y].length; x++) {
                sum += filter[y][x];
            }
        }
        return sum;
    }
    


}