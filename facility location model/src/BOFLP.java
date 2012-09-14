
import edu.uniandes.copa.ioutils.*;
import java.util.Random;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

/**
 * This class holds the information of an instance of a CFLP loaded from a data
 * file. It also contains utilities for generating, reading, and writing CFLP
 * data files. This class implements the singleton design pattern because only
 * one instance should be instantiated.
 * 
 * @author A.Medaglia - E.Gutiérrez
 * @version %I%, %G%
 */
public class BOFLP
{

    /** Grid size for random generation */
    private int LLGrid, ULGrid;

    /** Boudns for demand generation */
    private int LLDemand, ULDemand;

    /** Number of clients */
    protected int nClients;

    /** Number of depots */
    protected int nDepots;

    /** Base Cost for Calculating fixed operation cost of depots */
    private double depotOperationCost;

    /** Cost of transport one unit of product one unit of distance */
    private double transportCost;

    /** Demand of clients */
    protected int[] demand;

    /** Coordinates (x,y) of clients */
    private double[][] coordClient;

    /** Fixed cost for depots */
    protected int[] fixedCost;

    /** Capaciyy of depots */
    protected int[] capacity;

    /** Coordinates (x,y) of depots */
    private double[][] coordDepot;

    /** Distance between clients & depots */
    protected double[][] distance;

    /** Total transportation cost betwenn clients & depots */
    protected double[][] cost;

    /** Total capacity of depots */
    protected int sumCapacity;

    /** Total demands of clients */
    protected int sumDemands;

    /** Capacaty factor Rfactor = sumCapacity / sumDemands */
    private double Rfactor;

    /** Covering distance parameter for acceptable service level */
    protected int maxDistanceCover;

    /** Seed for random number generation */
    private long seed;

    /** Random number generator */
    private Random rnd;

    /** Coordinates (x,y) of Boundary */
    private double[][] coordBoundary;

    /** Number of point for the problem's geographic boundary */
    private int numPointsBoundary;

    /** Data file name of the CFLP  */
    private String filename;

    /** Unique instance (singleton design pattern)  */
    private static BOFLP instance = null;

    /** {@link Properties}file for settings         */
    private Properties settings;
    
    

    /**
     * Returns the singleton instance of {@link BOFLP}. If it does not exist, it
     * creates it.
     * 
     * @return A handle to the unique instance of {@link BOFLP}.
     *  
     */
    static public BOFLP instance()
    {
        if (instance == null)
        {
            instance = new BOFLP();
        }
        return instance;
    }

    /**
     * Prints the data for the problem loaded in the standard output.
     */
    public void print()
    {

        System.out.println("Clients  " + nClients);
        System.out.println("NDepots  " + nDepots);
        System.out.println("R factor " + Rfactor);

        for (int i = 1; i <= nClients; i++)
        {
            System.out.println("Demand [" + i + "] " + demand[i]);
        }

        for (int j = 1; j <= nDepots; j++)
        {
            System.out.println("Capacity [" + j + "] " + capacity[j]);
        }

        for (int j = 1; j <= nDepots; j++)
        {
            System.out.println("FixedCost [" + j + "] " + fixedCost[j]);
        }

        for (int i = 1; i <= nClients; i++)
        {
            for (int j = 1; j <= nDepots; j++)
            {
                System.out.println("Distance [" + i + "," + j + "] " + distance[i][j]);
            }
        }

        for (int i = 1; i <= nClients; i++)
        {
            for (int j = 1; j <= nDepots; j++)
            {
                System.out.println("Cost [" + i + "," + j + "] " + cost[i][j]);
            }
        }

        System.out.println("Total Demand   " + sumDemands);
        System.out.println("Total Capacity " + sumCapacity);

        for (int i = 1; i <= nClients; i++)
        {
            System.out.println("Coord Client [" + i + "] " + coordClient[i][0] + " , " + coordClient[i][1]);
        }

        for (int j = 1; j <= nDepots; j++)
        {
            System.out.println("Coord Depot  [" + j + "] " + coordDepot[j][0] + " , " + coordDepot[j][1]);
        }
        System.out.println("Max Distance Cover " + maxDistanceCover);

    }

    /**
     * Writes to disk the CFLP data file.
     */
    public void writeFile()
    {

        TXTFile f;
        String str;

        f = new TXTFile(filename);
        f.openTXTFile(TXTFile.WRITE_MODE);

        str = new String("Clients  " + nClients);
        f.writeLine(str);

        str = new String("NDepots  " + nDepots);
        f.writeLine(str);

        str = new String("R factor " + (int) Rfactor);
        f.writeLine(str);

        for (int i = 1; i <= nClients; i++)
        {
            str = new String("Demand [" + i + "] " + demand[i]);
            f.writeLine(str);
        }

        for (int j = 1; j <= nDepots; j++)
        {
            str = new String("Capacity [" + j + "] " + capacity[j]);
            f.writeLine(str);
        }

        for (int j = 1; j <= nDepots; j++)
        {
            fixedCost[j] = (int) (depotOperationCost * (1.0 + 0.1 * rnd.nextDouble()) * Math.sqrt(capacity[j]) + rnd
                    .nextDouble()
                    * depotOperationCost);
            str = new String("FixedCost [" + j + "] " + fixedCost[j]);
            f.writeLine(str);
        }

        for (int i = 1; i <= nClients; i++)
        {
            for (int j = 1; j <= nDepots; j++)
            {
                str = new String("Distance [" + i + "," + j + "] " + distance[i][j]);
                f.writeLine(str);
            }
        }

        for (int i = 1; i <= nClients; i++)
        {
            for (int j = 1; j <= nDepots; j++)
            {
                str = new String("Cost [" + i + "," + j + "] " + cost[i][j]);
                f.writeLine(str);
            }
        }

        for (int i = 1; i <= nClients; i++)
        {
            str = new String("Coord Client [" + i + "] " + coordClient[i][0] + " , " + coordClient[i][1]);
            f.writeLine(str);
        }

        for (int j = 1; j <= nDepots; j++)
        {
            str = new String("Coord Depot  [" + j + "] " + coordDepot[j][0] + " , " + coordDepot[j][1]);
            f.writeLine(str);
        }

        str = new String("Max Distance Cover " + maxDistanceCover);
        f.writeLine(str);

        f.closeTXTFile();
    }

    /**
     * Reads from disk a CFLP data file given the file name.
     * @param nf file name
     *  
     */
    public void readFile(String nf)
    {
        filename = new String(nf);
        readFile();
    }

    /**
     * Reads from disk a CFLP data file.
     */
    public void readFile()
    {

        TXTFile f;

        f = new TXTFile(filename);
        f.openTXTFile(TXTFile.READ_MODE);

        f.readLine();
        nClients = f.readNextInt();
               System.out.println("Clients "+ nClients);

        f.readLine();
        nDepots = f.readNextInt();
              System.out.println("Depots "+ nDepots);

        f.readLine();
        numPointsBoundary = f.readNextInt();
                System.out.println("BoundaryPoints "+ numPointsBoundary);

        demand = new int[nClients + 1];
        coordClient = new double[nClients + 1][2];
        fixedCost = new int[nDepots + 1];
        capacity = new int[nDepots + 1];
        coordDepot = new double[nDepots + 1][2];
        distance = new double[nClients + 1][nDepots + 1];
        cost = new double[nClients + 1][nDepots + 1];
        coordBoundary = new double[numPointsBoundary + 1][2];

        f.readLine();
        Rfactor = f.readNextDouble();
        // System.out.println("R factor " + Rfactor);

        sumDemands = 0;
        for (int i = 1; i <= nClients; i++)
        {
            f.readLine();
            f.readNextInt();
            demand[i] = f.readNextInt();
            sumDemands = sumDemands + demand[i];
            // System.out.println("Demand ["+i+"] "+demand[i]);
        }

        sumCapacity = 0;
        for (int j = 1; j <= nDepots; j++)
        {
            f.readLine();
            f.readNextInt();
            capacity[j] = f.readNextInt();
            sumCapacity = sumCapacity + capacity[j];
            // System.out.println("Capacity ["+j+"] "+capacity[j]);
        }

        for (int j = 1; j <= nDepots; j++)
        {
            f.readLine();
            f.readNextInt();
            fixedCost[j] = f.readNextInt();
            // System.out.println("FixedCost ["+j+"] "+fixedCost[j]);
        }

        for (int i = 1; i <= nClients; i++)
        {
            for (int j = 1; j <= nDepots; j++)
            {
                f.readLine();
                f.readNextInt();
                f.readNextInt();
                distance[i][j] = f.readNextDouble();
                // System.out.println("Distance ["+i+","+j+"] "+distance[i][j]);
            }
        }

        for (int i = 1; i <= nClients; i++)
        {
            for (int j = 1; j <= nDepots; j++)
            {
                f.readLine();
                f.readNextInt();
                f.readNextInt();
                cost[i][j] = f.readNextDouble();
                //                System.out.println("Cost ["+i+","+j+"] "+cost[i][j]);
            }
        }

        minx = Double.MAX_VALUE;
        miny = Double.MAX_VALUE;
        maxx = Double.MIN_VALUE;
        maxy = Double.MIN_VALUE;

        for (int i = 1; i <= nClients; i++)
        {
            f.readLine();
            f.readNextInt();
            coordClient[i][0] = f.readNextDouble();
            coordClient[i][1] = f.readNextDouble();
            //            String str = new String("Coord Depot ["+i+"]
            // "+coordClient[i][0]+" "+
            //                    coordClient[i][1]);
            //            System.out.println(str);
            minx = Math.min(minx, coordClient[i][0]);
            miny = Math.min(miny, coordClient[i][1]);
            maxx = Math.max(maxx, coordClient[i][0]);
            maxy = Math.max(maxy, coordClient[i][1]);
        }

        for (int j = 1; j <= nDepots; j++)
        {
            f.readLine();
            f.readNextInt();
            coordDepot[j][0] = f.readNextDouble();
            coordDepot[j][1] = f.readNextDouble();
            //            String str = new String("Coord Depot ["+j+"] "+coordDepot[j][0]+"
            // "+
            //                    coordDepot[j][1]);
            //            System.out.println(str);
            minx = Math.min(minx, coordDepot[j][0]);
            miny = Math.min(miny, coordDepot[j][1]);
            maxx = Math.max(maxx, coordDepot[j][0]);
            maxy = Math.max(maxy, coordDepot[j][1]);
        }

        for (int k = 1; k <= numPointsBoundary; k++)
        {
            f.readLine();
            f.readNextInt();
            coordBoundary[k][0] = f.readNextDouble();
            coordBoundary[k][1] = f.readNextDouble();
            //            String str = new String("Coord Frontier ["+k+"]
            // "+coordBoundary[k][0]+" "+
            //                    coordBoundary[k][1]);
            //            System.out.println(str);
            minx = Math.min(minx, coordBoundary[k][0]);
            miny = Math.min(miny, coordBoundary[k][1]);
            maxx = Math.max(maxx, coordBoundary[k][0]);
            maxy = Math.max(maxy, coordBoundary[k][1]);
        }
        double delta;
        delta = Math.max(maxx - minx, maxy - miny) / 2.0;
        double xc, yc;
        xc = (minx + maxx) / 2.0;
        yc = (miny + maxy) / 2.0;
        minx = xc - delta;
        miny = yc - delta;
        maxx = xc + delta;
        maxx = xc + delta;
        // Max Distance Cover = 10
        f.readLine();
        maxDistanceCover = f.readNextInt();

        f.closeTXTFile();
    }

    /**  coordinates of the minimax rectangle for the problem's geographic boundary */
    private double minx, miny, maxx, maxy;

    /**
     * Reads from disk a CFLP data file.
     * @param filename name of the coordinates file
     */
    public void readCoordinatesFile(String filename)
    {
        minx = Double.MAX_VALUE;
        miny = Double.MAX_VALUE;
        maxx = Double.MIN_VALUE;
        maxy = Double.MIN_VALUE;

        System.out.println("abrir el archivo " + filename);

        TXTFile f = new TXTFile(filename);
        f.openTXTFile(TXTFile.READ_MODE);

        f.readLine();
        nClients = f.readNextInt();
        System.out.println("Clients " + nClients);

        f.readLine();
        nDepots = f.readNextInt();
        System.out.println("Depots " + nDepots);

        f.readLine();
        numPointsBoundary = f.readNextInt();
        System.out.println("BoundaryPoints" + numPointsBoundary);

        coordClient = new double[nClients + 1][2];
        coordDepot = new double[nDepots + 1][2];
        coordBoundary = new double[numPointsBoundary + 1][2];
        distance = new double[nClients + 1][nDepots + 1];

        for (int i = 1; i <= nClients; i++)
        {
            f.readLine();
            f.readNextInt();
            coordClient[i][0] = f.readNextDouble();
            coordClient[i][1] = f.readNextDouble();
            String str = new String("Coord Depot [" + i + "] " + coordClient[i][0] + "  " + coordClient[i][1]);
            System.out.println(str);
            minx = Math.min(minx, coordClient[i][0]);
            miny = Math.min(miny, coordClient[i][1]);
            maxx = Math.max(maxx, coordClient[i][0]);
            maxy = Math.max(maxy, coordClient[i][1]);
        }

        for (int j = 1; j <= nDepots; j++)
        {
            f.readLine();
            f.readNextInt();
            coordDepot[j][0] = f.readNextDouble();
            coordDepot[j][1] = f.readNextDouble();
            String str = new String("Coord Depot [" + j + "] " + coordDepot[j][0] + "  " + coordDepot[j][1]);
            System.out.println(str);
            minx = Math.min(minx, coordDepot[j][0]);
            miny = Math.min(miny, coordDepot[j][1]);
            maxx = Math.max(maxx, coordDepot[j][0]);
            maxy = Math.max(maxy, coordDepot[j][1]);
        }

        for (int k = 1; k <= numPointsBoundary; k++)
        {
            f.readLine();
            f.readNextInt();
            coordBoundary[k][0] = f.readNextDouble();
            coordBoundary[k][1] = f.readNextDouble();
            String str = new String("Coord Frontier [" + k + "] " + coordBoundary[k][0] + "  " + coordBoundary[k][1]);
            System.out.println(str);
            minx = Math.min(minx, coordBoundary[k][0]);
            miny = Math.min(miny, coordBoundary[k][1]);
            maxx = Math.max(maxx, coordBoundary[k][0]);
            maxy = Math.max(maxy, coordBoundary[k][1]);
        }
        double delta;
        delta = Math.max(maxx - minx, maxy - miny) / 2.0;
        double xc, yc;
        xc = (minx + maxx) / 2.0;
        yc = (miny + maxy) / 2.0;
        minx = xc - delta;
        miny = yc - delta;
        maxx = xc + delta;
        maxx = xc + delta;

        f.closeTXTFile();

        for (int i = 1; i <= nClients; i++)
        {
            for (int j = 1; j <= nDepots; j++)
            {
                distance[i][j] = Math.sqrt(Math.pow(coordDepot[j][0] - coordClient[i][0], 2.0)
                        + Math.pow(coordDepot[j][1] - coordClient[i][1], 2.0));

                String dString = StringFormat.formatStr(distance[i][j], 10, 2);
                String str = new String("Distance [" + i + "," + j + "] " + dString);
                System.out.println(str);

            }
        }

    }

    /**
     * Draws locations for clients and depots on a graphical window.
     */
    public void drawLocations()
    {
        WGraphics wg;
        //VisFrame Vz2 = new VisFrame();
        wg = new WGraphics(500, 500, minx, miny, maxx, maxy);

        wg.clear();

        for (int i = 1; i <= nClients; i++)
        {
            wg.drawMark(coordClient[i][0], coordClient[i][1], Color.blue);
//            Ellipse2D.Double client2 = new Ellipse2D.Double(coordClient[i][0], coordClient[i][1],2,2);
//            Vz2.getPanel().drawShape(client2);
        }

        for (int j = 1; j <= nDepots; j++)
        {
            wg.drawMark(coordDepot[j][0], coordDepot[j][1], Color.red);
//            Rectangle2D.Double depot2 = new Rectangle2D.Double(coordDepot[j][0], coordDepot[j][1],5,5);
//            Vz2.getPanel().drawShape(depot2);
        }

        for (int k = 1; k < numPointsBoundary; k++)
        {
            wg.drawLine(coordBoundary[k][0], coordBoundary[k][1], coordBoundary[k + 1][0], coordBoundary[k + 1][1],
                    Color.black);
//            Line2D.Double line2= new Line2D.Double(coordBoundary[k][0], coordBoundary[k][1], coordBoundary[k + 1][0], coordBoundary[k + 1][1]);
//            Vz2.getPanel().drawLine(line2);
        }

    }

    /**
     * Draws assigments for clients on a graphical window. The assignment is
     * showed by a line between clients and depots.
     * @param assig assigment vector.
     *            <p>
     *            assig[i] indicates the assigned depot to client i
     */
    public void drawAssigments(int[] assig)
    {
       // WGraphics wg;
        VisFrame Vz = new VisFrame();
       // wg = new WGraphics(400, 400, minx, miny, maxx, maxy);
        boolean depotOpen[] = new boolean[nDepots + 1];

        int d;
        for (int i = 1; i <= nClients; i++)
        {
            d = assig[i - 1];
            depotOpen[d] = true;
          //  wg.drawLine(coordClient[i][0],coordClient[i][1],coordDepot[d][0],coordDepot[d][1], Color.lightGray);
            Line2D.Double line= new Line2D.Double(coordClient[i][0],coordClient[i][1],coordDepot[d][0],coordDepot[d][1]);
            Vz.getPanel().drawLine(line);
            
            //wg.drawMark(coordDepot[d][0], coordDepot[d][1], Color.red);
            Rectangle2D.Double depot = new Rectangle2D.Double(coordDepot[d][0], coordDepot[d][1],4,4);
            Vz.getPanel().drawShape(depot);
            
            //wg.drawMark(coordClient[i][0], coordClient[i][1], Color.blue);
            Ellipse2D.Double client = new Ellipse2D.Double(coordClient[i][0], coordClient[i][1],4,4);
            Vz.getPanel().drawShape(client);
        }

        for (int j = 1; j <= nDepots; j++)
        {
            if (depotOpen[j])
            {
            	//wg.drawMark(coordDepot[j][0], coordDepot[j][1], Color.red);
            	Rectangle2D.Double depot2 = new Rectangle2D.Double(coordDepot[j][0], coordDepot[j][1],4,4);
                Vz.getPanel().drawShape(depot2);
            }
            
        }

        for (int k = 1; k < numPointsBoundary; k++)
        {
//            wg.drawLine(coordBoundary[k][0], coordBoundary[k][1], coordBoundary[k + 1][0], coordBoundary[k + 1][1],
//                    Color.black);
            Line2D.Double line2= new Line2D.Double(coordBoundary[k][0], coordBoundary[k][1], coordBoundary[k + 1][0], coordBoundary[k + 1][1]);
            Vz.getPanel().drawLine(line2);
        }
        Vz.getPanel().repaint();
    }

    /**
     * @return the number of clients
     */
    public int getnClients()
    {
        return nClients;
    }

    /**
     * @return the number of depots
     */
    public int getnDepots()
    {
        return nDepots;
    }

    /**
     * @param i client
     * @return the demand for a given client
     */
    public int getDemand(int i)
    {
        return demand[i];
    }

    /**
     * @param j depot
     * @return the operating fixed cost for a given depot
     */
    public int getFixedCost(int j)
    {
        return fixedCost[j];
    }

    /**
     * @param i client
     * @param j depot
     * @return the total transport cost between a given client & depot
     */
    public double getCost(int i, int j)
    {
        return cost[i][j];
    }

    /**
     * @param j depot
     * @return the maximum capacity for a given depot
     */
    public int getCapacity(int j)
    {
        return capacity[j];
    }

    /**
     * @param i client
     * @param j depot
     * @return the distance between a given client & depot
     */
    public double getdistance(int i, int j)
    {
        return distance[i][j];
    }

    /**
     * Loads the parameter to generate the CLFP data randomly
     * @param filenameparams file name with CFLP parameters
     */
    public void initRandomGenerationParams(String filenameparams)
    {
        nClients = 2;
        nDepots = 2;
        Rfactor = 1;

        LLDemand = 10;
        ULDemand = 50;
        depotOperationCost = 100;
        transportCost = 0.05;
        maxDistanceCover = 10;

        LLGrid = 0;
        ULGrid = 100;

        filename = "data/default.dat.txt";

        settings = new Properties();

        try
        {
            FileInputStream sf = new FileInputStream(filenameparams + ".ini");
            settings.load(sf);
        }
        catch (FileNotFoundException e)
        {
            System.out.println("File with generator parameters not found. " + filenameparams);
            return;
            //e.printStackTrace();

        }
        catch (IOException e)
        {
            System.out.println("File I/O problem.");
            e.printStackTrace();
        }

        nClients = (new Integer(getProperty("nClients"))).intValue();
        nDepots = (new Integer(getProperty("nDepots"))).intValue();
        Rfactor = (new Integer(getProperty("Rfactor"))).intValue();
        maxDistanceCover = (new Integer(getProperty("MaxDCover"))).intValue();
        LLDemand = (new Integer(getProperty("LLDemand"))).intValue();
        ULDemand = (new Integer(getProperty("ULDemand"))).intValue();
        depotOperationCost = (new Double(getProperty("DepotOperationCost"))).doubleValue();
        transportCost = (new Double(getProperty("TransportCost"))).doubleValue();

        seed = (new Integer(getProperty("Seed"))).intValue();
        filename = new String(getProperty("FileName"));

    }

    /**
     * Gets the string value for a field in the property file.
     * @param key field name in the property file
     * @return the string value for the field
     */
    private String getProperty(String key)
    {
        return settings.getProperty(key);
    }

    /**
     * Initializes a CLFP data randomly
     */
    public void generateRandom()
    {

        rnd = new Random(seed);

        demand = new int[nClients + 1];
        coordClient = new double[nClients + 1][2];

        fixedCost = new int[nDepots + 1];
        capacity = new int[nDepots + 1];

        coordDepot = new double[nDepots + 1][2];

        distance = new double[nClients + 1][nDepots + 1];
        cost = new double[nClients + 1][nDepots + 1];

        sumDemands = 0;
        for (int i = 1; i <= nClients; i++)
        {
            demand[i] = (int) (LLDemand + rnd.nextDouble() * (ULDemand - LLDemand));
            sumDemands = sumDemands + demand[i];
        }
        for (int i = 1; i <= nClients; i++)
        {
            coordClient[i][0] = (int) (LLGrid + rnd.nextDouble() * (ULGrid - LLGrid));
            coordClient[i][1] = (int) (LLGrid + rnd.nextDouble() * (ULGrid - LLGrid));
        }

        sumCapacity = 0;
        for (int j = 1; j <= nDepots; j++)
        {
            capacity[j] = (int) (2 * LLDemand + rnd.nextDouble() * (4 * ULDemand - 2 * LLDemand));
            sumCapacity = sumCapacity + capacity[j];
        }

        double R;
        R = (double) sumCapacity / (double) sumDemands;
        R = Rfactor / R;

        sumCapacity = 0;
        for (int j = 1; j <= nDepots; j++)
        {
            capacity[j] = (int) (R * capacity[j]);
            sumCapacity = sumCapacity + capacity[j];
        }

        for (int j = 1; j <= nDepots; j++)
        {
            coordDepot[j][0] = (int) (LLGrid + rnd.nextDouble() * (ULGrid - LLGrid));
            coordDepot[j][1] = (int) (LLGrid + rnd.nextDouble() * (ULGrid - LLGrid));
        }
        for (int j = 1; j <= nDepots; j++)
        {
            //cast
            fixedCost[j] = (int) (depotOperationCost * (0.9 + 0.2 * rnd.nextDouble()) * Math.sqrt(capacity[j]) + rnd
                    .nextDouble()
                    * depotOperationCost);
        }
        for (int i = 1; i <= nClients; i++)
        {
            for (int j = 1; j <= nDepots; j++)
            {
                distance[i][j] = (int) Math.sqrt(Math.pow(coordClient[i][0] - coordDepot[j][0], 2)
                        + Math.pow(coordClient[i][1] - coordDepot[j][1], 2));
            }
        }

        for (int i = 1; i <= nClients; i++)
        {
            for (int j = 1; j <= nDepots; j++)
            {
                // cast cost[i][j]= (int) ( 1.0 * (double)
                // Math.max(distance[i][j],5)*(0.9+0.2*rnd.nextDouble())*(double)transportCost*(double)demand[i]
                // );
                cost[i][j] = (int) (1.0 * Math.max(distance[i][j], 5) * (0.9 + 0.2 * rnd.nextDouble()) * transportCost * demand[i]);
            }
        }

    }

}