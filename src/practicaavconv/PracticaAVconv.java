package practicaavconv;

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
    
    /*
    public class Settings {
 
        @Parameter(names = "-url", description = "Server address", required = true)
        private String url;

        @Parameter(names = "-token", description = "Authentication token", required = true)
        private String authenticationToken;

        @Parameter(names = "-month", description = "Number of month (1-12) for timesheet", required = true)
        private Integer month;

        @Parameter(names = "-year", description = "Year", required = true)
        private Integer year;
    }
    */
}

