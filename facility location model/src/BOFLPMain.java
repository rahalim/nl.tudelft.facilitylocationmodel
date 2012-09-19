import de.fhpotsdam.unfolding.examples.GreatCircleConnectionApp;
import edu.uniandes.copa.jga.*;
import edu.uniandes.copa.mojga.nsga2.*;
import java.util.ArrayList;

import processing.core.PApplet;

import nl.tudelft.simulation.dsol.animation.D2.GisRenderable2D;
import nl.tudelft.simulation.language.io.URLResource;
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
public class BOFLPMain
{

    /**
     * Starting point for the CFLP application.
     * 
     * @param args command line arguments.
     */
    public static void main(String args[])
    {
    	
//    	 new GisRenderable2D(simulator, URLResource
//    				.getResource("/models/maps/" + backgroundName
//    						+ ".map.xml"));
        /** Configuration file name */
        String configFileName = args[0];

        /** Number of replicates for experimentation */
        int numReplicates = 1;

        boolean single = true;
        
        PApplet.main(new String[] { "--present", "de.fhpotsdam.unfolding.examples.GreatCircleConnectionApp" });
        GreatCircleConnectionApp map =new GreatCircleConnectionApp();

        if (args.length == 2)
        {
            single = false;
            numReplicates = Integer.parseInt(args[1]);
        }
        if (single)
            runSingle(configFileName);
        else
            runMulti(configFileName, numReplicates);
    }

    /**
     * Executes a single run of the GA
     * 
     * @param configFileName configuraration file name
     */
    public static void runSingle(String configFileName)
    {

        GASettings gaSettings = GASettings.instance(configFileName);

        BOFLPSettings problemSettings = BOFLPSettings.instance(gaSettings.getProblemSettingsFile());

        BOFLP CFLProblem = BOFLP.instance();

        String str = new String(problemSettings.getDataFileName() + ".dat.txt");

        CFLProblem.readFile(str);

        String chrom = gaSettings.getGenotypeClassName();

        ArrayList genotypeParams = new ArrayList();

        if (chrom.compareTo("edu.uniandes.copa.jga.IntegerGenotype") == 0)
        {
            genotypeParams.add(new Integer(BOFLP.instance().getnClients()));

            for (int i = 1; i <= BOFLP.instance().getnClients(); i++)
            {
                genotypeParams.add(new Integer(1));
                genotypeParams.add(new Integer(BOFLP.instance().getnDepots()));
            }
        }

        if (chrom.compareTo("edu.uniandes.copa.jga.BinaryGenotype") == 0)
        {
            genotypeParams.add(0, new Integer(BOFLP.instance().getnDepots()));
        }

        GeneticAlgorithmHandler ga;
        ga = new GeneticAlgorithmHandler(configFileName, genotypeParams);

        ArrayList finalPopulation = ga.run();

        MOParetoUtils moUtils = new MOParetoUtils();

        ArrayList finalFrontierPopulation = moUtils.getFrontiers(finalPopulation, 1);

        eliminateDuplicatesFromPopulation(finalFrontierPopulation);

        moUtils.SortPopulationByObjective(finalFrontierPopulation, 0);

        String FileName = new String(problemSettings.getDataFileName() + ".out.txt");
        FileGAResults fileGAResults = new FileGAResults();
        fileGAResults.openFile(FileName, TXTFile.WRITE_MODE);

        double minValues[] = new double[2];
        double maxValues[] = new double[2];
        minValues[0] = problemSettings.getMinCost();
        minValues[1] = problemSettings.getMinCover();
        maxValues[0] = problemSettings.getMaxCost();
        maxValues[1] = problemSettings.getMaxCover();

        fileGAResults.writeHeaderProblem(problemSettings.getDataFileName(), minValues, maxValues);
        fileGAResults.writePopulationFitness(finalFrontierPopulation);
        fileGAResults.closeFile();
        MOEvaluatePerformance moEvaluate = new MOEvaluatePerformance();
        double S = moEvaluate.MOEvaluateS(FileName);
        System.out.println("Performance measure S = " + S);
        System.out.println("After " + 1 + " run, the size of the final front is : " + finalFrontierPopulation.size());
        generateDepotHistogram(finalFrontierPopulation);

        moUtils.drawFrontiers(finalFrontierPopulation);

        iostd.readline("Press Return to draw specific solutions : ");

        if (problemSettings.getdrawLocations())
        {
            moUtils.SortPopulationByObjective(finalFrontierPopulation, 0);
            int s = 0;
            while (s != -1)
            {
                s = iostd.readInt("Write Solution Number ( 1 to " + finalFrontierPopulation.size()
                        + "   or  -1 to exit  )  : ");
                if (s != -1)
                {

                    if (s >= 1 && s <= finalFrontierPopulation.size())
                    {
                        Individual ind = (Individual) finalFrontierPopulation.get(s - 1);
                        if (chrom.compareTo("edu.uniandes.copa.jga.IntegerGenotype") == 0)
                        {
                            IntegerGenotype genotype = (IntegerGenotype) ind.getGenotype();
                            BOFLP.instance().drawAssigments(genotype.getGenes());
                        }
                        if (chrom.compareTo("edu.uniandes.copa.jga.BinaryGenotype") == 0)
                        {
                            BinaryGenotype genotype = (BinaryGenotype) ind.getGenotype();
                            int[] assignment;
                            if (problemSettings.isCapacitated())
                            {
                                BOCFLPFitnessBinary cFitness = new BOCFLPFitnessBinary();
                                assignment = cFitness.run(genotype.getGenes());
                            }
                            else
                            {
                                BOUFLPFitnessBinary uFitness = new BOUFLPFitnessBinary();
                                assignment = uFitness.run(genotype.getGenes());
                            }
                            BOFLP.instance().drawAssigments(assignment);
                        }
                    }

                }
            }
        }

        System.exit(0);
    }

    /**
     * Executes multiple runs of the GA for making an experimental test.
     * 
     * @param configFileName configuraration file name
     * @param numReplicates number of replicates
     */
    public static void runMulti(String configFileName, int numReplicates)
    {

        GASettings gaSettings = GASettings.instance(configFileName);

        BOFLPSettings problemSettings = BOFLPSettings.instance(gaSettings.getProblemSettingsFile());

        BOFLP CFLProblem = BOFLP.instance();

        String str = new String(problemSettings.getDataFileName() + ".dat.txt");

        CFLProblem.readFile(str);

        String chrom = gaSettings.getGenotypeClassName();

        ArrayList genotypeParams = new ArrayList();

        if (chrom.compareTo("edu.uniandes.copa.jga.IntegerGenotype") == 0)
        {
            genotypeParams.add(new Integer(BOFLP.instance().getnClients()));

            for (int i = 1; i <= BOFLP.instance().getnClients(); i++)
            {
                genotypeParams.add(new Integer(1));
                genotypeParams.add(new Integer(BOFLP.instance().getnDepots()));
            }
        }

        if (chrom.compareTo("edu.uniandes.copa.jga.BinaryGenotype") == 0)
        {
            genotypeParams.add(0, new Integer(BOFLP.instance().getnDepots()));
        }

        GeneticAlgorithmHandler ga;
        ga = new GeneticAlgorithmHandler(configFileName, genotypeParams);

        System.out.println("run  SIndex   CPUTime   F.Evaluations   NumberOfSolutions");

        double minValues[] = new double[2];
        double maxValues[] = new double[2];
        minValues[0] = problemSettings.getMinCost();
        minValues[1] = problemSettings.getMinCover();
        maxValues[0] = problemSettings.getMaxCost();
        maxValues[1] = problemSettings.getMaxCover();

        ArrayList globalPopulation = new ArrayList();

        double time = 0;
        double maxTime = 0;

        for (int i = 1; i <= numReplicates; i++)
        {
            ArrayList finalpopulation = ga.run();

            MOParetoUtils moUtils = new MOParetoUtils();

            ArrayList finalFrontierPopulation = moUtils.getFrontiers(finalpopulation, 1);
            eliminateDuplicatesFromPopulation(finalFrontierPopulation);
            moUtils.SortPopulationByObjective(finalFrontierPopulation, 0);
            globalPopulation.addAll(finalFrontierPopulation);

            String FileName = new String(problemSettings.getDataFileName() + ".out" + i + ".txt");
            FileGAResults fileGAResults = new FileGAResults();
            fileGAResults.openFile(FileName, TXTFile.WRITE_MODE);
            minValues[0] = problemSettings.getMinCost();
            minValues[1] = problemSettings.getMinCover();
            maxValues[0] = problemSettings.getMaxCost();
            maxValues[1] = problemSettings.getMaxCover();

            fileGAResults.writeHeaderProblem(problemSettings.getDataFileName(), minValues, maxValues);
            fileGAResults.writePopulationFitness(finalFrontierPopulation);
            fileGAResults.closeFile();
            MOEvaluatePerformance moEvaluate = new MOEvaluatePerformance();
            double S = moEvaluate.MOEvaluateS(FileName);
            System.out.println(StringFormat.formatStr(i, 2) + "  " + StringFormat.formatStr(S, 8, 4) + "  "
                    + StringFormat.formatStr(ga.getStatCollector().getExecutionTime(), 8) + "  "
                    + StringFormat.formatStr(ga.getStatCollector().getFunctionEvaluations(), 8) + "          "
                    + StringFormat.formatStr(finalFrontierPopulation.size(), 3));

            time = time + ga.getStatCollector().getExecutionTime();
            maxTime = Math.max(maxTime, ga.getStatCollector().getExecutionTime());

        }

        time = time / numReplicates;

        System.out.println("After " + numReplicates + " runs,  CPU time (msec) :    average: " + time
                + "     maximum : " + maxTime);

        String GlobalFileName = new String(problemSettings.getDataFileName() + ".outGlobal" + ".txt");
        FileGAResults fileGAResults = new FileGAResults();
        fileGAResults.openFile(GlobalFileName, TXTFile.WRITE_MODE);

        MOParetoUtils moUtils = new MOParetoUtils();
        ArrayList finalFrontierPopulation = moUtils.getFrontiers(globalPopulation, 1);
        eliminateDuplicatesFromPopulation(finalFrontierPopulation);
        moUtils.SortPopulationByObjective(finalFrontierPopulation, 0);
        fileGAResults.writeHeaderProblem(problemSettings.getDataFileName(), minValues, maxValues);
        fileGAResults.writePopulationFitness(finalFrontierPopulation);

        System.out.println("After " + numReplicates + " runs,  the size of the final front is : "
                + finalFrontierPopulation.size());
        generateDepotHistogram(finalFrontierPopulation);

        System.exit(0);
    }

    /**
     * Generates randomly the data for a CFL problem. Parameters for data
     * generation must be defined in a property file ".ini" referenced in the
     * CFLPcSettings.ini file. (see data/p1/p1.ini)
     * 
     * @param configFileName configuraration file name
     */
    public static void generateProblem(String configFileName)
    {
        GASettings gaSettings = GASettings.instance(configFileName);
        BOFLPSettings problemSettings = BOFLPSettings.instance(gaSettings.getProblemSettingsFile());

        BOFLP CFLProblemI = BOFLP.instance();
        CFLProblemI.initRandomGenerationParams(problemSettings.getDataFileName());

        CFLProblemI.generateRandom();
        CFLProblemI.print();
        CFLProblemI.writeFile();

        System.exit(0);
    }

    /**
     * Eliminates the duplicated individuals from a population
     * @param population population to elimintate the duplicates
     */
    static public void eliminateDuplicatesFromPopulation(ArrayList population)
    {
        ArrayList newPopulation = new ArrayList();

        NSGAIIIndividualComparatorByObjectives comparator = new NSGAIIIndividualComparatorByObjectives(2);

        for (int i = 0; i < population.size(); i++)
        {
            Individual indPopulation = (Individual) population.get(i);

            boolean duplicated = false;
            for (int j = 0; j < newPopulation.size(); j++)
            {
                Individual indNewPopulation = (Individual) newPopulation.get(j);
                if (comparator.compare(indPopulation, indNewPopulation) == 0)
                    duplicated = true;
            }
            if (!duplicated)
            {
                newPopulation.add(indPopulation);
            }
        }

        population.clear();
        population.addAll(newPopulation);
    }

    /**
     * Generates the histogram data for count the times that each depot appears
     * as open in the solutions of a population
     * @param population population to be evaluated
     */
    static public void generateDepotHistogram(ArrayList population)
    {
        int depots = BOFLP.instance().getnDepots();

        int[] frecuencies = new int[depots + 1];

        String chrom = GASettings.instance().getGenotypeClassName();

        if (chrom.compareTo("edu.uniandes.copa.jga.BinaryGenotype") == 0)
        {

            for (int i = 0; i < population.size(); i++)
            {
                Individual ind = (Individual) population.get(i);
                BinaryGenotype genotype = (BinaryGenotype) ind.getGenotype();
                for (int j = 0; j < depots; j++)
                {
                    int open = genotype.getGene(j);
                    if (open == 1)
                        frecuencies[j]++;
                }
            }
        }

        System.out.println("Frequencies for depots on the final front");
        for (int j = 0; j < depots; j++)
        {
            System.out.println("Depot " + j + "   : " + frecuencies[j]);
        }

    }

}