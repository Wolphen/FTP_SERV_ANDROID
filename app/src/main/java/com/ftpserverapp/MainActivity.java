package com.ftpserverapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView statusTextView;
    private TextView ipPortTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusTextView = findViewById(R.id.tv_status);
        ipPortTextView = findViewById(R.id.tv_ip_port);

        // Démarrer le Service FTP
        Intent intent = new Intent(this, FTPService.class);
        startService(intent);

        // Mettre à jour l'état initial
        statusTextView.setText("Démarrage du serveur FTP...");
        ipPortTextView.setText("En attente d'adresse IP et de port...");

        // Enregistrer le BroadcastReceiver pour recevoir des mises à jour du service
        registerReceiver(ftpStatusReceiver, new IntentFilter("FTP_STATUS_UPDATE"), Context.RECEIVER_NOT_EXPORTED);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Désenregistrer le BroadcastReceiver pour éviter les fuites de mémoire
        unregisterReceiver(ftpStatusReceiver);
    }

    // BroadcastReceiver pour recevoir les mises à jour du service FTP
    private final BroadcastReceiver ftpStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String status = intent.getStringExtra("status");
            String ipPort = intent.getStringExtra("ipPort");

            if (status != null) {
                statusTextView.setText(status);
            }
            if (ipPort != null) {
                ipPortTextView.setText(ipPort);
            }
        }
    };
}
