package com.example.ihavetodoitfast;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

public class NoteListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new NoteListFragment();
    }

    public static Intent newIntent(Context packageContext){
        Intent intent = new Intent(packageContext, NoteListActivity.class);
        return intent;
    }
}
