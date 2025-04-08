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
import ru.hogwarts.school.service.AvatarService;
import ru.hogwarts.school.service.StudentService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AvatarControllerTest {
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private AvatarRepository avatarRepository;
    @Autowired
    private AvatarService avatarService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private FacultyRepository facultyRepository;



        @Test
    void uploadAvatar() {
        Faculty f = new Faculty("Гриффиндор", "красный");
        facultyRepository.save(f);

        Student s = new Student();
        s.setName("Barbie");
        s.setAge(17);
        s.setFaculty(f);
        studentService.addStudent(s);

        File avatarFile = new File("C:/Users/Снежана/IdeaProjects/school/src/test/resources/test.jpg");
        FileSystemResource fileResource = new FileSystemResource(avatarFile);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("avatar", fileResource);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = testRestTemplate.exchange(
                "/school/avatar/" + s.getId() + "/upload-avatar",
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
        studentService.addStudent(s);

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
                "http://localhost:" + port + "/school/avatar/" + s.getId() + "/avatar/download-preview",
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
        studentService.addStudent(s);

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
                "/school/avatar/" + s.getId() + "/download-avatar",
                byte[].class
        );
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertNotNull(response.getBody());
        assertEquals("image/jpg", response.getHeaders().getContentType().toString());
        assertEquals(bytes.length, response.getHeaders().getContentLength());
    }
}
