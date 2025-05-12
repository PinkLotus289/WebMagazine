package com.example.restservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.File;
import java.nio.file.Files;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logs")
@Tag(name = "Логирование", description = "Работа с логами приложения")
public class LogController {

    private static final Logger logger = LoggerFactory.getLogger(LogController.class);
    private static final String LOG_FILE_PATH = "logs/app.log";

    @GetMapping
    @Operation(
            summary = "Получить логи по дате",
            description = "Фильтрует строки в app.log, которые начинаются с переданной даты"
    )
    @ApiResponse(responseCode = "200", description = "Лог-файл возвращён")
    @ApiResponse(responseCode = "400", description = "Неверный формат даты")
    @ApiResponse(responseCode = "404", description = "Лог за указанную дату не найден")
    public ResponseEntity<Object> getLogsByDate(
            @Parameter(description = "Дата в формате yyyy-MM-dd")
            @RequestParam String date) {
        try {
            String sanitizedDate = date.replaceAll("[^\\d\\-]", "");
            logger.info("📂 Получен запрос на лог за дату {}", sanitizedDate);


            File logFile = new File(LOG_FILE_PATH);
            if (!logFile.exists()) {
                String safeDate = date.replaceAll("[^\\d\\-]", "");
                logger.warn("⚠️ Лог-файл не найден при запросе даты {}", safeDate);
                return ResponseEntity.status(404).body("Лог-файл не найден.");
            }

            List<String> filteredLines = Files.lines(logFile.toPath())
                    .filter(line -> line.startsWith(date))
                    .toList();

            if (filteredLines.isEmpty()) {
                String safeDate = date.replaceAll("[^\\d\\-]", "");
                logger.info("ℹ️ Логи за дату {} не найдены", safeDate);
                return ResponseEntity.status(404).body("Логи за дату " + date + " не найдены.");
            }

            File tempFile = File.createTempFile("log-" + date + "-", ".txt");
            Files.write(tempFile.toPath(), filteredLines);

            Resource resource = new FileSystemResource(tempFile);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=log-"
                            + date + ".txt")
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(resource);

        } catch (Exception e) {
            logger.error("❌ Ошибка при запросе логов по дате {}: {}", date, e.getMessage());
            return ResponseEntity.badRequest().body("❌ Неверный формат даты. Используй yyyy-MM-dd");
        }
    }
}
