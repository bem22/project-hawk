package utils;

import android.view.View;

public class ViewUtils {

    public static void setViewColor(View view,int opacity, int red, int green, int blue) {
        view.setBackgroundColor(opacity*16*16*16*16*16*16 + red*16*16*16*16 + green*16*16 + blue);
    }
}
