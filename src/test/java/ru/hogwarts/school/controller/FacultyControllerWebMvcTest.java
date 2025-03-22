package ru.hogwarts.school.controller;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.service.FacultyService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@AutoConfigureMockMvc
class FacultyControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;
    //аннотация
    private FacultyRepository facultyRepository;
    //аннотация
    private FacultyService facultyService;
    @InjectMocks
    private FacultyController facultyController;

    @Test
    void addFacultyTest() throws Exception {
        JSONObject facultyJSON = new JSONObject();
        facultyJSON.put("Faculty", "Faculty");
        facultyJSON.put("color", "green");
        Faculty f = new Faculty();
        f.setName("Faculty");
        f.setColor("green");

        when(facultyRepository.save(any(Faculty.class))).thenReturn(f);
        when(facultyRepository.findById(any(Long.class))).thenReturn(Optional.of(f));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/school/faculty/add")
                        .content(facultyJSON.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$.id").value("id"))
                .andExpect((ResultMatcher) jsonPath("$.name").value("name"))
                .andExpect((ResultMatcher) jsonPath("$.color").value("color"));
        mockMvc.perform(MockMvcRequestBuilders
                .get("/school/faculty/get?id=" + f.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$.id").value("id"))
                .andExpect((ResultMatcher) jsonPath("$.name").value("name"))
                .andExpect((ResultMatcher) jsonPath("$.color").value("color"));
    }
}