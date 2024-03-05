package com.teletronics.notes.api.domain;

import java.time.Instant;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
public abstract class AuditableEntity {
	


	@Field("createdDate")
	@CreatedDate
	protected Instant createdDate;

	
	
	@Field
	protected boolean deleted;
	
	@Field
	@Version
	protected Long version;
}
