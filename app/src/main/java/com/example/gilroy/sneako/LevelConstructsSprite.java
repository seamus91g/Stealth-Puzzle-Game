package com.example.gilroy.sneako;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.provider.ContactsContract;
import android.util.EventLog;
import android.util.Log;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LevelConstructsSprite implements ISprite {

    private final UUID ID;
    private LevelConstructs level;

    public LevelConstructsSprite(int[][][] wallmatrix, int dimension) {
        level = new LevelConstructs(wallmatrix, dimension);
        ID = UUID.randomUUID();
    }

    @Override
    public UUID getID() {
        return ID;
    }

    @Override
    public void draw(Canvas canvas) {
        List<WayPath> wallsToDraw = level.getWallsToDraw();
        for (WayPath wall : wallsToDraw) {
            canvas.drawPath(wall.path, wall.paint);
        }
    }

    @Override
    public void update() {

    }

    public MapNode getNode(Position position) {
        return level.getNode(position);
    }

    public static Bitmap getResizedBitmap(Bitmap bm, float newWidth, float newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = newWidth / width;
        float scaleHeight = newHeight / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }
}
