package com.example.gilroy.sneako;

import android.app.Activity;
import android.content.Context;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class GameScreen extends Activity {

    private static final String TAG = "LogTag";
    GestureDetector mgst;

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

        GameView gameView = new GameView(this);
        setContentView(gameView);
        mgst = new GestureDetector(this, gameView);
        gameView.setOnTouchListener(gameListener);
    }

    View.OnTouchListener gameListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return mgst.onTouchEvent(event);
        }
    };

}
