package com.dippin.bsp;

import static com.evrhel.bsp.Vector2.*;

/**
 * Class containing utilities for determining relative locations of objects.
 */
public class RelativePosition {

    public static final int BEHIND = 0;         // The object is behind another object
    public static final int FRONT = 1;          // The object is in front of another object
    public static final int INTERSECT = 2;      // The object intersects another object
    public static final int ON = 3;             // The object is on another object

    /**
     * Returns the relative position of a point to a plane.
     *
     * @param plane The plane.
     * @param test The point to test against.
     * @return ON, BEHIND, or FRONT if the point is on the plane, behind the plane,
     * or in front of the plane, respectively.
     */
    public static int positionOf(Plane plane, Vector2 test) {
        Vector2 planeNormal = plane.getNormal(), planeOrigin = plane.getOrigin();
        float prod = dot(planeNormal, test.sub(planeOrigin));
        if (prod == 0)
            return ON;
        else if (prod < 0)
            return BEHIND;
        else
            return FRONT;
    }

    /**
     * Returns the relative position of a polygon to a plane.
     *
     * @param plane The plane.
     * @param test The polygon.
     * @return BEHIND, FRONT, INTERSECT, or ON if the polygon is behind, in front of,
     * intersecting, or on the plane, respectively
     */
    public static int positionOf(Plane plane, Polygon test) {
        int start = positionOf(plane, test.getStart());
        int end = positionOf(plane, test.getEnd());
        if (start == end)
            return start;
        else if (start == ON)
            return end;
        else if (end == ON)
            return start;
        else
            return INTERSECT;
    }

    private RelativePosition() { }
}
