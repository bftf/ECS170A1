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
            return this.mCost > pO1.mCost ? 1 : -1;
        }
    }

    private double computeHeurisitc(final TerrainMap pMap, final Point pCurrentPoint)
    {
        double lDistance = pCurrentPoint.distance(pMap.getEndPoint());

        //lDistance = Math.sqrt(Math.pow(pCurrentPoint.x - pMap.getEndPoint().x, 2) + Math.pow(pCurrentPoint.y - pMap.getEndPoint().y, 2));

        double lHeightDifference = pMap.getTile(pMap.getEndPoint()) - pMap.getTile(pCurrentPoint);
        double lOneStep = lHeightDifference / lDistance;


        return lDistance * Math.exp(lOneStep);
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


        // add the root to both maps
        lDistance.put(pMap.getStartPoint(), 0D);
        lUnsettled.add(new PointCost(pMap.getStartPoint(), 0D));

        // helpers for the while loop
        PointCost lCurrent;
        Point lCurrentPoint = null;


        while(lUnsettled.size() != 0)
        {
            // remove the most promising node from unsettled
            lCurrent = lUnsettled.poll();
            lCurrentPoint = lCurrent.mPoint;
            double lCurrentTrueDistance = lDistance.get(lCurrentPoint);

            if (lCurrentPoint.x == pMap.getEndPoint().x && lCurrentPoint.y == pMap.getEndPoint().y)
            {
                for (PointCost lPC : lUnsettled)
                {
                    if (lPC.mCost < lDistance.get(lCurrentPoint) && lPC.mPoint.equals(lCurrentPoint))
                    {
                        continue;
                    }
                }
                // we can break because the first solution we find has to be the best solution
                break;
            }

            // get neighbors of the most promising node
            Point[] lLocalNeighbors = pMap.getNeighbors(lCurrentPoint);

            for (int i = 0; i < lLocalNeighbors.length; i++)
            {
                Double lRawCost = pMap.getCost(lCurrentPoint, lLocalNeighbors[i]);

                // heuristic
                Double lH = computeHeurisitc(pMap, lLocalNeighbors[i]);
                // compute new cost
                Double lEstimatedCost = lRawCost + lCurrentTrueDistance + lH;

                if (lH < 0.0 || lRawCost < 0.0)
                {
                    int a = 0;
                }

                if (lDistance.get(lLocalNeighbors[i]) == null || lRawCost + lCurrentTrueDistance < lDistance.get(lLocalNeighbors[i]))
                {
                    // we either found an unvisited node or we found a better path to said node
                    lDistance.put(lLocalNeighbors[i], lRawCost + lCurrentTrueDistance);
                    lPrevious.put(lLocalNeighbors[i], lCurrentPoint);

                    // add it to unsettled, it is worth exploring this node
                    PointCost lToBeExplored = new PointCost(lLocalNeighbors[i], lEstimatedCost);
                    lUnsettled.add(lToBeExplored);
                }
            }
        }

        assert(lCurrentPoint.equals(pMap.getEndPoint()));

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
