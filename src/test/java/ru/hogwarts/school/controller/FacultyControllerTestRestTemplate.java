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
import org.springframework.test.context.ActiveProfiles;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class FacultyControllerTestRestTemplate {

    @LocalServerPort
    private int port;
    @Autowired
    private FacultyController facultyController;
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private StudentController studentController;

    @Test
    void contextLoads() {
        assertThat(facultyController).isNotNull();
    }

    @Test
    void addFaculty() {
        Faculty f = new Faculty("faculty", "color");
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
        Faculty f = new Faculty("faculty", "color");
        facultyController.addFaculty(f);
        Assertions
                .assertThat(this.testRestTemplate.getForObject("http://localhost:" + port + "/school/faculty/get?id=" + f.getId(), String.class))
                .contains(f.getId().toString())
                .contains(f.getName())
                .contains(f.getColor());
    }

    @Test
    void removeFaculty() {
        Faculty f = new Faculty("faculty", "color");
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
        Faculty f = new Faculty("faculty", "color");
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
    }

    @Test
    void sortByColor() {
        Faculty f1 = new Faculty("faculty", "blue");
        Faculty f2 = new Faculty("Shmaculty", "white");
        facultyController.addFaculty(f1);
        facultyController.addFaculty(f2);
        String requestedColor = "white";
        assertThat(this.testRestTemplate.getForObject("http://localhost:" + port + "/school/faculty/sort?color=" + requestedColor, String.class))
                .contains(requestedColor);
    }

    @Test
    void findByNameOrColor() {
        Faculty f1 = new Faculty("faculty", "blue");
        Faculty f2 = new Faculty("Shmaculty", "white");
        facultyController.addFaculty(f1);
        facultyController.addFaculty(f2);
        String requestedName = "faculty";
        String requestedColor = "WhItE";
        assertThat(this.testRestTemplate.getForObject("http://localhost:" + port + "/school/faculty/find-by?name=" + requestedName, String.class))
                .isNotNull()
                .containsIgnoringCase(requestedName);
        assertThat(this.testRestTemplate.getForObject("http://localhost:" + port + "/school/faculty/find-by?color=" + requestedColor, String.class))
                .isNotNull()
                .containsIgnoringCase(requestedColor);
    }

    @Test
    void getAllStudents() {
        Faculty faculty = new Faculty("faculty", "blue");
        facultyController.addFaculty(faculty);
        Student s = new Student("Student", 17, faculty);
        studentController.addStudent(s);
        assertThat(this.testRestTemplate.getForObject("http://localhost:" + port + "/school/faculty/get-all-students?name=" + faculty.getName(), String.class))
                .isNotNull()
                .contains(s.getName());
    }
}
