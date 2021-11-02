package com.autobusi.notes.db.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface NoteRepository extends CrudRepository<Note, String> {
	public List<Note> findByDocId(String docId);
}
