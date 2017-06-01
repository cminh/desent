package com.example.desent.utils;

import android.content.res.Resources;
import android.util.TypedValue;

/**
 * Created by celine on 04/05/17.
 */
public class Utility {

    private Utility(){
        throw new AssertionError();
    }

    public static int pxToDp(int px){
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static int dpToPx(int dp){
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int spToPx(int sp) { return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, Resources.getSystem().getDisplayMetrics()); }

    public static String floatToStringNDecimals(float value, int n){
        return String.format("%."+ String.valueOf(n) + "f", value);
    }
}
