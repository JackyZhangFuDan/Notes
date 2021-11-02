package com.autobusi.notes.db.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "notes")
public class Note {

	@Id
	@Column(name="id")
	private String id;
	@Column(name="doc_id")
	private String docId;
	@Column(name="formatted_content")
	private String formattedContent;
	@Column(name="text_content")
	private String textContent;
	@Column(name="last_update_ts")
	private Timestamp lastUpdate;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDocId() {
		return docId;
	}
	public void setDocId(String docId) {
		this.docId = docId;
	}
	public String getFormattedContent() {
		return formattedContent;
	}
	public void setFormattedContent(String formattedContent) {
		this.formattedContent = formattedContent;
	}
	public String getTextContent() {
		return textContent;
	}
	public void setTextContent(String textContent) {
		this.textContent = textContent;
	}
	public Timestamp getLastUpdate() {
		return lastUpdate;
	}
	public void setLastUpdate(Timestamp lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
	
}
