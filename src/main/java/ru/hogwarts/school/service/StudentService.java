package ru.hogwarts.school.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.model.exception.EmptyStorageException;
import ru.hogwarts.school.model.exception.InvalidValueException;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {
    private Long counter = 0L;
    private final HashMap<Long, Student> students;

    public StudentService(HashMap<Long, Student> students) {
        this.students = students;
    }

    public void addStudent(Student student) {
        if (student.getName().isBlank() || student.getAge() == 0) {
            throw new InvalidValueException("Поля не могут быть пустыми!");
        }
        student.setId(++counter);
        students.put(student.getId(), student);
    }

    public Student getStudentByID(Long id) {
        if (students.isEmpty()) {
            throw new EmptyStorageException();
        }
        return students.get(id);
    }

    public void updateStudent(Student student) {
        if (students.isEmpty()) {
            throw new EmptyStorageException();
        }
        if (!students.containsKey(student.getId())) {
            throw new InvalidValueException("Факультета с идентификатором " + student.getId() + " нет в базе!");
        }
        students.put(student.getId(), student);
    }

    public void removeStudent(Long id) {
        if (students.isEmpty()) {
            throw new EmptyStorageException();
        }
        if (students.get(id) == null) {
            throw new InvalidValueException("Студента с идентификатором " + id + " нет в базе данных!");
        }
        students.remove(id);
    }

    public HashMap<Long, Student> getAllStudents() {
        if (students.isEmpty()) {
            throw new EmptyStorageException();
        }
        return students;
    }

    public List<Student> sortByAge(int age) {
        if (students.isEmpty()) {
            throw new EmptyStorageException();
        }
        if (age == 0) {
            throw new InvalidValueException("Возраст не может быть равен нулю!");
        }
        return students.values().stream()
                .filter(q -> q.getAge() == age)
                .toList();
    }
}
