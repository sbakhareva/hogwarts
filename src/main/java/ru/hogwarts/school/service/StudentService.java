package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.model.exception.EmptyStorageException;
import ru.hogwarts.school.model.exception.InvalidValueException;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class StudentService {
    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public void addStudent(Student student) {
        if (student.getName().isBlank() || student.getAge() == 0) {
            throw new InvalidValueException();
        }
        studentRepository.save(student);
    }

    public Optional<Student> getStudentByID(Long id) {
        List<Student> students = studentRepository.findAll();
        if (students.isEmpty()) {
            throw new EmptyStorageException();
        }
        if (!studentRepository.existsById(id)) {
            throw new InvalidValueException();
        }
        return studentRepository.findById(id);
    }

    public void updateStudent(Student student) {
        List<Student> students = studentRepository.findAll();
        if (students.isEmpty()) {
            throw new EmptyStorageException();
        }
        if (!studentRepository.existsById(student.getId())) {
            throw new InvalidValueException();
        }
        studentRepository.save(student);
    }

    public void removeStudent(Long id) {
        List<Student> students = studentRepository.findAll();
        if (students.isEmpty()) {
            throw new EmptyStorageException();
        }
        if (!studentRepository.existsById(id)) {
            throw new InvalidValueException();
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
            throw new InvalidValueException();
        }
        return students.stream()
                .filter(q -> q.getAge() == age)
                .toList();
    }

    public List<Student> findByAgeBetween(int ageMin, int ageMax) {
        List<Student> students = studentRepository.findAll();
        if (students.isEmpty()) {
            throw new EmptyStorageException();
        }
        List<Student> sortedStudents = studentRepository.findByAgeBetween(ageMin, ageMax);
        if (sortedStudents.isEmpty()) {
            throw new InvalidValueException();
        }
        return sortedStudents;
    }
}
