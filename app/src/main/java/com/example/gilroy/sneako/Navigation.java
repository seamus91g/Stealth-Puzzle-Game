package com.example.gilroy.sneako;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class Navigation {

    protected LinkedList<MapNode> route = new LinkedList<>();

    public Navigation(MapNode insertionPoint) {
        route.add(insertionPoint);
    }

    protected List<MapNode> travel(MapNode waypointNode, MapNode clicked) {
        if (waypointNode.equals(clicked)) return null;
        Map<MapNode, MapNode> parents = new HashMap<>();
        ArrayList<MapNode> route = new ArrayList<>();

        Queue<MapNode> bfs = new LinkedList<>();
        bfs.add(clicked);
        while (!bfs.isEmpty()) {
            MapNode thisNode = bfs.remove();
            for (int i = 0; i < thisNode.getNeighbourSize(); ++i) {
                if (parents.containsKey(thisNode.getNeighbour(i))) {
                    continue;
                }
                parents.put(thisNode.getNeighbour(i), thisNode);
                if (thisNode.getNeighbour(i).equals(waypointNode)) {
//                    route.add(thisNode.getNeighbour(i));  // Don't add starting node  (waypointNode)
                    route.add(thisNode);
                    if (thisNode.equals(clicked)) {
                        return route;
                    }
                    MapNode previousNode = parents.get(thisNode);
                    route.add(previousNode);
                    while (!previousNode.equals(clicked)) {
                        previousNode = parents.get(previousNode);
                        route.add(previousNode);
                    }
                    return route;
                }
                bfs.add(thisNode.getNeighbour(i));
            }
        }
        return null;
    }

}
