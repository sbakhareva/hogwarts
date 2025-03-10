package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hogwarts.school.model.Faculty;

import java.util.Optional;

@Repository
public interface FacultyRepository extends JpaRepository<Faculty, Long> {

    Faculty findByNameIgnoreCaseOrColorIgnoreCase(String name, String color);

    Optional<Faculty> findByNameIgnoreCaseContains(String name);
}
