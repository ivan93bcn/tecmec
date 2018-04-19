package practicaavconv;

import java.util.Scanner;

/**
 *
 * @author Ivan Toro and David Muntal
 */
public class PracticaAVconv {
    
    //public static String INPUT_ZIP_FILE = "test.zip";
    //public static String OUTPUT_FOLDER = "Cubo";

    public static void main( String[] args )
    {      
        Scanner keyboard = new Scanner(System.in);
        
        boolean ok;
        
        System.out.println("----- PROJECTE PRÀCTIQUES -----");
        
        do{
            System.out.print("\nNombre del archivo: ");

            String file = keyboard.next();
            String folder = file;

            //Unzip and convert to JPG if necessary
            Unzip unZip = new Unzip();
            ok = unZip.unZipIt(file+".zip",folder);
            
            if(!ok){
                System.out.println("\nIntroduzca un nombre correcto!");
            }
            
        } while(!ok);
    }
    
}

