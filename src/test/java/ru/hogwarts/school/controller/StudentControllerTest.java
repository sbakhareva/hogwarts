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
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.FacultyRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

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
    @Autowired
    private AvatarRepository avatarRepository;

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
                .assertThat(this.testRestTemplate.getForObject("http://localhost:" + port + "/school/student/getAll", String.class))
                .isNotNull();
    }

    @Test
    void getStudentById() {
        Student s = new Student();
        s.setName("Harry");
        s.setAge(11);
        s.setFaculty(facultyRepository.findByNameIgnoreCaseContains("Гриффиндор").get());
        studentController.addStudent(s);
        Assertions
                .assertThat(this.testRestTemplate.getForObject("http://localhost:" + port + "/school/student/get?id=" + s.getId(), Student.class))
                .isNotNull();
        studentController.deleteStudent(s.getId());
    }

    @Test
    void deleteStudent() {
        Student s = new Student();
        s.setName("Harry");
        s.setAge(11);
        s.setFaculty(facultyRepository.findByNameIgnoreCaseContains("Гриффиндор").get());
        studentController.addStudent(s);
        ResponseEntity response = testRestTemplate.exchange(
                "http://localhost:" + port + "/school/student/remove?id=" + s.getId(),
                HttpMethod.DELETE,
                new HttpEntity<>(s.getId()),
                Void.class
        );
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void editStudent() {
        Student s = new Student();
        s.setName("Harry");
        s.setAge(11);
        s.setFaculty(facultyRepository.findByNameIgnoreCaseContains("Гриффиндор").get());
        studentController.addStudent(s);
        s.setFaculty(facultyRepository.findByNameIgnoreCaseContains("Слизерин").get());
        ResponseEntity response = testRestTemplate.exchange(
                "http://localhost:" + port + "/school/student/edit",
                HttpMethod.PUT,
                new HttpEntity<>(s),
                Student.class
        );
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        studentController.deleteStudent(s.getId());
    }

    @Test
    void sortByAge() {
        Student s = new Student();
        s.setName("Barbie");
        s.setAge(17);
        s.setFaculty(facultyRepository.findByNameIgnoreCaseContains("Гриффиндор").get());
        studentController.addStudent(s);
        int age = 17;
        Assertions
                .assertThat(this.testRestTemplate.getForObject("http://localhost:" + port + "/school/student/sort?age=" + age, String.class))
                .contains("Barbie");

        studentController.deleteStudent(s.getId());
    }

    @Test
    void findBetweenAge() {
        int from = 16;
        int to = 18;
        Student s = new Student();
        s.setName("Barbie");
        s.setAge(17);
        s.setFaculty(facultyRepository.findByNameIgnoreCaseContains("Гриффиндор").get());
        studentController.addStudent(s);
        Assertions
                .assertThat(this.testRestTemplate.getForObject("http://localhost:" + port + "/school/student/findBetweenAge?from=" + from + "&to=" + to, String.class))
                .contains("Barbie");

        studentController.deleteStudent(s.getId());
    }

    @Test
    void getFaculty() {
        Faculty f = facultyRepository.findByNameIgnoreCaseContains("Гриффиндор").get();
        Student s = new Student();
        s.setName("Barbie");
        s.setAge(17);
        s.setFaculty(f);
        studentController.addStudent(s);
        Assertions
                .assertThat(this.testRestTemplate.getForObject("http://localhost:" + port + "/school/student/getFaculty?name=" + s.getName(), String.class))
                .contains("Гриффиндор");
        studentController.deleteStudent(s.getId());
    }

    @Test
    void uploadAvatar() throws IOException {
        Faculty f = facultyRepository.findByNameIgnoreCaseContains("Гриффиндор").get();
        Student s = new Student();
        s.setName("Barbie");
        s.setAge(17);
        s.setFaculty(f);
        studentController.addStudent(s);

        byte[] bytes = Files.readAllBytes(Path.of("src/test/resources/test.jpg"));

        Avatar studentAvatar = new Avatar();
        studentAvatar.setStudent(s);
        studentAvatar.setFilePath("/test.jpg");
        studentAvatar.setFileSize(11L);
        studentAvatar.setMediaType("image/jpg");
        studentAvatar.setPreview(bytes);
        avatarRepository.save(studentAvatar);

        ResponseEntity response = testRestTemplate.exchange(
                "http://localhost:" + port + "/school/student/{" + s.getId() + "}/avatar",
                HttpMethod.POST,
                new HttpEntity<>(studentAvatar),
                Avatar.class
        );
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        studentController.deleteStudent(s.getId());
    }
}