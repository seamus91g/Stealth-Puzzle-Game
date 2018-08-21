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

    public int xPosition() {
        return position.x;
    }

    public int yPosition() {
        return position.y;
    }

    public void addNeighbour(MapNode nb) {
        neighbours.add(nb);
    }

    public int getNeighbourSize() {
        return neighbours.size();
    }

    public MapNode getNeighbour(int index) {
        return neighbours.get(index);
    }

    public int getWaypointCount() {
        return waypoints.size();
    }

    public Waypoint getWaypoint(int index) {
        return waypoints.get(index);
    }

    public void addWaypoint(Waypoint wp) {
        waypoints.add(wp);
    }

    public void removeWaypoint(int index) {
        waypoints.remove(index);
    }

    public void removeTopWaypoint() {
        removeWaypoint(waypoints.size() - 1);
    }
}

