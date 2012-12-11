package com.cos.read_it_later;

import android.R;
import android.app.ListActivity;
import android.os.Bundle;

public class PendingItemsActivity extends ListActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(R.id.content, new PendingItemsFragment()).commit();
    }
}
