package com.example.gilroy.sneako;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerNav extends Navigation {

    private static final String TAG = "PlayerNav";
    private Waypoint topWaypoint = null;
    private Map<UUID, Waypoint> waypoints = new HashMap<>();
    private int tileHeight;

    PlayerNav(MapNode insertionPoint, int tileHeight) {
        super(insertionPoint);
        this.tileHeight = tileHeight;
    }

    protected Map<UUID, Waypoint> getWaypoints() {
        return Collections.unmodifiableMap(waypoints);
    }

    public Waypoint getTopWaypoint() {
        return topWaypoint;
    }

    public void removeWaypoint(MapNode clickedNode) {

        // if no waypoints, return
        // if top waypoint, no stack, return
        // if top waypoint, stack, remove next highest from stack
        if (clickedNode.getWaypointCount() == 0) {
//                || (clickedNode.getWaypointCount() == 1 && clickedNode.getWaypoint(0).equals(topWaypoint))){
            return;
        }
        Waypoint deleteWP;
        // If > 1 waypoints, and indexHighest equal topwaypoint, choose second top
        // Else, choose top
        if ((clickedNode.getWaypointCount() > 1)
                && (clickedNode.getWaypoint(clickedNode.getWaypointCount() - 1).equals(topWaypoint))) {
            deleteWP = clickedNode.getWaypoint(clickedNode.getWaypointCount() - 2);
            clickedNode.removeWaypoint(clickedNode.getWaypointCount() -2);
        } else {
            deleteWP = clickedNode.getWaypoint(clickedNode.getWaypointCount() - 1);
            clickedNode.removeTopWaypoint();
        }

        if (clickedNode.equals(topWaypoint.getWaypointNode()) && clickedNode.getWaypointCount() == 0) {    // TODO: Doesnt consider stacked
            // Remove all route nodes between clicked and previous
            deleteLast();
        }else{
            // Remove all route nodes between previous and next
            deleteNotLast(deleteWP);
        }

    }

    private void deleteLast(){
        waypoints.remove(topWaypoint.getID());
        MapNode previous;
        topWaypoint.delete();
        if (topWaypoint.getPrevWP() == null) {
            previous = route.get(0);
            topWaypoint = null;
        } else {
            previous = topWaypoint.getPrevWP().getWaypointNode();
            topWaypoint = topWaypoint.getPrevWP();
        }
        while (!route.peekLast().equals(previous)) {
            route.removeLast();                 //
        }
//        clickedNode.removeTopWaypoint();    //

    }

    private void deleteNotLast(Waypoint deleteWP){
        // TODO: When deleting waypoints, two sequential waypoints may end up stacked on same node.
        deleteWP.delete();
        waypoints.remove(deleteWP.getID());
        Iterator<MapNode> it = route.iterator();
        MapNode mn = it.next();
        MapNode prev;
        if (deleteWP.getPrevWP() != null) {
            prev = deleteWP.getPrevWP().getWaypointNode();
        } else {
            prev = mn;
        }
        List<MapNode> newPathSlice = travel(prev, deleteWP.getNextWP().getWaypointNode());
        // Get prev Wp and next Wp. Find path between them. Insert this path into middle of route LinkedList
        int index = 0;
        while (!mn.equals(prev)) {
            mn = it.next();
            ++index;
        }
        mn = it.next();
        Log.d(TAG, "Removing : (" + mn.xPosition() + "," + mn.yPosition() + ")");
        while (!mn.equals(deleteWP.getNextWP().getWaypointNode())) {
            it.remove();
            mn = it.next();
            Log.d(TAG, "Removing : (" + mn.xPosition() + "," + mn.yPosition() + ")");
        }
        it.remove();
        if(newPathSlice == null){
            // Null newPathSlice implies two sequential waypoints are stacked on same node.
            // Walk through the waypoints and delete sequential ones
            Waypoint wp = topWaypoint;
            while (wp.getPrevWP() != null){
                if(wp.getPrevWP().getWaypointNode().equals(wp.getWaypointNode())){
//                    removeWaypoint(wp.getWaypointNode());
                    if(wp.getID() == topWaypoint.getID()){
                        topWaypoint = topWaypoint.getPrevWP();
                    }
                    wp.delete();
                    waypoints.remove(wp.getID());
                    wp.getWaypointNode().removeWaypoint(wp.getID());
                    return;     // TODO: Pretty sure impossible to be more than one. Confirm this.
                }
                wp = wp.getPrevWP();
            }
        }else{
            route.addAll(index + 1, newPathSlice);
        }
    }

    protected List<WayPath> getPath() {
        List<WayPath> waypath = new ArrayList<>();
        int offset = tileHeight / 2;
        for (int k = 0; k < route.size() - 1; ++k) {
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2);
            paint.setColor(Color.RED);
            Path path = new Path();
            path.moveTo(route.get(k).xPosition() * tileHeight + offset, route.get(k).yPosition() * tileHeight + offset);
            path.lineTo(route.get(k + 1).xPosition() * tileHeight + offset, route.get(k + 1).yPosition() * tileHeight + offset);
//            canvas.drawPath(path, paint);
            waypath.add(new WayPath(path, paint));
        }
        return waypath;
    }

    public void addWaypoint(MapNode clickedNode) {

        // if innacessible square
        //      do nothing
        // if wayPoints == empty
        if (topWaypoint == null) {
//            //      add node
//
        }

//          else{
//
//        }
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
                topWaypoint);
//        if (topWaypoint != null) {
//            topWaypoint.setNextWP(waypoint);
//        }
//        clickedNode.removeWaypoint(waypoint);
        topWaypoint = waypoint;
        waypoints.put(waypoint.getID(), waypoint);

//        printDeets("End tap");
        Log.d(GameView.TAG, "[~~~~~~~~~~~~~~~~~~~~~~~~~~~]");
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

}

