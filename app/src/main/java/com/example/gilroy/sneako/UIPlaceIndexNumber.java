package com.example.gilroy.sneako;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;

import static com.example.gilroy.sneako.UIComponents.CANCEL_SELECTION;
import static com.example.gilroy.sneako.UIComponents.SELECTION_COMPLETE;

public class UIPlaceIndexNumber implements UI {

    private Position decidingIndexDialogPosition = null;
    private String dialogIndexText;
    private int dialogIndexNumber;
    private int dialogIndexY = 0;
    private int waypointCount;
    private int jumpDistance = 20;
    private Paint wpTextStyle = new Paint();
    private int dialogDrawShift = 5;
    private Rect cancelDialogRect;
    private WayPath[] indexDialogDrawItems = new WayPath[4];    // 1: arrow, circle, X

    // TODO: If no waypoints, auto set as number 1
//    public void enableDialogDecidingIndexNumber(Position dialogPosition, MapNode clickedNode, Waypoint waypoint, int waypointCount) {
    public UIPlaceIndexNumber(Position dialogPosition, Waypoint waypoint, int waypointCount) {
//        isDialogUpdated = false;
//        dialogClickedNode = clickedNode;
        this.waypointCount = waypointCount;
        int startingIndex = 1;
        if (waypoint != null) {
            startingIndex = waypoint.getWaypointIndex();
        }
        decidingIndexDialogPosition = dialogPosition;
        dialogIndexText = String.valueOf(startingIndex);
        dialogIndexNumber = startingIndex;
//        isDialogDisplayed = true;

        updateWaypointTextStyle();
    }

    @Override
    public void update() {
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
    @Override
    public void draw(Canvas canvas){
        drawDialogDecidingIndexNumber(canvas);
        drawCancelDialogOption(canvas);
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

    @Override
    public int getValue() {
        return dialogIndexNumber;
    }



    public int queryClick(Position click) {
        return cancelDialogRect.contains(click.x, click.y) ? CANCEL_SELECTION : SELECTION_COMPLETE;
    }

    // TODO: Optimise this next time you're not sleep deprived
    @Override
    public void adjust(float distanceY) {
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



}
