package com.teletronics.notes.api.domain;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.teletronics.notes.api.domain.i18n.NoteI18n;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@Document(collection = "notes")
public class Note extends AuditableEntity {

	@Id
	@Field("_id")
	private ObjectId id;
	


	@Indexed
	@Field("note_tags")
	private List<ObjectId> noteTags;
	
	@Field("i18n_data")
	private List<NoteI18n> i18nData;

}
