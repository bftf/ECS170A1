import java.awt.Point;
import java.util.*;


public class ShortestDivi implements AIModule
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
///*
        double heuristic = 0.0;

        double lGoalHeight = pMap.getTile(pMap.getEndPoint());  //getTile - HEIGHT OF TILE
        double lCurrentHeight = pMap.getTile(pCurrentPoint);

        double lDifferenceX = Math.abs(pMap.getEndPoint().x - pCurrentPoint.x);
        double lDifferenceY = Math.abs(pMap.getEndPoint().y - pCurrentPoint.y);
        double lMasterDiff = Math.max(lDifferenceX, lDifferenceY);  //max heightdiff between x and y coord
        double lMinDiff = Math.min(lDifferenceX, lDifferenceY);
        double lHeightDifference = Math.abs(lCurrentHeight - lGoalHeight);   //always positive  - to match the divisive optimal path cost

        double lDirectDistance = pCurrentPoint.distance(pMap.getEndPoint());

        //return Math.sqrt(lHeightDifference/Math.E) + lMasterDiff ;

        //return (lHeightDifference / Math.E) + lMasterDiff - lHeightDifference; - original

        //lHeightDifference / (0.5*Math.E) -- PathCost, 552.5487948977717, Uncovered, 83728, TimeTaken, 105720
        //                                 -- PathCost, 198.59165501141644, Uncovered, 2115, TimeTaken, 69
        //( lMinDiff / lHeightDifference) + lMasterDiff - lHeightDifference -- PathCost, 250 - divisive
        //lHeightDifference / (2*Math.E) -- PathCost, 552.5142702459052, Uncovered, 125396, TimeTaken, 123610
        //lHeightDifference / (lMasterDiff)  -- PathCost, 552.5116374761492, Uncovered, 137652, TimeTaken, 145225
        // (lHeightDifference / Math.E) + lMasterDiff -- PathCost, 552.9955885491745, Uncovered, 15812, TimeTaken, 1581
        //Math.abs( (Math.sqrt(lHeightDifference)) -- PathCost, 552.5265822768905, Uncovered, 131076, TimeTaken, 125831
        //(Math.sqrt(lHeightDifference)) + lMasterDiff -- PathCost, 553.9812790048047, Uncovered, 10219, TimeTaken, 1081
        //Math.sqrt(lHeightDifference/Math.E) + lMasterDiff -- PathCost, 552.9986846666419, Uncovered, 6092, TimeTaken, 545
        //                                                  -- PathCost, 198.59165501141644, Uncovered, 1407, TimeTaken, 31
        //Math.sqrt(lHeightDifference) + lMasterDiff ; -- PathCost, 553.9812790048047, Uncovered, 10219, TimeTaken, 984
        //                                             -- PathCost, 198.59165501141644, Uncovered, 1753, TimeTaken, 38
/*
double p = 1/lMasterDiff; // p <(minimum cost of taking one step)/(expected maximum path length)
heuristic *= (1.0 + p);
return heuristic;
*/
/*
double dx1 = lDifferenceX;
double dy1 = lDifferenceY;
double dx2 = start.x - goal.0;
double dy2 = start.y - goal.y;
cross = abs(dx1*dy2 - dx2*dy1)
double heuristic = 0;
heuristic += cross*0.001;
return (heuristic);
*/
/*
double D2 = Math.sqrt(2);
double D = lMasterDiff * Math.E;
double dx = lDifferenceX;
double dy = lDifferenceY;
return D * (dx + dy) + (D2 - 2 * D) * Math.min(dx, dy);
*/
///*
//Euclidean
        double D = 1; //D to the lowest cost between adjacent squares
        double dx = lDifferenceX;
        double dy = lDifferenceY;
        return D * Math.sqrt(dx * dx + dy * dy);
//PathCost, 198.59165501141644, Uncovered, 1004, TimeTaken, 23
//PathCost, 550.7157755911452, Uncovered, 2467, TimeTaken, 15
//*/

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


        while(!lUnsettled.isEmpty()) //(lUnsettled.size() != 0)
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
