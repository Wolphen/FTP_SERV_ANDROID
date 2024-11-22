package com.ftpserverapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private TextView serverStatusText, serverInfoText;
    private Button startButton, stopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        serverStatusText = findViewById(R.id.server_status);
        serverInfoText = findViewById(R.id.server_info_text);
        startButton = findViewById(R.id.start_button);
        stopButton = findViewById(R.id.stop_button);

        startButton.setOnClickListener(v -> startService(new Intent(this, FTPService.class)));
        stopButton.setOnClickListener(v -> stopService(new Intent(this, FTPService.class)));

        registerReceiver(ftpStatusReceiver, new IntentFilter("com.ftpserverapp.FTP_SERVER_STATUS"));
    }

    private final BroadcastReceiver ftpStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String status = intent.getStringExtra("status");
            String ipAddress = intent.getStringExtra("ipAddress");

            if ("started".equals(status)) {
                serverStatusText.setText("Serveur FTP démarré");
                serverInfoText.setText("Adresse : ftp://" + ipAddress + ":2121");
            } else if ("stopped".equals(status)) {
                serverStatusText.setText("Serveur arrêté");
                serverInfoText.setText("Adresse : En attente...");
            } else {
                serverStatusText.setText("Erreur : Serveur non disponible");
                serverInfoText.setText("Adresse : N/A");
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(ftpStatusReceiver);
    }
}
