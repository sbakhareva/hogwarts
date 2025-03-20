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

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FacultyControllerTest {

    @LocalServerPort
    private int port;
    @Autowired
    private FacultyController facultyController;
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void contextLoads() {
        assertThat(facultyController).isNotNull();
    }

    @Test
    void addFaculty() {
        Faculty f = new Faculty("Faculty", "color");
        ResponseEntity response = testRestTemplate.exchange(
                "http://localhost:" + port + "/school/faculty/add",
                HttpMethod.POST,
                new HttpEntity<>(f),
                Faculty.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void getFacultyById() {
        Faculty f = new Faculty("Faculty", "color");
        facultyController.addFaculty(f);
        Assertions
                .assertThat(this.testRestTemplate.getForObject("http://localhost:" + port + "/school/faculty/get?id=" + f.getId(), String.class))
                .contains(f.getId().toString())
                .contains(f.getName())
                .contains(f.getColor());

    }
}