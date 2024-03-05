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
public class NoteStatsVO {
	
	

	private  String word;
	
	private  Long count;
}
