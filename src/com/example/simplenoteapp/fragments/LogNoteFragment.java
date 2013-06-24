package com.example.simplenoteapp.fragments;

import java.io.Serializable;
import java.util.Date;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.simplenoteapp.R;
import com.example.simplenoteapp.SimpleNoteAppActivity;
import com.example.simplenoteapp.database.NoteDatabase;
import com.example.simplenoteapp.model.Note;

/**
 * Create and update a log note.
 */
public class LogNoteFragment extends NoteFragmentBase implements View.OnClickListener {
	private Button mSaveButton;
	private EditText mLogText;
	private TextView mTitleView;
	
	private NoteDatabase mNoteDatabase;
	
	public static Fragment newInstance(String title, Note note) {
		Fragment f = new LogNoteFragment();
        
        Bundle args = new Bundle();
        args.putString("title", title);
        if (note != null) {
        	args.putSerializable("note", note);
        }
        f.setArguments(args);
        
        return f;
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.log_note_fragment, container, false);
        
        mSaveButton = (Button)view.findViewById(R.id.save_button);
        mLogText = (EditText)view.findViewById(R.id.log_note_edit);
        mTitleView = (TextView)view.findViewById(R.id.log_note_title);
        
        mLogText.setText(new Date().toString() + System.getProperty("line.separator"));
        
        String title = getArguments().getString("title");
        mTitleView.setText(title);
        
        Serializable ser = getArguments().getSerializable("note");
        if (ser instanceof Note) {
        	Note note = (Note)ser;
        	mLogText.setText(note.getNotes());
        }
        
        return view;
    }
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        SimpleNoteAppActivity activity = (SimpleNoteAppActivity)getActivity();
        mNoteDatabase = activity.getNoteDatabase();
        
        mSaveButton.setOnClickListener(this);
    }
	
	@Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.save_button) {
        	Serializable ser = getArguments().getSerializable("note");
        	Note note = null;
        	String s = mLogText.getText().toString().trim();
        	if (ser instanceof Note) {
        		note = (Note)ser;
        		note.setNotes(s);
        		new DatabaseAsyncTask().execute(SimpleNoteAppActivity.UPDATE_NOTE, note, id);
        	} else {
        		note = new Note();
        		note.setType(SimpleNoteAppActivity.LOG_NOTE);
            	note.setNotes(s);
            	new DatabaseAsyncTask().execute(SimpleNoteAppActivity.ADD_NOTE, note, id);
        	}
        }
    }
	
	/**
	 * Asynctask object to perform database operations.
	 */
	private class DatabaseAsyncTask extends AsyncTask<Object, Integer, Object> {
    	private class DatabaseResult {
        	int id;
        }
    	
    	@Override
        protected Object doInBackground(Object... params) {
    		DatabaseResult dr = new DatabaseResult();
    		try {
    			Integer action = (Integer)params[0];
    			switch (action) {
    				case SimpleNoteAppActivity.ADD_NOTE: {
    					Note note = (Note)params[1];
    					mNoteDatabase.addNote(note);
    					dr.id = (Integer)params[2];
    					break;
    				}
    					
    				case SimpleNoteAppActivity.UPDATE_NOTE: {
    					Note note = (Note)params[1];
    					mNoteDatabase.updateNote(note);
    					dr.id = (Integer)params[2];
    					break;
    				}
    			}
    			
    		} catch (Exception ex) {
            }
    		
    		return dr;
    	}
    	
    	@Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            
            DatabaseResult dr = (DatabaseResult)result;
            if ((dr.id == R.id.save_button) && (mListener != null)) {
            	mListener.onAction(dr.id, null);
            }
    	}
    }
}