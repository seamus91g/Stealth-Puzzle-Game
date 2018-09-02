package com.example.gilroy.sneako;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class PlayerNavTest {
    int[][][] wallmatrix;
    LevelConstructs level;
    PlayerSprite player;
    PlayerSprite player2;

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
        player2 = new PlayerSprite(level.getNode(1, 0), 50, appContext.getResources());
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void addWaypointTest() {
        player.addWaypoint(level.getNode(1, 1));
        assertEquals(1, player.getWaypoints().size());
        assertEquals(true, player.getWaypoints().values().contains(level.getNode(1, 1).getWaypoint(player.getID(), 0)));
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
    public void addRemoveWaypointsTwoSoldiersTest() {
        player.addWaypoint(level.getNode(1, 1));
        player.addWaypoint(level.getNode(2, 2));
        player2.addWaypoint(level.getNode(4, 3));
        player2.addWaypoint(level.getNode(5, 3));
        player2.addWaypoint(level.getNode(6, 3));
        assertEquals(2, player.getWaypoints().size());
        assertEquals(3, player2.getWaypoints().size());
        player.removeWaypoint(level.getNode(1, 1));
        player.removeWaypoint(level.getNode(2, 2));
        assertEquals(0, player.getWaypoints().size());
        player2.removeWaypoint(level.getNode(4, 3));
        player2.removeWaypoint(level.getNode(5, 3));
        player2.removeWaypoint(level.getNode(6, 3));
        assertEquals(0, player2.getWaypoints().size());
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
    public void addRemoveStackWaypointsTwoSoldiers() {
        player.addWaypoint(level.getNode(1, 1));
        player.addWaypoint(level.getNode(2, 2));
        player.addWaypoint(level.getNode(1, 1));
        player2.addWaypoint(level.getNode(1, 1));
        player2.addWaypoint(level.getNode(2, 2));
        player2.addWaypoint(level.getNode(1, 1));
        assertEquals(3, player.getWaypoints().size());
        player.removeWaypoint(level.getNode(1, 1));
        assertEquals(2, player.getWaypoints().size());
        player.removeWaypoint(level.getNode(1, 1));
        assertEquals(1, player.getWaypoints().size());
        player.removeWaypoint(level.getNode(2, 2));
        assertEquals(0, player.getWaypoints().size());
        assertEquals(3, player2.getWaypoints().size());
        player2.removeWaypoint(level.getNode(1, 1));
        assertEquals(2, player2.getWaypoints().size());
        player2.removeWaypoint(level.getNode(1, 1));
        assertEquals(1, player2.getWaypoints().size());
        player2.removeWaypoint(level.getNode(2, 2));
        assertEquals(0, player2.getWaypoints().size());
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
        assertEquals(getTotalRoute(player.getTopWaypoint()).size(), player.route.size());
        assertEquals(1, player.getWaypoints().size());
        player.removeWaypoint(level.getNode(1, 1));
        assertEquals(1, player.route.size());
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

//    @Test
//    public void insertSlice(){      // TODO: This is not testing anything
////        LinkedList<MapNode> route = new LinkedList<>();
//        LinkedList<MapNode> newSlice = new LinkedList<>();
//        player.addWaypoint(level.getNode(0,5));
//        player.addWaypoint(level.getNode(3,5));
//        player.addWaypoint(level.getNode(0,5));
//        newSlice.add(level.getNode(1, 2));
//        newSlice.add(level.getNode(1, 3));
//        newSlice.add(level.getNode(1, 4));
//        newSlice.add(level.getNode(0, 4));
//        player.substituteSlice(player.getRoute(), level.getNode(0, 2).getWaypoint(0), level.getNode(0, 4), newSlice);
//        printRoute(player.getRoute());
//    }

    @Test
    public void routeRecalculateTest(){
        addLoopA();
        addLoopA();
        addLoopA();
        assertEquals(getTotalRoute(player.getTopWaypoint()).size(), player.route.size());
        clearNode(player.getID(),4, 2);
        assertEquals(getTotalRoute(player.getTopWaypoint()).size(), player.route.size());
        clearNode(player.getID(),3, 0);
        assertEquals(getTotalRoute(player.getTopWaypoint()).size(), player.route.size());
        clearNode(player.getID(),1, 0);
        assertEquals(getTotalRoute(player.getTopWaypoint()).size(), player.route.size());
        clearNode(player.getID(),1, 2);
        assertEquals(getTotalRoute(player.getTopWaypoint()).size(), player.route.size());
    }
    public List<MapNode> getTotalRoute(Waypoint wp){
        List<MapNode> testRoute = new ArrayList<>();
        List<MapNode> ll = new LinkedList<>();
        if(wp == null){
            ll.add(level.getInsertionPoint());
            return ll;        // There are no waypoints and thus there is no route
        }
        testRoute.add(wp.getWaypointNode());
//        int testRouteCount = 1;     // Travel algorithm doesn't count from starting node.
        while(wp.getPrevWP() != null){
            testRoute.addAll(player.travel(wp.getWaypointNode(), wp.getPrevWP().getWaypointNode()));
            wp = wp.getPrevWP();
        }
        testRoute.addAll(player.travel(wp.getWaypointNode(), level.getInsertionPoint()));
//        testRouteCount += player.travel(wp.getWaypointNode(), level.getInsertionPoint()).size();
        for(MapNode mn : testRoute){
            ll.add(0, mn);
        }
        return ll;
    }

    private void printRoute(List<MapNode> route) {
        StringBuilder shortestPath = new StringBuilder();
        for (MapNode mn : route) {
            shortestPath.append("(");
            shortestPath.append(mn.xPosition());
            shortestPath.append(",");
            shortestPath.append(mn.yPosition());
            shortestPath.append(")->");
        }
        Log.d("sneakoTest", shortestPath.toString());
    }
    @Test
    public void stressTest() {
        addLoopB();
        addLoopB();
        addLoopB();
        addLoopB();
        addLoopB();
        addLoopB();
        clearNode(player.getID(), 0, 8);
        clearNode(player.getID(),7, 4);
        clearNode(player.getID(),6, 9);
        clearNode(player.getID(),2, 2);
        assertEquals(0, player.getWaypoints().size());
    }

    public void clearNode(UUID id, int x, int y){
        while (level.getNode(x, y).getWaypointCount(id) > 0){
            player.removeWaypoint(level.getNode(x, y));
        }
    }

    public void addLoopA(){
        player.addWaypoint(level.getNode(3, 0));
        player.addWaypoint(level.getNode(4, 2));
        player.addWaypoint(level.getNode(1, 2));
        player.addWaypoint(level.getNode(1, 0));
    }
    public void addLoopB(){
        player.addWaypoint(level.getNode(2, 2));
        player.addWaypoint(level.getNode(7, 4));
        player.addWaypoint(level.getNode(6, 9));
        player.addWaypoint(level.getNode(0, 8));
    }
}