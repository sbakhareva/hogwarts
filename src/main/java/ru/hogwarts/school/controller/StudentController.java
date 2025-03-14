package ru.hogwarts.school.controller;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.dto.StudentDTO;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.AvatarService;
import ru.hogwarts.school.service.StudentService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/school/student")
public class StudentController {

    private final StudentService studentService;

    private final AvatarService avatarService;

    public StudentController(StudentService studentService, AvatarService avatarService) {
        this.studentService = studentService;
        this.avatarService = avatarService;
    }

    @PostMapping("/add")
    public Student addStudent(@RequestParam("/name") String name,
                              @RequestParam("/age") int age,
                              @RequestBody Faculty faculty) {
        Student s = new Student(name, age, faculty);
        studentService.addStudent(s);
        return s;
    }

    @GetMapping("/get")
    public Optional<StudentDTO> getStudentByID(@RequestParam("id") Long id) {
        return studentService.getStudentByID(id);
    }

    @GetMapping("/getAll")
    public List<StudentDTO> getStudents() {
        return studentService.getAllStudents();
    }

    @DeleteMapping("/remove")
    public ResponseEntity<String> deleteStudent(@RequestParam("id") Long id) {
        studentService.removeStudent(id);
        return ResponseEntity.ok().body("Студент с идентификатором " + id + " удален из списка!");
    }

    @PutMapping("/edit")
    public Student editStudent(@RequestBody Student student) {
        studentService.updateStudent(student);
        return student;
    }

    @GetMapping("/sort")
    public List<Student> sortStudentsByAge(@RequestParam("age") int age) {
        return studentService.sortByAge(age);
    }

    @GetMapping("/findBetweenAge")
    public List<Student> findDyAgeBetween(@RequestParam("from") int minAge,
                                          @RequestParam("to") int maxAge) {
        return studentService.findByAgeBetween(minAge, maxAge);
    }

    @GetMapping("/getFaculty")
    public Faculty getStudentsFaculty(@RequestParam("name") String name) {
        return studentService.getStudentsFaculty(name);
    }

    @PostMapping(value = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadAvatar(@PathVariable Long id,
                                               @RequestParam MultipartFile avatar) throws IOException {
        if (avatar.getSize() > 1024 * 300) {
            return ResponseEntity.badRequest().body("Размер файла слишком большой!");
        }
        avatarService.uploadAvatar(id, avatar);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{id}/avatar/preview")
    public ResponseEntity<byte[]> downloadAvatar(@PathVariable Long id) {
        Avatar avatar = avatarService.getAvatar(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(avatar.getMediaType()));
        headers.setContentLength(avatar.getPreview().length);

        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(avatar.getPreview());
    }

    @GetMapping(value = "/{id}/getAvatar")
    public void downloadCover(@PathVariable Long id,
                              HttpServletResponse response) throws IOException {
        Avatar avatar = avatarService.getAvatar(id);

        Path path = Path.of(avatar.getFilePath());
        try (InputStream is = Files.newInputStream(path);
             OutputStream os = response.getOutputStream())
        {
            response.setContentType(avatar.getMediaType());
            response.setContentLength((int) avatar.getFileSize());
            is.transferTo(os);
        }
    }
}
