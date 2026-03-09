package com.example.eventlotterysystemapp.ui;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

public class UiUtils {

    public static void showNotification(Context context, String title, String message) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
