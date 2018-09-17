package com.example.gilroy.sneako;

public class Map {
    private final int widthCount;
    private final int heightCount;
    private Position canvasPosition;
    private int tileHeight;

    Map(int[][][] wallmatrix, int tileHeight, float screenWidth, float screenHeight) {
        widthCount = wallmatrix[0][0].length;
        heightCount = wallmatrix[0].length;
        this.tileHeight = tileHeight;
        int xShift = (int) screenWidth / 2 - widthCount * tileHeight / 2;
        int yShift = (int) screenHeight / 2 - heightCount * tileHeight / 2;
        canvasPosition = new Position(xShift, yShift);
    }

    public Position clickLocation(float x, float y) {
        int fRegionX = (int) (x - canvasPosition.x);
        int fRegionY = (int) (y - canvasPosition.y);
        return new Position(fRegionX, fRegionY);
    }

    public Position clickRegion(float x, float y) {
        float fRegionX = ((x - canvasPosition.x) / tileHeight);
        int regionX = (int) ((fRegionX > 0 ) ? fRegionX : fRegionX - 1);
        float fRegionY = ((y - canvasPosition.y) / tileHeight);
        int regionY = (int) ((fRegionY > 0 ) ? fRegionY : fRegionY - 1);
        if ((regionX >= widthCount) || (regionX < 0)
                || (regionY >= heightCount) || (regionY < 0)) {
            return new Position(-1, -1);
        }
        return new Position(regionX, regionY);
    }

    public void shiftCanvas(float distanceX, float distanceY) {
        int xShift = (int) distanceX;
        int yShift = (int) distanceY;
        canvasPosition.x -= xShift;
        canvasPosition.y -= yShift;
    }
    public Position offsetPosition(){
        return canvasPosition;
    }

    public int getTileHeight(){
        return tileHeight;
    }

}
