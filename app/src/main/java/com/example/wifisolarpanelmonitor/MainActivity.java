package com.example.wifisolarpanelmonitor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private MonitorClient monitorClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        monitorClient = new MonitorClient("192.168.43.91", 5678);
        monitorClient.setListener(new MonitorClient.Listener() {
            @Override
            public void onConnectionEstablished() {
                Toast.makeText(MainActivity.this, "Connection established", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDataChanged() {
                Log.d("LogTag", "" + monitorClient.getPanel1().voltage);
                Log.d("LogTag", "" + monitorClient.getPanel1().current);
                Log.d("LogTag", "" + monitorClient.getPanel1().temperature);
                Log.d("LogTag", "" + monitorClient.getPanel1().light);
                Log.d("LogTag", "" + monitorClient.getPanel2().voltage);
                Log.d("LogTag", "" + monitorClient.getPanel2().current);
                Log.d("LogTag", "" + monitorClient.getPanel2().temperature);
                Log.d("LogTag", "" + monitorClient.getPanel2().light);
            }

            @Override
            public void onConnectionClosed() {
                Toast.makeText(MainActivity.this, "Connection broken", Toast.LENGTH_SHORT).show();
            }
        });
        monitorClient.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        monitorClient.close();
    }
}
