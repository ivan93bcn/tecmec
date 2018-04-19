package practicaavconv;

import com.beust.jcommander.Parameter;

/**
 *
 * @author Ivan Toro and David Muntal
 */
public class PracticaAVconv {

    public static void main( String[] args )
    {             
        String input = "", output = "";
        int fps = 0;

        Unzip unZip = new Unzip();
        
        /*Settings settings = new Settings();
        new JCommander(settings, args);*/
        
        int size = args.length - 1;
        
        for(int i = 0; i <= size; i++){
            
            switch (args[i]){
                case "-i":
                case "--input":
                    input = args[i+1]; 
                    i++;
                    break;
                case "-o":
                case "--output":
                    output = args[i+1];
                    i++;
                    break;
                case "--fps":
                    fps = Integer.parseInt(args[i+1]);
                    i++;
                    break;
                case "--binarization":
                    unZip.setBin(Integer.parseInt(args[i+1]));
                    i++;
                    break; 
                case "--negative":
                    unZip.setNeg(true);
                    break;
                case "--averaging":
                    unZip.setAve(Integer.parseInt(args[i+1]));
                    i++;
                    break;
                case "--sepia":
                    unZip.setSep(true);
                    break;
                case "--bn":
                    unZip.setBN(true);
                    break;
            }        
        }
        
        boolean ok = false;
        
        System.out.println("----- PROJECTE PRÀCTIQUES -----");
        
        do{

            //Unzip and convert to JPG if necessary
            ok = unZip.unZipIt(input+".zip", output, fps);

            if(!ok){
                System.out.println("\nParametros incorrectos!");
            }

        } while(!ok);
    }
    
    public class Args {

        @Parameter(names = { "-i", "--input" }, description = "Input file", required = true)
        private Integer input = 1;

        @Parameter(names = { "-o", "--output" }, description = "Output folder", required = true)
        private String output;

        @Parameter(names = "--fps", description = "FPS", required = false)
        private Integer fps;

        @Parameter(names = "--binarization", description = "Binarization filter", required = false)
        private Integer bin;

        @Parameter(names = "--negative", description = "Negative filter", required = false)
        private boolean neg = true;

        @Parameter(names = "--averaging", description = "Averaging filter", required = false)
        private boolean ave = true;

        @Parameter(names = "--sepia", description = "Sepia filter", required = false)
        private boolean sep = true;

        @Parameter(names = "--bn", description = "Black and white filter", required = false)
        private boolean bn = true;
    }
}

