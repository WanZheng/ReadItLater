package com.cos.read_it_later;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import com.cos.read_it_later.model.ItemInfo;
import com.cos.read_it_later.model.Provider;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SendService extends Service {
    private Task task;
    private ArrayList<BasicNameValuePair> postParams = new ArrayList<BasicNameValuePair>(5);
    private AndroidHttpClient http;
    private HttpPost post;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (task == null) {
            task = new Task();
            task.execute();
        }
        return START_NOT_STICKY;
    }

    private class Task extends AsyncTask<Object, Object, String> {
        @Override
        protected void onPreExecute() {
            postParams.add(new BasicNameValuePair("username", "xxx"));
            postParams.add(new BasicNameValuePair("password", "xxx"));
        }

        @Override
        protected String doInBackground(Object... objects) {
            while (true) {
                List<ItemInfo> items = fetchPendingItems();
                if (items == null) {
                    break;
                }

                Log.d(SendActivity.TAG, "got " + items.size() + " pending items");
                if (! sendItems(items)) {
                    return "failed";
                }
            }
            return "done";
        }

        @Override
        protected void onPostExecute(String message) {
            Toast.makeText(SendService.this, message, Toast.LENGTH_LONG).show();
            stopSelf();
        }
    }

    private boolean sendItems(List<ItemInfo> items) {
        boolean success = true;
        http = AndroidHttpClient.newInstance("ReadItLater");
        post = new HttpPost("https://www.instapaper.com/api/add");

        for (ItemInfo item : items) {
            if (! sendItem(item)) {
                success = false;
            }
        }

        http.close();
        return success;
    }

    private boolean sendItem(ItemInfo item) {
        Log.d(SendActivity.TAG, "sending " + item);

        for (int i=postParams.size()-1; i>=2; i--) {
            postParams.remove(i);
        }
        postParams.add(new BasicNameValuePair("url", item.url));
        postParams.add(new BasicNameValuePair("title", item.title));
        if (! item.comment.isEmpty()) {
            postParams.add(new BasicNameValuePair("comment", item.comment));
        }
        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postParams);
            post.setEntity(entity);
            StatusLine statusLine = http.execute(post, responseHandler);
            Log.d(SendActivity.TAG, statusLine.toString());

            if (statusLine.getStatusCode() == 201) {
                // TODO: remove item from db
                return true;
            }else{
                // TODO: notify user
                return false;
            }
        } catch (Exception e) {
            // TODO:
            throw new RuntimeException(e);
        }
    }

    private List<ItemInfo> fetchPendingItems() {
        ContentResolver resolver = getContentResolver();
        Cursor c = resolver.query(Provider.getUri(false), null, null, null, null);
        try {
            int count = c.getCount();
            if (count <= 0) {
                return null;
            }

            ArrayList<ItemInfo> items = new ArrayList<ItemInfo>(count);
            while (c.moveToNext()) {
                ItemInfo item = ItemInfo.createFromCursor(c);
                items.add(item);
            }
            return items;
        }finally {
            c.close();
        }
    }

    private final ResponseHandler<StatusLine> responseHandler = new ResponseHandler<StatusLine>() {
        @Override
        public StatusLine handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
            return response.getStatusLine();
        }
    };
}