package com.example.wifisolarpanelmonitor;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public final class EdotWifiStreamReader implements Closeable {

    private static final char DELIMITER = '$';

    private final InputStream inputStream;

    public EdotWifiStreamReader(@NonNull InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Nullable
    public String getNext()
    {
        StringBuilder s = new StringBuilder();
        int tempInt;
        char tempChar;
        while (true)
        {
            try {
                tempInt = inputStream.read();
                tempChar = (char) tempInt;
                Log.d("Debugger","Single: "+tempChar);
                if (tempInt == -1) {
                    break;
                }
                else if (tempChar == DELIMITER) {
                    Log.d("Debugger", s.toString());
                    return s.toString();
                }
                else {
                    s.append(tempChar);
                }
            } catch (IOException e) {
                Log.d("Debugger","error",e);
            }

        }
        return null;
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }
}
