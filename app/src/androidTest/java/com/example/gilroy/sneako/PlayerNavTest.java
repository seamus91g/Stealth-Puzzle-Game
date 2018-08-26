package com.example.gilroy.sneako;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PlayerNavTest {
    int[][][] wallmatrix;
    LevelConstructs level;
    PlayerSprite player;

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
        Context appContext = InstrumentationRegistry.getTargetContext();
        player = new PlayerSprite(level.getInsertionPoint(), 50, appContext.getResources());
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void addWaypointTest() {
        player.addWaypoint(level.getNode(1, 1));
        assertEquals(1, player.getWaypoints().size());
        assertEquals(true, player.getWaypoints().values().contains(level.getNode(1, 1).getWaypoint(0)));
    }

    @Test
    public void addRemoveWaypointsTest() {
        player.addWaypoint(level.getNode(1, 1));
        player.addWaypoint(level.getNode(2, 2));
        player.addWaypoint(level.getNode(3, 3));
        player.addWaypoint(level.getNode(5, 2));
        assertEquals(4, player.getWaypoints().size());
        player.removeWaypoint(level.getNode(1, 1));
        player.removeWaypoint(level.getNode(2, 2));
        player.removeWaypoint(level.getNode(3, 3));
        player.removeWaypoint(level.getNode(5, 2));
        assertEquals(0, player.getWaypoints().size());
    }

    @Test
    public void repeatedAddRemoveTest() {
        player.addWaypoint(level.getNode(1, 1));
        player.addWaypoint(level.getNode(2, 2));
        assertEquals(2, player.getWaypoints().size());
        player.removeWaypoint(level.getNode(1, 1));
        player.removeWaypoint(level.getNode(2, 2));
        assertEquals(0, player.getWaypoints().size());
        player.addWaypoint(level.getNode(3, 3));
        player.addWaypoint(level.getNode(5, 2));
        assertEquals(2, player.getWaypoints().size());
        player.removeWaypoint(level.getNode(3, 3));
        player.removeWaypoint(level.getNode(5, 2));
        assertEquals(0, player.getWaypoints().size());
        player.addWaypoint(level.getNode(4, 6));
        player.addWaypoint(level.getNode(6, 7));
        assertEquals(2, player.getWaypoints().size());
        player.removeWaypoint(level.getNode(4, 6));
        assertEquals(1, player.getWaypoints().size());
        player.addWaypoint(level.getNode(7, 2));
        assertEquals(2, player.getWaypoints().size());
        player.removeWaypoint(level.getNode(7, 2));
        player.removeWaypoint(level.getNode(6, 7));
        assertEquals(0, player.getWaypoints().size());
        player.addWaypoint(level.getNode(1, 1));
        player.addWaypoint(level.getNode(2, 2));
        assertEquals(2, player.getWaypoints().size());
        player.removeWaypoint(level.getNode(1, 1));
        player.removeWaypoint(level.getNode(2, 2));
        assertEquals(0, player.getWaypoints().size());
    }

    @Test
    public void addRemoveStackWaypoints() {

        player.addWaypoint(level.getNode(1, 1));
        player.addWaypoint(level.getNode(2, 2));
        player.addWaypoint(level.getNode(1, 1));
        assertEquals(3, player.getWaypoints().size());
        player.removeWaypoint(level.getNode(1, 1));
        assertEquals(2, player.getWaypoints().size());
        player.removeWaypoint(level.getNode(1, 1));
        assertEquals(1, player.getWaypoints().size());
        player.removeWaypoint(level.getNode(2, 2));
        assertEquals(0, player.getWaypoints().size());
    }

    @Test
    public void cantStackSameWaypointTest() {
        player.addWaypoint(level.getNode(1, 1));
        player.addWaypoint(level.getNode(2, 2));
        player.addWaypoint(level.getNode(1, 1));
        player.addWaypoint(level.getNode(1, 1));
        player.addWaypoint(level.getNode(1, 1));
        assertEquals(3, player.getWaypoints().size());
    }

    @Test
    public void induceSequentialStackTest() {

        player.addWaypoint(level.getNode(1, 1));
        player.addWaypoint(level.getNode(2, 2));
        player.addWaypoint(level.getNode(1, 1));
        assertEquals(3, player.getWaypoints().size());
        player.removeWaypoint(level.getNode(2, 2));
        assertEquals(1, player.getWaypoints().size());
        player.removeWaypoint(level.getNode(1, 1));
        assertEquals(0, player.getWaypoints().size());
    }

    @Test
    public void repeatedAddRemoveStackWaypoints() {
        player.addWaypoint(level.getNode(1, 1));
        player.addWaypoint(level.getNode(2, 2));
        player.addWaypoint(level.getNode(1, 1));
        assertEquals(3, player.getWaypoints().size());
        player.removeWaypoint(level.getNode(1, 1));
        player.removeWaypoint(level.getNode(1, 1));
        player.removeWaypoint(level.getNode(2, 2));
        assertEquals(0, player.getWaypoints().size());

        player.addWaypoint(level.getNode(1, 1));
        player.addWaypoint(level.getNode(2, 2));
        player.addWaypoint(level.getNode(1, 1));
        player.addWaypoint(level.getNode(2, 2));
        player.addWaypoint(level.getNode(1, 1));
        player.addWaypoint(level.getNode(2, 2));
        player.addWaypoint(level.getNode(1, 1));
        player.addWaypoint(level.getNode(2, 2));
        player.addWaypoint(level.getNode(1, 1));
        assertEquals(9, player.getWaypoints().size());
        player.removeWaypoint(level.getNode(1, 1));
        assertEquals(7, player.getWaypoints().size());
        player.removeWaypoint(level.getNode(1, 1));
        assertEquals(5, player.getWaypoints().size());
        player.removeWaypoint(level.getNode(1, 1));
        assertEquals(3, player.getWaypoints().size());
        player.removeWaypoint(level.getNode(1, 1));
        assertEquals(2, player.getWaypoints().size());
        player.removeWaypoint(level.getNode(1, 1));
        assertEquals(1, player.getWaypoints().size());
        player.removeWaypoint(level.getNode(2, 2));
        assertEquals(0, player.getWaypoints().size());
    }
}