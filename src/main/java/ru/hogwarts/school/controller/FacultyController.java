package ru.hogwarts.school.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/school/faculty")
public class FacultyController {

    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<Faculty>> getFaculties() {
        return ResponseEntity.ok(facultyService.getAllFaculties());
    }

    @PostMapping("/add")
    public ResponseEntity<Faculty> addFaculty(@RequestParam("/name") String name,
                                              @RequestParam("/color") String color) {
        Faculty f = new Faculty(name, color);
        facultyService.addFaculty(f);
        return ResponseEntity.ok(f);
    }

    @GetMapping("/get")
    public ResponseEntity<Optional<Faculty>> getFacultyByID(@RequestParam("id") Long id) {
        return ResponseEntity.ok(facultyService.getFacultyByID(id));
    }

    @DeleteMapping("/remove")
    public ResponseEntity<String> deleteFaculty(@RequestParam("id") Long id) {
        facultyService.removeFaculty(id);
        return ResponseEntity.ok("Студент с идентификатором " + id + " удален из списка!");
    }

    @PutMapping("/edit")
    public ResponseEntity<Faculty> editFaculty(@RequestBody Faculty faculty) {
        facultyService.updateFaculty(faculty);
        return ResponseEntity.ok(faculty);
    }

    @GetMapping("/sort")
    public ResponseEntity<List<Faculty>> sortByColor(@RequestParam("/color") String color) {
        return ResponseEntity.ok(facultyService.sortByColor(color));
    }

    @GetMapping("/findBy")
    public Faculty findByNameOrColor(@RequestParam(value = "name", required = false) String name,
                                     @RequestParam(value = "color", required = false) String color) {
        return facultyService.findByNameOrColor(name, color);
    }

    @GetMapping("/getAllStudents")
    public List<Student> getAllStudentsOfFaculty(@RequestParam("name") String name) {
        return facultyService.getAllStudentsOfFaculty(name);
    }
}
