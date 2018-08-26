package com.example.gilroy.sneako;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

class GameView extends SurfaceView implements
        SurfaceHolder.Callback,
        GestureDetector.OnDoubleTapListener,
        GestureDetector.OnGestureListener {
    public static final String TAG = "LogTag";
    private static final String TAG_AC = "Debug2";
    private MainThread thread;
    Map<UUID, ISprite> sprites = new LinkedHashMap<>();     // Drawing order is important
    int viewBuffer = 150;       // Margin space to allow for view window outside of map area
    boolean mapScroll = false;
    int[][][] wallmatrix;
    int numAllies;
    MapSprite map;
    LevelConstructsSprite level;
    PlayerSprite currentlyActive;

    {
        numAllies = 2;
        wallmatrix = new int[][][]{
                // Horizontal walls
                {
                        {0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 1, 1, 1, 1, 1, 1, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 1, 1, 0},
                        {0, 0, 1, 1, 0, 0, 0, 0},
                        {0, 0, 0, 0, 1, 1, 1, 0},
                        {0, 0, 0, 0, 0, 1, 1, 0},
                        {0, 1, 1, 1, 0, 1, 1, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0}},
                // Vertical walls
                {
                        {0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0},
                        {0, 1, 0, 1, 0, 0, 0, 0},
                        {1, 0, 0, 1, 0, 1, 0, 0},
                        {1, 1, 0, 1, 0, 1, 0, 0},
                        {1, 1, 0, 1, 0, 0, 0, 0},
                        {1, 0, 0, 1, 0, 0, 0, 0},
                        {0, 1, 0, 1, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0},
                }
        };
    }

    public GameView(Context context) {
        super(context);
        // Initialise the map
        map = new MapSprite(wallmatrix, getResources());
        level = new LevelConstructsSprite(wallmatrix, map.getTileHeight());
        sprites.put(map.getID(), map);
        sprites.put(level.getID(), level);
//        this.pixelDensity = GameScreen.SCREEN_DENSITY;
        getHolder().addCallback(this);
        thread = new MainThread(getHolder(), this);
        setFocusable(true);
        initialiseAllies();
    }

    private void initialiseAllies() {
        PlayerSprite ally;
        for (int i=0; i<numAllies; ++i){
            ally = new PlayerSprite(level.getInsertionPoint(), map.getTileHeight(), getResources());
            sprites.put(ally.getID(), ally);
            currentlyActive = ally;
        }
    }

    public void update() {
        for (ISprite sprite : sprites.values()) {
            sprite.update();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // Start the main thread
        thread.setRunning(true);
        thread.start();
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
            canvas.translate(map.offsetPosition().x, map.offsetPosition().y);
//            Log.d(TAG, "[ Drawing " + sprites.size() + "sprites! ]");
            for (ISprite cs : sprites.values()) {
                cs.draw(canvas);
            }
        }
    }

    /* OnGestureListener */
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (mapScroll) {
            map.shiftCanvas(distanceX, distanceY);
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
    public void onLongPress(MotionEvent e) {
        Log.d(TAG, "Action was onLongPress");
        if (map.clickRegion(e).x == -1){
            return;
        }
        MapNode clickedNode = level.getNode(map.clickRegion(e));
        // ActiveCharacter.removeWaypoint();
        // TODO: Must be able remove stacked waypoint even on top node
        if(clickedNode.getWaypointCount() == 0){
            return;
        }
        if(clickedNode.getWaypoint(0).equals(currentlyActive.getTopWaypoint())){
            return;
        }
        currentlyActive.removeWaypoint(clickedNode);
    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        if (map.clickRegion(event).x == -1){
            Log.d(TAG, "Out of bounds!~");
            return false;
        }
        MapNode clickedNode = level.getNode(map.clickRegion(event));
        // Array of all nodes. Check if already clicked, or if on top
        // Store first waypoint node, can then step through waypoints
        // node = allnodes[clickregion]
        if (currentlyActive.getTopWaypoint() != null
                && clickedNode.equals(currentlyActive.getTopWaypoint().getWaypointNode())) {
            Log.d(TAG, "Removing!");
            currentlyActive.removeWaypoint(clickedNode);
        }else{
            Log.d(TAG, "Adding!");
            currentlyActive.addWaypoint(clickedNode);
        }
//        printDeets("Start tap");
//        MapNode clickedNode = mapNodes[clickRegion.x][clickRegion.y];
        return false;
    }


    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//        Log.d(TAG, "Action was onFling");
        return false;
    }

    /* OnDoubleTapListener */
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
//        Log.d(TAG, "Action was onSingleTapConfirmed");
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

}
