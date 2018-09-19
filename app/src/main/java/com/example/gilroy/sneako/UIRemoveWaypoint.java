package com.example.gilroy.sneako;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;

import java.util.List;

import static com.example.gilroy.sneako.UIComponents.ADD_WAYPOINT;
import static com.example.gilroy.sneako.UIComponents.NO_SELECTION_MADE;
import static com.example.gilroy.sneako.UIComponents.REMOVE_WAYPOINT;


public class UIRemoveWaypoint implements UI {

    private int choice = 0;
    private int[] waypointsNumbers;
    private Rect[] wpRects;
    private Position decidingIndexDialogPosition = null;

//    public void enableRemoveWaypointDialog(Position position, List<Waypoint> nodeWaypoints) {
    public UIRemoveWaypoint(Position position, List<Waypoint> nodeWaypoints) {
        // For each waypoint, create numbered click circle
        // Get numbers of waypoints
//        isDialogUpdated = false;
//        isWaypointChoiceDisplayed = true;
        decidingIndexDialogPosition = position;
//        dialogClickedNode = nodeWaypoints.get(0).getWaypointNode();
        waypointsNumbers = new int[nodeWaypoints.size()];
        for (int i = 0; i < nodeWaypoints.size(); ++i) {
            waypointsNumbers[i] = nodeWaypoints.get(i).getWaypointIndex();
        }

    }
    @Override
    public void adjust(float adjustment){
        return;
    }

    @Override
    public int queryClick(Position click){
        for(int i=0; i<waypointsNumbers.length; ++i){
            if(wpRects[i].contains(click.x, click.y)){
                choice = i;
                return REMOVE_WAYPOINT;
            }
        }
        if(wpRects[waypointsNumbers.length].contains(click.x, click.y)){
            return ADD_WAYPOINT;
        }
        return NO_SELECTION_MADE;
    }
    @Override
    public int getValue(){
        return choice;
    }

    @Override
    public void update() {
        return;     // TODO:  this
    }
    @Override
    public void draw(Canvas canvas) {

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


}
