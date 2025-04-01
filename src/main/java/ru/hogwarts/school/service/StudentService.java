package ru.hogwarts.school.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.dto.StudentDTO;
import ru.hogwarts.school.dto.StudentDTOMapper;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.model.exception.EmptyStorageException;
import ru.hogwarts.school.model.exception.InvalidValueException;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.StudentRepository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
@Transactional
public class StudentService {
    @Value("${students.avatar.dir.path}")
    private String avatarsDir;
    private final AvatarRepository avatarRepository;
    private final StudentRepository studentRepository;
    private final StudentDTOMapper studentDTOMapper;
    private final FacultyService facultyService;

    public StudentService(AvatarRepository avatarRepository, StudentRepository studentRepository, StudentDTOMapper studentDTOMapper, FacultyService facultyService) {
        this.avatarRepository = avatarRepository;
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

    public Optional<Student> findStudent(Long id) {
        return studentRepository.findById(id);
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

    @Transactional
    public void removeStudent(Long id) {
        if (storageIsEmpty()) {
            throw new EmptyStorageException();
        }
        Student s = studentRepository.findById(id).orElseThrow(InvalidValueException::new);
        avatarRepository.deleteByStudentId(id);
        studentRepository.delete(s);
    }

    public void uploadAvatar(Long studentId, MultipartFile avatar) throws IOException {
        if (!studentRepository.existsById(studentId)) {
            throw new InvalidValueException();
        }
        Optional<Student> student = studentRepository.findById(studentId);

        Path filePath = Path.of(avatarsDir, studentId + "." + getExtension(avatar.getOriginalFilename()));
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);

        try (InputStream is = avatar.getInputStream();
             OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             BufferedOutputStream bos = new BufferedOutputStream(os, 1024)
        ) {
            bis.transferTo(bos);
        }

        Avatar studentAvatar = getAvatar(studentId);
        studentAvatar.setStudent(student.get());
        studentAvatar.setFilePath(filePath.toString());
        studentAvatar.setFileSize(avatar.getSize());
        studentAvatar.setMediaType(avatar.getContentType());
        studentAvatar.setPreview(generateImagePreview(filePath));

        avatarRepository.save(studentAvatar);
    }

    public Avatar getAvatar(Long id) {
        if (storageIsEmpty()) {
            throw new EmptyStorageException();
        }
        return avatarRepository.findByStudentId(id).orElse(new Avatar());
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    private byte[] generateImagePreview(Path filePath) throws IOException {
        try (InputStream is = Files.newInputStream(filePath);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            BufferedImage image = ImageIO.read(bis);

            int height = image.getHeight() / (image.getWidth() / 100);
            BufferedImage preview = new BufferedImage(100, height, image.getType());
            Graphics2D graphics = preview.createGraphics();
            graphics.drawImage(image, 0, 0, 100, height, null);
            graphics.dispose();

            ImageIO.write(preview, getExtension(filePath.getFileName().toString()), baos);
            return baos.toByteArray();
        }
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

    public String getNumberOfStudents() {
        return "Общее количество студентов в школе: " + studentRepository.countStudents();
    }

    public String getAvgAge() {
        return "Средний возраст учеников школы: " + studentRepository.countAvgAge();
    }

    public List<StudentDTO> getLastFiveStudents() {
        return studentRepository.getLastFiveStudents().stream()
                .map(studentDTOMapper)
                .collect(Collectors.toList());
    }

}
