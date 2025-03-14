package ru.hogwarts.school.service;

import jakarta.transaction.Transactional;
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
@Transactional
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

    public Student findStudent(Long id) {
        return studentRepository.findById(id).get();
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
        if (age == 0) {
            throw new InvalidValueException();
        }
        return studentRepository.findAll().stream()
                .filter(q -> q.getAge() == age)
                .toList();
    }

    public List<Student> findByAgeBetween(int ageMin, int ageMax) {
        if (storageIsEmpty()) {
            throw new EmptyStorageException();
        }
        List<Student> sorted = studentRepository.findAllByAgeBetween(ageMin, ageMax);
        if (ageMin >= ageMax || ageMax == 0 || sorted.isEmpty()) {
            throw new InvalidValueException();
        }
        return sorted;
    }

    public Faculty getStudentsFaculty(String name) {
        if (storageIsEmpty()) {
            throw new EmptyStorageException();
        }
        return Optional.of(studentRepository.findStudentByNameIgnoreCaseContains(name).get().getFaculty())
                .orElseThrow(InvalidValueException::new);
    }
}
