package ru.hogwarts.school.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/school/info")
public class InfoController {

    @Value("${server.port}")
    private String port;

    @GetMapping
    public String getInfo() {
        return "Приложение запущено на порту: " + port;
    }
}

