package com.example.ihavetodoitfast;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import database.NoteBaseHelper;
import database.NoteCursorWrapper;
import database.NoteSchema;

public class NoteBook {
    private static NoteBook sNoteBook;
    private Context mContext;
    private SQLiteDatabase mDatabase;


    public static NoteBook get(Context context){
        if(sNoteBook == null){
            sNoteBook = new NoteBook(context);
        }
        return sNoteBook;
    }

    private NoteBook(Context context){
        mContext = context.getApplicationContext();
        mDatabase = new NoteBaseHelper(mContext).getWritableDatabase();
    }

    public List<Note> getNotes(){
        List<Note> notes = new ArrayList<>();
        NoteCursorWrapper cursorWrapper = queryNotes(null,null);
        try{
            cursorWrapper.moveToFirst();
            while (!cursorWrapper.isAfterLast()){
                notes.add(cursorWrapper.getNote());
                cursorWrapper.moveToNext();
            }
        }finally {
            cursorWrapper.close();
        }
        return notes;
    }
    public Note getNote(UUID id){
        NoteCursorWrapper cursorWrapper = queryNotes(
                NoteSchema.NoteTable.Cols.UUID + " = ?",
                new String[]{id.toString()}
        );
        try {
            if(cursorWrapper.getCount()==0){
                return null;
            }
            cursorWrapper.moveToFirst();
            return cursorWrapper.getNote();
        }finally {
            cursorWrapper.close();
        }
    }

    private NoteCursorWrapper queryNotes(String whereClause, String[] whereAgrs){
        Cursor cursor = mDatabase.query(
                NoteSchema.NoteTable.NAME,
                null,
                whereClause,
                whereAgrs,
                null,
                null,
                null
        );
        return new NoteCursorWrapper(cursor);
    }

    public void addNote(Note note){
        ContentValues values = getContentValues(note);
        mDatabase.insert(NoteSchema.NoteTable.NAME,null,values);
    }

    public void updateNote(Note note){
        String uuidString = note.getID().toString();
        ContentValues values = getContentValues(note);
        mDatabase.update(NoteSchema.NoteTable.NAME,values, NoteSchema.NoteTable.Cols.UUID + " = ? ",
                new String[]{uuidString});
    }

    public void deleteNote(Note note){
        mDatabase.delete(NoteSchema.NoteTable.NAME,NoteSchema.NoteTable.Cols.UUID + " = ?",
                new String[]{note.getID().toString()});
    }

    private static ContentValues getContentValues(Note note){
        ContentValues values = new ContentValues();
        values.put(NoteSchema.NoteTable.Cols.UUID, note.getID().toString());
        values.put(NoteSchema.NoteTable.Cols.TITLE, note.getTitle());
        values.put(NoteSchema.NoteTable.Cols.DATE, note.getDate().getTime());
        values.put(NoteSchema.NoteTable.Cols.SOLVED, note.isSolved()?1:0);
        values.put(NoteSchema.NoteTable.Cols.RELATED_PERSON,note.getRelatedPerson());
        values.put(NoteSchema.NoteTable.Cols.DETAIL,note.getDetail());

        return values;
    }

    public File getPhotoFile(Note note){
        File filesDir = mContext.getFilesDir();
        return new File(filesDir,note.getPhotoFileName());
    }

}
