package com.teletronics.notes.api.service.model;

import java.util.List;

import lombok.Data;

@Data
public class PageOfRecords<T> {

	private final List<T> records;
	private final int totalRecords;
}
