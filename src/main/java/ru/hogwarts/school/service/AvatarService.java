package ru.hogwarts.school.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
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

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
@Transactional
public class AvatarService {
    @Value("${students.avatar.dir.path}")
    private String avatarsDir;
    private final AvatarRepository avatarRepository;
    private final StudentRepository studentRepository;

    public AvatarService(AvatarRepository avatarRepository, StudentRepository studentRepository) {
        this.avatarRepository = avatarRepository;
        this.studentRepository = studentRepository;
    }

    public boolean storageIsEmpty() {
        return studentRepository.findAll().isEmpty();
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

    public Avatar getAvatar(Long studentId) {
        if (storageIsEmpty()) {
            throw new EmptyStorageException();
        }
        if (!avatarRepository.existsById(studentId)) {
            throw new InvalidValueException();
        }
        return avatarRepository.findByStudentId(studentId).orElse(new Avatar());
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

    public List<Avatar> getAllAvatars(int page, int size) {
        if (page <= 0) {
            throw new InvalidValueException();
        }
        PageRequest pageRequest = PageRequest.of(page - 1, size);
          return avatarRepository.findAll(pageRequest).getContent();
    }

    @Transactional
    public void deleteAvatar(Long studentId) {
        avatarRepository.deleteByStudentId(studentId);
    }

    public void removeUnused() {
        avatarRepository.removeUnused();
    }
}
