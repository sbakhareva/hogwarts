package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.hogwarts.school.dto.StudentDTO;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findAllByAgeBetween(int ageMin, int ageMax);

    Optional<Student> findStudentByNameIgnoreCaseContains(String name);

    @Query(nativeQuery = false, value = "select count(*) from students")
    int countStudents();

    @Query(nativeQuery = true, value = "select avg(age) from students")
    float countAvgAge();

    @Query(nativeQuery = true, value = "select * from students order by id desc limit 5 ")
    List<Student> getLastFiveStudents();
}
