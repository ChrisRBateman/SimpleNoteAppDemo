package com.example.simplenoteapp.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.simplenoteapp.R;
import com.example.simplenoteapp.SimpleNoteAppActivity;
import com.example.simplenoteapp.database.NoteDatabase;
import com.example.simplenoteapp.model.Note;

/**
 * This fragment manages the list of notes and the actions from the New
 * Open and Delete buttons.
 */
public class StartFragment extends ListFragment implements View.OnClickListener {
	public static final String TAG = SimpleNoteAppActivity.TAG;
	
	private Button mNewButton;
	private Button mOpenButton;
	private Button mDeleteButton;
	
	private NoteDatabase mNoteDatabase;
	
	private ArrayAdapter<Note> mNoteAdapter;
	
	private StartFragmentListener mListener;
    public interface StartFragmentListener {
        public void onAction(int action, Note note);
    }
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (StartFragmentListener)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement StartFragmentListener");
        }
    }
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mNoteAdapter = new ArrayAdapter<Note>(getActivity(),
                android.R.layout.simple_list_item_single_choice, new ArrayList<Note>());
        setListAdapter(mNoteAdapter);
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.start_fragment, container, false);
        
        mNewButton = (Button)view.findViewById(R.id.new_button);
        mOpenButton = (Button)view.findViewById(R.id.open_button);
        mDeleteButton = (Button)view.findViewById(R.id.delete_button);
        
        return view;
    }
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		ListView listView = getListView();
        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        SimpleNoteAppActivity activity = (SimpleNoteAppActivity)getActivity();
        mNoteDatabase = activity.getNoteDatabase();
        
        mNewButton.setOnClickListener(this);
        mOpenButton.setOnClickListener(this);
        mDeleteButton.setOnClickListener(this);
        
        new DatabaseAsyncTask().execute(SimpleNoteAppActivity.GET_ALL_NOTES);
    }
	
	@Override
    public void onClick(View v) {
        int id = v.getId();
        Note note = null;
        
        switch (id) {
        	case R.id.delete_button:
        		deleteNote();
        		return;
        		
        	case R.id.open_button:
        		note = getSelectedNote();
        		break;
        		
        	case R.id.save_button:
        		break;
        }
        
        if (mListener != null) {
        	mListener.onAction(id, note);
        }
    }
	
	/**
	 * Delete the selected note. Display confirmation dialog before deleting.
	 */
	private void deleteNote() {
		final Note note = getSelectedNote();
		
		if (note != null) {
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
	        Resources res = getResources();
	        String text = res.getString(R.string.confirm_delete_text);
	        alertDialog.setMessage(text);
	        alertDialog.setPositiveButton(res.getString(R.string.yes_text), new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog,int which) {
	        		new DatabaseAsyncTask().execute(SimpleNoteAppActivity.DELETE_NOTE, note);
	            }
	        });
	        alertDialog.setNegativeButton(res.getString(R.string.no_text), new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	                dialog.cancel();
	            }
	        });
	        alertDialog.show();
		}
	}
	
	/**
	 * Returns the selected note
	 * 
	 * @return selected note
	 */
	private Note getSelectedNote() {
		Note note = null;
		ListView listView = getListView();
		
		SparseBooleanArray array = listView.getCheckedItemPositions();
		int size = listView.getCount();
		int selected = -1;
		for (int i = 0; i < size; i++) {
			if (array.get(i)) {
				selected = i;
				break;
			}
		}
		if (selected >= 0) {
			ListAdapter la = listView.getAdapter();
			note = (Note)la.getItem(selected);
		}
		
		return note;
	}
	
	/**
	 * Asynctask object to perform database operations.
	 */
	private class DatabaseAsyncTask extends AsyncTask<Object, Integer, Object> {
		private class DatabaseResult {
			List<Note> notes;
			Note note;
	    }
    	
    	@Override
        protected Object doInBackground(Object... params) {
    		DatabaseResult dr = new DatabaseResult();
    		try {
    			Integer action = (Integer)params[0];
    			switch (action) {
    				case SimpleNoteAppActivity.GET_ALL_NOTES:
    					dr.notes = mNoteDatabase.getAllNotes();
    					break;
    				
    				case SimpleNoteAppActivity.DELETE_NOTE:
    					dr.note = (Note)params[1];
    					mNoteDatabase.deleteNote(dr.note);
    					break;
    			}
    			
    		} catch (Exception ex) {
            }
    		
    		return dr;
    	}
    	
    	@Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            
            DatabaseResult dr = (DatabaseResult)result;
            if (dr.notes != null) {
            	mNoteAdapter.clear();
            	for (Note note : dr.notes) {
            		mNoteAdapter.add(note);
            	}
            }
            if (dr.note != null) {
            	mNoteAdapter.remove(dr.note);
            }
            mNoteAdapter.notifyDataSetChanged();
    	}
    }
}
