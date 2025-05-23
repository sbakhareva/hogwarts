package ru.hogwarts.school.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;

import java.util.List;

@RestController
@RequestMapping("/school/faculty")
public class FacultyController {

    private FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @GetMapping("/get-all")
    public List<Faculty> getFaculties() {
        return facultyService.getAllFaculties();
    }

    @PostMapping("/add")
    public Faculty addFaculty(@RequestBody Faculty faculty) {
        facultyService.addFaculty(faculty);
        return faculty;
    }

    @GetMapping("/get")
    public Faculty getFacultyByID(@RequestParam("id") Long id) {
        return facultyService.getFacultyByID(id);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<String> deleteFaculty(@RequestParam("id") Long id) {
        facultyService.removeFaculty(id);
        return ResponseEntity.ok("Факультет с идентификатором " + id + " удален из списка!");
    }

    @PutMapping("/edit")
    public Faculty editFaculty(@RequestBody Faculty faculty) {
        facultyService.editFaculty(faculty);
        return faculty;
    }

    @GetMapping("/sort")
    public List<Faculty> sortByColor(@RequestParam("color") String color) {
        return facultyService.sortByColor(color);
    }

    @GetMapping("/find-by")
    public List<Faculty> findByNameOrColor(@RequestParam(value = "name", required = false) String name,
                                           @RequestParam(value = "color", required = false) String color) {
        return facultyService.findByNameOrColor(name, color);
    }

    @GetMapping("/get-all-students")
    public List<Student> getAllStudentsOfFaculty(@RequestParam("name") String name) {
        return facultyService.getAllStudentsOfFaculty(name);
    }

    @GetMapping("/get-longest-faculty-name")
    public String getTheLongestFacultyName() {
        return facultyService.getTheLongestFacultyName();
    }

    @GetMapping("/get-sum")
    public String getSum() {
        return facultyService.getSum();
    }
}
