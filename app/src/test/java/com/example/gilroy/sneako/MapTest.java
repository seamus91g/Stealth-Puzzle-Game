package com.example.gilroy.sneako;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MapTest {
    int[][][] wallmatrix;

    @Before
    public void setUp() throws Exception {
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
    public void clickRegion() {
        Map map = new Map(wallmatrix, 50, 400, 500);
        assertEquals(0, map.clickRegion(20, 20).x);
        assertEquals(0, map.clickRegion(40, 40).y);
        assertEquals(1, map.clickRegion(60, 60).x);
        assertEquals(1, map.clickRegion(60, 60).y);
        assertEquals(1, map.clickRegion(50, 50).x);
        assertEquals(1, map.clickRegion(50, 50).y);
    }
    @Test
    public void clickRegionOutOfBounds() {
        Map map = new Map(wallmatrix, 50, 500, 600);
        assertEquals(-1, map.clickRegion(10, 10).x);
        assertEquals(-1, map.clickRegion(10, 10).y);
        assertEquals(-1, map.clickRegion(460, 560).x);
        assertEquals(-1, map.clickRegion(460, 560).y);
    }

    @Test
    public void shiftCanvas() {
        Map map = new Map(wallmatrix, 50, 500, 600);
        map.shiftCanvas(10,20);
        assertEquals(40, map.offsetPosition().x);
        assertEquals(30, map.offsetPosition().y);
    }

    @Test
    public void getTileHeight() {
        Map map = new Map(wallmatrix, 50, 500, 600);
        assertEquals(50, map.getTileHeight());
    }
}