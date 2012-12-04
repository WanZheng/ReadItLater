package com.cos.read_it_later;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class SendService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class LocalBinder extends Binder {
        public void send(CharSequence title, CharSequence url, CharSequence comment) {
            doSend(title, url, comment);
        }
    }

    private void doSend(CharSequence title, CharSequence url, CharSequence comment) {
        Log.d(SendActivity.TAG, "send: " + title + ", " + url + ", " + comment);
    }

    private final LocalBinder binder = new LocalBinder();
}