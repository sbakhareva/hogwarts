package ru.hogwarts.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.service.FacultyService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FacultyController.class)
class FacultyControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private FacultyRepository facultyRepository;
    @MockitoBean
    private FacultyService facultyService;

    @Test
    void addFacultyTest() throws Exception {
        JSONObject facultyJSON = new JSONObject();
        facultyJSON.put("name", "Faculty");
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(f.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(f.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.color").value(f.getColor()));
    }

    @Test
    void getFacultyByIdTest() throws Exception {
        Faculty f = new Faculty();
        f.setName("Faculty");
        f.setColor("green");

        when(facultyRepository.findById(anyLong())).thenReturn(Optional.of(f));
        when(facultyService.getFacultyByID(anyLong())).thenReturn(f);

        mockMvc.perform(get("/school/faculty/get?id=" + anyLong()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(f.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(f.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.color").value(f.getColor()));
    }

    @Test
    void deleteFacultyTest() throws Exception {
        Faculty f = new Faculty();
        f.setName("Faculty");
        f.setColor("green");

        when(facultyRepository.findById(anyLong())).thenReturn(Optional.of(f));

        mockMvc.perform(delete("/school/faculty/remove?id=" + anyLong()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").doesNotExist());
    }

    @Test
    void editFacultyTest() {
        Faculty f = new Faculty();
        f.setName("Faculty");
        f.setColor("green");


    }
}