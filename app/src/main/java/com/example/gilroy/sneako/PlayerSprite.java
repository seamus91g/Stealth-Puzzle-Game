package com.example.gilroy.sneako;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerSprite extends PlayerNav implements ISprite {

    private static final String TAG = "PlayerSprite";
    private final Bitmap waypointActive;
    private final Bitmap waypointInActive;
    private final Bitmap playerImage;
    private int playerSpriteOffset;
    private int tileHeight;
    private Position playerPosition;
    private Status playerStatus = Status.Wait;
    private MapNode next;
    private int routeIndex = 0;
    private int pendingDistance;
    private List<WayPath> waypath = new ArrayList<>();
    private Direction playerDirection = Direction.Down;
    private boolean isActivePlayer = false;
    private Paint wpTextStyle = new Paint();

    private enum Direction {
        Up,
        Right,
        Down,
        Left
    }

    private enum Status {
        Wait,
        Move
    }

    public PlayerSprite(MapNode insertionPoint, int tileHeight, Resources resources) {
        super(insertionPoint);
        Log.d(TAG, "Height: " + tileHeight);
        this.tileHeight = tileHeight;
        playerPosition = new Position(insertionPoint.getPosition(), tileHeight);
        Bitmap bmp = BitmapFactory.decodeResource(resources, R.drawable.selection_reticule_green);
        bmp = LevelConstructsSprite.getResizedBitmap(bmp, (4 * tileHeight) / 5, (4 * tileHeight) / 5);    // 80% size of tile
        waypointActive = bmp;
        bmp = BitmapFactory.decodeResource(resources, R.drawable.selection_reticule_grey);
        bmp = LevelConstructsSprite.getResizedBitmap(bmp, (4 * tileHeight) / 5, (4 * tileHeight) / 5);    // 80% size of tile
        waypointInActive = bmp;
        bmp = BitmapFactory.decodeResource(resources, R.drawable.ic_player_image);
        playerImage = LevelConstructsSprite.getResizedBitmap(bmp, (4 * tileHeight) / 5, (4 * tileHeight) / 5);    // 80% size of tile
        playerSpriteOffset = (tileHeight - playerImage.getWidth()) / 2;
        updateWaypointTextStyle();
    }

    public void setActive() {
        isActivePlayer = true;
        updatePath();
    }

    public void setInActive() {
        isActivePlayer = false;
        updatePath();
    }

    public void toggleActive() {
        isActivePlayer = !isActivePlayer;
        updatePath();
    }

    public void resetPlayer() {
        playerStatus = Status.Wait;
        routeIndex = 0;
        pendingDistance = 0;
        playerPosition = new Position(route.get(0).getPosition(), tileHeight);
        playerDirection = Direction.Down;
    }

    @Override
    public UUID getID() {
        return super.getID();
    }

    @Override
    public void draw(Canvas canvas) {
        // Draw player
        int playerHorPos = playerSpriteOffset + playerPosition.x;
        int playerVertPos = playerSpriteOffset + playerPosition.y;
        canvas.drawBitmap(playerImage, playerHorPos, playerVertPos, null);
        // Draw waypoints
        for (Waypoint cs : getWaypoints().values()) {
            drawWP(canvas, cs);
        }
        // Draw path
        for (WayPath waypath : waypath) {
            canvas.drawPath(waypath.path, waypath.paint);
        }
    }

    @Override
    public void update() {
        if (!isPathDrawUpdated()) {
            updatePath();
            setPathDrawUpdated();
        }
        if (playerStatus == Status.Wait) {
            return;
        }
        if (playerStatus == Status.Move) {
            updatePosition();
        }
    }

    private void updatePosition() {
        if (pendingDistance > 0) {
            int stepSize = 6;
            takeStep(stepSize);
            pendingDistance -= stepSize;
        } else {
            beginNextTile();
        }
    }

    private void takeStep(int stepSize) {
        switch (playerDirection) {
            case Right:
                playerPosition.x += stepSize;
                break;
            case Left:
                playerPosition.x -= stepSize;
                break;
            case Down:
                playerPosition.y += stepSize;
                break;
            case Up:
                playerPosition.y -= stepSize;
                break;
        }
    }

    private void beginNextTile() {
        playerPosition = new Position(next.getPosition(), tileHeight);  // Snap to position
        ++routeIndex;
        if (routeIndex == route.size()) {
            playerStatus = Status.Wait;
            return;
        }
        next = route.get(routeIndex);
        playerDirection = getDirection(next);
        takeStep(-pendingDistance);             // Account for previous Snap to position
        pendingDistance = tileHeight + pendingDistance;
    }

    private Direction getDirection(MapNode mn) {
        if (mn.xPosition() * tileHeight > playerPosition.x) {
            return Direction.Right;
        } else if (mn.xPosition() * tileHeight < playerPosition.x) {
            return Direction.Left;
        } else if (mn.yPosition() * tileHeight > playerPosition.y) {
            return Direction.Down;
        } else if (mn.yPosition() * tileHeight < playerPosition.y) {
            return Direction.Up;
        }
        return Direction.Down;      // Default to Down ...
    }

    public void beginMove() {
        if (getWaypoints().size() == 0) {
            return;
        }
        playerStatus = Status.Move;
        routeIndex = 0;
        next = route.get(routeIndex);
        beginNextTile();
    }

    private void drawWP(Canvas canvas, Waypoint cs) {
        int edgeDistance = (tileHeight - waypointActive.getHeight()) / 2;
        int horzDisp = cs.getPosition().x * tileHeight + edgeDistance;
        int vertDisp = cs.getPosition().y * tileHeight + edgeDistance;
        if (!isActivePlayer) {
            canvas.drawBitmap(waypointInActive, horzDisp, vertDisp, null);
            return;
        }
        canvas.drawBitmap(waypointActive, horzDisp, vertDisp, null);
        canvas.drawText(cs.getWaypointText(), horzDisp + 10 + cs.findLabelDrawShift(), vertDisp + 25, wpTextStyle);
    }

    private void updateWaypointTextStyle() {
        wpTextStyle.setStyle(Paint.Style.FILL);
        wpTextStyle.setStrokeWidth(1);
        wpTextStyle.setColor(Color.BLACK);
        wpTextStyle.setTextSize(20);
    }

    private void updatePath() {
        int color;
        if (isActivePlayer) {
            color = Color.RED;
        } else {
            color = Color.GRAY;
        }
        updatePath(color);
    }

    private void updatePath(int color) {
        waypath = new ArrayList<>();
        int offset = tileHeight / 2;
        Paint paint;
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setColor(color);
        Path path;
        for (int k = 0; k < route.size() - 1; ++k) {
            path = new Path();
            path.moveTo(route.get(k).xPosition() * tileHeight + offset, route.get(k).yPosition() * tileHeight + offset);
            path.lineTo(route.get(k + 1).xPosition() * tileHeight + offset, route.get(k + 1).yPosition() * tileHeight + offset);
            waypath.add(new WayPath(path, paint));
        }
    }
}
