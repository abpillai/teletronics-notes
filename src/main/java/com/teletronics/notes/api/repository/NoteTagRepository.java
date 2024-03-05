package com.teletronics.notes.api.repository;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.teletronics.notes.api.domain.NoteTag;
import com.teletronics.notes.api.domain.enumeration.SupportedLocale;
import com.teletronics.notes.api.service.vo.NoteTagVO;


public interface NoteTagRepository extends MongoRepository<NoteTag, ObjectId>  {

	@Aggregation(pipeline = {
			"{ $match: {i18n_data: {$elemMatch: {'language': ?0}}}}", 
			"{ $unwind: '$i18n_data' }", 
			"{ $match: {'i18n_data.language': ?0, deleted: {$in: [null, false]} }}",
			"{ $replaceWith: { $mergeObjects: [ '$$ROOT', '$i18n_data' ]}}",
			"{ $addFields: { 'e_id' : { $toString: '$_id' }}}",
			"{ $project: { 'i18n_data' : 0, '_id': 0, '_class': 0 } }"
	})
	List<NoteTagVO> findAllByLanguage(SupportedLocale language);

	@Query("{_id : ?0, deleted: {$in: [null, false]} }")
	Optional<NoteTag> findById(ObjectId id);
	
	@Query("{'_id': { $in: ?0 }, deleted: {$in: [null, false]} }")
	Iterable<NoteTag> findAllById(List<ObjectId> ids);
	
	@Query(value = "{ 'i18n_data.name' : ?0 }") 
	List<NoteTag> findByI18nDataName(String name);
}
