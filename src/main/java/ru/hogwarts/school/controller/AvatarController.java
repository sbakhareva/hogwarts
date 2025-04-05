package ru.hogwarts.school.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.service.AvatarService;
import ru.hogwarts.school.service.StudentService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/school/avatar")
public class AvatarController {
    private final AvatarService avatarService;
    private final StudentService studentService;

    public AvatarController(AvatarService avatarService, StudentService studentService) {
        this.avatarService = avatarService;
        this.studentService = studentService;
    }

    @PostMapping(value = "/{id}/upload-avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadAvatar(@PathVariable Long id,
                                               @RequestParam MultipartFile avatar) {
        if (avatar.getSize() > 1024 * 1024 * 5) {
            return ResponseEntity.badRequest().body("Размер файла слишком большой!");
        }
        try {
            avatarService.uploadAvatar(id, avatar);
        } catch (IOException e) {
            System.out.println("Текст ошибки!");
        }
        return ResponseEntity.ok("Для студента " + studentService.findStudent(id).get().getName() + " добавлен аватар!");
    }

    @GetMapping(value = "/{id}/avatar/download-preview")
    public ResponseEntity<byte[]> downloadPreview(@PathVariable Long id) {
        Avatar avatar = avatarService.getAvatar(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(avatar.getMediaType()));
        headers.setContentLength(avatar.getPreview().length);

        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(avatar.getPreview());
    }

    @GetMapping(value = "/{id}/download-avatar")
    public void downloadAvatar(@PathVariable Long id,
                               HttpServletResponse response) {
        Avatar avatar = avatarService.getAvatar(id);

        Path path = Path.of(avatar.getFilePath());
        try (InputStream is = Files.newInputStream(path);
             OutputStream os = response.getOutputStream()) {
            response.setContentType(avatar.getMediaType());
            response.setContentLength((int) avatar.getFileSize());
            is.transferTo(os);
        } catch (IOException e) {
            System.out.println("Текст ошибки!");
        }
    }

    @GetMapping(value = "/get-all")
    public List<Avatar> getAllAvatars(@RequestParam int page, @RequestParam int size) {
        return avatarService.getAllAvatars(page, size);
    }

    @DeleteMapping(value = "/delete")
    public String deleteAvatar(@RequestParam("student-id") Long studentId) {
        avatarService.deleteAvatar(studentId);
        return "Фото профиля для студента " + studentService.findStudent(studentId).get().getName() + " успешно удалено!";
    }
}
