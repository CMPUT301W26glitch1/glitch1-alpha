package com.example.eventlotterysystemapp.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class AccessibilityUtils {

    private static final String PREFS_NAME = "app_settings";
    private static final String KEY_ACCESSIBILITY_MODE = "accessibility_mode";

    public static void saveAccessibilityMode(Context context, boolean enabled) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_ACCESSIBILITY_MODE, enabled).apply();
    }

    public static boolean isAccessibilityModeOn(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_ACCESSIBILITY_MODE, false);
    }

    public static void applyAccessibilityMode(Activity activity) {
        if (!isAccessibilityModeOn(activity)) {
            return;
        }

        View root = activity.findViewById(android.R.id.content);
        if (root != null) {
            applyToView(root);
        }
    }

    private static void applyToView(View view) {
        if (view instanceof TextView) {
            TextView textView = (TextView) view;
            float currentSize = textView.getTextSize() / view.getResources().getDisplayMetrics().scaledDensity;
            textView.setTextSize(currentSize + 10);
        }

        if (view instanceof Button) {
            Button button = (Button) view;
            button.setTextSize(25);
            button.setMinHeight(150);
            button.setPadding(
                    button.getPaddingLeft() + 12,
                    button.getPaddingTop() + 12,
                    button.getPaddingRight() + 12,
                    button.getPaddingBottom() + 12
            );
        }

        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                applyToView(group.getChildAt(i));
            }
        }
    }
}

