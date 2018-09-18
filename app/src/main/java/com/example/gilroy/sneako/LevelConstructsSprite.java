package com.example.gilroy.sneako;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.Log;

import java.util.List;
import java.util.UUID;

public class LevelConstructsSprite implements ISprite {

    public static final int ADD_WAYPOINT = -1;
    public static final int NO_SELECTION_MADE = -2;
    private final UUID ID;
    private LevelConstructs level;
    private Position decidingIndexDialogPosition = null;
    private String dialogIndexText;
    private Paint wpTextStyle = new Paint();
    private int dialogDrawShift = 5;
    private boolean isDialogDisplayed = false;
    private int dialogIndexNumber;
    private int dialogIndexY = 0;
    private MapNode dialogClickedNode;
    private int waypointCount;
    private int jumpDistance = 20;
    private Rect cancelDialogRect;
    private int[] waypointsNumbers;
    private boolean isDialogUpdated = true;
    private Rect[] wpRects;
    private WayPath[] indexDialogDrawItems = new WayPath[4];    // 1: arrow, circle, X
    private boolean isWaypointChoiceDisplayed = false;

    public LevelConstructsSprite(int[][][] wallmatrix, int dimension) {
        level = new LevelConstructs(wallmatrix, dimension);
        ID = UUID.randomUUID();
        updateWaypointTextStyle();
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
        if (isDialogDisplayed && isDialogUpdated) {
            drawDialogDecidingIndexNumber(canvas);
            drawCancelDialogOption(canvas);
        }
        if (isWaypointChoiceDisplayed && isDialogUpdated) {     // TODO: Is isDialogUpdated necessary??
            drawRemoveWaypointDialog(canvas);
        }
    }

    @Override
    public void update() {
        if (!isDialogUpdated) {
            updateIndexSelectDialog();
            isDialogUpdated = true;
        }
    }

    public MapNode getNode(Position position) {
        return level.getNode(position);
    }


    public void updateIndexSelectDialog() {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(2);
        paint.setColor(Color.RED);
        Path path = new Path();
        // TODO: Make the dimensions a factor of the tileHeight
        path.moveTo(decidingIndexDialogPosition.x + dialogDrawShift * 2, decidingIndexDialogPosition.y + dialogDrawShift * 7);
        path.lineTo(decidingIndexDialogPosition.x + dialogDrawShift * 2 + 12, decidingIndexDialogPosition.y + dialogDrawShift * 7 - 20);
        path.lineTo(decidingIndexDialogPosition.x + dialogDrawShift * 2 + 24, decidingIndexDialogPosition.y + dialogDrawShift * 7);
        path.close();

        indexDialogDrawItems[0] = new WayPath(path, paint);

        Paint circlePaint = new Paint();
        Path cancelButtonCircle = new Path();
        int radius = 35;
        int xDimension = 22;
        circlePaint.setColor(Color.BLUE);
        cancelButtonCircle.addCircle(decidingIndexDialogPosition.x - dialogDrawShift * 3,
                decidingIndexDialogPosition.y - dialogDrawShift * 3,
                radius,
                Path.Direction.CW);
        indexDialogDrawItems[1] = new WayPath(cancelButtonCircle, circlePaint);

        Paint xShapePaint = new Paint();
        Path xShapePath = new Path();
        xShapePaint.setColor(Color.RED);
        xShapePaint.setStyle(Paint.Style.STROKE);
        xShapePaint.setStrokeWidth(5);
        xShapePath.moveTo(decidingIndexDialogPosition.x - dialogDrawShift * 3 - xDimension,
                decidingIndexDialogPosition.y - dialogDrawShift * 3 - xDimension);
        xShapePath.lineTo(decidingIndexDialogPosition.x - dialogDrawShift * 3 + xDimension,
                decidingIndexDialogPosition.y - dialogDrawShift * 3 + xDimension);
        xShapePath.moveTo(decidingIndexDialogPosition.x - dialogDrawShift * 3 - xDimension,
                decidingIndexDialogPosition.y - dialogDrawShift * 3 + xDimension);
        xShapePath.lineTo(decidingIndexDialogPosition.x - dialogDrawShift * 3 + xDimension,
                decidingIndexDialogPosition.y - dialogDrawShift * 3 - xDimension);
        indexDialogDrawItems[2] = new WayPath(xShapePath, xShapePaint);

    }

    private void drawDialogDecidingIndexNumber(Canvas canvas) {
        canvas.drawText(dialogIndexText, decidingIndexDialogPosition.x + dialogDrawShift * 8, decidingIndexDialogPosition.y + dialogDrawShift * 12, wpTextStyle);

        canvas.drawPath(indexDialogDrawItems[0].path, indexDialogDrawItems[0].paint);
        canvas.rotate(180, decidingIndexDialogPosition.x + dialogDrawShift * 2 + 12, decidingIndexDialogPosition.y + dialogDrawShift * 7 + 10);
        canvas.drawPath(indexDialogDrawItems[0].path, indexDialogDrawItems[0].paint);
        canvas.rotate(180, decidingIndexDialogPosition.x + dialogDrawShift * 2 + 12, decidingIndexDialogPosition.y + dialogDrawShift * 7 + 10);
    }

    private void drawCancelDialogOption(Canvas canvas) {
        canvas.drawPath(indexDialogDrawItems[1].path, indexDialogDrawItems[1].paint);
        canvas.drawPath(indexDialogDrawItems[2].path, indexDialogDrawItems[2].paint);

        int radius = 35;
        int clickZoneBuffer = 20;
        cancelDialogRect = new Rect(decidingIndexDialogPosition.x - dialogDrawShift * 3 - radius - clickZoneBuffer,
                decidingIndexDialogPosition.y - dialogDrawShift * 3 - radius - clickZoneBuffer,
                decidingIndexDialogPosition.x - dialogDrawShift * 3 + radius + clickZoneBuffer,
                decidingIndexDialogPosition.y - dialogDrawShift * 3 + radius + clickZoneBuffer);
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(cancelDialogRect, paint);

    }

    private void updateWaypointTextStyle() {
        wpTextStyle.setStyle(Paint.Style.FILL);
        wpTextStyle.setStrokeWidth(6);
        wpTextStyle.setColor(Color.BLACK);
        wpTextStyle.setTextSize(50);
    }

    public void enableRemoveWaypointDialog(Position position, List<Waypoint> nodeWaypoints) {
        // For each waypoint, create numbered click circle
        // Get numbers of waypoints
        isDialogUpdated = false;
        isWaypointChoiceDisplayed = true;
        decidingIndexDialogPosition = position;
        dialogClickedNode = nodeWaypoints.get(0).getWaypointNode();
        waypointsNumbers = new int[nodeWaypoints.size()];
        for (int i = 0; i < nodeWaypoints.size(); ++i) {
            waypointsNumbers[i] = nodeWaypoints.get(i).getWaypointIndex();
        }

    }
    public boolean isWPSelectDisplayed(){
        return isWaypointChoiceDisplayed;
    }
    public int queryClick(Position click){
        for(int i=0; i<waypointsNumbers.length; ++i){
            if(wpRects[i].contains(click.x, click.y)){
                return i;
            }
        }
        if(wpRects[waypointsNumbers.length].contains(click.x, click.y)){
            return ADD_WAYPOINT;
        }
        return NO_SELECTION_MADE;
    }
    public void drawRemoveWaypointDialog(Canvas canvas) {

        int tileSize = 93;
        int radius = 35;
        int buffer = 20;
        int buttonSize = radius * 2 + buffer;

        int xStart = decidingIndexDialogPosition.x + tileSize / 2 - (buttonSize * (waypointsNumbers.length + 1)) / 2;
        int yPos = decidingIndexDialogPosition.y - (buffer + buttonSize / 2);

        Paint circlePaint = new Paint();
        Path circlePath = new Path();
        circlePaint.setColor(Color.BLUE);

        Paint circleTextPaint = new Paint();
        circleTextPaint.setStyle(Paint.Style.STROKE);
        circleTextPaint.setTextSize(40);
        circleTextPaint.setStrokeWidth(6);
        circleTextPaint.setColor(Color.BLACK);

        Paint rectPaint = new Paint();
        rectPaint.setColor(Color.GREEN);
        rectPaint.setStrokeWidth(1);
        rectPaint.setStyle(Paint.Style.STROKE);
        wpRects = new Rect[waypointsNumbers.length +1];

        for (int i = 0; i < waypointsNumbers.length + 1; ++i) {
            circlePath.addCircle(xStart + buttonSize / 2 + i * buttonSize,
                    yPos,
                    radius,
                    Path.Direction.CW);
            Rect WPRect = new Rect(xStart + i * buttonSize,
                    yPos - buttonSize / 2,
                    xStart + (i + 1) * buttonSize,
                    yPos + buttonSize / 2);
            canvas.drawRect(WPRect, rectPaint);
            wpRects[i] = WPRect;
        }
        canvas.drawPath(circlePath, circlePaint);

        for (int i = 0; i < waypointsNumbers.length + 1; ++i) {
            int circleCenterPoint = xStart + buttonSize / 2 + i * buttonSize;
            String msg;
            if(i < waypointsNumbers.length){
                int wpNum = waypointsNumbers[i];
                msg = String.valueOf(wpNum);
            }else{
                msg = "+";
            }
            canvas.drawText(msg, circleCenterPoint - 15, yPos + 15, circleTextPaint);
        }

    }

    public Position getDialogPosition() {
        return decidingIndexDialogPosition;
    }

    // TODO:  Abstract this to its own class
    // TODO: If no waypoints, auto set as number 1
    public void enableDialogDecidingIndexNumber(Position dialogPosition, MapNode clickedNode, Waypoint waypoint, int waypointCount) {
        isDialogUpdated = false;
        dialogClickedNode = clickedNode;
        this.waypointCount = waypointCount;
        int startingIndex = 1;
        if (waypoint != null) {
            startingIndex = waypoint.getWaypointIndex();
        }
        decidingIndexDialogPosition = dialogPosition;
        dialogIndexText = String.valueOf(startingIndex);
        dialogIndexNumber = startingIndex;
        isDialogDisplayed = true;
    }

    public int getDialogIndexNumber() {
        return dialogIndexNumber;
    }

    public boolean wasCancelClicked(Position click) {
        return cancelDialogRect.contains(click.x, click.y);
    }

    public MapNode getDialogClickedNode() {
        return dialogClickedNode;
    }

    public void disableDialogSelectWaypoint() {
        isWaypointChoiceDisplayed = false;
    }
    public void disableDialogDecidingIndexNumber() {
        isDialogDisplayed = false;
        dialogIndexY = 0;
        dialogIndexNumber = 0;  // TODO: Why ..??
        dialogIndexText = "";
    }

    // TODO: Optimise this next time you're not sleep deprived
    public void shiftIndex(float distanceY) {
        dialogIndexY -= distanceY;
        if (dialogIndexY > jumpDistance) {
            if (dialogIndexNumber > 1) {
                --dialogIndexNumber;
            }
            dialogIndexY = 0;
            dialogIndexText = String.valueOf(dialogIndexNumber);
        } else if (dialogIndexY < -jumpDistance) {
            if (dialogIndexNumber <= waypointCount) {
                ++dialogIndexNumber;
            }
            dialogIndexY = 0;
            dialogIndexText = String.valueOf(dialogIndexNumber);
        }
    }

    public boolean isDialogDisplayed() {
        return isDialogDisplayed;
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
