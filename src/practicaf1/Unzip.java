package practicaf1;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Ivan Toro and David Muntal
 */
public class Unzip {

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
    
    static long T_ini = 0L;
    static long T_fin = 0L;
    static long N_coincidencias;
    static ArrayList<ImgContainer> list_teselas = new ArrayList();
    
    /*DefaultTableModel dm;  
    ArrayList<String> lineaComp = new ArrayList();*/
    
    static ArrayList<Tesela> arrayTeselas = new ArrayList<Tesela>();

    public Unzip() {
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
        
        /*this.dm = new DefaultTableModel();
        String[] columnNames = {"id_tesela_base", "x_base", "y_base", "x_destino", "y_destino", "valor_comp"};
        this.dm.setColumnIdentifiers(columnNames);*/
    }

    public boolean unZipIt(String zipFile, String outputFolder, int fps, boolean encode, boolean decode) {

        byte[] buffer = new byte[1024];

        ArrayList<BufferedImage> images = new ArrayList<>();

        try {
            //get the zip file content
            ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
            //get the zipped file list entry
            ZipEntry ze = zis.getNextEntry();

            //create output directory if not exists
            File folder = new File(outputFolder);
            if (!folder.exists()) {
                folder.mkdir();
            }

            //unzip the files  
            while (ze != null) {

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
                if (extension.equals("png") || extension.equals("gif") || extension.equals("bmp") || extension.equals("jpeg") || extension.equals("jpg")) {

                    BufferedImage bufferedImage = ImageIO.read(new File(outputFolder + File.separator + current));

                    // create a blank, RGB, same width and height, and a white background
                    BufferedImage newBufferedImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
                    newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0, Color.WHITE, null);

                    // get filtered image
                    BufferedImage filteredImage = this.getFilteredImage(newBufferedImage);

                    images.add(filteredImage);

                    // write to jpeg file
                    ImageIO.write(filteredImage, "jpeg", new File(outputFolder + File.separator + fileName + ".jpeg"));

                    // delete the original file unziped
                    newFile.delete();
                }

                ze = zis.getNextEntry();
            }

            zis.closeEntry();
            zis.close();

            //Reprodueix les imatges amb un fps concret
            //playList(images, fps);
            if (encode && !decode) {
                this.encode(images);
            } else if (!encode && decode) {
                this.decode(images);
            } else if (!encode && !decode) {
                this.encodedecode(images);
            }

            //System.out.println("Done");
            return true;

        } catch (IOException ex) {
            return false;
        }
    }

    public void encode(ArrayList<BufferedImage> images) {

        ArrayList<ImgContainer> images_encode = new ArrayList<ImgContainer>();
/*
        for (int i = 0; i < images.size() - 1; i++) {
            ImgContainer img_origen = new ImgContainer(ImgContainer.copia(images.get(i)), "Origen");
            ImgContainer img_final = new ImgContainer(ImgContainer.copia(images.get(i + 1)), "Final");

            images_encode.add(getEncode(img_origen, img_final));
        }
        */
        ImgContainer img_origen = new ImgContainer(ImgContainer.copia(images.get(0)), "Origen");
        ImgContainer img_final = new ImgContainer(ImgContainer.copia(images.get(0 + 1)), "Final");

        images_encode.add(getEncode(img_origen, img_final));

        File outputFile = new File("./encode.jpg");
        try {
        FileOutputStream fos = new FileOutputStream(outputFile);
        ImageIO.write(images_encode.get(0).getBufImg(), "jpeg", fos);
        fos.close();
        } catch (FileNotFoundException e1) {
        e1.printStackTrace();
        } catch (IOException e1) {
        e1.printStackTrace();
        }

    }

    public void decode(ArrayList<BufferedImage> images) {

    }

    public void encodedecode(ArrayList<BufferedImage> images) {

    }

    public ImgContainer getEncode(ImgContainer base, ImgContainer destino) {
        T_ini = System.nanoTime();
        teselar(base);
        ImgContainer result = new ImgContainer(ImgContainer.copia(destino.getBufImg()), "resultat");
        ImgContainer result_track = new ImgContainer(ImgContainer.copia(destino.getBufImg()), "proces");
        int id_tesela = 0;
        int n_teselas = list_teselas.size();
        long n_coincidencias = 0L;
        
        for (ImgContainer tes : list_teselas) {
            BufferedImage tesela = tes.getBufImg();
            int tes_x0 = tes.getX0();
            int tes_y0 = tes.getY0();
            System.out.print(tes.getName() + "/" + n_teselas + ": ");
            BufferedImage imagen = result.getBufImg();
            WritableRaster imagen_wras = (WritableRaster) imagen.getData();
            BufferedImage imagen_track = result_track.getBufImg();

            int alto_tesela = tesela.getHeight();
            int ancho_tesela = tesela.getWidth();
            int alto_imagen = imagen.getHeight();
            int ancho_imagen = imagen.getWidth();

            float rt = 0.0F;
            float gt = 0.0F;
            float bt = 0.0F;
            int n = alto_tesela * ancho_tesela;
            for (int i = 0; i < alto_tesela; i++) {
                for (int j = 0; j < ancho_tesela; j++) {
                    Color c = new Color(tesela.getRGB(j, i));
                    rt += c.getRed();
                    gt += c.getGreen();
                    bt += c.getBlue();
                }
            }
            rt /= n;
            gt /= n;
            bt /= n;

            boolean flag = false;

            int output_counter = 0;
            for (int j = tes_y0 - this.getSeek(); j <= tes_y0 + this.getSeek(); j++) {
                for (int i = tes_x0 - this.getSeek(); i <= tes_x0 + this.getSeek(); i++) {
                    if (i >= 0) {
                        if (i <= ancho_imagen - ancho_tesela) {
                            if (j >= 0) {
                                if (j <= alto_imagen - alto_tesela) {
                                    WritableRaster tesela_destino_wras = (WritableRaster) imagen_wras.createChild(i, j, ancho_tesela,
                                            alto_tesela, 0, 0, null);

                                    BufferedImage tesela_destino = new BufferedImage(imagen.getColorModel(), tesela_destino_wras,
                                            imagen.getColorModel().isAlphaPremultiplied(), null);
                                    float rtd = 0.0F;
                                    float gtd = 0.0F;
                                    float btd = 0.0F;
                                    int nd = alto_tesela * ancho_tesela;
                                    for (int h = 0; h < alto_tesela; h++) {
                                        for (int p = 0; p < ancho_tesela; p++) {
                                            Color cd = new Color(tesela_destino.getRGB(p, h));
                                            rtd += cd.getRed();
                                            gtd += cd.getGreen();
                                            btd += cd.getBlue();
                                        }
                                    }
                                    rtd /= nd;
                                    gtd /= nd;
                                    btd /= nd;
                                    double valor = funcioComparadora(rt, gt, bt, rtd, gtd, btd);
                                    if (valor < this.getQuality()) {
                                        n_coincidencias += 1L;

                                        //Blanco 
                                        /*for (int h = j; h < j + alto_tesela; h++) {
                                            for (int p = i; p < i + ancho_tesela; p++) {
                                                imagen.setRGB(p, h, new Color(255, 255, 255).getRGB());
                                            }
                                        }
                                        */
                                        
                                        //Suave
                                        for (int h = j; h < j + alto_tesela; h++) {
                                            for (int p = i; p < i + ancho_tesela; p++) {
                                                imagen.setRGB(p, h, new Color((int)rtd, (int)gtd, (int)btd).getRGB());
                                            }
                                        }
                                        
                                        if(!tesExists(arrayTeselas, id_tesela)){
                                            arrayTeselas.add(new Tesela(id_tesela,tes_x0,tes_y0,i,j,valor));
                                        }
                                        /*this.dm.addRow();
                                        
                                        this.lineaComp.add(String.valueOf(id_tesela)+", "+String.valueOf(tes_x0)+", "+String.valueOf(tes_y0)+", "+String.valueOf(i)+", "+String.valueOf(j)+", "+String.valueOf(valor));
                                        */              
                                        System.out.print(tes_x0 + "," + tes_y0 + "   " + valor);

                                        result = new ImgContainer(ImgContainer.copia(imagen), "Final");

                                        //result_track = new ImgContainer(ImgContainer.copia(dibujarTesela(imagen_track, tes)), "Proces");
                                        break;
                                    }
                                    if (this.getSeek() == 0) {
                                        System.out.print(".");
                                    } else if (this.getSeek() < 10) {
                                        if (output_counter % this.getSeek() == 0) {
                                            System.out.print(".");
                                        }
                                    } else if (Math.sqrt(output_counter) % this.getSeek() == 0.0D) {
                                        System.out.print(".");
                                    }
                                    output_counter++;
                                }
                            }
                        }
                    }
                }
            }
            id_tesela++;
            System.out.println();
        }
        N_coincidencias = n_coincidencias;
        T_fin = System.nanoTime();
        
        //Llamamos a la función que guarda los resultados en un fichero
        saveResults(arrayTeselas);
      
        return result;
    }

    public void saveResults(ArrayList<Tesela> teselas){
        //creo el archivo donde guardo mis detalles de compresion para poder hacer el DECODE    
        try{
            File compression = new File("./compression.txt");
            if(!compression.exists()){
                compression.createNewFile();
            }
            FileWriter fw = new FileWriter(compression.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
                for (Tesela tesela : teselas){ 
                    bw.write(tesela.toString());
                    bw.newLine();
                }

            bw.close();
            fw.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public boolean tesExists(ArrayList<Tesela> arrayTeselas, int id_tesela){    
        for(Tesela tes: arrayTeselas){
            if(tes.getId_tesela_base() == id_tesela){
                return true;
            }
        } 
        return false;
    }
    
    private static double funcioComparadora(double x1, double x2, double x3, double y1, double y2, double y3) {
        return 10.0D * (Math.sqrt(x1 - y1) + Math.sqrt(x2 - y2) + Math.sqrt(x3 - y3));
    }

    public BufferedImage dibujarTeselas(BufferedImage bi) {
        int altura = bi.getHeight();
        int ancho = bi.getWidth();

        int y_teselas = this.size_x;
        if (this.size_y > 0) {
            y_teselas = this.size_y;
        }
        double tamx = ancho / this.size_x;
        double tamy = altura / y_teselas;

        Graphics2D g2d = bi.createGraphics();

        g2d.setColor(Color.BLUE);
        for (int i = 0; i < this.size_x; i++) {
            for (int j = 0; j < y_teselas; j++) {
                if (i * tamx + tamx <= ancho) {
                    if (i * tamy + tamy <= altura) {
                        g2d.drawLine((int) (i * tamx), 0, (int) (i * tamx), altura);
                        g2d.drawLine(0, (int) (j * tamy), ancho, (int) (j * tamy));
                    }
                }
            }
        }
        g2d.dispose();
        return bi;
    }

    public static BufferedImage dibujarTesela(BufferedImage bi, ImgContainer tes) {
        int tamx = tes.getBufImg().getWidth();
        int tamy = tes.getBufImg().getHeight();
        int x0 = tes.getX0();
        int y0 = tes.getY0();
        for (int i = x0; i < x0 + tamx; i++) {
            for (int j = y0; j < y0 + tamy; j++) {
                int pixelInt = bi.getRGB(i, j);
                int r = new Color(pixelInt).getRed();
                int g = new Color(pixelInt).getGreen();
                int b = new Color(pixelInt).getBlue();
                bi.setRGB(i, j, new Color(r / 2, g / 2, b / 2).getRGB());
            }
        }
        return bi;
    }

    public ArrayList teselar(ImgContainer x) {
        list_teselas = new ArrayList();
        BufferedImage bi = x.getBufImg();
        int altura = bi.getHeight();
        int ancho = bi.getWidth();

        int y_teselas = this.size_x;
        if (this.size_y > 0) {
            y_teselas = this.size_y;
        }
        double tamx = ancho / this.size_x;
        double tamy = altura / y_teselas;

        int h = 0;
        WritableRaster wras = (WritableRaster) bi.getData();
        for (int i = 0; i < this.size_x; i++) {
            for (int j = 0; j < y_teselas; j++) {
                if (i * tamx + tamx <= ancho) {
                    if (j * tamy + tamy <= altura) {
                        WritableRaster tesela = (WritableRaster) wras.createChild((int) (i * tamx), (int) (j * tamy),
                                (int) tamx, (int) tamy, 0, 0, null);
                        BufferedImage imagen = new BufferedImage(bi.getColorModel(), tesela,
                                bi.getColorModel().isAlphaPremultiplied(), null);
                        ImgContainer tes = new ImgContainer(imagen, Integer.toString(h));
                        tes.setX0((int) (i * tamx));
                        tes.setY0((int) (j * tamy));
                        list_teselas.add(tes);
                        h++;
                    }
                }
            }
        }
        return list_teselas;
    }

    /**
     * Reprodueix les imatges en un Jframe
     *
     * @param images
     * @param fps
     */
    private void playList(ArrayList<BufferedImage> images, int fps) {

        JFrame frame = new JFrame();
        JLabel jlabel = new JLabel();

        frame.setVisible(true);
        for (BufferedImage img : images) {

            long t1 = System.currentTimeMillis();

            jlabel.setIcon(new ImageIcon(img));
            frame.getContentPane().add(jlabel);
            frame.pack();

            long t2 = System.currentTimeMillis();
            int miliseconds = (1000 / fps) - (int) (t2 - t1);
            try {
                if (miliseconds > 0) {
                    Thread.sleep(miliseconds);
                }
            } catch (InterruptedException e) {

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

        if (isNeg()) {
            imgFiltered = filter.negFilter(imgFiltered);
        }
        if (isSep()) {
            imgFiltered = filter.sepFilter(imgFiltered);
        }
        if (isBN()) {
            imgFiltered = filter.BNFilter(imgFiltered);
        }
        if (getBin() != 0) {
            imgFiltered = filter.binFilter(imgFiltered, getBin());
        }
        if (getAve() != 0) {
            imgFiltered = filter.aveFilter(imgFiltered, getAve());
        }

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
