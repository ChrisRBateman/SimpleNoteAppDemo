package com.example.simplenoteapp.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.example.simplenoteapp.SimpleNoteAppActivity;

/**
 * Base class for text, log, text+pic note fragments.
 */
public abstract class NoteFragmentBase extends Fragment { 
	public static final String TAG = SimpleNoteAppActivity.TAG;
	
	protected FragmentActionListener mListener;
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (FragmentActionListener)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement FragmentActionListener");
        }
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}