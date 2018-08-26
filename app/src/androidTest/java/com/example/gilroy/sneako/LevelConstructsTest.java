package com.example.gilroy.sneako;

import android.graphics.Color;
import android.graphics.Paint;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class LevelConstructsTest {
    private int[][][] wallmatrix;
    private Paint wallStyle;

    @Before
    public void setUp() throws Exception {

        wallStyle = new Paint();
        wallStyle.setStyle(Paint.Style.STROKE);
        wallStyle.setStrokeWidth(15);
        wallStyle.setColor(Color.BLACK);
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

    @Test
    public void getInsertionPoint() {
        LevelConstructs level = new LevelConstructs(wallmatrix, 50);
        assertEquals(0, level.getInsertionPoint().xPosition());
        assertEquals(0, level.getInsertionPoint().yPosition());
    }
    @Test
    public void wallPlacement(){
        Paint.Style[] styles = {Paint.Style.FILL, Paint.Style.STROKE};
        LevelConstructs level = new LevelConstructs(wallmatrix, 50);
//        MapNode corner = level.getNode(new Position(0, 0));
//        assertEquals(2, corner.getNeighbourSize());
        assertEquals(2, level.getNode(new Position(0, 0)).getNeighbourSize());
        assertEquals(3, level.getNode(new Position(0, 1)).getNeighbourSize());
        assertEquals(4, level.getNode(new Position(1, 1)).getNeighbourSize());
        assertEquals(4, level.getNode(new Position(2, 4)).getNeighbourSize());
        assertEquals(3, level.getNode(new Position(4, 2)).getNeighbourSize());
        assertEquals(4, level.getNode(new Position(5, 1)).getNeighbourSize());
        assertEquals(3, level.getNode(new Position(5, 3)).getNeighbourSize());
        assertEquals(2, level.getNode(new Position(6, 7)).getNeighbourSize());
    }
}