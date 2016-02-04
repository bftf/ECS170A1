import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a sample AI that moves as far horizontally as necessary to reach the target,
 * then as far vertically as necessary to reach the target.  It is intended primarily as
 * a demonstration of the various pieces of the program.
 * 
 * @author Francois Demoullin
 */

class PointCost
{
    public Point mPoint;
    public double mCost;
    public Point mPrevPoint;

    public PointCost(final Point pPoint, final double pCost)
    {
        mPoint = pPoint;
        mCost = pCost;
        mPrevPoint = null;
    }

    public PointCost(final Point pPoint, final double pCost, final Point pPredecessor)
    {
        mPoint = pPoint;
        mCost = pCost;
        mPrevPoint = pPredecessor;
    }

    public void addCost(double pCost)
    {
        mCost += pCost;
    }
}

public class FirstTryAI implements AIModule
{
    // Creates the path to the goal using Dijkstras.
    public List<Point> createPath(final TerrainMap pMap)
    {
        // Holds the final path
        ArrayList<Point> lPath = new ArrayList<Point>();

        ArrayList<PointCost> lUnsettled = new ArrayList<PointCost>();

        // Visited map
        Map<Point, Double> lVisited = new HashMap<Point, Double>();

        // initialize all bools to false, we havent visited any nodes yet
        for (int i = 0; i < pMap.getWidth(); i++)
        {
            for (int j = 0; j < pMap.getHeight(); j++)
            {
                lVisited[i][j] = false;
            }
        }

        // Keep track of where we are and add the start point.
        final Point lRoot = pMap.getStartPoint();

        // add root to path and say we have visited root
        // lVisited[lRoot.x][lRoot.y] = true;
        lUnsettled.add(new PointCost(lRoot, 0.0));

        // make sure start point is not the end point. That would be pretty silly though
        if (lRoot.x == pMap.getEndPoint().x && lRoot.y == pMap.getEndPoint().y)
        {
            // the start point happened to be at the end point.
            return lPath;
        }
        
        // here we go
        while(lUnsettled.size() != 0)
        {
            // sort unsettled list

            boolean lSwapped  = true;
            int j = 0;
            PointCost lTemp;
            while (lSwapped)
            {
                lSwapped = false;
                j++;
                for (int i = 0; i < lUnsettled.size() - j; i++)
                {
                    if (lUnsettled.get(i).mCost > lUnsettled.get(i + 1).mCost)
                    {
                        lTemp = lUnsettled.get(i);
                        lUnsettled.set(i, lUnsettled.get(i + 1));
                        lUnsettled.set(i + 1, lTemp);
                        lSwapped = true;
                    }
                }
            }

            // make sure we really sorted the list correctly
            for (int i = 0; i < lUnsettled.size(); i++)
            {
                if (i != lUnsettled.size() - 1)
                {
                    assert (lUnsettled.get(i).mCost <= lUnsettled.get(i + 1).mCost);
                }
            }

            if (lUnsettled.get(0).mPoint.x == pMap.getEndPoint().x && lUnsettled.get(0).mPoint.y == pMap.getEndPoint().y)
            {
                return lUnsettled.get(0).mPrevPoint;
            }

            // get neighbors of the most promising node
            Point[] lLocalNeighbors = pMap.getNeighbors(lUnsettled.get(0).mPoint);

            for (int i = 0; i < lLocalNeighbors.length; i++)
            {
                // make sure the neighbor is valid and we havent visited it yet
                if (!lVisited[lLocalNeighbors[i].x][lLocalNeighbors[i].y] && pMap.validTile(lLocalNeighbors[i]))
                {
                    // this node is valid and has not yet been visited. We are adding it to the unsettled list
                    double lOldCost = lUnsettled.get(0).mCost;
                    double lNewCost = pMap.getCost(lUnsettled.get(0).mPoint, lLocalNeighbors[i]);

                    lUnsettled.add(new PointCost(lLocalNeighbors[i], lOldCost + lNewCost, lUnsettled.get(0).mPrevPoints));
                }
            }

            // add the node we just expanded to visited
            lVisited[lUnsettled.get(0).mPoint.x][lUnsettled.get(0).mPoint.y] = true;
            // remove the node we just expanded from the unsettled list
            lUnsettled.remove(0);
            System.out.println(pMap.getNumVisited());
        }

        return null;
    }
}