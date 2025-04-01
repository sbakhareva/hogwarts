package ru.hogwarts.school.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.dialect.unique.CreateTableUniqueDelegate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.dto.StudentDTO;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
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

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping("/add")
    public Student addStudent(@RequestBody Student student) {
        studentService.addStudent(student);
        return student;
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
        return ResponseEntity.ok("Студент с идентификатором " + id + " удален из списка!");
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
    public List<Student> findDyAgeBetween(@RequestParam("from") int from,
                                          @RequestParam("to") int to) {
        return studentService.findByAgeBetween(from, to);
    }

    @GetMapping("/getFaculty")
    public Faculty getStudentsFaculty(@RequestParam("name") String name) {
        return studentService.getStudentsFaculty(name);
    }

    @PostMapping(value = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadAvatar(@PathVariable Long id,
                                               @RequestParam MultipartFile avatar) {
        if (avatar.getSize() > 1024 * 1024 * 5) {
            return ResponseEntity.badRequest().body("Размер файла слишком большой!");
        }
        try {
            studentService.uploadAvatar(id, avatar);
        } catch (IOException e) {
            System.out.println("Текст ошибки!");
        }
        return ResponseEntity.ok("Для студента " + studentService.findStudent(id).get().getName() + " добавлен аватар!");
    }

    @GetMapping(value = "/{id}/avatar/preview")
    public ResponseEntity<byte[]> downloadPreview(@PathVariable Long id) {
        Avatar avatar = studentService.getAvatar(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(avatar.getMediaType()));
        headers.setContentLength(avatar.getPreview().length);

        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(avatar.getPreview());
    }

    @GetMapping(value = "/{id}/getAvatar")
    public void downloadAvatar(@PathVariable Long id,
                               HttpServletResponse response) throws IOException {
        Avatar avatar = studentService.getAvatar(id);

        Path path = Path.of(avatar.getFilePath());
        try (InputStream is = Files.newInputStream(path);
             OutputStream os = response.getOutputStream()) {
            response.setContentType(avatar.getMediaType());
            response.setContentLength((int) avatar.getFileSize());
            is.transferTo(os);
        }
    }

    @GetMapping("/getNumber")
    public String getNumberOfStudents() {
        return studentService.getNumberOfStudents();
    }

    @GetMapping("/getAvgAge")
    public String getAvgAge() {
        return studentService.getAvgAge();
    }

    @GetMapping("/getLastFiveStudents")
    public List<StudentDTO> getLastFiveStudents() {
        return studentService.getLastFiveStudents();
    }
}
