package com.example.gilroy.sneako;

import android.graphics.Canvas;

public interface UI {
    void draw(Canvas canvas);
    void update();
    int queryClick(Position click);
    int getValue();
    void adjust(float adjustment);
}
