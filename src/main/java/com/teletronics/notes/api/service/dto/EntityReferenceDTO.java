package com.teletronics.notes.api.service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EntityReferenceDTO {

	private final String type;
	private final String id;
	private final String label;
	
}
