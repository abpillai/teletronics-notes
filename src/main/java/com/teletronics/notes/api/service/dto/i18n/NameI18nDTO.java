package com.teletronics.notes.api.service.dto.i18n;

import javax.validation.constraints.NotBlank;

import com.teletronics.notes.api.domain.enumeration.SupportedLocale;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NameI18nDTO {

	@NotBlank
	private String name;
	
	private SupportedLocale language;

}
