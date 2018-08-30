package com.example.gilroy.sneako;

// Simple Pair style class to handle coordinates of a position
class Position {
    int x;
    int y;

    Position(int x, int y) {
        this.x = x;
        this.y = y;
    }
    Position(Position position){
        this.x = position.x;
        this.y = position.y;
    }
}
