package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.model.exception.EmptyStorageException;
import ru.hogwarts.school.model.exception.InvalidValueException;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.*;
import java.util.stream.LongStream;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;
    private static final Logger logger = LoggerFactory.getLogger(FacultyService.class);

    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public boolean storageIsEmpty() {
        return facultyRepository.findAll().isEmpty();
    }

    public void addFaculty(Faculty faculty) {
        logger.info("Метод добавления факультета в хранилище");
        Optional.of(facultyRepository.save(faculty)).orElseThrow(() -> {
            logger.error("Переданы некорректные данные, невозможно сохранить");
            return new InvalidValueException();
        });
    }

    public Faculty getFacultyByID(Long id) {
        logger.info("Методполучения факультета по идентификатору");
        if (storageIsEmpty()) {
            logger.error("В хранилище нет данных");
            throw new EmptyStorageException();
        }
        return facultyRepository.findById(id).orElseThrow(() -> {
            logger.error("Факультета с переданным идентификатором не найдено");
            return new InvalidValueException();
        });
    }

    public void editFaculty(Faculty faculty) {
        logger.info("Метод редактирования данных факультета");
        if (storageIsEmpty()) {
            logger.error("В хранилище нет данных");
            throw new EmptyStorageException();
        }
        if (!facultyRepository.existsById(faculty.getId())) {
            logger.error("Такого факультета нет в хранилище");
            throw new InvalidValueException();
        }
        Optional.of(facultyRepository.save(faculty)).orElseThrow(() -> {
            logger.error("Переданы некорректные данные, невозможно сохранить");
            return new InvalidValueException();
        });
    }

    public void removeFaculty(Long id) {
        logger.info("Метод удаления факультета");
        if (storageIsEmpty()) {
            logger.error("В хранилище нет данных");
            throw new EmptyStorageException();
        }
        Faculty f = facultyRepository.findById(id).orElseThrow(() -> {
            logger.error("Факультета с переданным идентификатором не найдено");
            return new InvalidValueException();
        });
        facultyRepository.delete(f);
    }

    public List<Faculty> getAllFaculties() {
        logger.info("Метод получения списка всех факультетов");
        if (storageIsEmpty()) {
            logger.error("В хранилище нет данных");
            throw new EmptyStorageException();
        }
        return Collections.unmodifiableList(facultyRepository.findAll());
    }

    public List<Faculty> sortByColor(String color) {
        logger.info("Метод сортировки факультетов по цвету");
        if (storageIsEmpty()) {
            logger.error("В хранилище нет данных");
            throw new EmptyStorageException();
        }
        List<Faculty> f = facultyRepository.findAll().stream()
                .filter(q -> q.getColor().contains(color))
                .toList();
        if (color.isBlank() || color.isEmpty() || f.isEmpty()) {
            logger.error("Факультета с переданным цветом не найдено");
            throw new InvalidValueException();
        }
        return f;
    }

    public List<Faculty> findByNameOrColor(String name, String color) {
        logger.info("Метод поиска факультета по цвету или названию");
        if (storageIsEmpty()) {
            logger.error("В хранилище нет данных");
            throw new EmptyStorageException();
        }
        return Optional.of(facultyRepository.findByNameIgnoreCaseOrColorIgnoreCaseContains(name, color))
                .orElseThrow(() -> {
                    logger.error("Факультет с таким названием и/или цветом не найден");
                    return new InvalidValueException();
                });
    }

    public List<Student> getAllStudentsOfFaculty(String name) {
        logger.info("Метод получения списка всех студентов факультета");
        if (storageIsEmpty()) {
            logger.error("В хранилище нет данных");
            throw new EmptyStorageException();
        }
        Optional<Faculty> f = Optional.of(facultyRepository.findByNameIgnoreCaseContains(name)
                .orElseThrow(() -> {
                    logger.error("Факультет с таким названием не найден");
                    return new InvalidValueException();
                }));
        return f.get().getStudents();
    }

    public String getTheLongestFacultyName() {
        if (storageIsEmpty()) {
            logger.error("В хранилище нет данных");
            throw new EmptyStorageException();
        }
        return "Факультет с самым длинным названием: " + facultyRepository.findAll().stream()
                .max(Comparator.comparingInt(faculty -> faculty.getName().length()))
                .map(Faculty::getName)
                .get();
    }

    public String getSum() {
        long time = System.currentTimeMillis();
        long sum = LongStream.iterate(1L, a -> a + 1)
                .limit(1_000_000)
                .sum();
        return "Метод был выполнен за: " + (System.currentTimeMillis() - time) + ", сумма равна " + sum;
    }
}