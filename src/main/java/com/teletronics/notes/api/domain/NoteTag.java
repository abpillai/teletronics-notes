package com.teletronics.notes.api.domain;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.teletronics.notes.api.domain.i18n.NameI18n;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@Document(collection = "tags")
public class NoteTag extends AuditableEntity {
	
	@Id
	@Field("_id")
	private ObjectId id;
	
	@Field("i18n_data")
	private List<NameI18n> i18nData;

}
