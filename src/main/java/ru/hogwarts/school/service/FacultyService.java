package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.model.exception.EmptyStorageException;
import ru.hogwarts.school.model.exception.InvalidValueException;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;

    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public boolean storageIsEmpty() {
        return facultyRepository.findAll().isEmpty();
    }

    public void addFaculty(Faculty faculty) {
        Optional.of(facultyRepository.save(faculty)).orElseThrow(InvalidValueException::new);
    }

    public Faculty getFacultyByID(Long id) {
        if (storageIsEmpty()) {
            throw new EmptyStorageException();
        }
        return facultyRepository.findById(id).orElseThrow(InvalidValueException::new);
    }

    public void editFaculty(Faculty faculty) {
        if (storageIsEmpty()) {
            throw new EmptyStorageException();
        }
        Optional.of(facultyRepository.save(faculty)).orElseThrow(InvalidValueException::new);
    }

    public void removeFaculty(Long id) {
        if (storageIsEmpty()) {
            throw new EmptyStorageException();
        }
        Faculty f = facultyRepository.findById(id).orElseThrow(InvalidValueException::new);
        facultyRepository.delete(f);
    }

    public List<Faculty> getAllFaculties() {
        if (storageIsEmpty()) {
            throw new EmptyStorageException();
        }
        return Collections.unmodifiableList(facultyRepository.findAll());
    }

    public List<Faculty> sortByColor(String color) {
        if (storageIsEmpty()) {
            throw new EmptyStorageException();
        }
        if (color.isBlank() || color.isEmpty()) {
            throw new InvalidValueException();
        }
        List<Faculty> f = facultyRepository.findAll().stream()
                .filter(q -> q.getColor().contains(color))
                .toList();
        if (f.isEmpty()) {
            throw new InvalidValueException();
        }
        return f;
    }

    public Faculty findByNameOrColor(String name, String color) {
        if (storageIsEmpty()) {
            throw new EmptyStorageException();
        }
        return Optional.ofNullable(facultyRepository.findByNameIgnoreCaseOrColorIgnoreCaseContains(name, color))
                .orElseThrow(InvalidValueException::new);
    }

    public List<Student> getAllStudentsOfFaculty(String name) {
        if (storageIsEmpty()) {
            throw new EmptyStorageException();
        }
        Optional<Faculty> f = Optional.ofNullable(facultyRepository.findByNameIgnoreCaseContains(name).orElseThrow(InvalidValueException::new));
        return f.get().getStudents();
    }

}