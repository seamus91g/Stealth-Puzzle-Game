package com.example.gilroy.sneako;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.UUID;

public class Waypoint implements ISprite {
    private static int waypointCount = 0;
    private final UUID ID;
    private final Bitmap waypoint;
    private int vertDisp;
    private int horzDisp;
    private int pauseCount = 0;
    private int wpOrderNumber;
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
            waypointCount = 0;
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
        waypointCount = count - 1;
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

    public Waypoint(Bitmap bitmap, int across, int down, MapNode node, Waypoint prev) {
        waypoint = bitmap.copy(bitmap.getConfig(), true);
        int edgeDistance = (GameView.tileHeight - bitmap.getHeight()) / 2;
        vertDisp = down + edgeDistance;
        horzDisp = across + edgeDistance;
        ID = UUID.randomUUID();
        this.wpLocation = node;
        this.prevWP = prev;
        if (prev != null) {
            prev.nextWP = this;
        }
        node.addWaypoint(this);
        calculateIndexDrawShfit();
        wpOrderNumber = ++waypointCount;
    }

    public MapNode getWaypointNode() {
        return wpLocation;
    }

    @Override
    public UUID getID() {
        return ID;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(waypoint, horzDisp, vertDisp, null);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(1);
        paint.setColor(Color.BLACK);
        paint.setTextSize(20);
        String msg;
        if (drawShift > 0) {
            msg = "," + String.valueOf(wpOrderNumber);
        } else {
            msg = String.valueOf(wpOrderNumber);
        }
        canvas.drawText(msg, horzDisp + 10 + drawShift, vertDisp + 25, paint);
    }

    @Override
    public void update() {

    }
}
