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
    private final Bitmap playerImage;
    private int vertDisp;
    private int horzDisp;
    private int playerSpriteOffset;
    private int tileHeight;
    private Position playerPosition = new Position(0, 0);
    private Status playerStatus = Status.Wait;
    MapNode next;
    private int routeIndex = 0;
    private int pendingDistance;
    private Direction playerDirection = Direction.Down;

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
        super(insertionPoint, tileHeight);
        this.tileHeight = tileHeight;
        ID = UUID.randomUUID();
        playerPosition = new Position(insertionPoint.getPosition(), tileHeight);
        Bitmap bmp = BitmapFactory.decodeResource(resources, R.drawable.selection_reticule_green);
        bmp = LevelConstructsSprite.getResizedBitmap(bmp, (4 * tileHeight) / 5, (4 * tileHeight) / 5);    // 80% size of tile
        waypoint = bmp;
        bmp = BitmapFactory.decodeResource(resources, R.drawable.ic_player_image);
        playerImage = LevelConstructsSprite.getResizedBitmap(bmp, (4 * tileHeight) / 5, (4 * tileHeight) / 5);    // 80% size of tile
        playerSpriteOffset = (tileHeight - playerImage.getWidth()) / 2;
    }

    public void resetPlayer(){
        playerStatus = Status.Wait;
        routeIndex = 0;
        pendingDistance = 0;
        playerPosition = new Position(route.get(0).getPosition(), tileHeight);
        playerDirection = Direction.Down;
    }

    @Override
    public UUID getID() {
        return ID;
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
        for (WayPath waypath : getPath()) {
            canvas.drawPath(waypath.path, waypath.paint);
        }
    }

    @Override
    public void update() {
        if (playerStatus == Status.Wait) {
            return;
        } else if (playerStatus == Status.Move) {
            if (pendingDistance > 0) {
                int jumpSize = 4;
                switch (playerDirection) {
                    case Right:
                        playerPosition.x +=jumpSize;
                        break;
                    case Left:
                        playerPosition.x -=jumpSize;
                        break;
                    case Down:
                        playerPosition.y +=jumpSize;
                        break;
                    case Up:
                        playerPosition.y -=jumpSize;
                        break;
                }
                pendingDistance -= jumpSize;
            } else {
                playerPosition.x = next.getPosition().x * tileHeight;   // Snap to position
                playerPosition.y = next.getPosition().y * tileHeight;
                ++routeIndex;
                if (routeIndex == route.size()) {
                    playerStatus = Status.Wait;
                    return;
                }
                next = route.get(routeIndex);
                playerDirection = getDirection(next);
            }
        }
    }

    private Direction getDirection(MapNode mn) {
        if (mn.xPosition() * tileHeight > playerPosition.x) {
            pendingDistance = mn.xPosition() * tileHeight - playerPosition.x;
            return Direction.Right;
        } else if (mn.xPosition() * tileHeight < playerPosition.x) {
            pendingDistance = playerPosition.x - mn.xPosition() * tileHeight;
            return Direction.Left;
        } else if (mn.yPosition() * tileHeight > playerPosition.y) {
            pendingDistance = mn.yPosition() * tileHeight - playerPosition.y;
            return Direction.Down;
        } else if (mn.yPosition() * tileHeight < playerPosition.y) {
            pendingDistance = playerPosition.y - mn.yPosition() * tileHeight;
            return Direction.Up;
        }
        return Direction.Down;      // Default to Down ...
    }

    public void beginMove() {
        if (getWaypoints().size() == 0) {
            return;
        }
        playerStatus = Status.Move;
        routeIndex = 1;
        next = route.get(routeIndex);
        playerDirection = getDirection(next);
    }

    public void drawWP(Canvas canvas, Waypoint cs) {

        int edgeDistance = (tileHeight - waypoint.getHeight()) / 2;
        horzDisp = cs.getPosition().x * tileHeight + edgeDistance;
        vertDisp = cs.getPosition().y * tileHeight + edgeDistance;

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
