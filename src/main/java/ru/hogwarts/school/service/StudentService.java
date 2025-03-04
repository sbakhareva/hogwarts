package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Student;

import java.util.HashMap;

@Service
public class StudentService {
    private Long counter = 1L;
    private final HashMap<Long, Student> students;
    public StudentService(HashMap<Long, Student> students) {
        this.students = students;
    }

    public void addStudent(Student student) {
        student.setId(++counter);
        students.put(student.getId(), student);
    }

    public Student getStudentByID(Long id) {
        return students.get(id);
    }

    public Student updateStudent(Student student) {
        return students.put(student.getId(), student);
    }

    public void removeStudent(Long id) {
        students.remove(id);
    }

    public HashMap<Long, Student> getAllStudents() {
        return students;
    }
}
