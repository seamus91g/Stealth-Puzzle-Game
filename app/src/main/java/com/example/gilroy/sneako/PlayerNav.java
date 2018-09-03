package com.example.gilroy.sneako;

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
    private boolean isPathDrawUpdated = false;

    PlayerNav(MapNode insertionPoint) {
        super(insertionPoint);
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

    private void clearWaypoint(Waypoint wp) {
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
        isPathDrawUpdated = false;
        Waypoint deleteWP;
        // If > 1 waypoints, and indexHighest equal topwaypoint, choose second top
        // Else, choose top
        if ((clickedNode.getWaypointCount(ID) > 1)
                && (clickedNode.getWaypoint(ID, clickedNode.getWaypointCount(ID) - 1).equals(topWaypoint))) {
            deleteWP = clickedNode.getWaypoint(ID, clickedNode.getWaypointCount(ID) - 2);
        } else {
            deleteWP = clickedNode.getWaypoint(ID, clickedNode.getWaypointCount(ID) - 1);
        }

        if (deleteWP.equals(topWaypoint)) {
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

    private LinkedList<MapNode> reCalcRoute() {
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
        List<MapNode> pathToTake = travel(route.getLast(), clickedNode);
        if (pathToTake == null) {
            Log.d(GameView.TAG, "No path found!");
            return;
        }
        isPathDrawUpdated = false;
        route.addAll(pathToTake);

        Waypoint waypoint = new Waypoint(
                clickedNode,
                topWaypoint,
                ID);
        topWaypoint = waypoint;
        waypoints.put(waypoint.getID(), waypoint);
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
    protected void setPathDrawUpdated(){
        isPathDrawUpdated = true;
    }
    protected boolean isPathDrawUpdated(){
        return isPathDrawUpdated;
    }

    protected UUID getID() {
        return ID;
    }
}

