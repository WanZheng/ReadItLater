package com.cos.read_it_later.model;

import android.content.ContentUris;
import android.net.Uri;
import android.text.TextUtils;

class SqlArguments {
    public final String table;
    public final String where;
    public final String[] args;

    SqlArguments(Uri url, String where, String[] args) {
        if (url.getPathSegments().size() == 1) {
            this.table = url.getPathSegments().get(0);
            this.where = where;
            this.args = args;
        } else if (url.getPathSegments().size() != 2) {
            throw new IllegalArgumentException("Invalid URI: " + url);
        } else if (!TextUtils.isEmpty(where)) {
            throw new UnsupportedOperationException("WHERE clause not supported: " + url);
        } else {
            this.table = url.getPathSegments().get(0);
            this.where = "_id=" + ContentUris.parseId(url);
            this.args = null;
        }
    }

    SqlArguments(Uri url) {
        if (url.getPathSegments().size() == 1) {
            table = url.getPathSegments().get(0);
            where = null;
            args = null;
        } else {
            throw new IllegalArgumentException("Invalid URI: " + url);
        }
    }
}
