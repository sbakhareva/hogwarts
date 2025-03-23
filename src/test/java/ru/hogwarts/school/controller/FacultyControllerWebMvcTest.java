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
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.service.FacultyService;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FacultyController.class)
class FacultyControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;
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
    void editFacultyTest() throws Exception {
        Faculty f = new Faculty("Faculty", "green");
        when(facultyRepository.save(any(Faculty.class))).thenReturn(f);

        JSONObject facultyJSON = new JSONObject();
        facultyJSON.put("id", f.getId());
        facultyJSON.put("name", f.getName());
        facultyJSON.put("color", f.getColor());

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/school/faculty/edit")
                        .content(facultyJSON.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(f.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(f.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.color").value(f.getColor()));
    }

    @Test
    void sortByColor() throws Exception {
        Faculty f = new Faculty();
        f.setName("Shmaculty");
        f.setColor("red");

        String requestedColor = "red";

        when(facultyRepository.findAll()).thenReturn(List.of(f));
        when(facultyService.sortByColor(requestedColor)).thenReturn(List.of(f));

        mockMvc.perform(get("/school/faculty/sort?color=" + requestedColor))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(f.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(f.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].color").value(f.getColor()));
    }

    @Test
    void findByNameOrColorTest() throws Exception {
        Faculty f = new Faculty("Faculty", "blue");

        when(facultyRepository.findByNameIgnoreCaseOrColorIgnoreCaseContains(anyString(), anyString())).thenReturn(List.of(f));
        when(facultyService.findByNameOrColor(anyString(), anyString())).thenReturn(List.of(f));

        mockMvc.perform(get("/school/faculty/findBy?name=a&color=b"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(f.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(f.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].color").value(f.getColor()));
    }

    @Test
    void getAllStudentsTest() throws Exception {
        Faculty f = new Faculty("Faculty", "color");
        Student s = new Student("Student", 13, f);

        when(facultyRepository.findByNameIgnoreCaseContains(anyString())).thenReturn(Optional.of(f));
        when(facultyService.getAllStudentsOfFaculty(anyString())).thenReturn(List.of(s));

        mockMvc.perform(get("/school/faculty/getAllStudents?name=a"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(s.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(s.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].age").value(s.getAge()));
    }
}