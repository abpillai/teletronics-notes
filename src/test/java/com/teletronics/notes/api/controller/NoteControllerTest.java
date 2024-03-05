package com.teletronics.notes.api.controller;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teletronics.notes.api.domain.enumeration.SupportedLocale;
import com.teletronics.notes.api.repository.NoteRepository;
import com.teletronics.notes.api.repository.NoteTagRepository;
import com.teletronics.notes.api.service.NoteService;
import com.teletronics.notes.api.service.dto.NoteSearchDTO;
import com.teletronics.notes.api.service.vo.NoteTagVO;
import com.teletronics.notes.api.service.vo.NoteVO;

import lombok.extern.slf4j.Slf4j;

@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureDataMongo
@IntegrationTest
@Slf4j
@ActiveProfiles(value = "test")
public class NoteControllerTest {

	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	protected NoteService qService;

	@Autowired
	private NoteTagRepository noteTagRepository;

	@Autowired
	private NoteRepository noteRepository;

	@Autowired
	private NoteService noteService;

	public static void initTestUser(NoteTagRepository noteTagRepository, NoteRepository noteRepository) {
		noteRepository.deleteAll();
		noteTagRepository.deleteAll();
	}

	@BeforeEach
	public void initTest() {
		initTestUser(noteTagRepository, noteRepository);
	}

	@Test
	void createNoteTag() throws Exception {

		String json = "{\n" + "  \"name\": \"BUSINESS\"\n" + " \n" + "}";
		ObjectMapper mapper = new ObjectMapper();
		NoteTagVO vo = mapper.readValue(json, NoteTagVO.class);

		NoteTagVO result = noteService.saveNoteTag(vo, Optional.empty(), SupportedLocale.EN);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/tags/" + result.getId()).header("Content-Language", "EN"))

				.andExpect(jsonPath("$.id").value(result.getId())).andExpect(status().isOk());

	}

	@Test
	void createNote() throws Exception {

		String json = "{\n" + "  \"title\": \"Telematics\",\n" + "  \"text\": \"This is telematics2\",\n"
				+ "  \"noteTagIds\": [\n" + "    \"65e60c57ab45255618975814\"\n" + "  ]\n" + "}";
		ObjectMapper mapper = new ObjectMapper();
		NoteVO vo = mapper.readValue(json, NoteVO.class);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/notes").content(TestUtil.convertObjectToJsonBytes(vo))
				.contentType(MediaType.APPLICATION_JSON).header("Content-Language", "EN")
				.accept(MediaType.APPLICATION_JSON))

				.andExpect(jsonPath("$.id").exists()).andExpect(status().isOk());

	}

	@Test
	void getAllNotes() throws Exception {

		String json = "{\n" + "  \"title\": \"Telematics\",\n" + "  \"text\": \"This is telematics2\",\n"
				+ "  \"noteTagIds\": [\n" + "    \"65e60c57ab45255618975814\"\n" + "  ]\n" + "}";
		ObjectMapper mapper = new ObjectMapper();
		NoteVO vo = mapper.readValue(json, NoteVO.class);

		NoteVO result = noteService.saveNote(vo, Optional.empty(), SupportedLocale.EN);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/notes").header("Content-Language", "EN"))

				.andExpect(jsonPath("$.[*].id").value(hasItem(result.getId())))
				.andExpect(jsonPath("$.[*].text").doesNotExist()).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));

	}

	@Test
	void searchAllNotesById() throws Exception {

		String json1 = "{\n" + "  \"name\": \"BUSINESS\"\n" + " \n" + "}";
		ObjectMapper mapper = new ObjectMapper();
		NoteTagVO voTag = mapper.readValue(json1, NoteTagVO.class);

		NoteTagVO resultTag = noteService.saveNoteTag(voTag, Optional.empty(), SupportedLocale.EN);
		System.out.println("resultTag:" + resultTag);
		String json = "{\n" + "  \"title\": \"Telematics\",\n" + "  \"text\": \"This is telematics2\",\n"
				+ "  \"noteTagIds\": [\n" + "    \"" + resultTag.getId() + "\"\n" + "  ]\n" + "}";

		NoteVO vo = mapper.readValue(json, NoteVO.class);

		NoteVO result = noteService.saveNote(vo, Optional.empty(), SupportedLocale.EN);

		String json2 = "{\n" + "  \"tags\": [\n" + "    \"" + resultTag.getId() + "\"\n" + "  ]\n" + "}";
		NoteSearchDTO vo2 = mapper.readValue(json2, NoteSearchDTO.class);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/notes/search").header("Content-Language", "EN")
				.contentType(MediaType.APPLICATION_JSON)

				.content(TestUtil.convertObjectToJsonBytes(vo2))).andExpect(status().isOk())
				.andExpect(jsonPath("$.[*].id").value(result.getId()));

	}

	@Test
	void getNoteById() throws Exception {

		String json = "{\n" + "  \"title\": \"Telematics\",\n" + "  \"text\": \"This is telematics2\",\n"
				+ "  \"noteTagIds\": [\n" + "    \"65e60c57ab45255618975814\"\n" + "  ]\n" + "}";
		ObjectMapper mapper = new ObjectMapper();
		NoteVO vo = mapper.readValue(json, NoteVO.class);

		NoteVO result = noteService.saveNote(vo, Optional.empty(), SupportedLocale.EN);
		log.info("id:" + result.getId());

		mockMvc.perform(MockMvcRequestBuilders.get("/api/notes/" + result.getId()).header("Content-Language", "EN"))

				.andExpect(jsonPath("$.id").value(result.getId())).andExpect(jsonPath("$.text").value(result.getText()))
				.andExpect(status().isOk());

	}

	@Test
	void putNoteById() throws Exception {
		String json = "{\n" + "  \"title\": \"Telematics\",\n" + "  \"text\": \"This is telematics2\",\n"
				+ "  \"noteTagIds\": [\n" + "    \"65e60c57ab45255618975814\"\n" + "  ]\n" + "}";
		ObjectMapper mapper = new ObjectMapper();
		NoteVO vo = mapper.readValue(json, NoteVO.class);

		NoteVO result = noteService.saveNote(vo, Optional.empty(), SupportedLocale.EN);
		

		String jsonPut = "{\n" + "    \"id\": \"" + result.getId() + "\",\n" + "    \"title\": \"New Title\",\n"
				+ "    \"text\": \"hello1 alan i am here where are you and what are you doing hello are you there\",\n"
				+ "   \n" + "    \"noteTagIds\": [\n" + "        \"65e70a041f46ff663e5e9a4a\"\n" + "    ]\n" + "}";

		NoteVO vo2 = mapper.readValue(jsonPut, NoteVO.class);

		mockMvc.perform(MockMvcRequestBuilders.put("/api/notes/" + result.getId())
				.content(TestUtil.convertObjectToJsonBytes(vo2)).contentType(MediaType.APPLICATION_JSON)
				.header("Content-Language", "EN").accept(MediaType.APPLICATION_JSON))

				.andExpect(jsonPath("$.id").value(result.getId())).andExpect(jsonPath("$.title").value("New Title"))
				.andExpect(status().isOk());

	}
	
	@Test
	void deleteNoteById() throws Exception {

		String json = "{\n" + "  \"title\": \"Telematics\",\n" + "  \"text\": \"This is telematics2\",\n"
				+ "  \"noteTagIds\": [\n" + "    \"65e60c57ab45255618975814\"\n" + "  ]\n" + "}";
		ObjectMapper mapper = new ObjectMapper();
		NoteVO vo = mapper.readValue(json, NoteVO.class);

		NoteVO result = noteService.saveNote(vo, Optional.empty(), SupportedLocale.EN);
		log.info("id:" + result.getId());

		mockMvc.perform(MockMvcRequestBuilders.delete("/api/notes/" + result.getId()).header("Content-Language", "EN"))

				
				.andExpect(status().is4xxClientError());

	}
	
	@Test
	void getNoteStatsById() throws Exception {

		String json = "{\n" + "  \"title\": \"Telematics\",\n" + "  \"text\": \"This is telematics2\",\n"
				+ "  \"noteTagIds\": [\n" + "    \"65e60c57ab45255618975814\"\n" + "  ]\n" + "}";
		ObjectMapper mapper = new ObjectMapper();
		NoteVO vo = mapper.readValue(json, NoteVO.class);

		NoteVO result = noteService.saveNote(vo, Optional.empty(), SupportedLocale.EN);
		log.info("id:" + result.getId());

		mockMvc.perform(MockMvcRequestBuilders.get("/api/notes/" + result.getId()+ "/stats").header("Content-Language", "EN"))

		.andExpect(jsonPath("$.id").value(result.getId())).andExpect(jsonPath("$.stats").exists())
				.andExpect(status().isOk());

	}

}
