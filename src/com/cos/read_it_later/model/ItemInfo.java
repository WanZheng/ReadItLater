package com.cos.read_it_later.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

public class ItemInfo implements BaseColumns {
    public static final String URL = "url";
    public static final String TITLE = "title";
    public static final String COMMENT = "comment";

    public long id = -1;
    public String url;
    public String title;
    public String comment;

    public void onAddToDatabase(ContentValues values) {
        if (id > 0) {
            values.put(_ID, id);
        }
        values.put(URL, url);
        values.put(TITLE, title);
        values.put(COMMENT, comment);
    }

    public static ItemInfo createFromCursor(Cursor c) {
        ItemInfo item = new ItemInfo();
        item.id = c.getLong(c.getColumnIndex(_ID));
        item.url = c.getString(c.getColumnIndex(URL));
        item.title = c.getString(c.getColumnIndex(TITLE));
        item.comment = c.getString(c.getColumnIndex(COMMENT));

        return item;
    }
}
