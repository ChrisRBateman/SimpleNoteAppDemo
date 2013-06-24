package com.example.simplenoteapp.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.simplenoteapp.model.Note;

/**
 * Notes database class. Performs CRUD operations.
 */
public class NoteDatabase extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "notes.db";
    
    // Notes table name
    private static final String TABLE_NOTES = "notes";
    
    // Notes table column names
    private static final String KEY_ID = "id";
    private static final String KEY_TYPE = "type";
    private static final String KEY_NOTES = "notes";
    private static final String KEY_URL = "url";
    
    private SQLiteDatabase mDatabase = null;
	
	public NoteDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
	
	public void closeDB() {
        if ((mDatabase != null) && mDatabase.isOpen()) {
            mDatabase.close();
        }
        mDatabase = null;
        this.close();
    }
	
	private SQLiteDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = this.getWritableDatabase();
        }
        return mDatabase; 
    }
	
	@Override
    public void onCreate(SQLiteDatabase db) {
		String CREATE_NOTES_TABLE = "CREATE TABLE " + TABLE_NOTES + "("
                + KEY_ID + " INTEGER PRIMARY KEY," 
				+ KEY_TYPE + " INTEGER," 
                + KEY_NOTES + " TEXT," 
				+ KEY_URL + " TEXT" + ")";
		db.execSQL(CREATE_NOTES_TABLE);
    }
	
	@Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(db);
    }
	
	/**
	 * Add a note to database.
	 * 
	 * @param note the note to add
	 * @return note id
	 */
	public long addNote(Note note) {
        SQLiteDatabase db = getDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_TYPE, note.getType()); 
        values.put(KEY_NOTES, note.getNotes());
        values.put(KEY_URL, note.getUrl()); 
 
        long id = db.insert(TABLE_NOTES, null, values);
        
        return id;
    }
	
	/**
	 * Get a note from database.
	 * 
	 * @param id
	 * @return note or null if id not found
	 */
	public Note getNote(int id) {
		Note note = null;
        SQLiteDatabase db = getDatabase();
 
        Cursor cursor = db.query(TABLE_NOTES, new String[] { KEY_ID,
        		KEY_TYPE, KEY_NOTES, KEY_URL }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            note = new Note(Long.parseLong(cursor.getString(0)), Integer.parseInt(cursor.getString(1)), 
            		cursor.getString(2), cursor.getString(3));
            cursor.close();
        }
        
        return note;
    }
	
	/**
	 * Get all of the notes from database.
	 * 
	 * @return list of notes; may be empty
	 */
	public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<Note>();
        String selectQuery = "SELECT  * FROM " + TABLE_NOTES;
 
        SQLiteDatabase db = getDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        if (cursor.moveToFirst()) {
            do {
            	Note note = new Note();
            	note.setId(Long.parseLong(cursor.getString(0)));
            	note.setType(Integer.parseInt(cursor.getString(1)));
            	note.setNotes(cursor.getString(2));
            	note.setUrl(cursor.getString(3));
                notes.add(note);
            } while (cursor.moveToNext());
        }
        cursor.close();
 
        return notes;
    }
 
	/**
	 * Save modified note to database.
	 * 
	 * @param note the note that is updated
	 * @return the number of rows affected 
	 */
    public int updateNote(Note note) {
        SQLiteDatabase db = getDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_TYPE, note.getType()); 
        values.put(KEY_NOTES, note.getNotes());
        values.put(KEY_URL, note.getUrl()); 
        
        int rows = db.update(TABLE_NOTES, values, KEY_ID + " = ?",
                new String[] { String.valueOf(note.getId()) });
 
        return rows;
    }
 
    /**
     * Delete note from database.
     * 
     * @param note the note to delete
     */
    public void deleteNote(Note note) {
        SQLiteDatabase db = getDatabase();
        db.delete(TABLE_NOTES, KEY_ID + " = ?",
                new String[] { String.valueOf(note.getId()) });
    }
}