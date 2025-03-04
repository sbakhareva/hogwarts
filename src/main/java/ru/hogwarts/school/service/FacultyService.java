package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;

import java.util.HashMap;

@Service
public class FacultyService {

    private Long counter = 1L;
    private final HashMap<Long, Faculty> faculties;

    public FacultyService(HashMap<Long, Faculty> faculties) {
        this.faculties = faculties;
    }

    public Faculty addFaculty(Faculty faculty) {
        faculty.setId(++counter);
        faculties.put(faculty.getId(), faculty);
        return faculty;
    }

    public Faculty getFacultyByID(Long id) {
        return faculties.get(id);
    }

    public Faculty updateFaculty(Faculty faculty) {
        faculties.put(faculty.getId(), faculty);
        return faculty;
    }

    public Faculty removeFaculty(Long id) {
        return faculties.remove(id);
    }
}
