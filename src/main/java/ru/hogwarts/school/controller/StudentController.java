package ru.hogwarts.school.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/school/student")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }


    @GetMapping("/getAll")
    public ResponseEntity<HashMap<Long, Student>> getStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }
    @PostMapping("/add")
    public ResponseEntity<String> addStudent(@RequestBody Student student) {
        studentService.addStudent(student);
        return ResponseEntity.ok("Студент с идентификатором" + student.getId() + " добавлен в список!");
    }

    @GetMapping("/get")
    public ResponseEntity<String> getStudentByID(@RequestParam("id") Long id) {
        Student s = studentService.getStudentByID(id);
        if (s == null) {
            return ResponseEntity.badRequest().body("Студента с идентификатором " + id + " нет в базе!");
        }
        return ResponseEntity.ok(s.toString());
    }

    @DeleteMapping("/remove")
    public ResponseEntity<String> deleteStudent(@RequestParam("id") Long id) {
        studentService.removeStudent(id);
        return ResponseEntity.ok().body("Студент с идентификатором " + id + " удален из списка!");
    }

    @PutMapping("/edit")
    public ResponseEntity<Student> editStudent(@RequestBody Student student) {
        studentService.updateStudent(student);
        return ResponseEntity.ok(student);
    }

    @GetMapping("/sort")
    public ResponseEntity<List<Student>> sortStudentsByAge(@RequestParam("age") int age) {
        return ResponseEntity.ok(studentService.sortByAge(age));
    }
}
