package com.teletronics.notes.api.service.vo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoteTagVO {
	
	

	@Field("e_id")
	private  String id;
	@NotBlank
	@Pattern(regexp="^(BUSINESS|PERSONAL|IMPORTANT)$",message="invalid tag name")
	private String name;

}
