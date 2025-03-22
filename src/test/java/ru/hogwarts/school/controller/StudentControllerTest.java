package ru.hogwarts.school.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.FacultyRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(studentController).isNotNull();
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
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void getStudents() {
        assertThat(this.testRestTemplate.getForObject("http://localhost:" + port + "/school/student/getAll", String.class))
                .isNotNull();
    }

    @Test
    void getStudentById() {
        Student s = new Student();
        s.setName("Harry");
        s.setAge(11);
        s.setFaculty(facultyRepository.findByNameIgnoreCaseContains("Гриффиндор").get());
        studentController.addStudent(s);
        assertThat(this.testRestTemplate.getForObject("http://localhost:" + port + "/school/student/get?id=" + s.getId(), String.class))
                .contains(s.getId().toString())
                .contains((s.getName()));
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
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
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
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(s.getFaculty().getName().equals("Слизерин"));
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
        assertThat(this.testRestTemplate.getForObject("http://localhost:" + port + "/school/student/sort?age=" + age, String.class))
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
        assertThat(this.testRestTemplate.getForObject("http://localhost:" + port + "/school/student/findBetweenAge?from=" + from + "&to=" + to, String.class))
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
        assertThat(this.testRestTemplate.getForObject("http://localhost:" + port + "/school/student/getFaculty?name=" + s.getName(), String.class))
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

        byte[] bytes = Files.readAllBytes(Path.of("C:/Users/Снежана/IdeaProjects/school/src/test/resources/test.jpg"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        Avatar studentAvatar = new Avatar();
        studentAvatar.setStudent(s);
        studentAvatar.setFilePath("/1.jpg");
        studentAvatar.setFileSize(studentAvatar.getFileSize());
        studentAvatar.setMediaType("image/jpg");
        studentAvatar.setPreview(bytes);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("avatars", new FileSystemResource("src/test/resources/test.jpg"));
        HttpEntity<MultiValueMap<String, Object>> responseEntity = new HttpEntity<>(body, headers);
//
//        ResponseEntity response = testRestTemplate.exchange(
//                "http://localhost:" + port + "/school/student/{" + s.getId() + "}/avatar",
//                HttpMethod.POST,
//                responseEntity,
//                String.class
//        );
//        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}