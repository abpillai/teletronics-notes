package com.teletronics.notes.api.domain.i18n;

import java.time.Instant;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import com.teletronics.notes.api.domain.enumeration.SupportedLocale;

import lombok.Data;

@Data
public class NoteI18n {
	
	@Field("title")
	private String title;

	@Field("text")
	private String text;

	
	@Indexed
	@Field("language")
	private SupportedLocale language;

}
