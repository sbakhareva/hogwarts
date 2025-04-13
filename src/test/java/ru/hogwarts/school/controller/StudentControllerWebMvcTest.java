package ru.hogwarts.school.controller;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.hogwarts.school.dto.StudentDTO;
import ru.hogwarts.school.dto.StudentDTOMapper;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.StudentService;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentController.class)
class StudentControllerWebMvcTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private AvatarRepository avatarRepository;
    @MockitoBean
    private StudentRepository studentRepository;
    @MockitoBean
    private StudentDTOMapper studentDTOMapper;
    @MockitoBean
    private StudentService studentService;

    @Test
    void addStudentTest() throws Exception {
        Faculty f = new Faculty("Faculty", "yellow");
        JSONObject studentJson = new JSONObject();
        studentJson.put("name", "Student");
        studentJson.put("age", 13);
        studentJson.put("faculty_id", f.getId());

        Student s = new Student("Student", 13, f);

        when(studentRepository.save(any(Student.class))).thenReturn(s);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/school/student/add")
                        .content(studentJson.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(s.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(s.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.age").value(s.getAge()));
    }

    @Test
    void getStudentByIdTest() throws Exception {
        Faculty f = new Faculty("Faculty", "yellow");
        Student s = new Student("Student", 13, f);
        StudentDTO sDto = new StudentDTO(s.getId(), s.getName(), s.getAge(), s.getFaculty().getName());

        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(s));
        when(studentService.getStudentByID(anyLong())).thenReturn(sDto);

        mockMvc.perform(get("/school/student/get?id=1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(s.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(s.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.age").value(s.getAge()));
    }

    @Test
    void getAllStudentsTest() throws Exception {
        Faculty f = new Faculty("Faculty", "yellow");
        Student s = new Student("Student", 17, f);

        when(studentDTOMapper.apply(any(Student.class)))
                .thenReturn(new StudentDTO(s.getId(), s.getName(), s.getAge(), s.getFaculty().getName()));
        StudentDTO sDto = studentDTOMapper.apply(s);

        when(studentRepository.findAll()).thenReturn(List.of(s));
        when(studentService.getAllStudents()).thenReturn(List.of(sDto));

        mockMvc.perform(get("/school/student/get-all"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(s.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(s.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].age").value(s.getAge()));
    }

    @Test
    void removeStudentTest() throws Exception {
        Faculty f = new Faculty("Faculty", "yellow");
        Student s = new Student("Student", 13, f);

        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(s));

        mockMvc.perform(delete("/school/student/remove?id=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").doesNotExist());
    }

    @Test
    void editStudentTest() throws Exception {
        Faculty f = new Faculty("Faculty", "yellow");
        Student s = new Student("Student", 13, f);
        when(studentRepository.save(any(Student.class))).thenReturn(s);

        JSONObject studentJson = new JSONObject();
        studentJson.put("name", "Student");
        studentJson.put("age", 13);
        studentJson.put("faculty_id", f.getId());

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/school/student/edit")
                        .content(studentJson.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(s.getId()))
                .andExpect(jsonPath("$.name").value(s.getName()))
                .andExpect(jsonPath("$.age").value(s.getAge()));
    }

    @Test
    void sortStudentsByAgeTest() throws Exception {
        Faculty f = new Faculty("Faculty", "yellow");
        Student s = new Student("Student", 13, f);

        when(studentRepository.findAll()).thenReturn(List.of(s));
        when(studentService.sortByAge(anyInt())).thenReturn(List.of(s));

        mockMvc.perform(get("/school/student/sort?age=1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(s.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(s.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].age").value(s.getAge()));
    }

    @Test
    void findBetweenAgeTest() throws Exception {
        Faculty f = new Faculty("Faculty", "yellow");
        Student s = new Student("Student", 17, f);

        when(studentRepository.findAllByAgeBetween(anyInt(), anyInt())).thenReturn(List.of(s));
        when(studentService.findByAgeBetween(anyInt(), anyInt())).thenReturn(List.of(s));

        mockMvc.perform(get("/school/student/find-between-age?from=11&to=16"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(s.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(s.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].age").value(s.getAge()));
    }

    @Test
    void getFacultyTest() throws Exception {
        Faculty f = new Faculty("Faculty", "yellow");
        Student s = new Student("Student", 17, f);

        when(studentRepository.findStudentByNameIgnoreCaseContains(anyString())).thenReturn(Optional.of(s));
        when(studentService.getStudentsFaculty(anyString())).thenReturn(f);

        mockMvc.perform(get("/school/student/get-faculty?name=Student"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(f.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(f.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.color").value(f.getColor()));
    }

}