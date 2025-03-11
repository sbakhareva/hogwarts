package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.dto.StudentDTO;
import ru.hogwarts.school.dto.StudentDTOMapper;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.model.exception.EmptyStorageException;
import ru.hogwarts.school.model.exception.InvalidValueException;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final StudentDTOMapper studentDTOMapper;
    private final FacultyService facultyService;

    public StudentService(StudentRepository studentRepository, StudentDTOMapper studentDTOMapper, FacultyService facultyService) {
        this.studentRepository = studentRepository;
        this.studentDTOMapper = studentDTOMapper;
        this.facultyService = facultyService;
    }

    public boolean storageIsEmpty() {
        return studentRepository.findAll().isEmpty();
    }

    public void addStudent(Student student) {
        if (facultyService.getAllFaculties().isEmpty()) {
            throw new EmptyStorageException();
        }
        Optional.of(studentRepository.save(student)).orElseThrow(InvalidValueException::new);
    }

    public Optional<StudentDTO> getStudentByID(Long id) {
        if (storageIsEmpty()) {
            throw new EmptyStorageException();
        }
        return Optional.ofNullable(studentRepository.findById(id)
                .map(studentDTOMapper)).orElseThrow(InvalidValueException::new);
    }

    public List<StudentDTO> getAllStudents() {
        if (storageIsEmpty()) {
            throw new EmptyStorageException();
        }
        return studentRepository.findAll().stream()
                .map(studentDTOMapper)
                .collect(Collectors.toList());
    }

    public void updateStudent(Student student) {
        if (storageIsEmpty()) {
            throw new EmptyStorageException();
        }
        if (!studentRepository.existsById(student.getId())) {
            throw new InvalidValueException();
        }
        Optional.ofNullable(studentRepository.save(student)).orElseThrow(InvalidValueException::new);
    }

    public void removeStudent(Long id) {
        if (storageIsEmpty()) {
            throw new EmptyStorageException();
        }
        Student s = studentRepository.findById(id).orElseThrow(InvalidValueException::new);
        studentRepository.delete(s);
    }

    public List<Student> sortByAge(int age) {
        if (storageIsEmpty()) {
            throw new EmptyStorageException();
        }
        return Optional.ofNullable(studentRepository.findAll().stream()
                .filter(q -> q.getAge() == age)
                .toList()).orElseThrow(InvalidValueException::new);
    }

    public List<Student> findByAgeBetween(int ageMin, int ageMax) {
        if (storageIsEmpty()) {
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
        if (storageIsEmpty()) {
            throw new EmptyStorageException();
        }
        Optional<Student> s = studentRepository.findStudentByNameIgnoreCaseContains(name);
        if (name.isBlank() || name.isEmpty() || s.isEmpty()) {
            throw new InvalidValueException();
        }
        return s.get().getFaculty();
    }
}
