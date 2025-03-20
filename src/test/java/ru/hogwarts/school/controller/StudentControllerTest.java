package ru.hogwarts.school.controller;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StudentControllerTest {
    @LocalServerPort
    private int port;
    @Autowired
    private FacultyRepository facultyRepository;
    @Autowired
    private StudentController studentController;
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void contextLoads() {
        Assertions.assertThat(studentController).isNotNull();
    }

    @Test
    void addStudent() {
        Student s = new Student();
        s.setName("Harry");
        s.setAge(11);
        s.setFaculty(facultyRepository.findByNameIgnoreCaseContains("Гриффиндор").get());
        ResponseEntity response = testRestTemplate.exchange(
                "http://localhost:" + port + "/school/student/add",
                HttpMethod.POST,
                new HttpEntity<>(s),
                Student.class
        );
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void getStudents() {
        Assertions
                .assertThat(this.testRestTemplate.getForObject("http://localhost:" + port + "/school/student", String.class))
                .isNotNull();
    }
}