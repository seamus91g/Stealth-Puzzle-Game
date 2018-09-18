package com.example.gilroy.sneako;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.UUID;

public class Waypoint {
    private final UUID ID;
    private int pauseCount = 0;
    private int wpOrderNumber = 1;  // Default. Over-written if previous node
    private Waypoint prevWP;
    private Waypoint nextWP;
    private MapNode wpLocation;
    private int drawShift;
    private UUID soldierID;     // ID of the soldier to whom the waypoint belongs
    private String indexText;

    public Waypoint getPrevWP() {
        return prevWP;
    }

    public Waypoint getNextWP() {
        return nextWP;
    }

    void delete() {
        if (nextWP == null && prevWP == null) {
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

    public int findStackHeight() {
        return wpOrderNumber;
    }

    private void updateWPOrder() {
        Waypoint wp;
        if (this.prevWP != null) {
            wp = this.prevWP;
        } else {
            wp = this.nextWP;
        }
        while (wp.prevWP != null) {
            wp = wp.getPrevWP();
        }
        int count = 1;
        while (wp != null) {
            wp.wpOrderNumber = count;
            wp.calculateIndexDrawShfit();
            wp = wp.nextWP;
            ++count;
        }
    }

    private void calculateIndexDrawShfit() {
        drawShift = 0;
        if (wpLocation.getWaypointCount(soldierID) > 1) {
            for (int i = 0; i < wpLocation.getWaypointCount(soldierID); ++i) {
                if (wpLocation.getWaypoint(soldierID, i).equals(this)) {
                    break;
                } else if (wpLocation.getWaypoint(soldierID, i).wpOrderNumber > 9) {
                    drawShift += 20;
                } else {
                    drawShift += 10;
                }
            }
        }
        updateWaypointText();
    }

    public int findLabelDrawShift() {
        return drawShift;
    }

    public Waypoint(MapNode node, Waypoint prev, UUID soldierID) {
        this.soldierID = soldierID;
        ID = UUID.randomUUID();
        this.wpLocation = node;
        this.prevWP = prev;
        if (prev != null) {
            wpOrderNumber = prev.wpOrderNumber + 1;
            if (prev.nextWP != null) {        // Inserting waypoint in middle of route
                prev.nextWP.prevWP = this;
                incrementSubsequentWpNumbers(prev.nextWP, wpOrderNumber + 1);
            }
            this.nextWP = prev.nextWP;
            prev.nextWP = this;
        }
        node.addWaypoint(soldierID, this);      // TODO: does node really need to be a parameter .. ??
        calculateIndexDrawShfit();
    }

    public void incrementSubsequentWpNumbers(Waypoint next, int startingWPNumber) {
        do {
            next.wpOrderNumber = startingWPNumber;
            next.updateWaypointText();
            next = next.nextWP;
            ++startingWPNumber;
        } while (next != null);
    }

    public void updateWaypointText() {
        if (drawShift > 0) {
            indexText = "," + String.valueOf(wpOrderNumber);
        } else {
            indexText = String.valueOf(wpOrderNumber);
        }
    }

    public String getWaypointText() {
        return indexText;
    }

    public int getWaypointIndex() {
        return wpOrderNumber;
    }

    public MapNode getWaypointNode() {
        return wpLocation;
    }

    public Position getPosition() {
        return wpLocation.getPosition();
    }

    public UUID getID() {
        return ID;
    }

}
