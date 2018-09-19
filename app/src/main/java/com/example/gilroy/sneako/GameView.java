package com.example.gilroy.sneako;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
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
    MapSprite map;
    LevelConstructsSprite level;
    PlayerSprite currentlyActive;
    List<PlayerSprite> allies = new ArrayList<>();
    UIComponents uiComponents;

    public GameView(Context context, GameSetup gameSetup) {
        super(context);
        map = new MapSprite(gameSetup.wallmatrix, getResources());
        level = new LevelConstructsSprite(gameSetup.wallmatrix, map.getTileHeight());
        uiComponents = new UIComponents(map.getTileHeight());
        getHolder().addCallback(this);
        thread = new MainThread(getHolder(), this);
        setFocusable(true);
        // Everything is added to sprites in the order in which it should be drawn
        sprites.put(map.getID(), map);
        sprites.put(level.getID(), level);
        initialiseAllies(gameSetup.numAllies);
        sprites.put(uiComponents.getID(), uiComponents);
    }

    private void initialiseAllies(int numAllies) {
        PlayerSprite ally;
        for (int i = 0; i < numAllies; ++i) {
            ally = new PlayerSprite(level.getNode(new Position(i, 0)), map.getTileHeight(), getResources());
            sprites.put(ally.getID(), ally);
            allies.add(ally);
        }
        declareActivePlayer(allies.get(0));
    }

    public void declareActivePlayer(int index) {
        declareActivePlayer(allies.get(index));
    }

    private void declareActivePlayer(PlayerSprite active) {
        if (currentlyActive != null) {
            currentlyActive.setInActive();
        }
        currentlyActive = active;
        currentlyActive.setActive();
//        uiComponents.disableDialog();
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
                if (cs.equals(currentlyActive)) {
                    continue;
                }
                cs.draw(canvas);
            }
            currentlyActive.draw(canvas);
        }
    }

    public void startAction() {
        resetAction();
        for (PlayerSprite ps : allies) {
            ps.beginMove();
        }
    }

    public void resetAction() {
//        level.disableDialogSelectWaypoint();    // TODO: Is this needed ??    A: no
        for (PlayerSprite ps : allies) {
            ps.resetPlayer();
        }
    }

    /* OnGestureListener */
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (mapScroll) {
            map.shiftCanvas(distanceX, distanceY);
            return false;
        } else if (uiComponents.isDialogDisplayed()) {
            uiComponents.adjust(distanceY);
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

    public void clearWaypoints() {
        currentlyActive.clearWaypoints();
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.d(TAG, "Action was onLongPress");
        if (map.clickRegion(e).x == -1) {
            return;
        }
        resetAction();
        MapNode clickedNode = level.getNode(map.clickRegion(e));
        if (clickedNode.hasNoWaypointsExcludingTop(currentlyActive)) {
            uiComponents.enableWaypointPlaceUI(new Position(map.clickRegion(e), map.getTileHeight()),
                    clickedNode,
                    currentlyActive.getTopWaypoint(),
                    currentlyActive.getWaypoints().size());
        }else {
            uiComponents.enableWaypointRemoveUI(new Position(map.clickRegion(e), map.getTileHeight()), clickedNode.getWaypoints(currentlyActive.getID()));
        }
    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        // If UI component is displayed
        if (uiComponents.isDialogDisplayed()) {
            handleUIClick(new Position(map.clickLocation(event.getX(), event.getY())));
            return false;
        }
        if (map.clickRegion(event).x == -1) {
            Log.d(TAG, "Out of bounds!~");
            return false;
        }
        MapNode clickedNode = level.getNode(map.clickRegion(event));
        resetAction();
        if (currentlyActive.getTopWaypoint() != null
                && clickedNode.equals(currentlyActive.getTopWaypoint().getWaypointNode())) {
            Log.d(TAG, "Removing!");
            currentlyActive.removeWaypoint(clickedNode);
        } else {
            Log.d(TAG, "Adding!");
            currentlyActive.addWaypoint(clickedNode);
        }
        return false;
    }

    private void handleUIClick(Position position) {
        int choice = uiComponents.queryClick(position);
        switch (choice) {
            case UIComponents.SELECTION_COMPLETE:
                currentlyActive.addWaypoint(uiComponents.getDialogClickedNode(), uiComponents.getValue());
                uiComponents.disableDialog();
                break;
            case UIComponents.CANCEL_SELECTION:
                uiComponents.disableDialog();
                break;
            case UIComponents.ADD_WAYPOINT:
                uiComponents.enableWaypointPlaceUI(uiComponents.getDialogPosition(),
                        uiComponents.getDialogClickedNode(),
                        currentlyActive.getTopWaypoint(),
                        currentlyActive.getWaypoints().size());
                break;
            case UIComponents.REMOVE_WAYPOINT:
                currentlyActive.removeWaypoint(uiComponents.getDialogClickedNode().getWaypoint(currentlyActive.getID(), uiComponents.getValue()));
                uiComponents.disableDialog();
                break;
            default:
                break;
        }
        // TODO: UI should get disabled for click anywhere, including xml buttons
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
