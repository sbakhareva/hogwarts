package ru.hogwarts.school.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

@RestController
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addStudent(@RequestBody Student student) {
        if (student.getName().isBlank() || student.getAge() == 0) {
            return ResponseEntity.badRequest().body("Переданы некорректные данные!");
        }
        studentService.addStudent(student);
        return ResponseEntity.ok("Студент с идентификатором" + student.getId() + " добавлен в список!");
    }

    @GetMapping("/get")
    public ResponseEntity<Student> getStudentByID(@RequestParam("id") Long id) {
        Student s = studentService.getStudentByID(id);
        if (s == null) {
            ResponseEntity.badRequest().body("Студента с идентификатором " + id + " нет в базе!");
        }
        return ResponseEntity.ok(s);
    }

    @GetMapping("/remove")
    public ResponseEntity<String> deleteStudent(@PathVariable Long id) {
        Student s = studentService.getStudentByID(id);
        if (s == null) {
            ResponseEntity.badRequest().body("Студента с идентификатором " + id + " нет в базе!");
        }
        return ResponseEntity.ok().body("Студент с идентификатором " + id + " удален из списка!");
    }

    @PutMapping("/edit")
    public ResponseEntity<String> editStudent(Student student) {
        if (!studentService.getAllStudents().containsKey(student.getId())) {
            throw new RuntimeException("Студента с таким идентификатором нет в базе!");
        }
        studentService.updateStudent(student);
        return ResponseEntity.ok("Данные студента обновлены!");
    }
}
