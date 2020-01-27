package com.example.wifisolarpanelmonitor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private MonitorClient monitorClient;

    private TextView voltageView;
    private TextView currentView;
    private TextView temperatureView;
    private TextView lightView;

    private boolean panel1 = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
        String ip = HelperUtil.getWifiServerIP(getApplication());
        if (ip == null) {
            Toast.makeText(this, "Wifi not connected", Toast.LENGTH_SHORT).show();
            voltageView.setText(String.valueOf(0f));
            currentView.setText(String.valueOf(0f));
            temperatureView.setText(String.valueOf(0));
            lightView.setText(String.valueOf(0));
            return;
        }
        monitorClient = new MonitorClient(ip, 80);
        monitorClient.setListener(new MonitorClient.Listener() {
            @Override
            public void onConnectionEstablished() {
                Toast.makeText(MainActivity.this, "Connection established", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDataChanged() {
                MonitorClient.SolarData solarData = panel1 ? monitorClient.getPanel1() : monitorClient.getPanel2();
                voltageView.setText(String.valueOf(solarData.voltage));
                currentView.setText(String.valueOf(solarData.current));
                temperatureView.setText(String.valueOf(solarData.temperature));
                lightView.setText(String.valueOf(solarData.light));
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
        if (monitorClient != null) {
            monitorClient.close();
        }
    }

    private void bindViews() {
        voltageView = findViewById(R.id.voltage);
        currentView = findViewById(R.id.current);
        temperatureView = findViewById(R.id.temperature);
        lightView = findViewById(R.id.light);

        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.panels, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                panel1 = position == 0;
                if (monitorClient != null) {
                    MonitorClient.Listener listener = monitorClient.getListener();
                    if (listener != null) {
                        monitorClient.getListener().onDataChanged();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

}
