package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.model.exception.EmptyStorageException;
import ru.hogwarts.school.model.exception.InvalidValueException;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class StudentService {
    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public void addStudent(Student student) {
        if (student.getName().isBlank() || student.getAge() == 0) {
            throw new InvalidValueException("Поля не могут быть пустыми!");
        }
        studentRepository.save(student);
    }

    public Optional<Student> getStudentByID(Long id) {
        List<Student> students = studentRepository.findAll();
        if (students.isEmpty()) {
            throw new EmptyStorageException();
        }
        if (!studentRepository.existsById(id)) {
            throw new InvalidValueException("Студента с идентификатором " + id + " нет в базе данных!");
        }
        return studentRepository.findById(id);
    }

    public void updateStudent(Student student) {
        List<Student> students = studentRepository.findAll();
        if (students.isEmpty()) {
            throw new EmptyStorageException();
        }
        if (!studentRepository.existsById(student.getId())) {
            throw new InvalidValueException("Студента с идентификатором " + student.getId() + " нет в базе!");
        }
        studentRepository.save(student);
    }

    public void removeStudent(Long id) {
        List<Student> students = studentRepository.findAll();
        if (students.isEmpty()) {
            throw new EmptyStorageException();
        }
        if (!studentRepository.existsById(id)) {
            throw new InvalidValueException("Студента с идентификатором " + id + " нет в базе данных!");
        }
        studentRepository.deleteById(id);
    }

    public List<Student> getAllStudents() {
        List<Student> students = studentRepository.findAll();
        if (students.isEmpty()) {
            throw new EmptyStorageException();
        }
        return Collections.unmodifiableList(students);
    }

    public List<Student> sortByAge(int age) {
        List<Student> students = studentRepository.findAll();
        if (students.isEmpty()) {
            throw new EmptyStorageException();
        }
        if (age == 0) {
            throw new InvalidValueException("Возраст не может быть равен нулю!");
        }
        return students.stream()
                .filter(q -> q.getAge() == age)
                .toList();
    }
}
