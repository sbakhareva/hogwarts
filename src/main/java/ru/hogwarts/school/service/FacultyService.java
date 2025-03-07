package ru.hogwarts.school.service;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.exception.EmptyStorageException;
import ru.hogwarts.school.model.exception.InvalidValueException;

import java.util.HashMap;
import java.util.List;

@Service
public class FacultyService {

    private Long counter = 0L;
    private final HashMap<Long, Faculty> faculties;

    public FacultyService(HashMap<Long, Faculty> faculties) {
        this.faculties = faculties;
    }

    public void addFaculty(Faculty faculty) {
        if (faculty.getName().isBlank() || faculty.getColor().isBlank()) {
            throw new InvalidValueException("Поля не могут быть пустыми!");
        }
        faculty.setId(++counter);
        faculties.put(faculty.getId(), faculty);
    }

    public Faculty getFacultyByID(Long id) {
        if (faculties.isEmpty()) {
            throw new EmptyStorageException();
        }
        Faculty faculty = faculties.get(id);
        if (faculty == null) {
            throw new InvalidValueException("Факультета с идентификатором " + id + " нет в базе!");
        }
        return faculty;
    }

    public void updateFaculty(Faculty faculty) {
        if (faculties.isEmpty()) {
            throw new EmptyStorageException();
        }
        if (!faculties.containsKey(faculty.getId())) {
            throw new InvalidValueException("Факультета с идентификатором " + faculty.getId() + " нет в базе!");
        }
        faculties.put(faculty.getId(), faculty);
    }

    public void removeFaculty(Long id) {
        if (faculties.isEmpty()) {
            throw new EmptyStorageException();
        }
        if (faculties.get(id) == null) {
            throw new InvalidValueException("Факультета с идентификатором " + id + " нет в базе!");
        }
        faculties.remove(id);
    }

    public HashMap<Long, Faculty> getAllFaculties() {
        if (faculties.isEmpty()) {
            throw new EmptyStorageException();
        }
        return faculties;
    }

    public List<Faculty> sortByColor(String color) {
        if (faculties.isEmpty()) {
            throw new EmptyStorageException();
        }
        if (color.isBlank()) {
            throw new InvalidValueException("Поле 'Цвет' не может быть пустым!");
        }
        return faculties.values().stream()
                .filter(q -> q.getColor().contains(color))
                .toList();
    }
}
