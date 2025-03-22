package ru.hogwarts.school.controller;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FacultyControllerTest {

    @LocalServerPort
    private int port;
    @Autowired
    private FacultyController facultyController;
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private StudentController studentController;

//    @BeforeEach
//    void setUp() {
//        Faculty f1 = new Faculty("Faculty", "blue");
//        Faculty f2 = new Faculty("Shmaculty", "white");
//        facultyController.addFaculty(f1);
//        facultyController.addFaculty(f2);
//    }
//    @AfterEach
//    void cleanUp() {
//        Faculty f1 = facultyController.findByNameOrColor("Faculty", "blue");
//        Faculty f2 = facultyController.findByNameOrColor("Shmaculty", "white");
//        facultyController.deleteFaculty(f1.getId());
//        facultyController.deleteFaculty(f2.getId());
//    }

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

    @Test
    void removeFaculty() {
        Faculty f = new Faculty("Faculty", "color");
        facultyController.addFaculty(f);
        ResponseEntity response = testRestTemplate.exchange(
                "http://localhost:" + port + "/school/faculty/remove?id=" + f.getId(),
                HttpMethod.DELETE,
                new HttpEntity<>(f.getId()),
                Void.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void editFaculty() {
        Faculty f = new Faculty("Faculty", "color");
        facultyController.addFaculty(f);
        f.setName("Факультет");

        ResponseEntity response = testRestTemplate.exchange(
                "http://localhost:" + port + "/school/faculty/edit",
                HttpMethod.PUT,
                new HttpEntity<>(f),
                Faculty.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(f.getName().equals("Факультет"));
        facultyController.deleteFaculty(f.getId());
    }

    @Test
    void sortByColor() {
        Faculty f1 = new Faculty("Faculty", "blue");
        Faculty f2 = new Faculty("Shmaculty", "white");
        facultyController.addFaculty(f1);
        facultyController.addFaculty(f2);
        String requestedColor = "white";
        assertThat(this.testRestTemplate.getForObject("http://localhost:" + port + "/school/faculty/sort?color=" + requestedColor, String.class))
                .contains(requestedColor);
        facultyController.deleteFaculty(f1.getId());
        facultyController.deleteFaculty(f2.getId());
    }

    @Test
    void findByNameOrColor() {
        Faculty f1 = new Faculty("Faculty", "blue");
        Faculty f2 = new Faculty("Shmaculty", "white");
        facultyController.addFaculty(f1);
        facultyController.addFaculty(f2);
        String requestedName = "faculty";
        String requestedColor = "WhItE";
        assertThat(this.testRestTemplate.getForObject("http://localhost:" + port + "/school/faculty/findBy?name=" + requestedName, String.class))
                .isNotNull()
                .containsIgnoringCase(requestedName);
        assertThat(this.testRestTemplate.getForObject("http://localhost:" + port + "/school/faculty/findBy?color=" + requestedColor, String.class))
                .isNotNull()
                .containsIgnoringCase(requestedColor);
        facultyController.deleteFaculty(f1.getId());
        facultyController.deleteFaculty(f2.getId());
    }

    @Test
    void getAllStudents() {
        Faculty f1 = new Faculty("Faculty", "blue");
        facultyController.addFaculty(f1);
        Student s = new Student("Student", 13, f1);
        studentController.addStudent(s);
        assertThat(this.testRestTemplate.getForObject("http://localhost:" + port + "/school/faculty/getAllStudents?name=" + f1.getName(), String.class))
                .isNotNull()
                .contains(s.getName());
        studentController.deleteStudent(s.getId());
        facultyController.deleteFaculty(f1.getId());
    }
}
