package com.example.gilroy.sneako;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;

import java.util.List;
import java.util.UUID;

public class UIComponents implements ISprite, UI {

    public static final int ADD_WAYPOINT = -1;
    public static final int NO_SELECTION_MADE = -2;
    public static final int REMOVE_WAYPOINT = -3;
    public static final int CANCEL_SELECTION = -4;
    public static final int SELECTION_COMPLETE = -5;
    private final UUID ID;
    private UI currentUIDisplayed = null;
    private Position decidingIndexDialogPosition = null;
    private boolean isDialogDisplayed = false;
    private MapNode dialogClickedNode;
    private boolean isDialogUpdated = true;

    public UIComponents(int dimension){

        ID = UUID.randomUUID();
    }


    @Override
    public UUID getID() {
        return ID;
    }

    @Override
    public void draw(Canvas canvas) {
        if(currentUIDisplayed != null){
            currentUIDisplayed.draw(canvas);
        }
    }

    @Override
    public void update() {
        if(currentUIDisplayed != null) {
            currentUIDisplayed.update();
        }

    }
    public void enableWaypointRemoveUI(Position dialogPosition, List<Waypoint> nodeWaypoints){
        dialogClickedNode = nodeWaypoints.get(0).getWaypointNode();
        decidingIndexDialogPosition = dialogPosition;
        currentUIDisplayed = new UIRemoveWaypoint(dialogPosition, nodeWaypoints);
    }
    public void enableWaypointPlaceUI(Position dialogPosition, MapNode clickedNode, Waypoint waypoint, int waypointCount){
        dialogClickedNode = clickedNode;
        decidingIndexDialogPosition = dialogPosition;
        currentUIDisplayed = new UIPlaceIndexNumber(dialogPosition, waypoint, waypointCount);
    }

    @Override
    public int queryClick(Position position){
        if(currentUIDisplayed != null){
            return currentUIDisplayed.queryClick(position);
        }
        return 0;
    }
    @Override
    public int getValue(){
        if(currentUIDisplayed != null){
            return currentUIDisplayed.getValue();
        }
        return 0;
    }

    @Override
    public void adjust(float adjustment) {
        currentUIDisplayed.adjust(adjustment);
    }

    public boolean isDialogDisplayed(){
        return (currentUIDisplayed != null);
    }

    public Position getDialogPosition() {
        return decidingIndexDialogPosition;
    }

    public MapNode getDialogClickedNode() {
        return dialogClickedNode;
    }

    // TODO: Combine these into a single one
    public void disableDialog(){
        currentUIDisplayed = null;
    }
//    public void disableDialogSelectWaypoint() {
//        isWaypointChoiceDisplayed = false;
//    }
//    public void disableDialogDecidingIndexNumber() {
//        isDialogDisplayed = false;
//    }

    ////////////////////////

}
