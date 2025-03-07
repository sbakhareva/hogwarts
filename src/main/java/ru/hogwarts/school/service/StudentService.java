package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
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

    public void addStudent(String name, int age, Faculty faculty) {
        if (name.isBlank() || age == 0) {
            throw new InvalidValueException();
        }
        studentRepository.save(new Student(name, age, faculty));
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
        List<Student> sortedStudents = studentRepository.findAllByAgeBetween(ageMin, ageMax);
        if (sortedStudents.isEmpty()) {
            throw new InvalidValueException();
        }
        return sortedStudents;
    }

    public Faculty getFaculty(Long id) {
        return studentRepository.findById(id).get().getFaculty();
    }
}
