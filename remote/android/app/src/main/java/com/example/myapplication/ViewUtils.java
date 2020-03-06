package com.example.myapplication;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class ViewUtils {

    ViewUtils(RemoteState state) {
        this.state = state;
    }

    private RemoteState state;

    private int defaultStickPositionHorizontal = 529;
    private int defaultStickPositionVertical = 513;

    public static void setViewColor(View view, int opacity, int red, int green, int blue) {
        view.setBackgroundColor(opacity*16*16*16*16*16*16 + red*16*16*16*16 + green*16*16 + blue);
    }

    CircleView stickPositionLeft;
    CircleView stickPositionRight;
    Button connectButton;
    ImageView connectionIcon;

    public int[] stickPositionLeftXY= new int[2];
    public int[] stickPositionRightXY = new int[2];

    public void showConnectionIcon(boolean connected) {
        if(connected) {
            connectionIcon.setBackgroundResource(R.drawable.signal_icon);
        } else {
            connectionIcon.setBackgroundResource(R.drawable.no_signal_icon);
        }
    }

    void updateAxesUI() {
        stickPositionLeft.setX(defaultStickPositionHorizontal+ state.getRawAxes().get(1) * 500);
        stickPositionLeft.setY(defaultStickPositionVertical - state.getRawAxes().get(2) * 500);
        stickPositionRight.setX(defaultStickPositionHorizontal  + state.getRawAxes().get(3) * 500);
        //stickPositionRight.setY(defaultStickPositionVertical - state.getRawAxes().get(4) * 500);
    }
}
