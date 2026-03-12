package com.example.eventlotterysystemapp.ui;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

/**
 * Utilities to be used
 */
public class UiUtils {

    /**
     * Displays a pop-up notification
     * @param context The current activity context
     * @param title The title of the pop-up notification
     * @param message The message to be displayed
     */
    public static void showNotification(Context context, String title, String message) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
