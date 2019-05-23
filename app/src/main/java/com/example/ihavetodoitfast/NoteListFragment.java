package com.example.ihavetodoitfast;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class NoteListFragment extends Fragment {
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mCrimeAdapter;
    private boolean mSubtitleVisible;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list,menu);
        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if(mSubtitleVisible){
            subtitleItem.setTitle(R.string.hide_total);
        } else {
            subtitleItem.setTitle(R.string.show_total);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.new_crime:
                Note note = new Note();
                NoteBook noteBook = NoteBook.get(getActivity());
                noteBook.addNote(note);
                Intent intent = NotePageActivity.newIntent(getActivity(), note.getID());
                startActivity(intent);
                return true;
            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private void updateSubtitle(){
        NoteBook noteBook = NoteBook.get(getActivity());
        int crimeCount = noteBook.getNotes().size();
        String subtitle = getString(R.string.subtitle_format,crimeCount);

        if(!mSubtitleVisible){
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_list,container,false);
        mCrimeRecyclerView = (RecyclerView)view.findViewById(R.id.note_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
        updateSubtitle();
    }

    private void updateUI(){
        NoteBook noteBook = NoteBook.get(getActivity());
        List<Note> notes = noteBook.getNotes();
        if (mCrimeAdapter == null){
            mCrimeAdapter = new CrimeAdapter(notes);
            mCrimeRecyclerView.setAdapter(mCrimeAdapter);
        }
        else {
            mCrimeAdapter.setNotes(notes);
            mCrimeAdapter.notifyDataSetChanged();
        }


    }

    public class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private Note mNote;
        private ImageView mSolvedImageView;

        public CrimeHolder(LayoutInflater inflater,ViewGroup parent){
            super(inflater.inflate(R.layout.list_item_note,parent,false));
            itemView.setOnClickListener(this);
            mTitleTextView = (TextView) itemView.findViewById(R.id.note_title);
            mDateTextView = (TextView)itemView.findViewById(R.id.note_date);
            mSolvedImageView = (ImageView) itemView.findViewById(R.id.note_solved);
        }


        public void bind(Note note){
            mNote = note;
            mTitleTextView.setText(mNote.getTitle());
            mDateTextView.setText(mNote.getDate().toString());
            mSolvedImageView.setVisibility(note.isSolved()? View.VISIBLE:View.GONE);
        }

        @Override
        public void onClick(View v) {
            Intent intent = NotePageActivity.newIntent(getActivity(), mNote.getID());
            startActivity(intent);
        }
    }

    public class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder>{
        private List<Note> mNotes;

        public CrimeAdapter(List<Note> notes){
            mNotes = notes;
        }

        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return  new CrimeHolder(layoutInflater,viewGroup);
        }

        @Override
        public void onBindViewHolder(@NonNull CrimeHolder crimeHolder, int i) {
            Note note = mNotes.get(i);
            crimeHolder.bind(note);
        }

        @Override
        public int getItemCount() {
            return mNotes.size();
        }

        public void setNotes(List<Note> notes){
            mNotes = notes;
        }
    }
}
