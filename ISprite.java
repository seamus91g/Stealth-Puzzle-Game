package com.example.gilroy.sneako;

import android.graphics.Canvas;

import java.util.UUID;

public interface ISprite {
    UUID getID();
    void draw(Canvas canvas);
    void update();
}
