package com.ftpserverapp;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import org.apache.ftpserver.usermanager.impl.BaseUser;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.listener.ListenerFactory;

import java.util.Collections;


public class FTPService extends Service {
    private static final String TAG = "FTPService";
    private FtpServer ftpServer;
    private java.util.Collections Collections;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(this::startFtpServer).start();

        Notification notification = new NotificationCompat.Builder(this, App.CHANNEL_ID)
                .setContentTitle("Serveur FTP")
                .setContentText("Le serveur est en cours d'exécution.")
                .setSmallIcon(R.drawable.ic_ftp_server)
                .build();

        startForeground(1, notification);
        return START_STICKY;
    }

    private void startFtpServer() {
        try {
            if (ftpServer != null && !ftpServer.isStopped()) {
                Log.e(TAG, "Serveur FTP déjà en cours d'exécution.");
                return;
            }

            // Créer le serveur FTP et configurer le port
            FtpServerFactory serverFactory = new FtpServerFactory();
            ListenerFactory listenerFactory = new ListenerFactory();
            listenerFactory.setPort(2121);

            // Configurer un utilisateur anonyme
            BaseUser user = new BaseUser();
            user.setName("anonymous");
            user.setHomeDirectory("/sdcard"); // Répertoire racine
            user.setAuthorities(Collections.singletonList(new org.apache.ftpserver.usermanager.impl.WritePermission()));

            // Ajouter le listener et l'utilisateur
            serverFactory.addListener("default", listenerFactory.createListener());
            serverFactory.getUserManager().save(user);

            // Démarrer le serveur
            ftpServer = serverFactory.createServer();
            ftpServer.start();

            String ipAddress = getLocalIpAddress();
            sendStatusBroadcast("started", ipAddress);
            Log.d(TAG, "Serveur FTP démarré à l'adresse : ftp://" + ipAddress + ":2121");

        } catch (Exception e) {
            Log.e(TAG, "Erreur lors du démarrage du serveur FTP", e);
            sendStatusBroadcast("error", null);
        }
    }


    private String getLocalIpAddress() {
        try {
            for (java.util.Enumeration<java.net.NetworkInterface> en = java.net.NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                java.net.NetworkInterface intf = en.nextElement();
                for (java.util.Enumeration<java.net.InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    java.net.InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof java.net.Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (java.net.SocketException ex) {
            Log.e(TAG, "Erreur lors de la récupération de l'adresse IP", ex);
        }
        return "N/A";
    }

    private void sendStatusBroadcast(String status, String ipAddress) {
        Intent intent = new Intent("com.ftpserverapp.FTP_SERVER_STATUS");
        intent.putExtra("status", status);
        intent.putExtra("ipAddress", ipAddress);
        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        if (ftpServer != null && !ftpServer.isStopped()) {
            ftpServer.stop();
            sendStatusBroadcast("stopped", null);
            Log.d(TAG, "Serveur FTP arrêté");
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
