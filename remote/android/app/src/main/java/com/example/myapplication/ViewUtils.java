package com.example.myapplication;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.myapplication.CircleView;
import com.example.myapplication.R;

public class ViewUtils {

    ViewUtils() {
    }
    public int defaultStickPositionLeftX = 529;
    public int defaultStickPositionLeftY = 513;
    public int defaultStickPositionRightX = 1638;
    public int defaultStickPositionRightY = 513;

    public static void setViewColor(View view, int opacity, int red, int green, int blue) {
        view.setBackgroundColor(opacity*16*16*16*16*16*16 + red*16*16*16*16 + green*16*16 + blue);
    }

    public CircleView stickPositionLeft;
    public CircleView stickPositionRight;
    Button connectButton;
    public ImageView connectionIcon;

    public int[] stickPositionLeftXY= new int[2];
    public int[] stickPositionRightXY = new int[2];

    public void showConnectionIcon(boolean connected) {
        if(connected) {
            connectionIcon.setBackgroundResource(R.drawable.signal_icon);
        } else {
            connectionIcon.setBackgroundResource(R.drawable.no_signal_icon);
        }
    }
}
