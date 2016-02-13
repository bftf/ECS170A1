/**
 * Created by Francois on 2016-02-11.
 */

import java.awt.Point;
import java.util.*;


public class FirstAStarTry implements AIModule
{
    // private class -> this would be a struct in a C like language
    private class PointCost implements Comparable<PointCost>
    {
        // terrible coding practice, I know
        public Point mPoint;
        public double mCost;

        // constructor
        public PointCost(final Point pPoint, final double pCost)
        {
            mPoint = pPoint;
            mCost = pCost;
        }

        // comparator is implemented to return the smallest of 2 elements
        @Override
        public int compareTo(PointCost pO1)
        {
            if (pO1.mCost - this.mCost > 0)
            {
                return -1;
            }
            if (pO1.mCost - this.mCost < 0 )
            {
                return 1;
            }
            return 0;
        }
    }

    private Double computeHeurisitc(final TerrainMap pMap, final Point pCurrentPoint, final Double pMinCost)
    {
        double lCurrentHeight = pMap.getTile(pCurrentPoint);
        double lGoalHeight = pMap.getTile(pMap.getEndPoint());
        double lHeightDifference = lGoalHeight - lCurrentHeight;

        Double lSmallestDistance = Math.sqrt(Math.pow(pCurrentPoint.x - pMap.getEndPoint().x, 2)) + Math.sqrt(Math.pow(pCurrentPoint.y - pMap.getEndPoint().y, 2));

        return lSmallestDistance * Math.exp(lHeightDifference);
    }

    // Creates the path to the goal using a heurisitc.

    public List<Point> createPath(final TerrainMap pMap)
    {
        // Holds the final path
        ArrayList<Point> lPath = new ArrayList<>();

        // keeps track of all not yet expanded nodes
        PriorityQueue<PointCost> lUnsettled = new PriorityQueue<>();
        // hashes a point to its SHORTEST distance from the root node
        Map<Point, Double> lDistance = new HashMap<>();
        // hashes a node to its previous node
        // the root is not hashed (root hashes to null)
        Map<Point, Point> lPrevious = new HashMap<>();

        Double lAverageCost = 0D;
        int lCounter = 0;

        // add the root to both maps
        lDistance.put(pMap.getStartPoint(), 0D);
        lUnsettled.add(new PointCost(pMap.getStartPoint(), computeHeurisitc(pMap, pMap.getStartPoint(), lAverageCost)));

        // helpers for the while loop
        PointCost lCurrent = null;
        Point lCurrentPoint = null;



        while(lUnsettled.size() != 0)
        {
            // remove the most promising node from unsettled
            lCurrent = lUnsettled.poll();
            lCurrentPoint = lCurrent.mPoint;

            if (lCurrentPoint.x == pMap.getEndPoint().x && lCurrentPoint.y == pMap.getEndPoint().y)
            {
                // we can break because the first solution we find has to be the best solution
                break;
            }

            // get neighbors of the most promising node
            Point[] lLocalNeighbors = pMap.getNeighbors(lCurrentPoint);

            for (int i = 0; i < lLocalNeighbors.length; i++)
            {
                lCounter++;
                Double lRawCost = pMap.getCost(lCurrentPoint, lLocalNeighbors[i]);
                lAverageCost = (lAverageCost + lRawCost);

                // heuristic
                Double lH = computeHeurisitc(pMap, lLocalNeighbors[i], lAverageCost);
                // compute new cost
                Double lCost = pMap.getCost(lCurrentPoint, lLocalNeighbors[i]) + lCurrent.mCost +  lH;

                if (lDistance.get(lLocalNeighbors[i]) == null || lCost < lDistance.get(lLocalNeighbors[i]))
                {
                    // we either found an unvisited node or we found a better path to said node
                    lDistance.put(lLocalNeighbors[i], lCost);
                    PointCost lToBeExplored = new PointCost(lLocalNeighbors[i], lCost);
                    lPrevious.put(lLocalNeighbors[i], lCurrentPoint);

                    // add it to unsettled, it is worth exploring this node
                    lUnsettled.add(lToBeExplored);
                }
            }
        }

        // add the goal node first
        lPath.add(lCurrentPoint);

        // go back up the path and return
        Point lPrev = lPrevious.get(lCurrentPoint);
        while (lPrev != null)
        {
            lPath.add(0, lPrev);
            lPrev = lPrevious.get(lPrev);
        }

        return lPath;
    } // memory from the maps is de-allocated here! No mem leaks possible
}
