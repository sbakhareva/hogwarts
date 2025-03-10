package ru.hogwarts.school.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.dto.StudentDTO;
import ru.hogwarts.school.model.Faculty;
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
}
