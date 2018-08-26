package com.example.gilroy.sneako;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.MotionEvent;

import java.util.UUID;

public class MapSprite implements ISprite {

    private final UUID ID;
    private BitmapDrawable image;
    private Map map;
    private int tileHeight = 0;

    MapSprite(int[][][] wallmatrix, Resources resources) {
        createMap(resources);
        float wer = GameScreen.SCREEN_WIDTH;
        map = new Map(wallmatrix, tileHeight, GameScreen.SCREEN_WIDTH , GameScreen.SCREEN_HEIGHT);
        image.setTileModeX(Shader.TileMode.REPEAT);
        image.setTileModeY(Shader.TileMode.REPEAT);
        Rect mapRect = new Rect(0, 0, wallmatrix[0][0].length * tileHeight, wallmatrix[0].length * tileHeight);
        image.setBounds(mapRect);
        ID = UUID.randomUUID();
    }

    private void createMap(Resources resources) {
        // Get a scaled bitmap and a drawable to display to canvas
        Bitmap tile = getScaledBitmap(R.drawable.ice_2, resources);
        BitmapDrawable bmpd = new BitmapDrawable(resources, tile);
        tileHeight = tile.getHeight();
        image = bmpd;
    }

    public Bitmap getScaledBitmap(int drawResource, Resources resources) {
        BitmapFactory.Options bmpopt = new BitmapFactory.Options();
        bmpopt.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, drawResource, bmpopt);
        int srcWidth = bmpopt.outWidth;
        bmpopt.inJustDecodeBounds = false;
        bmpopt.inSampleSize = 8;
        bmpopt.inScaled = true;
        bmpopt.inDensity = srcWidth;
        bmpopt.inTargetDensity = (int) ((45 * GameScreen.SCREEN_DENSITY) * (bmpopt.inSampleSize));
        return BitmapFactory.decodeResource(resources, drawResource, bmpopt);
    }

    @Override
    public UUID getID() {
        return ID;
    }

    @Override
    public void draw(Canvas canvas) {
        image.draw(canvas);
    }

    @Override
    public void update() {
        return;
    }

    public int getTileHeight() {
        return map.getTileHeight();
    }

    public Position clickRegion(float x, float y) {
        return map.clickRegion(x, y);
    }

    public Position clickRegion(MotionEvent event) {
        return clickRegion(event.getX(), event.getY());
    }

    public void shiftCanvas(float distanceX, float distanceY) {
        map.shiftCanvas(distanceX, distanceY);
    }

    public Position offsetPosition() {
        return map.offsetPosition();
    }

//    public static Bitmap getResizedBitmap(Bitmap bm, float newWidth, float newHeight) {
//        int width = bm.getWidth();
//        int height = bm.getHeight();
//        float scaleWidth = ((float) newWidth) / width;
//        float scaleHeight = ((float) newHeight) / height;
//        // CREATE A MATRIX FOR THE MANIPULATION
//        Matrix matrix = new Matrix();
//        // RESIZE THE BIT MAP
//        matrix.postScale(scaleWidth, scaleHeight);
//
//        // "RECREATE" THE NEW BITMAP
//        Bitmap resizedBitmap = Bitmap.createBitmap(
//                bm, 0, 0, width, height, matrix, false);
//        bm.recycle();
//        return resizedBitmap;
//    }
}
