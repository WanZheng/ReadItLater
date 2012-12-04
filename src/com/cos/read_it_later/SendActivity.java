package com.cos.read_it_later;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SendActivity extends Activity implements View.OnClickListener {
    public static final String TAG = "ReadItLater";
    private boolean committed = false;
    private SendService.LocalBinder service;
    private EditText title;
    private EditText url;
    private EditText comment;
    private Button button;

    private static final int MSG_SEND = 0x01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        button = (Button) findViewById(R.id.send);
        button.setOnClickListener(this);

        title = (EditText) findViewById(R.id.title);
        url = (EditText) findViewById(R.id.url);
        comment = (EditText) findViewById(R.id.comment);

        bindService(new Intent(this, SendService.class), serviceConnection, Context.BIND_AUTO_CREATE);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unbindService(serviceConnection);
    }

    private void handleSendText(Intent intent) {
        String url = intent.getStringExtra(Intent.EXTRA_TEXT);
        String title = intent.getStringExtra(Intent.EXTRA_SUBJECT);
        // TODO: extra: share_screenshot: android.graphics.Bitmap@415b0bf8

        this.title.setText(title);
        this.url.setText(url);
    }

    @Override
    public void onClick(View view) {
        title.setEnabled(false);
        url.setEnabled(false);
        comment.setEnabled(false);

        button.setEnabled(false);
        button.setText(R.string.sending);

        committed = true;

        handler.sendEmptyMessage(MSG_SEND);
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            service = (SendService.LocalBinder) iBinder;
            handler.sendEmptyMessage(MSG_SEND);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            service = null;
        }
    };

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SEND:
                    if (service == null || !committed) {
                        return;
                    }
                    service.send(((TextView)title).getText(), ((TextView)url).getText(), ((TextView)comment).getText());
                    finish();
                    break;
            }
        }
    };
}