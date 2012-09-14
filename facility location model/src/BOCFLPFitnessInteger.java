
import edu.uniandes.copa.jga.*;

import java.util.ArrayList;

/**
 * This class evaluates a solution for the BCFLP using a {@link IntegerGenotype}.
 * 
 * @author A.Medaglia - E.Gutiérrez
 * @version %I%, %G%
 */
public class BOCFLPFitnessInteger extends FitnessFunction
{
    // instance variables

    /**
     * Evaluates a solution for the BCFL problem using a {@link IntegerGenotype}.
     * @param gt Genotype to be evaluated
     * @return multi-objective evaluation for the BCFL problem
     */
    public ArrayList evaluate(Genotype gt)
    {

        IntegerGenotype igt;
        igt = (IntegerGenotype) gt;

        // Evaluation
        double cost = evalCost(igt.getGenes());
        double covering = evalCover(igt.getGenes());
        double overCapacity = 2.0 * evalOverCapacity(igt.getGenes());

        if (overCapacity > 0.0)
        {
            double penalty = 1.5 + overCapacity;
            cost = cost * penalty;
        }

        // Loading returning vector
        ArrayList fitness = new ArrayList();
        fitness.add(0, new Double(cost));
        fitness.add(1, new Double(covering));
        fitness.add(2, new Double(overCapacity));

        return fitness;
    }

    /**
     * Evaluates the total cost (tixed + transportation costs) for a given
     * solution.
     * @param assig assigment vector.
     *            <p>
     *            assig[i] indicates the assigned depot to client i
     * @return the total cost
     */
    public double evalCost(int[] assig)
    {
        /** Indicates if each depot is open */
        BOFLP BFLProblem = BOFLP.instance();

        boolean[] openDepots;

        int nClients = BFLProblem.nClients;
        int nDepots = BFLProblem.nDepots;

        openDepots = new boolean[nDepots + 1];

        double cost;

        for (int j = 1; j <= nDepots; j++)
        {
            openDepots[j] = false;
        }
        cost = 0;
        for (int i = 1; i <= nClients; i++)
        {
            int depot = assig[i - 1];
            cost = cost + BFLProblem.cost[i][depot];
            openDepots[depot] = true;
        }
        for (int j = 1; j <= nDepots; j++)
        {
            if (openDepots[j])
                cost = cost + BFLProblem.fixedCost[j];
        }
        return cost;
    }

    /**
     * Evaluates the covering index for a given solution.
     * @param assig assigment vector.
     *            <p>
     *            assig[i] indicates the assigned depot to client i
     * @return the evaluated covering index
     */
    public double evalCover(int[] assig)
    {
        BOFLP BFLProblem = BOFLP.instance();

        int nClients = BFLProblem.nClients;
        double maxDistanceCover = BFLProblem.maxDistanceCover;

        double coverDemand;

        coverDemand = 0;
        for (int i = 1; i <= nClients; i++)
        {
            if (BFLProblem.distance[i][assig[i - 1]] <= maxDistanceCover)
                coverDemand = coverDemand + BFLProblem.demand[i];
        }

        return coverDemand / BFLProblem.sumDemands;

    }

    /**
     * Evaluates the the percentage of exceeded capacity with respect to the
     * total capacity for a given solution.
     * @param assig assigment vector.
     *            <p>
     *            assig[i] indicates the assigned depot to client i
     * @return the percentage of exceeded capacity with respect to the total
     *         capacity
     */
    public double evalOverCapacity(int[] assig)
    {
        BOFLP BFLProblem = BOFLP.instance();

        int nClients = BFLProblem.nClients;
        int nDepots = BFLProblem.nDepots;

        double overCapacity;

        int[] requiredCapacity;
        requiredCapacity = new int[nDepots + 1];

        for (int i = 1; i <= nClients; i++)
        {
            int depot = assig[i - 1];
            requiredCapacity[depot] = requiredCapacity[depot] + BFLProblem.demand[i];
        }
        overCapacity = 0;
        for (int j = 1; j <= nDepots; j++)
        {
            overCapacity = overCapacity + Math.max(0, requiredCapacity[j] - BFLProblem.capacity[j]);
        }
        overCapacity = overCapacity / BFLProblem.sumCapacity;
        return overCapacity;
    }

}