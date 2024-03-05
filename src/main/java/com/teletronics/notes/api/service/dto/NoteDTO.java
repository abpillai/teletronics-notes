package com.teletronics.notes.api.service.dto;

import java.time.Instant;

import lombok.Data;

@Data
public class NoteDTO {

	private final String id;
	private final String title;
	private final String text;
	private final Instant createdDate;
	
}
