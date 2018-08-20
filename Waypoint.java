package com.example.gilroy.sneako;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.UUID;

public class Waypoint implements ISprite {

    private final UUID ID;
    private final Bitmap waypoint;
    private int vertDisp;
    private int horzDisp;
    private int pauseCount = 0;
    private Waypoint prevWP;
    private Waypoint nextWP;
    private MapNode wpLocation;

    public Waypoint getPrevWP() {
        return prevWP;
    }

    public Waypoint getNextWP() {
        return nextWP;
    }

    void delete(){
        prevWP.nextWP = nextWP;
        nextWP.prevWP = prevWP;
    }

    public Waypoint(Bitmap bitmap, int across, int down, MapNode node, Waypoint prev) {
        waypoint = bitmap.copy(bitmap.getConfig(), true);
        int edgeDistance = (GameView.tileHeight - bitmap.getHeight()) / 2;
        vertDisp = down + edgeDistance;
        horzDisp = across + edgeDistance;
        ID = UUID.randomUUID();
        this.wpLocation = node;
        this.prevWP = prev;
    }
    public void setNextWP(Waypoint next){
        this.nextWP = next;
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
    }

    @Override
    public void update() {

    }
}
