package com.example.gilroy.sneako;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerNav extends Navigation {
    private final UUID ID;

    private static final String TAG = "PlayerNav";
    private Waypoint topWaypoint = null;
    private Map<UUID, Waypoint> waypoints = new HashMap<>();
    private int tileHeight;

    PlayerNav(MapNode insertionPoint, int tileHeight) {
        super(insertionPoint);
        this.tileHeight = tileHeight;
        ID = UUID.randomUUID();
    }

    public List<MapNode> getRoute() {
        return route;
    }

    protected Map<UUID, Waypoint> getWaypoints() {
        return Collections.unmodifiableMap(waypoints);
    }

    public Waypoint getTopWaypoint() {
        return topWaypoint;
    }

    public void clearWaypoint(Waypoint wp) {
        wp.getWaypointNode().removeWaypoint(ID, wp.getID());
        waypoints.remove(wp.getID());
        if (wp.equals(topWaypoint)) {
            topWaypoint = topWaypoint.getPrevWP();
        }
        wp.delete();
    }

    public void removeWaypoint(MapNode clickedNode) {
        // if no waypoints, return
        // if top waypoint, no stack, return
        // if top waypoint, stack, remove next highest from stack
        if (clickedNode.getWaypointCount(ID) == 0) {
            return;
        }
        Waypoint deleteWP;
        // If > 1 waypoints, and indexHighest equal topwaypoint, choose second top
        // Else, choose top
        if ((clickedNode.getWaypointCount(ID) > 1)
                && (clickedNode.getWaypoint(ID, clickedNode.getWaypointCount(ID) - 1).equals(topWaypoint))) {
            deleteWP = clickedNode.getWaypoint(ID, clickedNode.getWaypointCount(ID) - 2);
        } else {
            deleteWP = clickedNode.getWaypoint(ID, clickedNode.getWaypointCount(ID) - 1);
        }

        if (deleteWP.equals(topWaypoint)) {    // TODO: Doesnt consider stacked
            // Remove all route nodes between clicked and previous
            deleteLast();
            clearWaypoint(deleteWP);
        } else {
            // Remove all route nodes between previous and next
            deleteNotLast(deleteWP);
        }
    }

    private void deleteLast() {
        MapNode previous;
        if (topWaypoint.getPrevWP() == null) {
            previous = route.get(0);
        } else {
            previous = topWaypoint.getPrevWP().getWaypointNode();
        }
        deleteToEnd(previous);
    }

    // Delete to end, non inclusive of start
    private void deleteToEnd(MapNode begin) {
        route.removeLast();                 //
        while (!route.peekLast().equals(begin)) {
            route.removeLast();                 //
        }
    }

    private void deleteNotLast(Waypoint deleteWP) {
        MapNode prevNode;
        if (deleteWP.getPrevWP() != null) {
            prevNode = deleteWP.getPrevWP().getWaypointNode();
        } else {
            prevNode = route.get(0);      // First node
        }
        MapNode nextNode = deleteWP.getNextWP().getWaypointNode();
        if (nextNode.equals(prevNode)) {
            clearWaypoint(deleteWP.getNextWP());
        }
        clearWaypoint(deleteWP);
        route = reCalcRoute();
    }

    public LinkedList<MapNode> reCalcRoute() {
        List<MapNode> testRoute = new ArrayList<>();
        Waypoint wp = topWaypoint;
        testRoute.add(wp.getWaypointNode());
        while (wp.getPrevWP() != null) {
            testRoute.addAll(travel(wp.getWaypointNode(), wp.getPrevWP().getWaypointNode()));
            wp = wp.getPrevWP();
        }
        testRoute.addAll(travel(wp.getWaypointNode(), route.get(0)));
        LinkedList<MapNode> ll = new LinkedList<>();
        for (MapNode mn : testRoute) {
            ll.add(0, mn);
        }
        return ll;
    }

    public void addWaypoint(MapNode clickedNode) {

        if (topWaypoint == null) {
//            //      add node
        }

        List<MapNode> pathToTake = travel(route.getLast(), clickedNode);
        if (pathToTake == null) {
            Log.d(GameView.TAG, "No path found!");
            return;
        }
        route.addAll(pathToTake);
        // if node = on top
        //      Add pause
        // wayPointStack.add(node)
        // if node in stack already
        //      Display integer count in

        Waypoint waypoint = new Waypoint(
                clickedNode,
                topWaypoint,
                ID);
//        if (topWaypoint != null) {
//            topWaypoint.setNextWP(waypoint);
//        }
//        clickedNode.removeWaypoint(waypoint);
        topWaypoint = waypoint;
        waypoints.put(waypoint.getID(), waypoint);

//        printDeets("End tap");
        Log.d(GameView.TAG, "[~~~~~~~~~~~~~~~~~~~~~~~~~~~]");
    }

    protected List<WayPath> getPath(int color) {
        List<WayPath> waypath = new ArrayList<>();
        int offset = tileHeight / 2;
        for (int k = 0; k < route.size() - 1; ++k) {
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2);
            paint.setColor(color);
            Path path = new Path();
            path.moveTo(route.get(k).xPosition() * tileHeight + offset, route.get(k).yPosition() * tileHeight + offset);
            path.lineTo(route.get(k + 1).xPosition() * tileHeight + offset, route.get(k + 1).yPosition() * tileHeight + offset);
//            canvas.drawPath(path, paint);
            waypath.add(new WayPath(path, paint));
        }
        return waypath;
    }

    public void printDeets(String note) {
        Log.d(TAG, "\n======== " + note + " =========");
        printRoute(route);
        printWaypoints(topWaypoint);
    }

    private void printRoute(List<MapNode> route) {
        StringBuilder shortestPath = new StringBuilder();
        for (MapNode mn : route) {
            shortestPath.append("(");
            shortestPath.append(mn.xPosition());
            shortestPath.append(",");
            shortestPath.append(mn.yPosition());
            shortestPath.append(")->");
        }
        Log.d(TAG, shortestPath.toString());
    }

    private void printWaypoints(Waypoint topWaypoint) {
        StringBuilder msg = new StringBuilder("Waypoints :");
        if (topWaypoint != null) {
            msg.append("(")
                    .append(topWaypoint.getWaypointNode().xPosition())
                    .append(",")
                    .append(topWaypoint.getWaypointNode().yPosition())
                    .append("), ");
            Waypoint prev = topWaypoint.getPrevWP();
            while (prev != null) {
                msg.append("(")
                        .append(prev.getWaypointNode().xPosition())
                        .append(",")
                        .append(prev.getWaypointNode().yPosition())
                        .append("), ");
                prev = prev.getPrevWP();
            }
            Log.d(TAG, msg.toString());
        } else {
            Log.d(TAG, "No waypoints!");
        }
    }

    protected UUID getID() {
        return ID;
    }
}

