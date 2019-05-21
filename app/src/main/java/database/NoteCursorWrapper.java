package database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.ihavetodoitfast.Note;

import java.util.Date;
import java.util.UUID;

public class NoteCursorWrapper extends CursorWrapper {
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public NoteCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Note getNote(){
        String uuidString = getString(getColumnIndex(NoteSchema.NoteTable.Cols.UUID));
        String title = getString(getColumnIndex(NoteSchema.NoteTable.Cols.TITLE));
        long date = getLong(getColumnIndex(NoteSchema.NoteTable.Cols.DATE));
        int isSolved = getInt(getColumnIndex(NoteSchema.NoteTable.Cols.SOLVED));

        Note note = new Note(UUID.fromString(uuidString));
        note.setTitle(title);
        note.setDate(new Date(date));
        note.setSolved(isSolved!=0);

        return note;
    }
}
