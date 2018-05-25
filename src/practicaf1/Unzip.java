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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipOutputStream;
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
    private int fps;
    
    static int n_teselas;
    static long T_ini = 0L;
    static long T_fin = 0L;
    static long N_coincidencias;
    static ArrayList<ImgContainer> list_teselas = new ArrayList(); 
    
    static ArrayList<Tesela> arrayTeselas = new ArrayList<Tesela>();
        
    static ArrayList<ArrayList<Tesela>> finalData = new ArrayList<ArrayList<Tesela>>();
        
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
        this.fps = 12;
    }

    public boolean unZipIt(String zipFile, String outputFolder, boolean encode, boolean decode) {

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
            folderToZip("./Cubo", "CuboJPG");
            
            if (encode && !decode) {
                this.encode(images);
            } else if (!encode && decode) {
                this.decode();
            } else if (encode && decode) {
                this.encodedecode(images);
            } else{
                playList(images, this.fps);
            }
            
            //Reprodueix les imatges amb un fps concret

            System.out.println("Done");
            
            
            
            return true;

        } catch (IOException ex) {
            return false;
        }
        
    }

    public void encode(ArrayList<BufferedImage> images) {
        T_ini = System.nanoTime();
        System.out.println("Codificando imagenes...");
        
        ArrayList<ImgContainer> images_origenes = new ArrayList<ImgContainer>();
        ArrayList<ImgContainer> images_finales = new ArrayList<ImgContainer>();
        ArrayList<BufferedImage> imagesEncoded = new ArrayList<BufferedImage>();
        
        //Añadimos en un array las imagenes de origen
        for(int i = 0; i < images.size(); i += this.gop+1){
            images_origenes.add(new ImgContainer(ImgContainer.copia(images.get(i)), "Origen".concat(String.valueOf(i))));
        }
        
        //Añadimos en un array las imagenes a codificar
        for(int i = 0; i < images.size(); i++){
            if(i % (this.gop+1) != 0){
               images_finales.add(new ImgContainer(ImgContainer.copia(images.get(i)), "Final".concat(String.valueOf(i))));
            }
        }
        
        try {
            int x = 0, xf = 0;
            int contNombre=0;
            
            int correctFinals = (images_origenes.size()-1) * this.gop; //imagen origen de inicio y de final
            int diff =  images_finales.size() - correctFinals;

            for(int i = 0; i < images_origenes.size(); i++){
                String nombreArchivo = "";
                if (contNombre<10){
                    nombreArchivo = "codification/Cubo0"+contNombre+".jpeg";
                }else{
                    nombreArchivo = "codification/Cubo"+contNombre+".jpeg";
                }
                File outputFile = new File(nombreArchivo);
                FileOutputStream fos = new FileOutputStream(outputFile);
                ImageIO.write(images_origenes.get(i).getBufImg(), "jpeg", fos);
                imagesEncoded.add(images_origenes.get(i).getBufImg());
                contNombre++;
                if(i == images_origenes.size() - 1){
                    
                    //System.out.println("origen: " + i);
                    for(int j = correctFinals; j < images_finales.size(); j++){
                        if (contNombre<10){
                             nombreArchivo = "codification/Cubo0"+contNombre+".jpeg";
                        }else{
                             nombreArchivo = "codification/Cubo"+contNombre+".jpeg";
                        }
                        outputFile = new File(nombreArchivo);
                        fos = new FileOutputStream(outputFile);
                        
                        ImgContainer image_code = getEncode(images_origenes.get(i), images_finales.get(j));
                        //System.out.println("origen: " + i + " final: " + j);
                        ImageIO.write(image_code.getBufImg(), "jpeg", fos);
                        imagesEncoded.add(image_code.getBufImg());
                        fos.close();
                        contNombre++;
                    }
                } else{
                    for(int j = 0; j < this.gop; j++){
                        if (contNombre < 10){
                             nombreArchivo = "codification/Cubo0"+contNombre+".jpeg";
                        }else{
                             nombreArchivo = "codification/Cubo"+contNombre+".jpeg";
                        }
                        outputFile = new File(nombreArchivo);
                        fos = new FileOutputStream(outputFile);
                                              
                        ImgContainer image_code = getEncode(images_origenes.get(i), images_finales.get(x));
                        //System.out.println("origen: " + i + " final: " + x);
                        ImageIO.write(image_code.getBufImg(), "jpeg", fos);
                        imagesEncoded.add(image_code.getBufImg());
                        fos.close();
                        x += 1;
                        contNombre++;
                    }
                }
            }
           // fos.close();
            
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        
        saveResults(finalData);
                
        System.out.println("Reproduciendo imagenes codificadas...");
        T_fin = System.nanoTime();
        System.out.println("Tiempo de Codificación: "+(T_fin - T_ini)/1000 + " milisegundos");
        
        folderToZip("./codification", "codification");     
        
        
        File f1 = new File("./codification.zip");
        long codificado = f1.length();
        File f2 = new File("./CuboJPG.zip");
        long original = f2.length();
        float valor = codificado*100/original;
        System.out.println("original: "+original+", codificado:"+codificado);
        System.out.println("La proporcion de compresión es 100:"+valor);
        
        
        
        playList(imagesEncoded, this.fps);
        
        
    }

    /*FALTA QUE LE PASEMOS EL ARCHIVO COMPRIMIDO
       *Lo abra, guarde las imagenes en el arrayList de Buffered images
       *Abra el archivo de texto y se lo pase al metodo decodeFile
        Y luego llame al metodo getDecode y le pase la imagen original 0, la imagen codificada, y la matriz myData
    */
    public void decode() throws IOException {
        T_ini= System.nanoTime();
        System.out.println("Decodificando imagenes...");
        
        ArrayList<ArrayList<Tesela>> arrayTes = decodeFile(); //llenamos el arraylist con los datos del fichero
        
        ArrayList<int[][]> datos = new ArrayList<int[][]>();
        
        for (ArrayList<Tesela> arrayT: arrayTes){
            DefaultTableModel dm = new DefaultTableModel();
            String[] columnNames = { "id_tesela_base", "x_base", "y_base", "x_destino", "y_destino", "valor_comp" };
            dm.setColumnIdentifiers(columnNames);
            for(Tesela tes: arrayT){
                String valor = String.valueOf(tes.getValor_comp());
                dm.addRow(new Object[] { tes.getId_tesela_base(), tes.getX_base(),tes.getY_base(), tes.getX_destino(), tes.getY_destino(), Double.valueOf(valor)});
            }
            datos.add(tableToMatrix(dm));
        }
        
        String zipFile = "codification.zip";
        String outputFolder = "codification";
        byte[] buffer = new byte[1024];
        ArrayList<BufferedImage> imagesEncoded = new ArrayList<BufferedImage>();

        //get the zip file content
        ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
        //get the zipped file list entry
        ZipEntry ze = zis.getNextEntry();

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

                imagesEncoded.add(newBufferedImage);

                // write to jpeg file
                ImageIO.write(newBufferedImage, "jpeg", new File(outputFolder + File.separator + fileName + ".jpeg"));

                // delete the original file unziped
                newFile.delete();
            }

            ze = zis.getNextEntry();
        }

        zis.closeEntry();
        zis.close();
        
        decodeloop(datos, imagesEncoded);        
    }

    public void decodeloop(ArrayList<int[][]> datos, ArrayList<BufferedImage> imagesEncoded) throws FileNotFoundException, IOException{
        ArrayList<BufferedImage> imagesDecoded = new ArrayList<BufferedImage>();
        int cont = 0;
        for(int[][] data: datos){
            if(cont < imagesEncoded.size()){
                imagesDecoded.add(imagesEncoded.get(cont));
            }
            int gops = cont;
            for(int i = 0; i < this.gop; i++){
                if(cont < imagesEncoded.size() && (gops+1) < imagesEncoded.size()){
                    imagesDecoded.add(getDecode(new ImgContainer(imagesEncoded.get(cont),""), new ImgContainer(imagesEncoded.get(gops+1),""), data).getBufImg());
                    gops++;
                }
            }
            cont += this.gop+1;
        }
        
        System.out.println("Reproduciendo imagenes decodificadas...");
        T_fin = System.nanoTime();
        System.out.println("Tiempo de Decodificación: "+(T_fin - T_ini)/1000000 + " milisegundos");
        
        playList(imagesDecoded, this.fps);
    }
    
    public void encodedecode(ArrayList<BufferedImage> images) throws IOException {
        this.encode(images);
        this.decode();
    }
    
    //Abre el archivo y crea una tabla por los detalles de cada imagen codificada, y guarda en el ArrayList de tablas
    public ArrayList<ArrayList<Tesela>> decodeFile() throws FileNotFoundException, IOException{
      
        ZipInputStream zis = new ZipInputStream(new FileInputStream("codification.zip"));
        ZipEntry ze = zis.getNextEntry();
        
        /*String[] name = current.split("[.]");
        //System.out.println(ze.getName());
        String fileName = name[0];
        String extension = name[1];

        File newFile = new File(outputFolder + File.separator + current);*/
        
        String name="codification/compression.txt";
        ArrayList<ArrayList<Tesela>> arrayDecode = new ArrayList<ArrayList<Tesela>>();

        try {   
            FileReader fr = new FileReader(name);
            BufferedReader br = new BufferedReader(fr);

            String sCurrentLine;            
            
            ArrayList<Tesela> arrayTeselasDecode = new ArrayList<Tesela>();

            while ((sCurrentLine = br.readLine()) != null) {                
                String[] myRow = sCurrentLine.trim().split("\\s*,\\s*");
                //miramos en que parte del archivo estamos
                int numFoto = 0;

                //Estamos leyendo la cabecera de una nueva sub-imagen, Creamos la tabla para esa imagen
                if (myRow.length == 2){
                    numFoto = Integer.valueOf(myRow[1]);
                    arrayTeselasDecode = new ArrayList<Tesela>();

                }else if(myRow.length == 1){ //Estamos leyendo el fin de esa imagen                 
                    arrayDecode.add(arrayTeselasDecode);

                }else if(myRow.length == 6){ //Estamos leyendo los datos de teselas de esa imagen

                    String[] data = sCurrentLine.split(",");
                    arrayTeselasDecode.add(new Tesela(Integer.parseInt(data[0]),Integer.parseInt(data[1]),Integer.parseInt(data[2]),Integer.parseInt(data[3]),Integer.parseInt(data[4]),Double.parseDouble(data[5])));
                }
            }          
            
            br.close();
            fr.close();

        } catch (IOException e) { 
            e.printStackTrace();
        }        
        return arrayDecode;
    }

    //recibe una tabla y devuelve una matriz con los datos de las teselas de esa imagen
    public int[][] tableToMatrix(DefaultTableModel imgTable){
        int[][] myData = new int[imgTable.getColumnCount()][imgTable.getRowCount()];
        
        //System.out.println(myData.length + " - " + myData[0].length);
        for (int i = 0; i < myData.length; i++) {
          for (int j = 0; j < myData[i].length; j++)
          {
            //System.out.println(myData[i].length + " ; " + i + " , " + j);
            try
            {
              myData[i][j] = Integer.parseInt(imgTable.getValueAt(j, i).toString());
            }
            catch (NumberFormatException e)
            {
              myData[i][j] = ((int)Double.parseDouble(imgTable.getValueAt(j, i).toString()));
            }
          }
        }  
        return myData;
    }    
 
    public ImgContainer getDecode(ImgContainer base, ImgContainer encoded, int[][] data){
        
        teselar(base);
        ImgContainer result = new ImgContainer(ImgContainer.copia(encoded.getBufImg()), "encoded");

        BufferedImage img = result.getBufImg();
        int count_tesela = 0;
        n_teselas = data[0].length;
        for (int k = data[0].length - 1; k >= 0; k--)
        {
          int x0_dest = data[3][k];
          int y0_dest = data[4][k];
          ImgContainer tesela = (ImgContainer)list_teselas.get(data[0][k]);

          //System.out.println("decoding id " + tesela.getName() + " (" + count_tesela + " of " + n_teselas + ")");
          BufferedImage tes = tesela.getBufImg();
          int alto_tesela = tes.getHeight();
          int ancho_tesela = tes.getWidth();

            for (int i = 0; i < alto_tesela; i++) {
                for (int j = 0; j < ancho_tesela; j++){
                  int RGB = tes.getRGB(j, i);
                  img.setRGB(j + x0_dest, i + y0_dest, RGB);
                }
            }    
          count_tesela++;
        }
        return result;
   }
  
    public ImgContainer getEncode(ImgContainer base, ImgContainer destino) {
        
        teselar(base);
        ImgContainer result = new ImgContainer(ImgContainer.copia(destino.getBufImg()), "resultat");
        ImgContainer result_track = new ImgContainer(ImgContainer.copia(destino.getBufImg()), "proces");
        int id_tesela = 0;
        n_teselas = list_teselas.size();
        long n_coincidencias = 0L;
        arrayTeselas = new ArrayList<Tesela>();
    
        for (ImgContainer tes : list_teselas) {
            BufferedImage tesela = tes.getBufImg();
            int tes_x0 = tes.getX0();
            int tes_y0 = tes.getY0();
            //System.out.print(tes.getName() + "/" + n_teselas + ": ");
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

                                        result = new ImgContainer(ImgContainer.copia(imagen), "DecodeImg");

                                        //result_track = new ImgContainer(ImgContainer.copia(dibujarTesela(imagen_track, tes)), "Proces");
                                        break;
                                    }
                                    if (this.getSeek() == 0) {
                                        //System.out.print(".");
                                    } else if (this.getSeek() < 10) {
                                        if (output_counter % this.getSeek() == 0) {
                                            //System.out.print(".");
                                        }
                                    } else if (Math.sqrt(output_counter) % this.getSeek() == 0.0D) {
                                        //System.out.print(".");
                                    }
                                    output_counter++;
                                }
                            }
                        }
                    }
                }
            }
            id_tesela++;
            //System.out.println();
        }
        N_coincidencias = n_coincidencias;
        
        finalData.add(arrayTeselas);
        //saveResults(arrayTeselas, indice);
      
        return result;
    }

    public void saveResults(ArrayList<ArrayList<Tesela>> teselas){
        //creo el archivo donde guardo mis detalles de compresion para poder hacer el DECODE    
        try{
            //create output directory if not exists
            File folder = new File("codification");
            if (!folder.exists()) {
                folder.mkdir();
            }
            File compression = new File("codification/compression.txt");
            if(!compression.exists()){
                compression.createNewFile();
            }
            FileWriter fw = new FileWriter(compression.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
             
            int mycont=0;
            for (ArrayList<Tesela> arrayT : teselas){
                mycont++;
                bw.write("Foto,"+mycont);
                bw.newLine();
                for (Tesela tesela : arrayT){ 
                    bw.write(tesela.toString());
                    bw.newLine();
                }
                bw.write("Fin"); //indica el final de los datos de esa imagen
                bw.newLine();
                
                if (mycont == this.gop){
                    mycont++;
                }
            
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
        for(int i = 0; i < 4; i++){
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

    public static void folderToZip(String folder, String zipFolder){
        File directoryToZip = new File(folder);
        List<File> fileList = new ArrayList<File>();
        getAllFiles(directoryToZip, fileList);
        writeZipFile(directoryToZip, fileList, zipFolder);
    }
    
    public static void getAllFiles(File dir, List<File> fileList) {
        File[] files = dir.listFiles();
        for (File file : files) {
            fileList.add(file);
            if (file.isDirectory()) {
                getAllFiles(file, fileList);
            }
        }
    }

    public static void writeZipFile(File directoryToZip, List<File> fileList, String zipFolder) {

        try {
                FileOutputStream fos = new FileOutputStream(zipFolder + ".zip");
                ZipOutputStream zos = new ZipOutputStream(fos);

                for (File file : fileList) {
                        if (!file.isDirectory()) { // we only zip files, not directories
                                addToZip(directoryToZip, file, zos);
                        }
                }

                zos.close();
                fos.close();
        } catch (FileNotFoundException e) {
                e.printStackTrace();
        } catch (IOException e) {
                e.printStackTrace();
        }
    }

    public static void addToZip(File directoryToZip, File file, ZipOutputStream zos) throws FileNotFoundException, IOException {

        FileInputStream fis = new FileInputStream(file);

        // we want the zipEntry's path to be a relative path that is relative
        // to the directory being zipped, so chop off the rest of the path
        String zipFilePath = file.getCanonicalPath().substring(directoryToZip.getCanonicalPath().length() + 1,file.getCanonicalPath().length());
        ZipEntry zipEntry = new ZipEntry(zipFilePath);
        zos.putNextEntry(zipEntry);

        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
                zos.write(bytes, 0, length);
        }
        zos.closeEntry();
        fis.close();
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

    public int getFps() {
        return fps;
    }

    public void setFps(int fps) {
        this.fps = fps;
    }
    
}
