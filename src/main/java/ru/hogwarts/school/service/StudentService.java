package ru.hogwarts.school.service;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.dto.StudentDTO;
import ru.hogwarts.school.dto.StudentDTOMapper;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.model.exception.EmptyStorageException;
import ru.hogwarts.school.model.exception.InvalidValueException;
import ru.hogwarts.school.model.exception.NoMatchingResultsException;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;

@Service
@Transactional
public class StudentService {
    private final AvatarRepository avatarRepository;
    private final StudentRepository studentRepository;
    private final StudentDTOMapper studentDTOMapper;
    private final FacultyService facultyService;
    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    public StudentService(AvatarRepository avatarRepository, StudentRepository studentRepository, StudentDTOMapper studentDTOMapper, FacultyService facultyService) {
        this.avatarRepository = avatarRepository;
        this.studentRepository = studentRepository;
        this.studentDTOMapper = studentDTOMapper;
        this.facultyService = facultyService;
    }

    public boolean storageIsEmpty() {
        return studentRepository.findAll().isEmpty();
    }

    public boolean isStudentOldEnough(int age) {
        return age > 16;
    }

//    public boolean isUnique(String name) {
//        return studentRepository.findAll().contains(name);
//    }

    public void addStudent(Student student) {
        logger.info("Добавление студента в базу данных");
        if (facultyService.getAllFaculties().isEmpty()) {
            logger.error("Хранилище пустое");
            throw new EmptyStorageException();
        }
        if (!isStudentOldEnough(student.getAge())) {
            logger.error("Возраст студента должен быть больше 16");
            throw new InvalidValueException();
        }
        Optional.of(studentRepository.save(student)).orElseThrow(() -> {
            logger.error("Переданы некорректные данные, невозможно сохранить");
            return new InvalidValueException();
        });
    }

    public StudentDTO getStudentByID(Long id) {
        logger.info("Метод поиска студента по id");
        if (storageIsEmpty()) {
            logger.error("Хранилище пустое");
            throw new EmptyStorageException();
        }
        return studentRepository.findById(id).map(studentDTOMapper)
                .orElseThrow(() -> {
                    logger.error("В хранилище нет студента с переданным идентификатором");
                    return new InvalidValueException();
                });
    }

    public Optional<Student> findStudent(Long id) {
        return studentRepository.findById(id);
    }

    public List<StudentDTO> getAllStudents() {
        logger.info("Метод получения всех студентов");
        if (storageIsEmpty()) {
            logger.error("Хранилище пустое");
            throw new EmptyStorageException();
        }
        return studentRepository.findAll().stream()
                .map(studentDTOMapper)
                .collect(Collectors.toList());
    }

    public Student updateStudent(Student student) {
        logger.info("Метод обновления данных студента");
        if (storageIsEmpty()) {
            logger.error("Хранилище пустое");
            throw new EmptyStorageException();
        }
        return Optional.of(studentRepository.save(student)).orElseThrow(() -> {
            logger.error("Переданы некорректные данные студента или студента, невозможно обновить данные или сохранить");
            return new InvalidValueException();

        });
    }

    @Transactional
    public void removeStudent(Long id) {
        logger.info("Метод удаления данных студента");
        if (storageIsEmpty()) {
            logger.error("Хранилище пустое");
            throw new EmptyStorageException();
        }
        Student s = studentRepository.findById(id).orElseThrow(() -> {
            logger.error("Студента с переданным идентификатором не найдено");
            return new InvalidValueException();
        });
        avatarRepository.deleteByStudentId(id);
        studentRepository.delete(s);
    }

    public List<Student> sortByAge(int age) {
        logger.info("Метод сортировки студентов по возрасту");
        if (storageIsEmpty()) {
            logger.error("Хранилище пустое");
            throw new EmptyStorageException();
        }
        if (age <= 16) {
            logger.warn("Переданный возраст меньше или равен минимальному возрасту ученика");
            throw new InvalidValueException();
        }
        return studentRepository.findAll().stream()
                .filter(q -> q.getAge() == age)
                .toList();
    }

    public List<Student> findByAgeBetween(int ageMin, int ageMax) {
        logger.info("Метод поиска студентов в возрастном диапазоне");
        if (storageIsEmpty()) {
            logger.error("Хранилище пустое");
            throw new EmptyStorageException();
        }
        List<Student> sorted = studentRepository.findAllByAgeBetween(ageMin, ageMax);
        if (ageMin >= ageMax || ageMin <= 16 || sorted.isEmpty()) {
            logger.error("Передан неправильный возрастной диапазон или студентов с возрастом в это диапазоне не найдено");
            throw new InvalidValueException();
        }
        return sorted;
    }

    public Faculty getStudentsFaculty(String name) {
        logger.info("Метод получения факультета студента");
        if (storageIsEmpty()) {
            logger.error("Хранилище пустое");
            throw new EmptyStorageException();
        }
        Student student = studentRepository.findStudentByNameIgnoreCaseContains(name).orElseThrow(() -> {
            logger.error("Студента с таким именем нет в ханилище");
            return new InvalidValueException();
        });
        return student.getFaculty();
    }

    public String getNumberOfStudents() {
        logger.info("Метод получения количества студентов");
        if (storageIsEmpty()) {
            logger.error("Хранилище пустое");
            throw new EmptyStorageException();
        }
        return "Общее количество студентов в школе: " + studentRepository.countStudents();
    }

    public String getAvgAge() {
        logger.info("Метод получения среднего возраста студентов");
        if (storageIsEmpty()) {
            logger.error("Хранилище пустое");
            throw new EmptyStorageException();
        }
        return "Средний возраст учеников школы: " + studentRepository.countAvgAge();
    }

    public String getAvgAgeV2() {
        logger.info("Метод получения среднего возраста студентов с использованием стримов");
        if (storageIsEmpty()) {
            logger.error("Хранилище пустое");
            throw new EmptyStorageException();
        }
        return "Средний возраст студентов школы: " + studentRepository.findAll().stream()
                .mapToInt(Student::getAge)
                .average().getAsDouble();
    }


    public List<StudentDTO> getLastFiveStudents() {
        logger.info("Метод получения последних пяти студентов");
        if (storageIsEmpty()) {
            logger.error("Хранилище пустое");
            throw new EmptyStorageException();
        }
        return studentRepository.getLastFiveStudents().stream()
                .map(studentDTOMapper)
                .collect(Collectors.toList());
    }

    public List<String> getNamesStartWithA() {
        logger.info("Метод получения студентов с именем, начинающимся на 'А'");
        if (storageIsEmpty()) {
            logger.error("Хранилище пустое");
            throw new EmptyStorageException();
        }
        List<String> sorted = studentRepository.findAll().stream()
                .filter(student -> student.getName().startsWith("А"))
                .map(s -> s.getName().toUpperCase())
                .toList();
        if (sorted.isEmpty()) {
            throw new NoMatchingResultsException();
        }
        return sorted;
    }

    public void printNames() {
        List<String> names = studentRepository.findAll().stream()
                .map(Student::getName).toList();

        System.out.println(names.get(0) + " of first thread");
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(names.get(1) + " of first thread");
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            new Thread(() -> {
                System.out.println(names.get(2) + " of second thread");
                System.out.println(names.get(3) + " of second thread");
            }).start();
            sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            new Thread(() -> {
                System.out.println(names.get(4) + " of third thread");
                System.out.println(names.get(5) + " of third thread");
            }).start();
            sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void printSynchNames() {
        List<String> names = studentRepository.findAll().stream()
                .map(Student::getName).toList();

        System.out.println(names.get(0) + " from first thread");
        System.out.println(names.get(1) + " from first thread");

        new Thread(() -> {
            System.out.println(names.get(2) + " from second thread");
            System.out.println(names.get(3) + " from second thread");
        }).start();

        new Thread(() -> {
            System.out.println(names.get(4) + " from third thread");
            System.out.println(names.get(5) + " from third thread");
        }).start();
    }
}
