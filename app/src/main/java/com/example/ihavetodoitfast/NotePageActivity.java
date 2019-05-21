package com.example.ihavetodoitfast;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;
import java.util.UUID;

public class NotePageActivity extends AppCompatActivity {
    private static final String EXTRA_Note_ID = "com.example.ihavetodoitfast.note_id";

    private ViewPager mViewPager;
    private List<Note> mNotes;

    public static Intent newIntent(Context packageContext, UUID NoteID){
        Intent intent = new Intent(packageContext, NotePageActivity.class);
        intent.putExtra(EXTRA_Note_ID,NoteID);
        return intent;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_pager);
        UUID noteID = (UUID)getIntent().getSerializableExtra(EXTRA_Note_ID);

        mViewPager = (ViewPager)findViewById(R.id.activity_note_pager_view_pager);
        mNotes = NoteBook.get(this).getNotes();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int i) {
                return NoteFragment.newInstance(mNotes.get(i).getID());
            }

            @Override
            public int getCount() {
                return mNotes.size();
            }
        });
        for(int i = 0; i < mNotes.size(); i++){
            if(mNotes.get(i).getID().equals(noteID)){
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }
}
