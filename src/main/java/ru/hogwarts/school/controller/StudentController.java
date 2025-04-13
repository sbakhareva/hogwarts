package ru.hogwarts.school.controller;

import jakarta.websocket.server.PathParam;
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
    public Optional<StudentDTO> getStudentByID(@PathParam("id") Long id) {
        return studentService.getStudentByID(id);
    }

    @GetMapping("/get-all")
    public List<StudentDTO> getStudents() {
        return studentService.getAllStudents();
    }

    @DeleteMapping("/remove")
    public String deleteStudent(@RequestParam("id") Long id) {
        studentService.removeStudent(id);
        return "Студент с идентификатором " + id + " удален из списка!";
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

    @GetMapping("/find-between-age")
    public List<Student> findDyAgeBetween(@RequestParam int from,
                                          @RequestParam int to) {
        return studentService.findByAgeBetween(from, to);
    }

    @GetMapping("/get-faculty")
    public Faculty getStudentsFaculty(@RequestParam("name") String name) {
        return studentService.getStudentsFaculty(name);
    }

    @GetMapping("/get-number")
    public String getNumberOfStudents() {
        return studentService.getNumberOfStudents();
    }

    @GetMapping("/get-avg-age")
    public String getAvgAge() {
        return studentService.getAvgAge();
    }

    @GetMapping("/get-last-five-students")
    public List<StudentDTO> getLastFiveStudents() {
        return studentService.getLastFiveStudents();
    }
}
