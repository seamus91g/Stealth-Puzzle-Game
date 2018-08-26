package com.example.gilroy.sneako;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import java.util.ArrayList;
import java.util.List;

public class LevelConstructs {
    private List<WayPath> wallsToDraw;
    private int[][][] wallmatrix;
    private int tileHeight;
    private MapNode[][] mapNodes;

    LevelConstructs(int[][][] wallmatrix, int dimension) {
        this.wallmatrix = wallmatrix;
        tileHeight = dimension;
        int WIDTH_COUNT = wallmatrix[0][0].length;
        int HEIGHT_COUNT = wallmatrix[0].length;
        mapNodes = new MapNode[WIDTH_COUNT][HEIGHT_COUNT];
        setUpWalls();
        resetMapNodes();
    }

    public List<WayPath> getWallsToDraw() {
        return wallsToDraw;
    }

    public MapNode getInsertionPoint() {
        return mapNodes[0][0];
    }

    public MapNode getNode(Position position) {
        return getNode(position.x, position.y);
    }

    public MapNode getNode(int posX, int posY) {
        return mapNodes[posX][posY];
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

    private void setUpWalls() {
        wallsToDraw = new ArrayList<>();
        Paint wallStyle;

        wallStyle = new Paint();
        wallStyle.setStyle(Paint.Style.STROKE);
        wallStyle.setStrokeWidth(15);
        wallStyle.setColor(Color.BLACK);
        for (int q = 0; q < 2; ++q) {
            for (int y = 0; y < wallmatrix[0].length; ++y) {
                for (int x = 0; x < wallmatrix[0][y].length; ++x) {
                    if (wallmatrix[q][y][x] == 1) {
                        Path wall = new Path();
                        if (q == 0) {
                            wall.moveTo(x * tileHeight, y * tileHeight + tileHeight);
                            wall.lineTo(x * tileHeight + tileHeight, y * tileHeight + tileHeight);
                        } else {
                            wall.moveTo(x * tileHeight + tileHeight, y * tileHeight);
                            wall.lineTo(x * tileHeight + tileHeight, y * tileHeight + tileHeight);
                        }
                        wallsToDraw.add(new WayPath(wall, wallStyle));
                    }
                }
            }
        }
    }
}

