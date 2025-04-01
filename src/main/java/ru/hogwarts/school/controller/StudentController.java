package ru.hogwarts.school.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.dto.StudentDTO;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

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
