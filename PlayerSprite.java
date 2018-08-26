package com.example.gilroy.sneako;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.UUID;

public class PlayerSprite extends PlayerNav implements ISprite {
    private final UUID ID;

    private final Bitmap waypoint;
    private int vertDisp;
    private int horzDisp;
    private int tileHeight;

    public PlayerSprite(MapNode insertionPoint, int tileHeight, Resources resources) {
        super(insertionPoint, tileHeight);
        this.tileHeight = tileHeight;
        ID = UUID.randomUUID();

        Bitmap bmp = BitmapFactory.decodeResource(resources, R.drawable.selection_reticule_green);
        bmp = LevelConstructsSprite.getResizedBitmap(bmp, (4 * tileHeight) / 5, (4 * tileHeight) / 5);    // 80% size of tile
//            waypBmp = new BitmapDrawable(getResources(), bmp);
        waypoint = bmp;
//        waypoint = bitmap.copy(bitmap.getConfig(), true);
    }

    @Override
    public UUID getID() {
        return ID;
    }

    @Override
    public void draw(Canvas canvas) {
        for (Waypoint cs : getWaypoints().values()) {
            drawWP(canvas, cs);
        }
        for (WayPath waypath : getPath()) {
            canvas.drawPath(waypath.path, waypath.paint);
        }
    }

    @Override
    public void update() {

    }

    public void drawWP(Canvas canvas, Waypoint cs) {

        int edgeDistance = (tileHeight - waypoint.getHeight()) / 2;
        horzDisp = cs.getPosition().x*tileHeight + edgeDistance;
        vertDisp = cs.getPosition().y*tileHeight + edgeDistance;

        canvas.drawBitmap(waypoint, horzDisp, vertDisp, null);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(1);
        paint.setColor(Color.BLACK);
        paint.setTextSize(20);
        String msg;

        if (cs.findLabelDrawShift() > 0) {
            msg = "," + String.valueOf(cs.findStackHeight());
        } else {
            msg = String.valueOf(cs.findStackHeight());
        }
        canvas.drawText(msg, horzDisp + 10 + cs.findLabelDrawShift(), vertDisp + 25, paint);
    }
}
