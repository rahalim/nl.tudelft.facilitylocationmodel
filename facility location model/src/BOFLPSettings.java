
import edu.uniandes.copa.jga.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * This class holds global information about the CFL problem to be used in the
 * GA.
 * <p>
 * This class implements the singleton design pattern because only one instance
 * should be instantiated.
 * 
 * @author A.Medaglia - E.Gutiérrez
 * @version %I%, %G%
 */
public final class BOFLPSettings extends Object
{

    /**
     * Unique instance (singleton design pattern)
     */
    private static BOFLPSettings instance = null;

    /**
     * Settings loaded from configuration file
     */
    private Properties data;

    /**
     * File name of the settings {@link Properties}file
     */
    private String dataFileName;

    
    /**
     * Indicates if the problem is capacitated or not
     */
    private boolean capacitated;
    
    /**
     * @return Returns the capacitated.
     */
    public boolean isCapacitated()
    {
        return capacitated;
    }
    /**
     * Boolean flag to indicate if the user want to draw location after the GA
     * running.
     */
    private boolean drawLocations;

    /**
     * minimum and maximun values of objective functions loaded from ProblemSettings
     * used to computated the Pareto's frontier performance metric
     */
    private double minCost, maxCost,minCover,maxCover;

    /**
     * Constructor for objects of class ProblemSettings
     * @param Filename Problem settings file name
     */
    private BOFLPSettings(String Filename)
    {
        load(Filename);
        initializeElements();
    }

    /**
     * Returns the singleton instance of {@link BOFLPSettings}. If it does not
     * exist, it creates it.
     * 
     * It uses the problem data file from {@link GASettings}
     * @param Filename Problem settings file name
     * 
     * @return A handle to the unique instance of {@link BOFLPSettings}.
     *  
     */
    static public BOFLPSettings instance(String Filename)
    {
        if (instance == null)
        {
            instance = new BOFLPSettings(Filename);
        }
        return instance;
    }

    /**
     * Returns the singleton instance of {@link BOFLPSettings}.
     * 
     * @return A handle to the unique instance of {@link BOFLPSettings}.
     *  
     */
    static public BOFLPSettings instance()
    {
        if (instance != null)
        {
            return instance;
        }
        return null;
    }

    /**
     * Loads the configuration file into a {@link Properties}object.
     * 
     * @param fn Name of the configuration file
     */
    private void load(String fn)
    {

        dataFileName = new String(fn);

        data = new Properties();

        try
        {
            FileInputStream sf = new FileInputStream(fn);
            data.load(sf);
        }
        catch (FileNotFoundException e)
        {
            System.out.println("JGA>  File with initial data not found.");
            e.printStackTrace();
        }
        catch (IOException e)
        {
            System.out.println("JGA>  File I/O problem.");
            e.printStackTrace();
        }
    }

    
    /**
     * Initializes the globally accessible parameters of the FBranin function
     */
    private void initializeElements()
    {

        dataFileName = (new String(getProperty("DATAFILENAME")));
        String str = (new String(getProperty("CAPACITATED")));
        if (str.compareToIgnoreCase("YES") == 0)
            capacitated = true;
        else
            capacitated = false;
        str = (new String(getProperty("DRAWLOCATIONS")));
        if (str.compareToIgnoreCase("YES") == 0)
            drawLocations = true;
        else
            drawLocations = false;

        str = (new String(getProperty("MINCOST")));
        minCost = Double.parseDouble(str);
        
        str = (new String(getProperty("MAXCOST")));
        maxCost = Double.parseDouble(str);
        
        str = (new String(getProperty("MINCOVER")));
        minCover = Double.parseDouble(str);
        
        str = (new String(getProperty("MAXCOVER")));
        maxCover = Double.parseDouble(str);
        
    }

    /**
     * @return the data problem file name
     */
    public String getDataFileName()
    {
        return dataFileName;
    }

    /**
     * @return the draw locations flag
     */
    public boolean getdrawLocations()
    {
        return drawLocations;
    }

    /**
     * Gets the string value for a field in the property file.
     * @param key field name in the property file
     * @return the string value for the field
     */
    private String getProperty(String key)
    {
        return data.getProperty(key);
    }

    /**
     * Prints the problem parameters loaded in the standard output.
     */
    public void print()
    {
        System.out.println("*************************************************");
        System.out.println("Data problem:  " + dataFileName);
        System.out.println("*************************************************");
    }

    /**
     * @return Returns the drawLocations.
     */
    public boolean isDrawLocations()
    {
        return drawLocations;
    }
    /**
     * @return Returns the maxCost.
     */
    public double getMaxCost()
    {
        return maxCost;
    }
    /**
     * @return Returns the maxCover.
     */
    public double getMaxCover()
    {
        return maxCover;
    }
    /**
     * @return Returns the minCost.
     */
    public double getMinCost()
    {
        return minCost;
    }
    /**
     * @return Returns the minCover.
     */
    public double getMinCover()
    {
        return minCover;
    }
}