package com.cos.read_it_later.model;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.SyncStateContract;
import android.text.TextUtils;

public class Provider extends ContentProvider {
    static final String TABLE_NAME = "tbl_pending_items";
    private DBOpenHelper openHelper;
    private static final String PARAMETER_NOTIFY = "notify";
    private static final String AUTHORITY = "com.cos.read_it_later";

    @Override
    public boolean onCreate() {
        openHelper = new DBOpenHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: don't understand it
        SqlArguments args = new SqlArguments(uri, null, null);
        if (TextUtils.isEmpty(args.where)) {
            return "vnd.android.cursor.dir/" + args.table;
        }else{
            return "vnd.android.cursor.item/" + args.table;
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // TODO: don't understand it
        SqlArguments args = new SqlArguments(uri, selection, selectionArgs);
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(args.table);

        SQLiteDatabase db = openHelper.getWritableDatabase();
        Cursor result = queryBuilder.query(db, projection, args.where, args.args, null, null, sortOrder);
        result.setNotificationUri(getContext().getContentResolver(), uri);

        return result;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SqlArguments args = new SqlArguments(uri);

        SQLiteDatabase db = openHelper.getWritableDatabase();
        final long rowId = dbInsertAndCheck(openHelper, db, args.table, null, values);
        if (rowId <= 0) {
            return null;
        }

        uri = ContentUris.withAppendedId(uri, rowId);
        sendNotify(uri);

        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SqlArguments args = new SqlArguments(uri, selection, selectionArgs);

        SQLiteDatabase db = openHelper.getWritableDatabase();
        int count = db.delete(args.table, args.where, args.args);
        if (count > 0) {
            sendNotify(uri);
        }

        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SqlArguments args = new SqlArguments(uri, selection, selectionArgs);

        SQLiteDatabase db = openHelper.getWritableDatabase();
        int count = db.update(args.table, values, args.where, args.args);
        if (count > 0) {
            sendNotify(uri);
        }

        return count;
    }

    public static Uri getUri(boolean notify) {
        return getUri(-1, notify);
    }

    public static Uri getUri(long id, boolean notify) {
        return Uri.parse("content://" +
                AUTHORITY + "/" + TABLE_NAME
                + (id >= 0 ? ("/" + id) : "")
                + "?" + PARAMETER_NOTIFY + "=" + notify);
    }

    private static long dbInsertAndCheck(DBOpenHelper openHelper, SQLiteDatabase db, String table, String nullColumnHack, ContentValues values) {
        if (! values.containsKey(SyncStateContract.Columns._ID)) {
            throw new RuntimeException("Error: attempting to add item without specifying an id");
        }
        return db.insert(table, nullColumnHack, values);
    }

    private void sendNotify(Uri uri) {
        String notify = uri.getQueryParameter(PARAMETER_NOTIFY);
        if (notify == null || "true".equals(notify)) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
    }
}
