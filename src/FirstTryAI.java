import java.awt.Point;
import java.util.*;

/**
 * This is a sample AI that moves as far horizontally as necessary to reach the target,
 * then as far vertically as necessary to reach the target.  It is intended primarily as
 * a demonstration of the various pieces of the program.
 * 
 * @author Francois Demoullin
 */

class PointCost implements Comparable<PointCost>
{
    public Point mPoint;
    public double mCost;

    // constructor
    public PointCost(final Point pPoint, final double pCost)
    {
        mPoint = pPoint;
        mCost = pCost;
    }

    // for priority queue
    public int compareTo(PointCost pOther)
    {
        return (int)(pOther.mCost - this.mCost);
    }
}

public class FirstTryAI implements AIModule
{
    // Creates the path to the goal using Dijkstras.
    public List<Point> createPath(final TerrainMap pMap)
    {
        // Holds the final path
        ArrayList<Point> lPath = new ArrayList<Point>();

        PriorityQueue<PointCost> lUnsettled = new PriorityQueue<>();
        Map<Point, Double> lDistance = new HashMap<>();
        Map<Point, Point> lPrevious = new HashMap<>();

        lDistance.put(pMap.getStartPoint(), 0D);
        lUnsettled.add(new PointCost(pMap.getStartPoint(), 0D));

        PointCost lCurrent = null;
        Point lCurrentPoint = null;

        // here we go
        while(lUnsettled.size() != 0)
        {
            lCurrent = lUnsettled.poll();
            lCurrentPoint = lCurrent.mPoint;

            if (lCurrentPoint == pMap.getEndPoint())
            {
                break;
            }

            // get neighbors of the most promising node
            Point[] lLocalNeighbors = pMap.getNeighbors(lCurrentPoint);

            for (int i = 0; i < lLocalNeighbors.length; i++)
            {
                Double lCost = pMap.getCost(lCurrentPoint, lLocalNeighbors[i]) + lCurrent.mCost + 1;
                if (lDistance.get(lLocalNeighbors[i]) == null || lCost < lDistance.get(lLocalNeighbors[i]))
                {                lDistance.put(lLocalNeighbors[i], lCost);

                    PointCost lToBeExplored = new PointCost(lLocalNeighbors[i], lCost);
                    lPrevious.put(lLocalNeighbors[i], lCurrentPoint);
                    lUnsettled.add(lToBeExplored);
                }
            }
        }

        Point lPrev = lPrevious.get(lCurrentPoint);
        while (lPrev != null)
        {
            lPath.add(lPrev);
            lPrev = lPrevious.get(lPrev);
        }

        return lPath;
    }
}