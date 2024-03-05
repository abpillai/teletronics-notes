package com.teletronics.notes.api.service.dto;

import java.time.Instant;
import java.util.List;

import javax.validation.constraints.NotBlank;

import com.teletronics.notes.api.service.model.MetaInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoteSearchDTO {


	private  List<String> tags;
	private int limit;
	private int page;
	
}
