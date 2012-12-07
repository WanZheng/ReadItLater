package com.cos.read_it_later;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.cos.read_it_later.model.ItemInfo;
import com.cos.read_it_later.model.Provider;

public class SendActivity extends Activity implements View.OnClickListener {
    public static final String TAG = "ReadItLater";
    private EditText titleEditor;
    private EditText urlEditor;
    private EditText commentEditor;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        button = (Button) findViewById(R.id.send);
        button.setOnClickListener(this);

        titleEditor = (EditText) findViewById(R.id.title);
        urlEditor = (EditText) findViewById(R.id.url);
        commentEditor = (EditText) findViewById(R.id.comment);

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
    }

    private void handleSendText(Intent intent) {
        String url = intent.getStringExtra(Intent.EXTRA_TEXT);
        String title = intent.getStringExtra(Intent.EXTRA_SUBJECT);
        // TODO: extra: share_screenshot: android.graphics.Bitmap@415b0bf8

        titleEditor.setText(title);
        urlEditor.setText(url);
    }

    @Override
    public void onClick(View view) {
        AsyncTask<Object, Object, Object> task = new AsyncTask<Object, Object, Object>() {
            private ItemInfo info = new ItemInfo();

            @Override
            protected void onPreExecute() {
                titleEditor.setEnabled(false);
                urlEditor.setEnabled(false);
                commentEditor.setEnabled(false);

                button.setEnabled(false);
                button.setText(R.string.sending);

                info.url = ((TextView) urlEditor).getText().toString();
                info.title = ((TextView) titleEditor).getText().toString();
                info.comment = ((TextView) commentEditor).getText().toString();
            }

            @Override
            protected Object doInBackground(Object... objects) {
                ContentResolver resolver = getContentResolver();
                ContentValues values = new ContentValues();
                info.onAddToDatabase(values);
                resolver.insert(Provider.getUri(false), values);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                startService(new Intent(SendActivity.this, SendService.class));
                finish();
            }
        };
        task.execute();
    }
}