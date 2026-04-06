package com.example.eventlotterysystemapp.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.eventlotterysystemapp.data.models.UserSession;

public class AccessibilityUtils {

    private static final String PREFS_NAME = "app_settings";
    private static final float TEXT_INCREASE_SP = 4f;

    private static String getKey(Context context) {
        try {
            String email = UserSession.getUser().getEmail();
            if (email != null && !email.isEmpty()) {
                return "accessibility_mode_" + email;
            }
        } catch (Exception ignored) {}
        return "accessibility_mode_global";
    }

    public static void saveAccessibilityMode(Context context, boolean enabled) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(getKey(context), enabled).apply();
    }

    public static boolean isAccessibilityModeOn(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(getKey(context), false);
    }

    /** Apply to a full Activity (called in onCreate) */
    public static void applyAccessibilityMode(Activity activity) {
        if (!isAccessibilityModeOn(activity)) return;
        View root = activity.findViewById(android.R.id.content);
        if (root != null) applyToView(root);
    }

    /** Apply to a single RecyclerView item view (called in onBindViewHolder) */
    public static void applyToItemView(Context context, View itemView) {
        if (!isAccessibilityModeOn(context)) return;
        applyToView(itemView);
    }

    private static void applyToView(View view) {
        if (view instanceof Button) {
            Button button = (Button) view;
            float currentSize = button.getTextSize() / view.getResources().getDisplayMetrics().scaledDensity;
            if (currentSize < 20f) {
                button.setTextSize(currentSize + TEXT_INCREASE_SP);
            }
            // Let the button grow to fit the enlarged text instead of clipping it
            ViewGroup.LayoutParams params = button.getLayoutParams();
            if (params != null) {
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                button.setLayoutParams(params);
            }
        } else if (view instanceof TextView) {
            TextView textView = (TextView) view;
            float currentSize = textView.getTextSize() / view.getResources().getDisplayMetrics().scaledDensity;
            if (currentSize < 24f) {
                textView.setTextSize(currentSize + TEXT_INCREASE_SP);
            }
        }

        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                applyToView(group.getChildAt(i));
            }
        }
    }
}