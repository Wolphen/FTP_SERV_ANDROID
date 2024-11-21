package com.ftpserverapp;

import android.app.Service;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;

import java.io.File;
import java.util.Collections;

public class FTPService extends Service {

    private static final String TAG = "FTPService";
    private FtpServer ftpServer;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startFtpServer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopFtpServer();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startFtpServer() {
        try {
            FtpServerFactory serverFactory = new FtpServerFactory();
            ListenerFactory listenerFactory = new ListenerFactory();

            listenerFactory.setPort(2121);

            BaseUser user = new BaseUser();
            user.setName("anonymous");
            user.setHomeDirectory("/sdcard");
            user.setAuthorities(Collections.singletonList(new org.apache.ftpserver.usermanager.impl.WritePermission()));

            serverFactory.addListener("default", listenerFactory.createListener());
            serverFactory.getUserManager().save(user);

            ftpServer = serverFactory.createServer();
            ftpServer.start();

            String ipAddress = getLocalIpAddress();
            String statusMessage = "Serveur FTP démarré avec succès.";
            String ipPortMessage = "Connectez-vous à : ftp://" + ipAddress + ":2121";

            sendStatusUpdate(statusMessage, ipPortMessage);

            Log.d(TAG, statusMessage + " Adresse : " + ipPortMessage);
        } catch (Exception e) {
            String errorMessage = "Erreur lors du démarrage du serveur FTP.";
            sendStatusUpdate(errorMessage, null);
            Log.e(TAG, errorMessage, e);
        }
    }

    private void stopFtpServer() {
        if (ftpServer != null && !ftpServer.isStopped()) {
            ftpServer.stop();
            ftpServer = null;
            Log.d(TAG, "Serveur FTP arrêté.");
        }
    }

    private String getLocalIpAddress() {
        try {
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            if (wifiManager != null) {
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                int ipAddress = wifiInfo.getIpAddress();
                return (ipAddress & 0xFF) + "." +
                        ((ipAddress >> 8) & 0xFF) + "." +
                        ((ipAddress >> 16) & 0xFF) + "." +
                        ((ipAddress >> 24) & 0xFF);
            }
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de la récupération de l'adresse IP : ", e);
        }
        return null;
    }

    // Méthode pour envoyer les mises à jour d'état via un Broadcast
    private void sendStatusUpdate(String status, String ipPort) {
        Intent intent = new Intent("FTP_STATUS_UPDATE");
        intent.putExtra("status", status);
        intent.putExtra("ipPort", ipPort);
        sendBroadcast(intent);
    }
}
