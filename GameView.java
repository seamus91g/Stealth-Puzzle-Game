package com.example.gilroy.sneako;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;

class GameView extends SurfaceView implements
        SurfaceHolder.Callback,
        GestureDetector.OnDoubleTapListener,
        GestureDetector.OnGestureListener {
    public static final String TAG = "LogTag";
    private static final String TAG_AC = "Debug2";
    private MainThread thread;
    Map<UUID, ISprite> sprites = new LinkedHashMap<>();     // Drawing order is important
    LinkedList<MapNode> route = new LinkedList<>();
    //    BitmapDrawable waypBmp;
    Bitmap waypBmp;
    Waypoint topWaypoint = null;
    private final float pixelDensity;
    int viewBuffer = 150;       // Margin space to allow for view window outside of map area
    Position canvasPosition = new Position(0, 0);
    boolean mapScroll = false;
    static int tileHeight = 0;
    int[][][] wallmatrix;
    MapNode[][] mapNodes = new MapNode[GameScreen.WIDTH_COUNT][GameScreen.HEIGHT_COUNT];

    {
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

        this.pixelDensity = GameScreen.SCREEN_DENSITY;
        Log.d(TAG, "Pixel density: " + pixelDensity);
        getHolder().addCallback(this);
        thread = new MainThread(getHolder(), this);
        setFocusable(true);
        resetMapNodes();
        route.add(mapNodes[0][0]);
    }

    private void resetMapNodes() {
        for (int i = 0; i < mapNodes.length; ++i) {
            for (int j = 0; j < mapNodes[i].length; ++j) {
                mapNodes[i][j] = new MapNode(new Position(i, j));
            }
        }
        for (int i = 0; i < mapNodes.length; ++i) {
            for (int j = 0; j < mapNodes[i].length; ++j) {
                // Find where the walls are
                boolean wallBottom = (wallmatrix[0][j][i] == 1);
                boolean wallRight = (wallmatrix[1][j][i] == 1);
                boolean wallTop = false;
                boolean wallLeft = false;
                if (j != 0) {
                    wallTop = (wallmatrix[0][j - 1][i] == 1);
                }
                if (i != 0) {
                    wallLeft = (wallmatrix[1][j][i - 1] == 1);
                }
                // Add neighbours, skip walls
                if (i != 0 && !wallLeft) {
                    mapNodes[i][j].addNeighbour(mapNodes[i - 1][j]);
                }
                if (i != mapNodes.length - 1 && !wallRight) {
                    mapNodes[i][j].addNeighbour(mapNodes[i + 1][j]);
                }
                if (j != 0 && !wallTop) {
                    mapNodes[i][j].addNeighbour(mapNodes[i][j - 1]);
                }
                if (j != mapNodes[i].length - 1 && !wallBottom) {
                    mapNodes[i][j].addNeighbour(mapNodes[i][j + 1]);
                }
            }
        }
    }

    public void update() {
        for (ISprite sprite : sprites.values()) {
            sprite.update();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // Initialise the map
        ISprite map = createMap();
        sprites.put(map.getID(), map);
        // Start the main thread
        thread.setRunning(true);
        thread.start();
    }

    private ISprite createMap() {
        // Get a scaled bitmap and a drawable to display to canvas
        Bitmap tile = getScaledBitmap(R.drawable.ice_2);
        BitmapDrawable bmpd = new BitmapDrawable(getResources(), tile);
        // Shift the map to the center of the screen
        int xShift = (int) GameScreen.SCREEN_WIDTH / 2 - GameScreen.WIDTH_COUNT * tile.getWidth() / 2;
        int yShift = (int) GameScreen.SCREEN_HEIGHT / 2 - GameScreen.HEIGHT_COUNT * tile.getHeight() / 2;
        canvasPosition = new Position(xShift, yShift);
        // Create the map sprite
        tileHeight = tile.getHeight();
        return new MapSprite(bmpd, tileHeight);
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
//            Log.d(TAG, "[ Drawing " + sprites.size() + "sprites! ]");
            for (ISprite cs : sprites.values()) {
                cs.draw(canvas);
            }
            int offset = tileHeight / 2;
            for (int k = 0; k < route.size() - 1; ++k) {
                Paint paint = new Paint();
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(2);
                paint.setColor(Color.RED);
                Path path = new Path();
                path.moveTo(route.get(k).xPosition() * tileHeight + offset, route.get(k).yPosition() * tileHeight + offset);
                path.lineTo(route.get(k + 1).xPosition() * tileHeight + offset, route.get(k + 1).yPosition() * tileHeight + offset);
                canvas.drawPath(path, paint);
            }
            for (int q = 0; q < 2; ++q) {
                for (int y = 0; y < wallmatrix[0].length; ++y) {
//                for (int x = 0; x < 4; ++x) {
                    for (int x = 0; x < wallmatrix[0][y].length; ++x) {
//                    for (int y = 0; y < 2; ++y) {
                        if (wallmatrix[q][y][x] == 1) {
                            Paint wallStyle = new Paint();
                            wallStyle.setStyle(Paint.Style.STROKE);
                            wallStyle.setStrokeWidth(15);
                            wallStyle.setColor(Color.BLACK);
                            Path wall = new Path();
                            if (q == 0) {
                                wall.moveTo(x * tileHeight, y * tileHeight + tileHeight);
                                wall.lineTo(x * tileHeight + tileHeight, y * tileHeight + tileHeight);
                            } else {
                                wall.moveTo(x * tileHeight + tileHeight, y * tileHeight);
                                wall.lineTo(x * tileHeight + tileHeight, y * tileHeight + tileHeight);
                            }
                            canvas.drawPath(wall, wallStyle);
                        }
                    }
                }
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
    public void onLongPress(MotionEvent e) {
        Log.d(TAG, "Action was onLongPress");
        Position clickRegion = clickRegion(e);
        if (clickRegion.x == -1) {
            return;
        }
        MapNode clickedNode = mapNodes[clickRegion.x][clickRegion.y];
        // if no waypoints, return
        // if top waypoint, no stack, return
        // if top waypoint, stack, remove next highest from stack
        if (clickedNode.getWaypointCount() == 0
                || (clickedNode.getWaypointCount() == 1 && clickedNode.getWaypoint(0).equals(topWaypoint)))
            return;
        Waypoint deleteWP;
        if (clickedNode.getWaypoint(clickedNode.getWaypointCount() - 1).equals(topWaypoint)) {
            deleteWP = clickedNode.getWaypoint(clickedNode.getWaypointCount() - 2);
        } else {
            deleteWP = clickedNode.getWaypoint(clickedNode.getWaypointCount() - 1);
        }

        deleteWP.delete();
        sprites.remove(deleteWP.getID());
        Iterator<MapNode> it = route.iterator();
        MapNode mn = it.next();
        MapNode prev;
        if (deleteWP.getPrevWP() != null) {
            prev = deleteWP.getPrevWP().getWaypointNode();
        } else {
            prev = mn;
        }
        List<MapNode> newPathSlice = travel(prev, deleteWP.getNextWP().getWaypointNode());
        // Get prev Wp and next Wp. Find path between them. Insert this path into middle of route LinkedList
        int index = 0;
        while (!mn.equals(prev)) {
            mn = it.next();
            ++index;
        }
        it.next();
        while (!mn.equals(deleteWP.getNextWP().getWaypointNode())) {
            it.remove();
            mn = it.next();
        }
        it.remove();
        route.addAll(index + 1, newPathSlice);
    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        Position clickRegion = clickRegion(event);
        if (clickRegion.x == -1) {
            return false;
        }
        // Array of all nodes. Check if already clicked, or if on top
        // Store first waypoint node, can then step through waypoints
        // node = allnodes[clickregion]
//        printDeets("Start tap");
        MapNode clickedNode = mapNodes[clickRegion.x][clickRegion.y];
        // if innacessible square
        //      do nothing
        // if wayPoints == empty
        if (topWaypoint == null) {
//            //      add node
//
        } else if (clickedNode.equals(topWaypoint.getWaypointNode())) {
            // Remove all route nodes between clicked and previous
            sprites.remove(topWaypoint.getID());
            MapNode previous;
            topWaypoint.delete();
            if (topWaypoint.getPrevWP() == null) {
                previous = route.get(0);
                topWaypoint = null;
            } else {
                previous = topWaypoint.getPrevWP().getWaypointNode();
                topWaypoint = topWaypoint.getPrevWP();
            }
            while (!route.peekLast().equals(previous)) {
                route.removeLast();                 //
            }
            clickedNode.removeTopWaypoint();    //
            return false;
        }
//          else{
//
//        }
        List<MapNode> pathToTake = travel(route.getLast(), clickedNode);
        if (pathToTake == null) {
            Log.d(TAG, "No path found!");
            return false;
        }
        route.addAll(pathToTake);
        // if node = on top
        //      Add pause
        // wayPointStack.add(node)
        // if node in stack already
        //      Display integer count in
        if (waypBmp == null) {
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.selection_reticule_green);
            bmp = getResizedBitmap(bmp, (4 * tileHeight) / 5, (4 * tileHeight) / 5);    // 80% size of tile
//            waypBmp = new BitmapDrawable(getResources(), bmp);
            waypBmp = bmp;
        }

        Waypoint waypoint = new Waypoint(
                waypBmp,
                clickRegion.x * tileHeight,
                clickRegion.y * tileHeight,
                clickedNode,
                topWaypoint);
//        if (topWaypoint != null) {
//            topWaypoint.setNextWP(waypoint);
//        }
//        clickedNode.addWaypoint(waypoint);
        topWaypoint = waypoint;
        sprites.put(waypoint.getID(), waypoint);

//        printDeets("End tap");
        Log.d(TAG, "[~~~~~~~~~~~~~~~~~~~~~~~~~~~]");
        return false;
    }

    private List<MapNode> travel(MapNode waypointNode, MapNode clicked) {
        if (waypointNode.equals(clicked)) return null;
        Map<MapNode, MapNode> parents = new HashMap<>();
        ArrayList<MapNode> route = new ArrayList<>();

        Queue<MapNode> bfs = new LinkedList<>();
        bfs.add(clicked);
        while (!bfs.isEmpty()) {
            MapNode thisNode = bfs.remove();
            for (int i = 0; i < thisNode.getNeighbourSize(); ++i) {
                if (parents.containsKey(thisNode.getNeighbour(i))) {
                    continue;
                }
                parents.put(thisNode.getNeighbour(i), thisNode);
                if (thisNode.getNeighbour(i).equals(waypointNode)) {
//                    route.add(thisNode.getNeighbour(i));  // Don't add starting node  (waypointNode)
                    route.add(thisNode);
                    if (thisNode.equals(clicked)) {
                        return route;
                    }
                    MapNode previousNode = parents.get(thisNode);
                    route.add(previousNode);
                    while (!previousNode.equals(clicked)) {
                        previousNode = parents.get(previousNode);
                        route.add(previousNode);
                    }
                    return route;
                }
                bfs.add(thisNode.getNeighbour(i));
            }
        }
        return null;
    }

    private Position clickRegion(float x, float y) {
        int regionX = (int) ((x - canvasPosition.x) / tileHeight);
        int regionY = (int) ((y - canvasPosition.y) / tileHeight);
        if ((regionX >= GameScreen.WIDTH_COUNT) || (regionX < 0)
                || (regionY >= GameScreen.HEIGHT_COUNT) || (regionY < 0)) {
            Log.d(TAG, "Out of bounds!!!");
            return new Position(-1, -1);
        }
        return new Position(regionX, regionY);
    }

    private Position clickRegion(MotionEvent event) {
        return clickRegion(event.getX(), event.getY());
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

    private void printDeets(String note) {
        Log.d(TAG, "\n======== " + note + " =========");
        printRoute(route);
        printWaypoints(topWaypoint);
    }

    private void printRoute(List<MapNode> route) {
        StringBuilder shortestPath = new StringBuilder();
        for (MapNode mn : route) {
            shortestPath.append("(");
            shortestPath.append(mn.xPosition());
            shortestPath.append(",");
            shortestPath.append(mn.yPosition());
            shortestPath.append(")->");
        }
        Log.d(TAG, shortestPath.toString());
    }

    private void printWaypoints(Waypoint topWaypoint) {
        StringBuilder msg = new StringBuilder("Waypoints :");
        if (topWaypoint != null) {
            msg.append("(")
                    .append(topWaypoint.getWaypointNode().xPosition())
                    .append(",")
                    .append(topWaypoint.getWaypointNode().yPosition())
                    .append("), ");
            Waypoint prev = topWaypoint.getPrevWP();
            while (prev != null) {
                msg.append("(")
                        .append(prev.getWaypointNode().xPosition())
                        .append(",")
                        .append(prev.getWaypointNode().yPosition())
                        .append("), ");
                prev = prev.getPrevWP();
            }
            Log.d(TAG, msg.toString());
        } else {
            Log.d(TAG, "No waypoints!");
        }
    }

}
