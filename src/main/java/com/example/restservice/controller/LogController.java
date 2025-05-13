package com.example.restservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
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
    private static final String SAFE_DATE_REGEX = "[^\\d\\-]";
    private static final Pattern DATE_PATTERN = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");

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
        if (!DATE_PATTERN.matcher(date).matches()) {
            logger.warn("⛔ Получен некорректный формат даты: {}", date);
            return ResponseEntity.badRequest().body("❌ Неверный формат даты. Используй yyyy-MM-dd");
        }

        try {
            String sanitizedDate = date.replaceAll(SAFE_DATE_REGEX, "");
            logger.info("📂 Получен запрос на лог за дату {}", sanitizedDate);

            File logFile = new File(LOG_FILE_PATH);
            if (!logFile.exists()) {
                logger.warn("⚠️ Лог-файл не найден при запросе даты {}", sanitizedDate);
                return ResponseEntity.status(404).body("Лог-файл не найден.");
            }

            List<String> filteredLines = Files.lines(logFile.toPath())
                    .filter(line -> line.startsWith(date))
                    .collect(Collectors.toList());

            if (filteredLines.isEmpty()) {
                logger.info("ℹ️ Логи за дату {} не найдены", sanitizedDate);
                return ResponseEntity.status(404).body("Логи за дату " + date + " не найдены.");
            }

            Path secureDir = Paths.get("logs", "temp");
            Files.createDirectories(secureDir);
            Path tempFile = Files.createTempFile(secureDir, "log-", ".txt");
            Files.write(tempFile, filteredLines);

            Resource resource = new FileSystemResource(tempFile);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=log-"
                            + sanitizedDate + ".txt")
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(resource);

        } catch (Exception e) {
            logger.error("❌ Ошибка при запросе логов по дате {}: {}", date, e.getMessage(), e);
            return ResponseEntity.badRequest().body("❌ Неверный формат даты. Используй yyyy-MM-dd");
        }
    }
}

