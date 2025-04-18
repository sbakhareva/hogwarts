package ru.hogwarts.school.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Profile;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;


import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class StudentControllerTestRestTemplate {

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
        assertThat(studentController).isNotNull();
    }

    @Test
    void addStudent() {
        Faculty f = new Faculty("Гриффиндор", "красный");
        facultyRepository.save(f);

        Student s = new Student();
        s.setName("Harry");
        s.setAge(11);
        s.setFaculty(f);
        ResponseEntity response = testRestTemplate.exchange(
                "http://localhost:" + port + "/school/student/add",
                HttpMethod.POST,
                new HttpEntity<>(s),
                Student.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void getStudents() {
        Faculty f = new Faculty("Гриффиндор", "красный");
        facultyRepository.save(f);

        Student s = new Student();
        s.setName("Harry");
        s.setAge(11);
        s.setFaculty(f);
        studentController.addStudent(s);

        assertThat(this.testRestTemplate.getForObject("http://localhost:" + port + "/school/student/getAll", String.class))
                .isNotNull();
    }

    @Test
    void getStudentById() {
        Faculty f = new Faculty("Гриффиндор", "красный");
        facultyRepository.save(f);

        Student s = new Student();
        s.setName("Harry");
        s.setAge(11);
        s.setFaculty(f);
        studentController.addStudent(s);

        assertThat(this.testRestTemplate.getForObject("http://localhost:" + port + "/school/student/get?id=" + s.getId(), String.class))
                .contains(s.getId().toString())
                .contains((s.getName()));
    }

    @Test
    void deleteStudent() {
        Faculty f = new Faculty("Гриффиндор", "красный");
        facultyRepository.save(f);

        Student s = new Student();
        s.setName("Harry");
        s.setAge(11);
        s.setFaculty(f);
        studentController.addStudent(s);

        ResponseEntity response = testRestTemplate.exchange(
                "http://localhost:" + port + "/school/student/remove?id=" + s.getId(),
                HttpMethod.DELETE,
                new HttpEntity<>(s.getId()),
                Void.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void editStudent() {
        Faculty f = new Faculty("Гриффиндор", "красный");
        facultyRepository.save(f);

        Student s = new Student();
        s.setName("Harry");
        s.setAge(11);
        s.setFaculty(f);
        studentController.addStudent(s);

        Faculty f2 = new Faculty("Слизерин", "зеленый");
        facultyRepository.save(f2);
        s.setFaculty(f2);

        ResponseEntity response = testRestTemplate.exchange(
                "http://localhost:" + port + "/school/student/edit",
                HttpMethod.PUT,
                new HttpEntity<>(s),
                String.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.toString().contains("Слизерин"));
    }

    @Test
    void sortByAge() {
        Faculty f = new Faculty("Гриффиндор", "красный");
        facultyRepository.save(f);

        Student s = new Student();
        s.setName("Barbie");
        s.setAge(17);
        s.setFaculty(f);
        studentController.addStudent(s);

        int age = 17;
        assertThat(this.testRestTemplate.getForObject("http://localhost:" + port + "/school/student/sort?age=" + age, String.class))
                .contains("Barbie");
    }

    @Test
    void findBetweenAge() {
        int from = 17;
        int to = 19;

        Faculty f = new Faculty("Гриффиндор", "красный");
        facultyRepository.save(f);

        Student s = new Student();
        s.setName("Barbie");
        s.setAge(18);
        s.setFaculty(f);
        studentController.addStudent(s);

        assertThat(this.testRestTemplate.getForObject("http://localhost:" + port + "/school/student/find-between-age?from=" + from + "&to=" + to, String.class))
                .contains("Barbie");
    }

    @Test
    void getFaculty() {
        Faculty f = new Faculty("Гриффиндор", "красный");
        facultyRepository.save(f);

        Student s = new Student();
        s.setName("Barbie");
        s.setAge(17);
        s.setFaculty(f);
        studentController.addStudent(s);

        assertThat(this.testRestTemplate.getForObject("http://localhost:" + port + "/school/student/get-faculty?name=" + s.getName(), String.class))
                .contains("Гриффиндор");
    }
}