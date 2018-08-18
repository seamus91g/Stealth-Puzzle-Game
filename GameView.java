package com.example.gilroy.sneako;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

class GameView extends SurfaceView implements
        SurfaceHolder.Callback,
        GestureDetector.OnDoubleTapListener,
        GestureDetector.OnGestureListener {
    private static final String TAG = "LogTag";
    private static final String TAG_AC = "Debug2";
    private MainThread thread;
    MapSprite mapSprite;
    ArrayList<ISprite> sprites = new ArrayList<>();
    private final float pixelDensity;
    int viewBuffer = 150;       // Margin space to allow for view window outside of map area
    Position canvasPosition = new Position(0, 0);
    boolean mapScroll = false;

    public GameView(Context context) {
        super(context);
        this.pixelDensity = GameScreen.SCREEN_DENSITY;
        Log.d(TAG, "Pixel density: " + pixelDensity);
        getHolder().addCallback(this);
        thread = new MainThread(getHolder(), this);
        setFocusable(true);
    }

    public void update() {
        for (ISprite sprite : sprites) {
            sprite.update();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // Get a scaled bitmap and a drawable to display to canvas
        Bitmap tile = getScaledBitmap(R.drawable.scifi_floortile_spec);
        BitmapDrawable bmpd = new BitmapDrawable(getResources(), tile);
        // Shift the map to the center of the screen
        int xShift = (int) GameScreen.SCREEN_WIDTH / 2 - GameScreen.WIDTH_COUNT * tile.getWidth() / 2;
        int yShift = (int) GameScreen.SCREEN_HEIGHT / 2 - GameScreen.HEIGHT_COUNT * tile.getHeight() / 2;
        canvasPosition = new Position(xShift, yShift);
        // Create the map sprite
        mapSprite = new MapSprite(bmpd, tile.getHeight());
        sprites.add(mapSprite);
        // Start the main thread
        thread.setRunning(true);
        thread.start();
    }

    public Bitmap getScaledBitmap(int drawResource) {
        BitmapFactory.Options bmpopt = new BitmapFactory.Options();
        bmpopt.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), drawResource, bmpopt);
        int srcWidth = bmpopt.outWidth;
        bmpopt.inJustDecodeBounds = false;
        bmpopt.inSampleSize = 8;
        bmpopt.inScaled = true;
        bmpopt.inDensity = srcWidth;
        bmpopt.inTargetDensity = (int) ((45 * pixelDensity) * (bmpopt.inSampleSize));
        return BitmapFactory.decodeResource(getResources(), drawResource, bmpopt);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                thread.setRunning(false);
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            retry = false;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas != null) {
            canvas.translate(canvasPosition.x, canvasPosition.y);
            for (ISprite cs : sprites) {
                cs.draw(canvas);
            }
        }
    }

    /* OnGestureListener */
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (mapScroll) {
            int xShift = (int) distanceX;
            int yShift = (int) distanceY;
            canvasPosition.x -= xShift;
            canvasPosition.y -= yShift;
        }
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        Log.d(TAG, "Action was onDOWN");
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        Log.d(TAG, "Action was onShowPress");

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.d(TAG, "Action was onSingleTapUp");
        return false;
    }


    @Override
    public void onLongPress(MotionEvent e) {
        Log.d(TAG, "Action was onLongPress");

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.d(TAG, "Action was onFling");
        return false;
    }

    /* OnDoubleTapListener */
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        Log.d(TAG, "Action was onSingleTapConfirmed");
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
//        logActions(event);
        if (action == MotionEvent.ACTION_POINTER_DOWN) {
            mapScroll = true;
        } else if (action == MotionEvent.ACTION_UP) {
            mapScroll = false;
        }
        return true;
    }

    public void logActions(MotionEvent event) {
        int action = event.getActionMasked();
        if (event.getPointerCount() > 1) {
            Log.d(TAG_AC, "Multitouch event");
            Log.d(TAG, "Multitouch event");
        }
        if (Build.VERSION.SDK_INT >= 19) {
            Log.d(TAG, "!!! The action is " + MotionEvent.actionToString(action));
            if (action != MotionEvent.ACTION_MOVE) {
                Log.d(TAG_AC, "!!!= The action is " + MotionEvent.actionToString(action));
            }
        } else {
            Log.d(TAG, "The action number is :" + action);
            Log.d(TAG_AC, "The action number is :" + action);
        }
    }

    // Simple Pair style class to handle coordinates of a position
    class Position {
        int x;
        int y;

        Position(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
