package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import database.NoteSchema.NoteTable;

public class NoteBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "NoteBase.db";
    public NoteBaseHelper(Context context){
        super(context,DATABASE_NAME,null,VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+ NoteTable.NAME + "(" +
                "_id integer primary key autoincrement,"+
                NoteTable.Cols.UUID+","+
                NoteTable.Cols.TITLE+","+
                NoteTable.Cols.DATE +","+
                NoteTable.Cols.SOLVED+","+
                NoteTable.Cols.RELATED_PERSON +
                ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
