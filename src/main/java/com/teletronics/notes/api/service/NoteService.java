package com.teletronics.notes.api.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import com.teletronics.notes.api.domain.Note;
import com.teletronics.notes.api.domain.NoteTag;
import com.teletronics.notes.api.domain.enumeration.SupportedLocale;

import com.teletronics.notes.api.domain.i18n.NameI18n;
import com.teletronics.notes.api.domain.i18n.NoteI18n;
import com.teletronics.notes.api.repository.NoteRepository;
import com.teletronics.notes.api.repository.NoteTagRepository;
import com.teletronics.notes.api.service.dto.EntityReferenceDTO;
import com.teletronics.notes.api.service.dto.NoteDTO;
import com.teletronics.notes.api.service.dto.NoteSearchDTO;
import com.teletronics.notes.api.service.dto.i18n.NoteI18nDTO;
import com.teletronics.notes.api.service.dto.i18n.NameI18nDTO;
import com.teletronics.notes.api.service.mapper.BeanMapper;
import com.teletronics.notes.api.service.model.MetaInfo;
import com.teletronics.notes.api.service.model.PageOfRecords;
import com.teletronics.notes.api.service.vo.NoteStatsVO;
import com.teletronics.notes.api.service.vo.NoteTagVO;
import com.teletronics.notes.api.service.vo.NoteVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoteService {

	private final BeanMapper beanMapper;
	

	private final NoteRepository noteRepository;
	

	private final NoteTagRepository noteTagRepository;
	private final MongoTemplate mongoTemplate;

	// start of note_tag

	public NoteTagVO saveNoteTag(NoteTagVO noteTagVO, Optional<String> tagId, SupportedLocale language) {
		//System.out.println(noteTagRepository.findByI18nDataName(noteTagVO.getName()));
		if(noteTagRepository.findByI18nDataName(noteTagVO.getName()).size()>0)
			throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "Tag already exists");
		
		Optional<NoteTag> noteTagOpt = Optional.empty();
		if (tagId.isPresent()) {
			NoteTag existingRecord = noteTagRepository.findById(new ObjectId(tagId.get()))
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "Invalid tagId"));
			noteTagOpt = Optional.of(existingRecord);
		}

		NoteTag saved = noteTagRepository.save(beanMapper.mapNoteTag(noteTagVO, noteTagOpt, language));
		return beanMapper.mapNoteTagVO(saved, language);
	}

	public List<NoteTagVO> getNoteTags(SupportedLocale language) {
		return noteTagRepository.findAllByLanguage(language);
	}

	public NoteTagVO getNoteTag(String tagId, SupportedLocale language) {

		NoteTag noteTag = noteTagRepository.findById(new ObjectId(tagId))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "Invalid tagId"));
		return beanMapper.mapNoteTagVO(noteTag, language);
	}

	public NoteTagVO saveNoteTagI18n(NameI18nDTO nameI18nDTO, String tagId, SupportedLocale language) {

		NoteTag noteTag = noteTagRepository.findById(new ObjectId(tagId))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "Invalid tagId"));

		NameI18n nameI18n = new NameI18n();
		BeanUtils.copyProperties(nameI18nDTO, nameI18n);
		nameI18n.setLanguage(language);

		if (noteTag.getI18nData() != null) {

			List<NameI18n> i18nData = noteTag.getI18nData();

			i18nData.removeIf(e -> e.getLanguage().equals(nameI18n.getLanguage()));
			i18nData.add(nameI18n);

		} else {
			noteTag.setI18nData(Arrays.asList(nameI18n));
		}

		NoteTag saved = noteTagRepository.save(noteTag);
		return beanMapper.mapNoteTagVO(saved, nameI18n.getLanguage());
	}

	public List<EntityReferenceDTO> getEntityLinksForNoteTag(String tagId) {

		Query query = new Query();
		query.addCriteria(
				Criteria.where("note_tags").in(new ObjectId(tagId))
					.andOperator(new Criteria().orOperator(
							Criteria.where("deleted").exists(false), 
							Criteria.where("deleted").is(false)
							)
						)
				);

		List<EntityReferenceDTO> entityReferences = new ArrayList<>();

		List<Note> notes = mongoTemplate.find(query, Note.class);

		for (Note note : notes) {

			EntityReferenceDTO entityReference = EntityReferenceDTO.builder().type("FAQ").id(note.getId().toHexString())
					.label(note.getI18nData().get(0).getTitle()).build();
			entityReferences.add(entityReference);

		}

		
		return entityReferences;
	}


	public void deleteNoteTag(String tagId) {

		NoteTag noteTag = noteTagRepository.findById(new ObjectId(tagId))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "Invalid tagId"));

		List<EntityReferenceDTO> entityReferences = getEntityLinksForNoteTag(tagId);

		if (entityReferences.size() > 0) {
			throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "Active Links Found");
		}

		noteTag.setDeleted(true);
		noteTagRepository.save(noteTag);
	}

	// end of note_tag

	// start of note

	public NoteVO saveNote(NoteVO noteVO, Optional<String> noteId, SupportedLocale language) {

		Optional<Note> noteOpt = Optional.empty();
		if (noteId.isPresent()) {
			Note existingRecord = noteRepository.findById(new ObjectId(noteId.get()))
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "Invalid noteId"));
			noteOpt = Optional.of(existingRecord);
		}


		List<NoteTag> noteTags = getNoteTags(noteVO.getNoteTagIds());
		
		

		Note saved = noteRepository.save(beanMapper.mapNote(noteVO, 
				noteTags.stream().map(a -> a.getId()).collect(Collectors.toList()), noteOpt, language));
		return beanMapper.mapNoteVO(saved, language, getNoteMetaInfo(saved));

	}
	
	

	public List<NoteVO> getNotes(NoteSearchDTO noteSearchDTO, SupportedLocale language) {
		List<NoteVO> list= noteRepository.findAllByLanguage(language, PageRequest.of(noteSearchDTO.getPage(), noteSearchDTO.getLimit(), Sort.by(Sort.Direction.DESC, "createdDate")));
		return list;
	}

	public NoteVO getNote(String noteId, SupportedLocale language) {

		Note note = noteRepository.findById(new ObjectId(noteId))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "Invalid noteId"));
		return beanMapper.mapNoteVO(note, language, getNoteMetaInfo(note));
	}
	
	public NoteVO getNoteStats(String noteId, SupportedLocale language) {

		Note note = noteRepository.findById(new ObjectId(noteId))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "Invalid noteId"));
		NoteVO result= beanMapper.mapNoteVO(note, language, getNoteMetaInfo(note));
		
		
		Map<String,Long> words = new TreeMap<>(Arrays.stream(result.getText().split(" ")).
				map(s-> s.toLowerCase())
				.sorted((f1,f2) -> f2.compareTo(f1))
				.collect(Collectors.groupingBy( Function.identity(), Collectors.counting() ))).descendingMap();
		result.setStats(words);
		
		
		return result;
		
	}

	
	/*public PageOfRecords<NoteDTO> searchNoteDTO(NoteSearchDTO noteSearchDTO, SupportedLocale language) {
		List<Note> notes = searchNote(noteSearchDTO, language);

		if (notes.isEmpty())
			return new PageOfRecords<>(new ArrayList<>(), 0);
		
		int totalRecords = notes.size();
		if (noteSearchDTO.getLimit() > 0) {
			if (noteSearchDTO.getLimit() <= notes.size()) {
				notes = notes.subList(0, noteSearchDTO.getLimit());
			} else {
				notes.subList(0, notes.size());
			}
		}

		List<NoteDTO> list= notes.stream().map(f -> beanMapper.mapNoteDTO(f, language)).filter(Objects::nonNull)
				.collect(Collectors.toList());
		return new PageOfRecords<>(list, totalRecords);

	}*/

	public List<NoteVO> searchNoteVO(NoteSearchDTO noteSearchDTO, SupportedLocale language) {

		List<Note> notes = searchNote(noteSearchDTO, language);
		if (notes.isEmpty())
			return new ArrayList<>();

		List<NoteVO> list = notes.stream().map(f -> {
			NoteVO note = beanMapper.mapNoteVO(f, language, null);
			note.setText(null);
			note.setNoteTagIds(null);
			return note;
		}).filter(Objects::nonNull).collect(Collectors.toList());

		return list;
	}

	private List<Note> searchNote(NoteSearchDTO noteSearchDTO, SupportedLocale language) {
		
		Query query = new Query();
		query.addCriteria(
				Criteria.where("i18n_data").elemMatch(Criteria.where("language").is(language))
				);

		query.addCriteria(
				new Criteria().orOperator(
						Criteria.where("deleted").exists(false), 
						Criteria.where("deleted").is(false)
						)
			);
		
		

		if (noteSearchDTO.getTags() != null && noteSearchDTO.getTags().size() > 0) {
			List<ObjectId> tagIds = noteSearchDTO.getTags().stream().map(s -> new ObjectId(s))
					.collect(Collectors.toList());

			query.addCriteria(Criteria.where("note_tags").in(tagIds));
		}

		//return mongoTemplate.find(query.with(Sort.by(Sort.Direction.DESC, "createdDate")), Note.class);
		
		return mongoTemplate.find(query.with(PageRequest.of(noteSearchDTO.getPage(), noteSearchDTO.getLimit(), Sort.by(Sort.Direction.DESC, "createdDate"))), Note.class);
	}

	

	public NoteVO saveNoteI18n(NoteI18nDTO noteI18nDTO, String noteId, SupportedLocale language) {

		Note note = noteRepository.findById(new ObjectId(noteId))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "Invalid noteId"));

		NoteI18n noteI18n = new NoteI18n();
		BeanUtils.copyProperties(noteI18nDTO, noteI18n);
		noteI18n.setLanguage(language);

		if (note.getI18nData() != null) {

			List<NoteI18n> i18nData = note.getI18nData();

			i18nData.removeIf(e -> e.getLanguage().equals(noteI18n.getLanguage()));
			i18nData.add(noteI18n);

		} else {
			note.setI18nData(Arrays.asList(noteI18n));
		}

		Note saved = noteRepository.save(note);
		return beanMapper.mapNoteVO(saved, language, getNoteMetaInfo(saved));

	}

	
	
	
	@Transactional
	public void deleteNote(String noteId) {

		Note note = noteRepository.findById(new ObjectId(noteId))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "Invalid noteId"));

		note.setDeleted(true);
		noteRepository.save(note);
		
		
		
	}


	private List<NoteTag> getNoteTags(List<String> noteTagIds) {

		List<NoteTag> result = new ArrayList<>();

		if (noteTagIds == null || noteTagIds.isEmpty())
			return result;

		List<ObjectId> noteTagObjectIds = noteTagIds.stream().map(s -> new ObjectId(s)).collect(Collectors.toList());
		Iterable<NoteTag> noteTagsIterable = noteTagRepository.findAllById(noteTagObjectIds);

		noteTagsIterable.forEach(result::add);

		return result;
	}
	
	private MetaInfo getNoteMetaInfo(Note entity) {
		
		
		
		MetaInfo metaInfo = new MetaInfo();
		metaInfo.setVersion(entity.getVersion());
		
		
		
		return metaInfo;
	}
	
	

}
