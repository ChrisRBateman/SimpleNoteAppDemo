package com.example.simplenoteapp.model;

import java.io.Serializable;

/**
 * Note class. Represents each note from database.
 */
public class Note implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private long id;
    private int type; // text, log or text+pic
    private String notes;
    private String url;
    
    public Note() {      
    }
    
    public Note(long id, int type, String text, String url) {
        this.id = id;
        this.type = type;
    }

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	@Override
    public String toString() {
		if (notes.length() > 20) {
			return notes.substring(0, 20) + "...";
		} else {
			return notes;
		}
    }
}