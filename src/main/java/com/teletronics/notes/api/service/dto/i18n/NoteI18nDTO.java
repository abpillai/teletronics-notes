package com.teletronics.notes.api.service.dto.i18n;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class NoteI18nDTO {
	
	@NotBlank
	private String question;

	@NotBlank
	private String answer;

}
