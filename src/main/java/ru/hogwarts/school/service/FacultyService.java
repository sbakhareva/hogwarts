package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.model.exception.EmptyStorageException;
import ru.hogwarts.school.model.exception.InvalidValueException;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;

    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public boolean storageIsNotEmpty() {
        return true;
    }

    public void addFaculty(Faculty faculty) {
        if (faculty.getName().isBlank() || faculty.getColor().isBlank()) {
            throw new InvalidValueException();
        }
        facultyRepository.save(faculty);
    }

    public Optional<Faculty> getFacultyByID(Long id) {
        List<Faculty> faculties = facultyRepository.findAll();
        if (faculties.isEmpty()) {
            throw new EmptyStorageException();
        }
        if (!facultyRepository.existsById(id)) {
            throw new InvalidValueException();
        }
        return facultyRepository.findById(id);
    }

    public void updateFaculty(Faculty faculty) {
        List<Faculty> faculties = facultyRepository.findAll();
        if (faculties.isEmpty()) {
            throw new EmptyStorageException();
        }
        if (!facultyRepository.existsById(faculty.getId())) {
            throw new InvalidValueException();
        }
        facultyRepository.save(faculty);
    }

    public void removeFaculty(Long id) {
        List<Faculty> faculties = facultyRepository.findAll();
        if (faculties.isEmpty()) {
            throw new EmptyStorageException();
        }
        if (!facultyRepository.existsById(id)) {
            throw new InvalidValueException();
        }
        facultyRepository.deleteById(id);
    }

    public List<Faculty> getAllFaculties() {
        List<Faculty> faculties = facultyRepository.findAll();
        if (faculties.isEmpty()) {
            throw new EmptyStorageException();
        }
        return Collections.unmodifiableList(faculties);
    }

    public List<Faculty> sortByColor(String color) {
        List<Faculty> faculties = facultyRepository.findAll();
        if (faculties.isEmpty()) {
            throw new EmptyStorageException();
        }
        if (color.isBlank()) {
            throw new InvalidValueException();
        }
        return faculties.stream()
                .filter(q -> q.getColor().contains(color))
                .toList();
    }

    public Faculty findByNameOrColor(String name, String color) {
        List<Faculty> faculties = facultyRepository.findAll();
        if (faculties.isEmpty()) {
            throw new EmptyStorageException();
        }
        return facultyRepository.findByNameIgnoreCaseOrColorIgnoreCase(name, color);
    }

    public List<Student> getAllStudentsOfFaculty(String name) {
        List<Faculty> faculties = facultyRepository.findAll();
        if (faculties.isEmpty()) {
            throw new EmptyStorageException();
        }
        Optional<Faculty> f = facultyRepository.findByNameIgnoreCaseContains(name);
        if (f.isEmpty()) {
            throw new InvalidValueException();
        }
        return f.get().getStudents();
    }
}
