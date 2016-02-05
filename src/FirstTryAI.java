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

            if (lCurrentPoint.x == pMap.getEndPoint().x && lCurrentPoint.y == pMap.getEndPoint().y)
            {
                break;
            }
            // just make sure
            assert(lCurrentPoint.x != pMap.getEndPoint().x || lCurrentPoint.y != pMap.getEndPoint().y);

            // get neighbors of the most promising node
            Point[] lLocalNeighbors = pMap.getNeighbors(lCurrentPoint);

            for (int i = 0; i < lLocalNeighbors.length; i++)
            {
                Double lCost = pMap.getCost(lCurrentPoint, lLocalNeighbors[i]) + lCurrent.mCost;

                if (lDistance.get(lLocalNeighbors[i]) == null || lCost < lDistance.get(lLocalNeighbors[i]))
                {
                    lDistance.put(lLocalNeighbors[i], lCost);
                    PointCost lToBeExplored = new PointCost(lLocalNeighbors[i], lCost);
                    lPrevious.put(lLocalNeighbors[i], lCurrentPoint);
                    lUnsettled.add(lToBeExplored);
                }
            }
        }

        // add the goal node
        lPath.add(lCurrentPoint);
        // go back up the path and return
        Point lPrev = lPrevious.get(lCurrentPoint);
        while (lPrev != null)
        {
            lPath.add(0, lPrev);
            lPrev = lPrevious.get(lPrev);
        }

        return lPath;
    }
}