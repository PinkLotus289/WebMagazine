package com.example.restservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
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

    private static final String LOG_FILE_PATH = "logs/app.log";

    @GetMapping
    @Operation(
            summary = "Получить логи по дате",
            description = "Фильтрует строки в app.log, которые начинаются с переданной даты"
    )
    @ApiResponse(responseCode = "200", description = "Лог-файл возвращён")
    @ApiResponse(responseCode = "400", description = "Неверный формат даты")
    @ApiResponse(responseCode = "404", description = "Лог за указанную дату не найден")
    public ResponseEntity<?> getLogsByDate(
            @Parameter(description = "Дата в формате yyyy-MM-dd")
            @RequestParam String date) {
        try {
            LocalDate parsedDate = LocalDate.parse(date);
            File logFile = new File(LOG_FILE_PATH);
            if (!logFile.exists()) {
                return ResponseEntity.status(404).body("Лог-файл не найден.");
            }
            List<String> filteredLines = Files.lines(logFile.toPath())
                    .filter(line -> line.startsWith(date))
                    .collect(Collectors.toList());

            if (filteredLines.isEmpty()) {
                return ResponseEntity.status(404).body("Логи за дату " + date + " не найдены.");
            }

            String response = String.join("\n", filteredLines);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=log-" + date + ".txt")
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("❌ Неверный формат даты. Используй yyyy-MM-dd");
        }
    }
}
