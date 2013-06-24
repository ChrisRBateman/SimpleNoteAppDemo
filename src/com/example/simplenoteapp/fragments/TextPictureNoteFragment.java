package com.example.simplenoteapp.fragments;

import java.io.Serializable;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.simplenoteapp.R;
import com.example.simplenoteapp.SimpleNoteAppActivity;
import com.example.simplenoteapp.database.NoteDatabase;
import com.example.simplenoteapp.model.Note;

/**
 * Create and update a text+picture note.
 */
public class TextPictureNoteFragment extends NoteFragmentBase implements View.OnClickListener {
	private Button mSaveButton;
	private EditText mNoteText;
	private Button mLoadButton;
	private TextView mUrlMessageView;
	private EditText mImageUrlText;
	private ImageView mUrlImage;
	private TextView mTitleView;
	
	private NoteDatabase mNoteDatabase;
	
	public static Fragment newInstance(String title, Note note) {
		Fragment f = new TextPictureNoteFragment();
        
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
        View view = inflater.inflate(R.layout.text_picture_note_fragment, container, false);
        
        mSaveButton = (Button)view.findViewById(R.id.save_button);
        mNoteText = (EditText)view.findViewById(R.id.text_picture_note_edit);
        mLoadButton = (Button)view.findViewById(R.id.load_button);
        mUrlMessageView = (TextView)view.findViewById(R.id.url_image_message);
        mImageUrlText = (EditText)view.findViewById(R.id.text_picture_image_url_edit);
        mUrlImage = (ImageView)view.findViewById(R.id.url_image);
        mTitleView = (TextView)view.findViewById(R.id.text_picture_note_title);
        
        String title = getArguments().getString("title");
        mTitleView.setText(title);
        
        Serializable ser = getArguments().getSerializable("note");
        if (ser instanceof Note) {
        	Note note = (Note)ser;
        	mNoteText.setText(note.getNotes());
        	mImageUrlText.setText(note.getUrl());
        }
        
        return view;
    }
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        SimpleNoteAppActivity activity = (SimpleNoteAppActivity)getActivity();
        mNoteDatabase = activity.getNoteDatabase();
        
        mSaveButton.setOnClickListener(this);
        mLoadButton.setOnClickListener(this);
    }
	
	@Override
    public void onClick(View v) {
        int id = v.getId();
        switch (v.getId()) {
        	case R.id.load_button: {
        		String url = mImageUrlText.getText().toString().trim();
        		new BitmapAsyncTask().execute(url);
        		break;
        	}
        		
        	case R.id.save_button: {
        		Serializable ser = getArguments().getSerializable("note");
            	Note note = null;
            	String s = mNoteText.getText().toString().trim();
            	String url = mImageUrlText.getText().toString().trim();
            	if (ser instanceof Note) {
            		note = (Note)ser;
            		note.setNotes(s);
            		note.setUrl(url);
            		new DatabaseAsyncTask().execute(SimpleNoteAppActivity.UPDATE_NOTE, note, id);
            	} else {
            		note = new Note();
            		note.setType(SimpleNoteAppActivity.TEXT_PICTURE_NOTE);
                	note.setNotes(s);
                	note.setUrl(url);
                	new DatabaseAsyncTask().execute(SimpleNoteAppActivity.ADD_NOTE, note, id);
            	}
        		break;
        	}
        }
    }
	
	/**
	 * Asynctask object to load url image in background.
	 */
    private class BitmapAsyncTask extends AsyncTask<String, Integer, Bitmap> {
    	Bitmap bmp = null;
    	
    	@Override
    	protected void onPreExecute() {
    		mUrlMessageView.setVisibility(View.VISIBLE);
    		mUrlMessageView.setText(getResources().getString(R.string.loading));
    		mUrlImage.setVisibility(View.GONE);
    	}
    	
    	@Override
        protected Bitmap doInBackground(String... params) {
    		try {
    			String urlString = params[0];
    			URL url = new URL(urlString);
    	        bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
    		} catch (Exception ex) {
            }
    		
    		return bmp;
    	}
    	
    	@Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            if (result != null) {
            	mUrlImage.setVisibility(View.VISIBLE);
            	mUrlImage.setImageBitmap(result);
            	mUrlMessageView.setVisibility(View.GONE);
            } else {
            	mUrlMessageView.setText(getResources().getString(R.string.load_error));
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