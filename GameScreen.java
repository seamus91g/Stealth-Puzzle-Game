package com.example.gilroy.sneako;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class GameScreen extends Activity {

    static float SCREEN_DENSITY;
    static float SCREEN_WIDTH;
    static float SCREEN_HEIGHT;
    static int HEIGHT_COUNT = 8;
    static int WIDTH_COUNT = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        SCREEN_DENSITY = this.getResources().getDisplayMetrics().density;
        SCREEN_WIDTH = this.getResources().getDisplayMetrics().widthPixels;
        SCREEN_HEIGHT = this.getResources().getDisplayMetrics().heightPixels;

//        setContentView(new GameView(this));
    }
}
