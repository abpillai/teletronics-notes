package com.teletronics.notes.api.repository;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.teletronics.notes.api.domain.Note;
import com.teletronics.notes.api.domain.enumeration.SupportedLocale;
import com.teletronics.notes.api.service.vo.NoteVO;



public interface NoteRepository extends MongoRepository<Note, ObjectId>  {

	@Aggregation(pipeline = {
			"{ $match: {i18n_data: {$elemMatch: {'language': ?0}}}}", 
			"{ $unwind: '$i18n_data' }", 
			"{ $match: {'i18n_data.language': ?0, deleted: {$in: [null, false]} }}",
			"{ $replaceWith: { $mergeObjects: [ '$$ROOT', '$i18n_data' ]}}",
			"{ $addFields: { 'e_id' : { $toString: '$_id' }, 'noteTagIds' : { $map: { input: '$note_tags', as: 'noteTagId', in: { $toString: '$$noteTagId' }}}}}",
			"{ $project: { 'i18n_data' : 0, '_id': 0, '_class': 0, 'note_tags': 0, 'text': 0 } }"
	})
	List<NoteVO> findAllByLanguage(SupportedLocale language, Pageable page);
	
	@Query("{_id : ?0, deleted: {$in: [null, false]} }")
	Optional<Note> findById(ObjectId id);
	
	@Query("{_id: { $in: ?0 }, deleted: {$in: [null, false]} }")
	Iterable<Note> findAllById(List<ObjectId> ids);
}
