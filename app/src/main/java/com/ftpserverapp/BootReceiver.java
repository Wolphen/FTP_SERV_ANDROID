package com.ftpserverapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d(TAG, "Événement BOOT_COMPLETED capturé, démarrage du service FTP.");
            Intent serviceIntent = new Intent(context, FTPService.class);
            context.startForegroundService(serviceIntent);
        }
    }
}
