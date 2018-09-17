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

import java.util.HashMap;

import java.util.Map;

public class GameScreen extends Activity {

    private static final String TAG = "LogTag";
    GestureDetector mgst;
    GameView gameView;
    Map<String, Integer> playerTags = new HashMap<>();

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
        GameSetup gameSetup = new GameSetup();
        gameView = new GameView(this, gameSetup);
        LinearLayout buttons = new LinearLayout(this);

        Button startButton = new Button(this);
        Button resetButton = new Button(this);
        Button clearButton = new Button(this);
        int buttonWidth = (int) (SCREEN_WIDTH / 5 - 5);
        startButton.setLayoutParams(new LinearLayout.LayoutParams(buttonWidth, 90));
        resetButton.setLayoutParams(new LinearLayout.LayoutParams(buttonWidth, 90));
        clearButton.setLayoutParams(new LinearLayout.LayoutParams(buttonWidth, 90));
//        int width = 100;
        startButton.setText("Start");
        resetButton.setText("Reset");
        clearButton.setText("Clear");

        buttons.addView(startButton);
        buttons.addView(resetButton);
        buttons.addView(clearButton);

        if (gameSetup.numAllies > 1) {
            for (int i = 0; i < gameSetup.numAllies; ++i) {
                Button playerX = new Button(this);
                playerX.setLayoutParams(new LinearLayout.LayoutParams(buttonWidth, 90));
                String tag = "Player_" + i;
                playerX.setTag(tag);
                playerX.setText(String.valueOf(i + 1));
                playerX.setOnClickListener(changePlayerListener);
                buttons.addView(playerX);
                playerTags.put(tag, i);
            }
        }

        gameFrame.addView(gameView);
        gameFrame.addView(buttons);

        setContentView(gameFrame);
        mgst = new GestureDetector(this, gameView);
        gameView.setOnTouchListener(gameListener);

        startButton.setOnClickListener(startListener);
        resetButton.setOnClickListener(resetListener);
        clearButton.setOnClickListener(clearListener);
    }

    private View.OnClickListener startListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            gameView.startAction();
        }
    };
    private View.OnClickListener changePlayerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String tag = (String) v.getTag();
            gameView.declareActivePlayer(playerTags.get(tag));
        }
    };
    private View.OnClickListener resetListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            gameView.resetAction();
        }
    };
    private View.OnClickListener clearListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            gameView.clearWaypoints();
        }
    };


    View.OnTouchListener gameListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return mgst.onTouchEvent(event);
        }
    };

}
