package com.example.simplenoteapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.example.simplenoteapp.database.NoteDatabase;
import com.example.simplenoteapp.fragments.FragmentActionListener;
import com.example.simplenoteapp.fragments.LogNoteFragment;
import com.example.simplenoteapp.fragments.StartFragment;
import com.example.simplenoteapp.fragments.TextNoteFragment;
import com.example.simplenoteapp.fragments.TextPictureNoteFragment;
import com.example.simplenoteapp.model.Note;

/**
 * SimpleNoteApp launch activity 
 */
public class SimpleNoteAppActivity extends FragmentActivity implements StartFragment.StartFragmentListener, 
																	   FragmentActionListener {
	public static final String TAG = "SimpleNoteAppTag";
	
	// Note types
	public static final int TEXT_NOTE = 100;
	public static final int LOG_NOTE = 101;
	public static final int TEXT_PICTURE_NOTE = 102;
	
	// Database actions
	public static final int ADD_NOTE = 1;
	public static final int GET_NOTE = 2;
	public static final int UPDATE_NOTE = 3;
	public static final int DELETE_NOTE = 4;
	public static final int GET_ALL_NOTES = 5;
	
	private String[] newNoteTitles = new String[3];
	private String[] updateNoteTitles = new String[3];
	
	private NoteDatabase mNoteDatabase;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_simple_note_app);
		
		mNoteDatabase = new NoteDatabase(this);
		
		if (savedInstanceState == null) {
            Fragment newFragment = new StartFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.fragment_container, newFragment, "startFragmentTag").commit();
        }
		
		newNoteTitles[0] = getResources().getString(R.string.new_text_note_title);
		newNoteTitles[1] = getResources().getString(R.string.new_log_note_title);
		newNoteTitles[2] = getResources().getString(R.string.new_text_pic_note_title);
		
		updateNoteTitles[0] = getResources().getString(R.string.update_text_note_title);
		updateNoteTitles[1] = getResources().getString(R.string.update_log_note_title);
		updateNoteTitles[2] = getResources().getString(R.string.update_text_pic_note_title);
	}
	
	@Override
    protected void onDestroy() {
        super.onDestroy();
        if (mNoteDatabase != null) {
        	mNoteDatabase.closeDB();
        }
    }
	
	/**
	 * Returns database.
	 * 
	 * @return the database object.
	 */
	public NoteDatabase getNoteDatabase() {
        return mNoteDatabase;
    }
	
	/**
	 * Respond to actions from start/note screens.
	 * 
	 * @param action the action required
	 * @param note data needed for the action; could be null
	 */
	public void onAction(int action, Note note) {
		switch (action) {
			case R.id.new_button:
				showNewNoteChoices();
				break;
				
			case R.id.open_button:
				showUpdateFragment(note);
				break;
				
			case R.id.save_button:
				getSupportFragmentManager().popBackStack();
				break;
		}
	}
	
	/**
	 * Popup a dialog to show new note choices.
	 */
	private void showNewNoteChoices() {
		final CharSequence[] items = {
				getResources().getString(R.string.text_note_type), 
				getResources().getString(R.string.log_note_type), 
				getResources().getString(R.string.text_pic_note_type) };

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Select type of note");
		builder.setItems(items, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
		    	loadNoteFragment(item, newNoteTitles, null);
		    }
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	/**
	 * Display the correct update fragment depending on the note.
	 * 
	 *  @param note the note object
	 */
	private void showUpdateFragment(Note note) {
		if (note != null) {
			loadNoteFragment(note.getType() - TEXT_NOTE, updateNoteTitles, note);
		}
	}
	
	/**
	 * Loads fragment to create new note.
	 * 
	 *  @param type the type of note to create 
	 *  @param title titles for the fragments
	 *  @param note the note object for doing updates
	 */
	private void loadNoteFragment(int type, String title[], Note note) {
		Fragment fragment = null;
		String tag = "";
		switch (type) {
			case 0:
				fragment = TextNoteFragment.newInstance(title[0], note);
				tag = "textNoteFragmentTag";
				break;
			
			case 1:
				fragment = LogNoteFragment.newInstance(title[1], note);
				tag = "logNoteFragmentTag";
				break;
				
			case 2:
				fragment = TextPictureNoteFragment.newInstance(title[2], note);
				tag = "textPictureNoteFragmentTag";
				break;
		}
		if (fragment != null) {
			FragmentManager fragmentManager = getSupportFragmentManager();
		    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		    fragmentTransaction.replace(R.id.fragment_container, fragment, tag);
		    fragmentTransaction.addToBackStack(null);
		    fragmentTransaction.commit(); 
		}
	}
}
