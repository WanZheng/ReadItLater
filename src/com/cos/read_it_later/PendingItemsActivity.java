package com.cos.read_it_later;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;
import com.cos.read_it_later.model.ItemInfo;
import com.cos.read_it_later.model.Provider;

public class PendingItemsActivity extends ListActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ContentResolver resolver = getContentResolver();
        Cursor c = resolver.query(Provider.getUri(false), null, null, null, null);
        startManagingCursor(c);
        setListAdapter(new SimpleCursorAdapter(this, R.layout.item_view, c,
                new String[] {ItemInfo.TITLE, ItemInfo.URL, ItemInfo.COMMENT},
                new int[] {R.id.title, R.id.url, R.id.comment}));
    }
}
