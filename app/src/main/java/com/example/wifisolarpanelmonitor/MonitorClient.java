package com.example.wifisolarpanelmonitor;

public class MonitorClient {

    public static class SolarData {
        float voltage;
        float current;
        int temperature;
        int light;
    }

    public interface Listener {
        void onConnectionEstablished();
        void onDataChanged();
        void onConnectionClosed();
    }

    private SolarData panel1 = new SolarData();
    private SolarData panel2 = new SolarData();
    private Listener listener;

    public MonitorClient(String ip, int port) {
        new ReceiverThread(ip, port, new ReceiverThread.StatusListener() {
            private int i=0;
            @Override
            public void onConnectionEstablished() {
                listener.onConnectionEstablished();
            }

            @Override
            public void onData(String data) {
                switch (i) {
                    case 0: panel1.voltage = Float.valueOf(data); break;
                    case 1: panel1.current = Float.valueOf(data); break;
                    case 2: panel1.temperature = Integer.valueOf(data); break;
                    case 3: panel1.light = Integer.valueOf(data); break;
                    case 4: panel2.voltage = Float.valueOf(data); break;
                    case 5: panel2.current = Float.valueOf(data); break;
                    case 6: panel2.temperature = Integer.valueOf(data); break;
                    case 7: panel2.light = Integer.valueOf(data); break;
                }
                i++;
                if (i == 8) {
                    listener.onDataChanged();
                    i = 0;
                }
            }

            @Override
            public void onConnectionClosed() {
                listener.onConnectionClosed();
            }
        }).start();
    }

    public SolarData getPanel1() {
        return panel1;
    }

    protected void setPanel1(SolarData panel1) {
        this.panel1 = panel1;
    }

    public SolarData getPanel2() {
        return panel2;
    }

    protected void setPanel2(SolarData panel2) {
        this.panel2 = panel2;
    }

    public Listener getListener() {
        return listener;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }
}
