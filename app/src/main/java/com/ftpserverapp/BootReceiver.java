package com.ftpserverapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            // Démarrer le service FTP
            Intent serviceIntent = new Intent(context, FTPService.class);
            context.startForegroundService(serviceIntent);
        }
    }
}
