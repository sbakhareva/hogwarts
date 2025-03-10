package ru.hogwarts.school.dto;

public record StudentDTO(
        Long id,
        String name,
        int age,
        String Faculty
) {
}
