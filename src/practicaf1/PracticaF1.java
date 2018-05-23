package practicaf1;

/**
 *
 * @author Ivan Toro and David Muntal
 */
public class PracticaF1 {

    public static void main( String[] args )
    {             
        String input = "", output = "";
        int fps = 24;
        boolean encode = false, decode = false;

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
                case "-e":
                case "--encode":
                    encode = true;
                    break;
                case "-d":
                case "--decode":
                    decode = true;
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
                case "--nTiles":
                    int j = getValueTiles(args, i);
                    unZip.setSize_x(Integer.parseInt(args[i+1]));
                    if(j > 1){
                        unZip.setSize_y(Integer.parseInt(args[i+1]));
                    }
                    i += j;
                    break;
                case "--seekRange":
                    unZip.setSeek(Integer.parseInt(args[i+1]));
                    i++;
                    break;
                case "--GOP":
                    unZip.setGop(Integer.parseInt(args[i+1]));
                    i++;
                    break;
                case "--quality":
                    unZip.setQuality(Integer.parseInt(args[i+1]));
                    i++;
                    break;
                case "-b":
                case "--batch":
                    unZip.setBatch(true);
                    break;
            }        
        }
        boolean ok = false;
        
        System.out.println("----- PROJECTE PRÀCTIQUES -----");
        
        do{ 
            ok = unZip.unZipIt(input, output, fps, encode, decode);
            
            if(!ok){
                System.out.println("\nParametros incorrectos!");
            }

        } while(!ok);
    }
    
    public static int getValueTiles(String[] args, int index){
        int val = -1;
        
        if(index + 1 <= args.length - 1){
            if(args[index + 1].split("-").length <= 1){
                val = 1;
            }
        } 
        if(index + 2 <= args.length - 1){
            if(args[index + 2].split("-").length <= 1){
                val = 2;
            }
        }
  
        return val;
    }
    
    // Ho utilitzarem més endevant amb el jcommander
    /*
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
    }*/
}

