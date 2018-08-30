package com.example.gilroy.sneako;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class GameScreen extends Activity {

    private static final String TAG = "LogTag";
    GestureDetector mgst;
    GameView gameView;

    static float SCREEN_DENSITY;
    static float SCREEN_WIDTH;
    static float SCREEN_HEIGHT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        SCREEN_DENSITY = this.getResources().getDisplayMetrics().density;
        SCREEN_WIDTH = this.getResources().getDisplayMetrics().widthPixels;
        SCREEN_HEIGHT = this.getResources().getDisplayMetrics().heightPixels;

        FrameLayout gameFrame = new FrameLayout(this);
        gameView = new GameView(this);
        LinearLayout buttons = new LinearLayout(this);

        Button startButton = new Button(this);
        Button resetButton = new Button(this);
        int width = 100;
        startButton.setText("Start");
//        startButton.setWidth(width);
        resetButton.setText("Reset");
//        resetButton.setWidth(width);

        buttons.addView(startButton);
        buttons.addView(resetButton);
        gameFrame.addView(gameView);
        gameFrame.addView(buttons);

        setContentView(gameFrame);
        mgst = new GestureDetector(this, gameView);
        gameView.setOnTouchListener(gameListener);

        startButton.setOnClickListener(startListener);
        resetButton.setOnClickListener(resetListener);
    }

    private View.OnClickListener startListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            gameView.startAction();
        }
    };
    private View.OnClickListener resetListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            gameView.resetAction();
        }
    };


    View.OnTouchListener gameListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return mgst.onTouchEvent(event);
        }
    };

}
