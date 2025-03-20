package ru.hogwarts.school.dto;


import org.springframework.cglib.core.internal.Function;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Student;

import java.util.stream.Collectors;

@Service
public class StudentDTOMapper implements Function<Student, StudentDTO>, java.util.function.Function<Student, StudentDTO> {
    @Override
    public StudentDTO apply(Student student) {
        return new StudentDTO(
                student.getId(),
                student.getName(),
                student.getAge(),
                student.getFaculty().getName()
        );
    }
}
