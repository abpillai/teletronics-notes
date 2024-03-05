package com.teletronics.notes.api.service.vo;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;

import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.Unwrapped.Empty;

import com.teletronics.notes.api.service.model.MetaInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoteVO {
	
	@Field("e_id")
	private  String id;

	
	@NotBlank
	private  String title;
	
	@NotBlank
	private  String text;
	
	
	private  Instant createdDate;
	
	private Map<String,Long> stats;
	
	private  List<@NotBlank String> noteTagIds;
	
	private  MetaInfo metaInfo;
}
