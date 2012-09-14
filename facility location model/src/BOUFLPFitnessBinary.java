
import edu.uniandes.copa.jga.*;

import java.util.ArrayList;

/**
 * This class evaluates a solution for the BUFLP using a {@link BinaryGenotype}.
 * @author A.Medaglia, E.Gutiérrez, and J.G.Villegas
 * @version %I%, %G%
 */
public class BOUFLPFitnessBinary extends FitnessFunction
{

    /**
     * Evaluates a solution for the BUFLP problem using a {@link BinaryGenotype}.
     * @param gt Genotype to be evaluated
     * @return multi-objective evaluation for the BUCFL problem
     */
    public ArrayList evaluate(Genotype gt)
    {
        BinaryGenotype igt;
        igt = (BinaryGenotype) gt;

        ArrayList fitness = new ArrayList();

        int[] assigment;
        double cost, covering, overCapacity;

        // Run the heuristic for detailed assigment;
        assigment = run(igt.getGenes());
        // Evaluation
        cost = evalCost(assigment);
        covering = evalCover(assigment);
        overCapacity = evalOverCapacity(assigment);

        fitness.add(0, new Double(cost));
        fitness.add(1, new Double(covering));
        fitness.add(2, new Double(overCapacity));

        return fitness;

    }

    /**
     * This heuristic implements a greedy algorithm to make a detailed assigment
     * for the BUFLP problem given a list of open depots.
     * <p>
     * The heuristic attempts minimizing costs without deterioring covering.
     * @param locations locations status. 1, if location is open; 0, if location
     *            is closed.
     * @return detailed depot assigment vector.
     *         <p>
     *         int[] assigment = run (locations); assigment[i] indicates the
     *         depot assigned to client i
     */
    public int[] run(byte[] locations)
    {
        int[] assigment; //Assigment vector

        BOFLP BFLProblem = BOFLP.instance();

        int nClients = BFLProblem.nClients;
        int nDepots = BFLProblem.nDepots;
        double maxDistanceCover = BFLProblem.maxDistanceCover;

        assigment = new int[nClients + 1];

        int depotInsideOfMinCost; // The depot with the minimum cost inside the
        // covering distance
        int depotOutsideOfMinCost; // The depot with the minimum cost outside
        // the covering distance
        double minCostDepotInside; // The minimum cost for the depot inside the
        // covering distance
        double minCostDepotOutside; // The minimum cost for the depot outside
        // the covering distance

        // Main loop.
        // For each client: select and assign the depot
        for (int i = 1; i <= nClients; i++)
        {
            // Initial values to start the inner loop
            depotInsideOfMinCost = 0;
            depotOutsideOfMinCost = 0;
            minCostDepotInside = Double.MAX_VALUE;
            minCostDepotOutside = Double.MAX_VALUE;

            // Inner loop.
            // For each depot: determines the depot with the minimum cost
            // (inside the covering distance for client i)
            // and the depot with the minimum cost (outside the covering
            // distance for client i) (if exists)
            for (int j = 1; j <= nDepots; j++)
            {
                if (locations[j - 1] == 1)
                // If location j is open
                {
                    if (BFLProblem.distance[i][j] <= maxDistanceCover)
                    //	Updates minCostIn and minDepotIn
                    {
                        if ((BFLProblem.cost[i][j] <= minCostDepotInside))
                        {
                            minCostDepotInside = BFLProblem.cost[i][j];
                            depotInsideOfMinCost = j;
                        }
                    }
                    else
                    {
                        if ((BFLProblem.cost[i][j] <= minCostDepotOutside))
                        {
                            // Updates minCosOut and minDepotOut
                            minCostDepotOutside = BFLProblem.cost[i][j];
                            depotOutsideOfMinCost = j;
                        }
                    }
                }
            }
            // Post:
            // if there is at leat a depot inside the covering distance, then
            // depotInsideMinCost!= 0, and depotInsideOfMinCost is the depot
            // with minimum cost.
            // if there is at least a depot outside the covering distance, then
            // depotOutsideMinCost!= 0 and depotOutsideMinCost is the depot with
            // minimum cost.
            // if all depots are outside the covering distance, then
            // depotInsideOfMinCost==0 and depotOutsideMinCost!=0
            // if all depots are inside the covering distance, then
            // depotInsideOfMinCost!=0 and depotOutsideMinCost==0

            //Makes the assigment
            if (depotInsideOfMinCost != 0)
            {
                assigment[i - 1] = depotInsideOfMinCost;
            }
            else
            {
                assigment[i - 1] = depotOutsideOfMinCost;
            }

        }

        return assigment;
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