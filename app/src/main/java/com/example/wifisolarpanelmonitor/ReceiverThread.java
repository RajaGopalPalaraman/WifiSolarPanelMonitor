package com.example.wifisolarpanelmonitor;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.net.Socket;

public class ReceiverThread extends Thread {

    private static final int RECEIVE_WHAT = 0;
    private static final int DISCONNECT_WHAT = 1;

    private final String ip;
    private final int port;
    private final Handler mainThreadHandler;
    private final StatusListener statusListener;

    private EdotWifiStreamReader wifiStreamReader;
    private Handler localHandler;

    public interface StatusListener {
        void onConnectionEstablished();
        void onData(String data);
        void onConnectionClosed();
    }

    public ReceiverThread(String ip, int port, StatusListener statusListener) {
        this.ip = ip;
        this.port = port;
        mainThreadHandler = new Handler(Looper.getMainLooper());
        this.statusListener = statusListener;
    }

    @Override
    public void run() {
        super.run();
        try {
            final Socket socket = new Socket(ip, port);
            wifiStreamReader = new EdotWifiStreamReader(socket.getInputStream());
            Looper.prepare();
            localHandler = new Handler(Looper.myLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what) {
                        case RECEIVE_WHAT:
                            receiveHandler();
                            break;
                        case DISCONNECT_WHAT:
                            try {
                                wifiStreamReader.close();
                                socket.close();
                            } catch (IOException ignored) { }
                            mainThreadHandler.post(statusListener::onConnectionClosed);
                            getLooper().quit();
                            break;
                    }
                }
            };
            mainThreadHandler.post(statusListener::onConnectionEstablished);
            localHandler.sendEmptyMessage(RECEIVE_WHAT);
            Looper.loop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        localHandler.removeMessages(RECEIVE_WHAT);
        localHandler.sendEmptyMessage(DISCONNECT_WHAT);
    }

    protected void receiveHandler() {
        String data = wifiStreamReader.getNext();
        if (data == null) {
            close();
        } else if (!data.isEmpty()) {
            mainThreadHandler.post(() -> statusListener.onData(data));
            localHandler.sendEmptyMessage(RECEIVE_WHAT);
        }
    }

}
