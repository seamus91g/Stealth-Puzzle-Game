package com.example.gilroy.sneako;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.UUID;

public class Waypoint {
//    private static int waypointCount = 0;
    private final UUID ID;
    private int pauseCount = 0;
    private int wpOrderNumber = 1;  // Default. Over-written if previous node
    private Waypoint prevWP;
    private Waypoint nextWP;
    private MapNode wpLocation;
    private int drawShift;

    public Waypoint getPrevWP() {
        return prevWP;
    }

    public Waypoint getNextWP() {
        return nextWP;
    }

    void delete() {
        if (nextWP == null && prevWP == null) {
//            waypointCount = 0;
            return;
        }
        if (nextWP == null) {
            prevWP.nextWP = null;
        } else if (prevWP == null) {
            nextWP.prevWP = null;
        } else {
            prevWP.nextWP = nextWP;
            nextWP.prevWP = prevWP;
        }
        updateWPOrder();
    }

    public int findStackHeight(){
        return wpOrderNumber;
    }

    private void updateWPOrder() {
        Waypoint wp;
        if(this.prevWP != null) {
            wp = this.prevWP;
        }else{
            wp = this.nextWP;
        }
        while (wp.prevWP != null) {
            wp = wp.getPrevWP();
        }
        int count = 1;
        while (wp != null) {
            wp.wpOrderNumber = count;
//            wp.setStackHeight();
            wp.calculateIndexDrawShfit();
            wp = wp.nextWP;
            ++count;
        }
//        waypointCount = count - 1;
    }

    private void calculateIndexDrawShfit() {
        drawShift = 0;
        if (wpLocation.getWaypointCount() > 1) {
            for (int i = 0; i < wpLocation.getWaypointCount(); ++i) {
                if (wpLocation.getWaypoint(i).equals(this)) {
                    return;
                } else if (wpLocation.getWaypoint(i).wpOrderNumber > 9) {
                    drawShift += 20;
                } else {
                    drawShift += 10;
                }
            }
        }
    }
    public int findLabelDrawShift(){
        return drawShift;
    }

    public Waypoint(MapNode node, Waypoint prev) {
        ID = UUID.randomUUID();
        this.wpLocation = node;
        this.prevWP = prev;
        if (prev != null) {
            prev.nextWP = this;
            wpOrderNumber = prev.wpOrderNumber +1;
        }
        node.addWaypoint(this);
        calculateIndexDrawShfit();
//        wpOrderNumber = ++waypointCount;
    }

    public MapNode getWaypointNode() {
        return wpLocation;
    }
    public Position getPosition(){
        return wpLocation.getPosition();
    }

//    @Override
    public UUID getID() {
        return ID;
    }

//    @Override


//    @Override
//    public void update() {
//
//    }
}
