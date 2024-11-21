package com.ftpserverapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Collections;
import androidx.appcompat.app.AppCompatActivity;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "FTPServerApp";

    private FtpServer ftpServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startFtpButton = findViewById(R.id.btn_start_ftp);
        Button stopFtpButton = findViewById(R.id.btn_stop_ftp);
        TextView statusTextView = findViewById(R.id.tv_status);

        startFtpButton.setOnClickListener(view -> {
            if (ftpServer == null || ftpServer.isStopped()) {
                startFtpServer(statusTextView);
            } else {
                Toast.makeText(this, "Le serveur FTP est déjà en cours d'exécution.", Toast.LENGTH_SHORT).show();
            }
        });

        stopFtpButton.setOnClickListener(view -> stopFtpServer(statusTextView));
    }

    private void startFtpServer(TextView statusTextView) {
        try {
            FtpServerFactory serverFactory = new FtpServerFactory();
            ListenerFactory listenerFactory = new ListenerFactory();

            // Configurer le port du serveur FTP
            listenerFactory.setPort(2121);

            // Configurer un utilisateur de base sans mot de passe
            BaseUser user = new BaseUser();
            user.setName("anonymous");
            user.setHomeDirectory(getExternalFilesDir(null).getAbsolutePath());
            user.setAuthorities(Collections.singletonList(new org.apache.ftpserver.usermanager.impl.WritePermission()));

            serverFactory.addListener("default", listenerFactory.createListener());
            serverFactory.getUserManager().save(user);

            // Démarrer le serveur FTP
            ftpServer = serverFactory.createServer();
            ftpServer.start();

            statusTextView.setText("Serveur FTP démarré sur le port 2121");
            statusTextView.setTextColor(getResources().getColor(android.R.color.holo_green_dark));

            Log.d(TAG, "Serveur FTP démarré sur le port 2121.");
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors du démarrage du serveur FTP : ", e);
            statusTextView.setText("Erreur lors du démarrage du serveur FTP.");
            statusTextView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }
    }

    private void stopFtpServer(TextView statusTextView) {
        if (ftpServer != null && !ftpServer.isStopped()) {
            ftpServer.stop();
            ftpServer = null;
            statusTextView.setText("Serveur FTP arrêté.");
            statusTextView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            Log.d(TAG, "Serveur FTP arrêté.");
        } else {
            Toast.makeText(this, "Le serveur FTP n'est pas en cours d'exécution.", Toast.LENGTH_SHORT).show();
        }
    }
}
