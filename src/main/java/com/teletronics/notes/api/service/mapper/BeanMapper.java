package com.teletronics.notes.api.service.mapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.teletronics.notes.api.constants.NotesConstants;
import com.teletronics.notes.api.domain.Note;
import com.teletronics.notes.api.domain.NoteTag;
import com.teletronics.notes.api.domain.enumeration.SupportedLocale;
import com.teletronics.notes.api.domain.i18n.NameI18n;
import com.teletronics.notes.api.domain.i18n.NoteI18n;
import com.teletronics.notes.api.service.dto.NoteDTO;
import com.teletronics.notes.api.service.model.MetaInfo;
import com.teletronics.notes.api.service.vo.NoteTagVO;
import com.teletronics.notes.api.service.vo.NoteVO;



@Service
public class BeanMapper {

	

	

	public NoteTag mapNoteTag(NoteTagVO noteTagVO, Optional<NoteTag> noteTagOpt, SupportedLocale supportedLocale) {

		NoteTag noteTag;
		
		if (noteTagOpt.isPresent()) {
			noteTag = noteTagOpt.get();
		} else {
			noteTag = new NoteTag();
		}
		
		
		NameI18n nameI18n = new NameI18n();
		nameI18n.setName(noteTagVO.getName());
		nameI18n.setLanguage(supportedLocale);
		
		if (noteTag.getI18nData() != null) {
			
			List<NameI18n> i18nData = noteTag.getI18nData();
			
			i18nData.removeIf(e -> e.getLanguage().equals(supportedLocale));
			i18nData.add(nameI18n);
			
		} else {
			noteTag.setI18nData(Arrays.asList(nameI18n));
		}

		
		return noteTag;
	}

	public NoteTagVO mapNoteTagVO(NoteTag noteTag, SupportedLocale supportedLocale) {
		NameI18n nameI18n = noteTag.getI18nData().stream().filter(e -> e.getLanguage().equals(supportedLocale)).findAny()
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, String.format(NotesConstants.LANGUAGE_NOT_FOUND, supportedLocale)));
		
		return new NoteTagVO(noteTag.getId().toHexString(), nameI18n.getName());
	}
	
	public NoteDTO mapNoteDTO(Note note, SupportedLocale supportedLocale) {
		
		NoteI18n noteI18n = note.getI18nData().stream().filter(e -> e.getLanguage().equals(supportedLocale)).findAny()
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, String.format(NotesConstants.LANGUAGE_NOT_FOUND, supportedLocale)));
		
		return new NoteDTO(note.getId().toHexString(), noteI18n.getTitle(), noteI18n.getText(), note.getCreatedDate());
	}
	
	public Note mapNote(NoteVO noteVO, List<ObjectId> noteTags, Optional<Note> noteOpt, SupportedLocale supportedLocale) {
		
		Note note;
		if (noteOpt.isPresent()) {
			note = noteOpt.get();
		} else {
			note = new Note(); 
		}

		
		note.setNoteTags(noteTags);
		
		
		
		NoteI18n noteI18n = new NoteI18n();
		noteI18n.setTitle(noteVO.getTitle());
		noteI18n.setText(noteVO.getText());
		noteI18n.setLanguage(supportedLocale);	
		
		if (note.getI18nData() != null) {
			
			List<NoteI18n> i18nData = note.getI18nData();
			
			i18nData.removeIf(e -> e.getLanguage().equals(supportedLocale));
			i18nData.add(noteI18n);
			
		} else {
			note.setI18nData(Arrays.asList(noteI18n));
		}
		
		return note;
	}
	


	public NoteVO mapNoteVO(Note note, SupportedLocale supportedLocale, MetaInfo metaInfo) {
		
		NoteI18n noteI18n = note.getI18nData().stream().filter(e -> e.getLanguage().equals(supportedLocale)).findAny()
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, String.format(NotesConstants.LANGUAGE_NOT_FOUND, supportedLocale)));

		return new NoteVO(note.getId().toHexString(),noteI18n.getTitle(), noteI18n.getText(), note.getCreatedDate(),null,
				note.getNoteTags().stream().map(f -> f.toHexString()).collect(Collectors.toList()), metaInfo);
	}
	
	

	
	


}
