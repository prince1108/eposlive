package com.foodciti.foodcitipartener.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.TypedValue;
import android.view.View;

import com.foodciti.foodcitipartener.R;

import java.util.HashMap;
import java.util.Map;

public class CommonMethods {
    private Context context;

    public CommonMethods(Context context) {
        this.context = context;
    }

    public Map<String, Integer> getColorMap() {
        final Map<String, Integer> colorMap = new HashMap<>();
        String[] colors = context.getResources().getStringArray(R.array.color_string);
        for (String color : colors) {
            String name = color.split("\\|")[0];
            String hex = color.split("\\|")[1];
            colorMap.put(name, Color.parseColor(hex));
        }
        return colorMap;
    }

    public static float convertDpToPx(Context context, float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static float convertPxToDp(Context context, float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static int convertSpToPx(Context context, float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

    public static int dpToSp(float dp, Context context) {
        return (int) (convertDpToPx(context, dp) / context.getResources().getDisplayMetrics().scaledDensity);
    }

    public static void setGradientDrawable(Context context, View view, int color) {
        /*int[][] states = new int[][] {
                new int[] { android.R.attr.state_enabled}, // enabled
                new int[] {-android.R.attr.state_enabled}, // disabled
                new int[] {-android.R.attr.state_checked}, // unchecked
                new int[] { android.R.attr.state_pressed}  // pressed
        };*/

        int gradientStart = lighten(color, 0.80);
        int gradientEnd = darken(color, 0.50);
        int[] colors = {gradientStart, color, gradientEnd};

        StateListDrawable stateListDrawable = new StateListDrawable();

        GradientDrawable gradientDefault = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
        gradientDefault.setShape(GradientDrawable.RECTANGLE);
        gradientDefault.setStroke((int) CommonMethods.convertDpToPx(context, 1), color);
        gradientDefault.setCornerRadius(CommonMethods.convertDpToPx(context, 5));

        gradientStart = lighten(color, 0.80);
        GradientDrawable gradientPressed = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{gradientStart, gradientStart, gradientStart});
        gradientPressed.setShape(GradientDrawable.RECTANGLE);
        gradientPressed.setStroke((int) CommonMethods.convertDpToPx(context, 1), color);
        gradientPressed.setCornerRadius(CommonMethods.convertDpToPx(context, 5));

        GradientDrawable gradientFocussed = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
        gradientFocussed.setShape(GradientDrawable.RECTANGLE);
        gradientFocussed.setStroke((int) CommonMethods.convertDpToPx(context, 1), color);
        gradientFocussed.setCornerRadius(CommonMethods.convertDpToPx(context, 5));

        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, gradientPressed);
        stateListDrawable.addState(new int[]{android.R.attr.state_focused}, gradientFocussed);
        stateListDrawable.addState(new int[]{}, gradientDefault);
        view.setBackground(stateListDrawable);
//        view.setClickable(true);
    }

    public static int lighten(int color, double fraction) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        red = lightenColor(red, fraction);
        green = lightenColor(green, fraction);
        blue = lightenColor(blue, fraction);
        int alpha = Color.alpha(color);
        return Color.argb(alpha, red, green, blue);
    }

    public static int darken(int color, double fraction) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        red = darkenColor(red, fraction);
        green = darkenColor(green, fraction);
        blue = darkenColor(blue, fraction);
        int alpha = Color.alpha(color);

        return Color.argb(alpha, red, green, blue);
    }

    private static int darkenColor(int color, double fraction) {
        return (int) Math.max(color - (color * fraction), 0);
    }

    private static int lightenColor(int color, double fraction) {
        return (int) Math.min(color + (color * fraction), 255);
    }
}
