package ru.hogwarts.school.controller;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.FacultyRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
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
        assertThat(s.getFaculty().getName().equals("Слизерин"));
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
        int from = 16;
        int to = 18;

        Faculty f = new Faculty("Гриффиндор", "красный");
        facultyRepository.save(f);

        Student s = new Student();
        s.setName("Barbie");
        s.setAge(17);
        s.setFaculty(f);
        studentController.addStudent(s);

        assertThat(this.testRestTemplate.getForObject("http://localhost:" + port + "/school/student/findBetweenAge?from=" + from + "&to=" + to, String.class))
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

        assertThat(this.testRestTemplate.getForObject("http://localhost:" + port + "/school/student/getFaculty?name=" + s.getName(), String.class))
                .contains("Гриффиндор");
    }

    @Test
    void uploadAvatar() {
        Faculty f = new Faculty("Гриффиндор", "красный");
        facultyRepository.save(f);

        Student s = new Student();
        s.setName("Barbie");
        s.setAge(17);
        s.setFaculty(f);
        studentController.addStudent(s);

        File avatarFile = new File("C:/Users/Снежана/IdeaProjects/school/src/test/resources/test.jpg");
        FileSystemResource fileResource = new FileSystemResource(avatarFile);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("avatar", fileResource);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = testRestTemplate.exchange(
                "/school/student/" + s.getId() + "/avatar",
                HttpMethod.POST,
                requestEntity,
                String.class
        );
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertTrue(response.getBody().contains("добавлен аватар"));
    }

    @Test
    void downloadPreview() throws IOException {
        Faculty f = new Faculty("Гриффиндор", "красный");
        facultyRepository.save(f);

        Student s = new Student();
        s.setName("Barbie");
        s.setAge(17);
        s.setFaculty(f);
        studentController.addStudent(s);

        byte[] bytes = Files.readAllBytes(Path.of("C:/Users/Снежана/IdeaProjects/school/src/test/resources/test.jpg"));

        Avatar studentAvatar = new Avatar();
        studentAvatar.setStudent(s);
        studentAvatar.setFilePath("/1.jpg");
        studentAvatar.setFileSize(studentAvatar.getFileSize());
        studentAvatar.setMediaType("image/jpg");
        studentAvatar.setPreview(bytes);

        avatarRepository.save(studentAvatar);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(studentAvatar.getMediaType()));
        headers.setContentLength(studentAvatar.getPreview().length);

        ResponseEntity<byte[]> response = testRestTemplate.getForEntity(
                "http://localhost:" + port + "/school/student/" + s.getId() + "/avatar/preview",
                byte[].class
        );
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void downloadAvatarTest() throws IOException {
        Faculty f = new Faculty("Гриффиндор", "красный");
        facultyRepository.save(f);

        Student s = new Student();
        s.setName("Barbie");
        s.setAge(17);
        s.setFaculty(f);
        studentController.addStudent(s);

        File avatarFile = new File("C:/Users/Снежана/IdeaProjects/school/src/test/resources/test.jpg");
        byte[] bytes = Files.readAllBytes(Path.of("C:/Users/Снежана/IdeaProjects/school/src/test/resources/test.jpg"));

        Avatar studentAvatar = new Avatar();
        studentAvatar.setStudent(s);
        studentAvatar.setFilePath(avatarFile.getAbsolutePath());
        studentAvatar.setFileSize(avatarFile.length());
        studentAvatar.setMediaType("image/jpg");
        studentAvatar.setPreview(bytes);

        avatarRepository.save(studentAvatar);

        ResponseEntity<byte[]> response = testRestTemplate.getForEntity(
                "/school/student/" + s.getId() + "/getAvatar",
                byte[].class
        );
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertNotNull(response.getBody());
        assertEquals("image/jpg", response.getHeaders().getContentType().toString());
        assertEquals(bytes.length, response.getHeaders().getContentLength());
    }
}