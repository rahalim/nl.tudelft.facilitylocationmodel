
import edu.uniandes.copa.jga.*;

import edu.uniandes.copa.ioutils.*;

/**
 * Application for solving a Bi-criteria Capacitated Facility Location Problem
 * (CFLP). This problem is the capacitated extension to the original facility
 * location problem worked by Medaglia, Palacios and Villegas (2004).
 * <p>
 * Optimization criteria are total cost (including fixed and transportation
 * costs), and covering index (service level measure).
 * 
 * @author A.Medaglia - E.Gutiérrez
 * @version %I%, %G%
 *  
 */
public class FLPMainReadCoord
{

    /**
     * Starting point for the CFLP application.
     * @param args command line arguments.
     */
    public static void main(String args[])
    {
        String configFileName = args[0];
        generateDistancesProblem(configFileName);
    }

    /**
     * Generates randomly the data for a CFL problem. Parameters for data
     * generation must be defined in a property file ".ini" referenced in the
     * CFLPcSettings.ini file. (see data/p1/p1.ini)
     * @param configFileName configuraration file name
     */
    public static void generateDistancesProblem(String configFileName)
    {
        GASettings gaSettings = GASettings.instance(configFileName);

        BOFLPSettings problemSettings = BOFLPSettings.instance(gaSettings.getProblemSettingsFile());

        BOFLP CFLProblem = BOFLP.instance();

        String str = new String(problemSettings.getDataFileName() + ".coord.txt");
        
        System.out.println("halo"+str);

        CFLProblem.readCoordinatesFile(str);
        
        CFLProblem.drawLocations();
        
        iostd.readline("Press Return to finish  : ");

        System.exit(0);
    }

}