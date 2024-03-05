package com.teletronics.notes.api.domain.i18n;

import org.springframework.data.mongodb.core.mapping.Field;

import com.teletronics.notes.api.domain.enumeration.SupportedLocale;

import lombok.Data;

@Data
public class NameI18n {

	@Field("name")
	private String name;
	
	@Field("language")
	private SupportedLocale language;
}
