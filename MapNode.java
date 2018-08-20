package com.example.gilroy.sneako;

import java.util.ArrayList;
import java.util.List;

public class MapNode {
    private final Position position;
    private List<MapNode> neighbours;
    private List<Waypoint> waypoints = new ArrayList<>();

    MapNode(Position pos) {
        this(pos.x, pos.y);
    }

    MapNode(int x, int y) {
        this.position = new Position(x, y);
        neighbours = new ArrayList<>();
    }

    int xPosition() {
        return position.x;
    }

    int yPosition() {
        return position.y;
    }

    void addNeighbour(MapNode nb) {
        neighbours.add(nb);
    }

    int getNeighbourSize() {
        return neighbours.size();
    }

    MapNode getNeighbour(int index) {
        return neighbours.get(index);
    }

    int getWaypointCount() {
        return waypoints.size();
    }

    void addWaypoint(Waypoint wp) {
        waypoints.add(wp);
    }

    void removeWaypoint(int index) {
        waypoints.remove(index);
    }

    void removeTopWaypoint() {
        waypoints.remove(waypoints.size() - 1);
    }

}

