package ru.hogwarts.school.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.hogwarts.school.dto.StudentDTOMapper;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.FacultyService;
import ru.hogwarts.school.service.StudentService;

import static org.junit.jupiter.api.Assertions.*;

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
}