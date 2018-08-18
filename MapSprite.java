package com.example.gilroy.sneako;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;

public class MapSprite implements ISprite{

    private BitmapDrawable image;
    private Rect mapRect;

    public MapSprite(BitmapDrawable bmp, int dimension){
        image = bmp;
        image.setTileModeX(Shader.TileMode.REPEAT);
        image.setTileModeY(Shader.TileMode.REPEAT);
        int mapWidth = GameScreen.WIDTH_COUNT * dimension;
        int mapHeight = GameScreen.HEIGHT_COUNT * dimension;
        mapRect = new Rect(0, 0, mapWidth, mapHeight);
        image.setBounds(mapRect);
    }

    @Override
    public void draw(Canvas canvas){
        image.draw(canvas);
    }
    @Override
    public void update(){
        return;
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
