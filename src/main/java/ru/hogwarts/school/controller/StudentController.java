package ru.hogwarts.school.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/school/student")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }


    @GetMapping("/getAll")
    public ResponseEntity<List<Student>> getStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @PostMapping("/add")
    public ResponseEntity<Student> addStudent(@RequestParam("/name") String name,
                                              @RequestParam("/age") int age) {
        Student s = new Student(name, age);
        studentService.addStudent(s);
        return ResponseEntity.ok(s);
    }

    @GetMapping("/get")
    public ResponseEntity<Optional <Student>> getStudentByID(@RequestParam("id") Long id) {
        return ResponseEntity.ok(studentService.getStudentByID(id));
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
