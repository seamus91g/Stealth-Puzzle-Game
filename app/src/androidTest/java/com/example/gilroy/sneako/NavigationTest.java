package com.example.gilroy.sneako;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class NavigationTest {
    int[][][] wallmatrix;
    LevelConstructs level;

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
        level = new LevelConstructs(wallmatrix, 50);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testZeroDistance() {
        MapNode start = level.getInsertionPoint();
        MapNode end = level.getInsertionPoint();
        Navigation nav = new Navigation(start);
        List<MapNode> routeFound = nav.travel(end, start);
        assertEquals(null, routeFound);
    }
    @Test
    public void travelNear() {
        MapNode start = level.getInsertionPoint();
        MapNode end = level.getNode(new Position(2, 2));
        Navigation nav = new Navigation(start);
        List<MapNode> routeFound = nav.travel(end, start);
        assertEquals(4, routeFound.size());
    }
    @Test
    public void travelFar() {
        MapNode start = level.getInsertionPoint();
        MapNode end = level.getNode(new Position(4, 3));
        Navigation nav = new Navigation(start);
        List<MapNode> routeFound = nav.travel(end, start);
        assertEquals(13, routeFound.size());
    }

}