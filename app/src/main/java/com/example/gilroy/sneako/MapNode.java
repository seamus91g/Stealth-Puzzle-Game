package com.example.gilroy.sneako;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.Map;

public class MapNode {
    private final Position position;
    private List<MapNode> neighbours;
    private Map<UUID, List<Waypoint>> waypoints = new HashMap<>();

    MapNode(Position pos) {
        this(pos.x, pos.y);
    }

    MapNode(int x, int y) {
        this.position = new Position(x, y);
        neighbours = new ArrayList<>();
    }

    public Position getPosition(){
        return position;
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

    public int getWaypointCount(UUID id) {
        if(waypoints.containsKey(id)){
            return waypoints.get(id).size();
        }else{
            return 0;
        }
    }

    public Waypoint getWaypoint(UUID id, int index) {
        return waypoints.get(id).get(index);
    }

    public void addWaypoint(UUID id, Waypoint wp) {
        if(!waypoints.containsKey(id)){
            waypoints.put(id, new ArrayList<Waypoint>());
        }
        waypoints.get(id).add(wp);
    }

    public void removeWaypoint(UUID id, int index) {
        waypoints.get(id).remove(index);
    }

    public void removeWaypoint(UUID idSoldier, UUID idWaypoint) {
        for (int i=0; i<waypoints.get(idSoldier).size(); ++i) {
            if(waypoints.get(idSoldier).get(i).getID() == idWaypoint){
                waypoints.get(idSoldier).remove(i);
                return;
            }
        }
    }
}

