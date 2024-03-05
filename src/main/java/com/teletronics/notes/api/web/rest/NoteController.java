package com.teletronics.notes.api.web.rest;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.teletronics.notes.api.domain.enumeration.SupportedLocale;
import com.teletronics.notes.api.service.NoteService;
import com.teletronics.notes.api.service.dto.EntityReferenceDTO;
import com.teletronics.notes.api.service.dto.NoteSearchDTO;
import com.teletronics.notes.api.service.dto.i18n.NameI18nDTO;
import com.teletronics.notes.api.service.dto.i18n.NoteI18nDTO;
import com.teletronics.notes.api.service.vo.NoteTagVO;
import com.teletronics.notes.api.service.vo.NoteVO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(path = "/api", produces = { MediaType.APPLICATION_JSON_VALUE })
@RequiredArgsConstructor
public class NoteController {

	private final NoteService noteService;
	
	// note_tag crud
	
	@PostMapping("/tags")
	public ResponseEntity<NoteTagVO> saveNoteTag(
			@RequestHeader("Content-Language") Optional<SupportedLocale> acceptLanguage,
			@Valid @RequestBody NoteTagVO noteTagVO) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(noteService.saveNoteTag(noteTagVO, Optional.empty(), acceptLanguage.orElse(SupportedLocale.EN)));
	}

	
	@GetMapping("/tags")
	public ResponseEntity<List<NoteTagVO>> getNoteTags(
			@RequestHeader("Accept-Language") Optional<SupportedLocale> acceptLanguage) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(noteService.getNoteTags(acceptLanguage.orElse(SupportedLocale.EN)));
	}

	
	@GetMapping("/tags/{id}")
	public ResponseEntity<NoteTagVO> getNoteTag(
			@RequestHeader("Accept-Language") Optional<SupportedLocale> acceptLanguage, @PathVariable("id") String id) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(noteService.getNoteTag(id, acceptLanguage.orElse(SupportedLocale.EN)));
	}

	
	
	
	@PutMapping("/tags/{id}/i18n_data")
	public ResponseEntity<NoteTagVO> updateNoteTagI18n(
			@RequestHeader("Content-Language") Optional<SupportedLocale> acceptLanguage, @PathVariable("id") String id,
			@Valid @RequestBody NameI18nDTO nameI18nDTO) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(noteService.saveNoteTagI18n(nameI18nDTO, id, acceptLanguage.orElse(SupportedLocale.EN)));
	}

	
	@GetMapping("/note_tag/{id}/entity_links")
	public ResponseEntity<List<EntityReferenceDTO>> getEntityLinksForNoteTag(@PathVariable("id") String id) {
		return ResponseEntity.status(HttpStatus.OK).body(noteService.getEntityLinksForNoteTag(id));
	}

	
	@DeleteMapping("/note_tag/{id}")
	public ResponseEntity<Void> deleteNoteTag(@PathVariable("id") String id) {
		noteService.deleteNoteTag(id);
		return ResponseEntity.status(HttpStatus.ACCEPTED).build();
	}

	// end of note_tag

	// note
	
	@PostMapping("/notes")
	public ResponseEntity<NoteVO> saveNote(@RequestHeader("Content-Language") Optional<SupportedLocale> acceptLanguage,
			@Valid @RequestBody NoteVO noteVO) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(noteService.saveNote(noteVO, Optional.empty(), acceptLanguage.orElse(SupportedLocale.EN)));
	}

	
	@GetMapping("/notes")
	public ResponseEntity<List<NoteVO>> getNotes(
			@RequestHeader("Accept-Language") Optional<SupportedLocale> acceptLanguage,
			@Valid @RequestParam Optional<Integer> page, @Valid @RequestParam Optional<Integer> limit) {
		NoteSearchDTO noteSearchDTO = new NoteSearchDTO();
		noteSearchDTO.setPage(page.orElse(0));
		noteSearchDTO.setLimit(limit.orElse(10));
		return ResponseEntity.status(HttpStatus.OK)
				.body(noteService.getNotes(noteSearchDTO, acceptLanguage.orElse(SupportedLocale.EN)));
	}
	
	@PostMapping("/notes/search")
	public ResponseEntity<List<NoteVO>> searchNote(
			@RequestHeader("Content-Language") Optional<SupportedLocale> acceptLanguage,
			@Valid @RequestBody NoteSearchDTO noteSearchDTO) {
		System.out.println("limit:"+noteSearchDTO.getLimit());
		if(noteSearchDTO.getLimit()==0)
			noteSearchDTO.setLimit(10);
		return ResponseEntity.status(HttpStatus.OK)
				.body(noteService.searchNoteVO(noteSearchDTO, acceptLanguage.orElse(SupportedLocale.EN)));
	}

	
	@GetMapping("/notes/{id}")
	public ResponseEntity<NoteVO> getNoteStats(@RequestHeader("Accept-Language") Optional<SupportedLocale> acceptLanguage,
			@PathVariable("id") String id) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(noteService.getNote(id, acceptLanguage.orElse(SupportedLocale.EN)));
	}
	
	@GetMapping("/notes/{id}/stats")
	public ResponseEntity<NoteVO> getNote(@RequestHeader("Accept-Language") Optional<SupportedLocale> acceptLanguage,
			@PathVariable("id") String id) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(noteService.getNoteStats(id, acceptLanguage.orElse(SupportedLocale.EN)));
	}

	
	
	@PutMapping("/notes/{id}")
	public ResponseEntity<NoteVO> updateNote(@RequestHeader("Content-Language") Optional<SupportedLocale> acceptLanguage,
			@PathVariable("id") String id, @Valid @RequestBody NoteVO noteVO) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(noteService.saveNote(noteVO, Optional.of(id), acceptLanguage.orElse(SupportedLocale.EN)));
	}

	
	

	
	@PutMapping("/notes/{id}/i18n_data")
	public ResponseEntity<NoteVO> updateNoteI18n(
			@RequestHeader("Content-Language") Optional<SupportedLocale> acceptLanguage, @PathVariable("id") String id,
			@Valid @RequestBody NoteI18nDTO noteI18nDTO) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(noteService.saveNoteI18n(noteI18nDTO, id, acceptLanguage.orElse(SupportedLocale.EN)));
	}

	
	
	
	@DeleteMapping("/note/{id}")
	public ResponseEntity<Void> deleteNote(@PathVariable("id") String id) {
		noteService.deleteNote(id);
		return ResponseEntity.status(HttpStatus.ACCEPTED).build();
	}


	// end of note

}
