package com.autobusi.notes.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.print.attribute.standard.DateTimeAtCompleted;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.autobusi.notes.db.model.Note;
import com.autobusi.notes.db.model.NoteRepository;


@RestController
@RequestMapping(value="/api/v1/notes/*")
public class NotesController {
	@Autowired
	private NoteRepository noteRepo;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@GetMapping(value="/")
	public ResponseEntity read(@Nullable @RequestParam String id, @Nullable @RequestParam String docId){
		if ((id == null || id.isEmpty()) && (docId == null || docId.isEmpty()) ){
			return ResponseEntity.badRequest().build();
		}
		
		if(id != null && !id.isEmpty()){
			Optional<Note> theNote = this.noteRepo.findById(id);
			if(theNote.isPresent()){
				JSONObject o = new JSONObject();
				try{
					o.put("id", theNote.get().getId());
					o.put("docId", theNote.get().getDocId());
					o.put("content", new JSONObject(theNote.get().getFormattedContent()));
					
				}catch(JSONException jEx){
					this.logger.error("error when reading note and transfer it to json to return", jEx);
				}
				return ResponseEntity.ok(o.toString());
			}else{
				return ResponseEntity.notFound().build();
			}
		}else{
			List<Note> theNotes = this.noteRepo.findByDocId(docId);
			if(theNotes != null && !theNotes.isEmpty()){
				JSONArray notes = new JSONArray();
				for (Note n : theNotes){
					try{
						JSONObject o = new JSONObject();
						o.put("id", n.getId());
						o.put("docId", n.getDocId());
						o.put("content", new JSONObject(n.getFormattedContent()));
						notes.put(o);
					}catch(JSONException jEx){
						this.logger.error("error when reading note and transfer it to json to return", jEx);
						continue;
					}
				}
				return ResponseEntity.ok(notes.toString());
			}else{
				return ResponseEntity.notFound().build();
			}
		}
	}
	
	@PostMapping(value="/")
	public ResponseEntity manipulate(@RequestBody String jsonData){
		if (jsonData == null || jsonData.isEmpty()){
			return ResponseEntity.noContent().build();
		}
		
		try {
			jsonData = URLDecoder.decode(jsonData, "UTF-8");
			JSONObject noteJson = new JSONObject(jsonData);
			if(!noteJson.has("docId")){
				return ResponseEntity.badRequest().body("missing doc id");
			}
			if(noteJson.has("id") && !noteJson.getString("id").isEmpty()){
				Note result = this.updateNote(noteJson.getString("id"),noteJson.getString("docId"),noteJson.getJSONObject("content"));
				if( result != null){
					return ResponseEntity.ok(result.getId());
				};
			}else{
				Note result = this.createNote(noteJson.getString("docId"),noteJson.getJSONObject("content") );
				if(result != null){
					return ResponseEntity.ok(result);
				};
			}
			return ResponseEntity.internalServerError().body("fail to create/save content");
		}  catch (UnsupportedEncodingException e1) {
			this.logger.error("fail to decode json data, {}", e1);
			return ResponseEntity.internalServerError().build();
		}catch (JSONException e) {
			this.logger.error("processing note create/update fail, {}", e);
			return ResponseEntity.internalServerError().build();
		}
	}
	
	private Note createNote(String docId,JSONObject contentJson){
		Note note = new Note();
		note.setId(UUID.randomUUID().toString());
		note.setDocId(docId);
		note.setFormattedContent(contentJson.toString());
		note.setLastUpdate(new Timestamp(new Date().getTime()));
		note = this.noteRepo.save(note);
		return note;
	}
	
	private Note updateNote(String id,String docId, JSONObject contentJson){
		Optional<Note> optNote = this.noteRepo.findById(id);
		if(!optNote.isPresent()){
			return null;
		}
		Note note = optNote.get();
		note.setDocId(docId);
		note.setFormattedContent(contentJson.toString());
		note.setLastUpdate(new Timestamp(new Date().getTime()));
		note = this.noteRepo.save(note);
		return note;
	}
}
