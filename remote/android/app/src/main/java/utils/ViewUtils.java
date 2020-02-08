package utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.example.myapplication.CircleView;
import com.example.myapplication.R;

public class ViewUtils {
    private Activity activity;
    public ViewUtils(Activity activity) {
        this.activity = activity;
    }

    public static void setViewColor(View view,int opacity, int red, int green, int blue) {
        view.setBackgroundColor(opacity*16*16*16*16*16*16 + red*16*16*16*16 + green*16*16 + blue);
    }

    CircleView circleLeft;
    CircleView circleRight;

    // TODO: Refactor locationsL/R and move to View utils
    int[] locationsL = new int[2];
    int[] locationsR = new int[2];

}
