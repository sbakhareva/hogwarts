package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.dto.StudentDTO;
import ru.hogwarts.school.dto.StudentDTOMapper;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.model.exception.EmptyStorageException;
import ru.hogwarts.school.model.exception.InvalidValueException;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final StudentDTOMapper studentDTOMapper;

    public StudentService(StudentRepository studentRepository, StudentDTOMapper studentDTOMapper) {
        this.studentRepository = studentRepository;
        this.studentDTOMapper = studentDTOMapper;
    }

    public boolean storageIsNotEmpty() {
        return true;
    }

    public void addStudent(Student student) {
        if (student.getName().isBlank() || student.getAge() == 0) {
            throw new InvalidValueException();
        }
        studentRepository.save(student);
    }

    public Optional<StudentDTO> getStudentByID(Long id) {
        List<Student> students = studentRepository.findAll();
        if (students.isEmpty()) {
            throw new EmptyStorageException();
        }
        if (!studentRepository.existsById(id)) {
            throw new InvalidValueException();
        }
        return studentRepository.findById(id)
                .map(studentDTOMapper);
    }

    public List<StudentDTO> getAllStudents() {
        List<Student> students = studentRepository.findAll();
        if (students.isEmpty()) {
            throw new EmptyStorageException();
        }
        return students.stream()
                .map(studentDTOMapper)
                .collect(Collectors.toList());
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
        if (ageMin == 0 || ageMax == 0) {
            throw new InvalidValueException();
        }
        List<Student> sortedStudents = studentRepository.findAllByAgeBetween(ageMin, ageMax);
        if (sortedStudents.isEmpty()) {
            throw new InvalidValueException();
        }
        return sortedStudents;
    }

    public Faculty getStudentsFaculty(String name) {
        List<Student> students = studentRepository.findAll();
        if (students.isEmpty()) {
            throw new EmptyStorageException();
        }
        Optional<Student> s = studentRepository.findStudentByNameIgnoreCaseContains(name);
        if (name.isBlank() || name.isEmpty() || s.isEmpty()) {
            throw new InvalidValueException();
        }
        return s.get().getFaculty();
    }
}
