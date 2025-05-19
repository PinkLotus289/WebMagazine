package com.example.restservice.service;

import com.example.restservice.model.LogTask;
import com.example.restservice.model.TaskStatus;
import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class LogGenerationService {

    private final Map<String, LogTask> taskMap = new ConcurrentHashMap<>();
    private final Path sourceLog = Paths.get("logs/app.log");
    private final Path outputDir = Paths.get("logs/generated");

    private static final DateTimeFormatter LOG_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @PostConstruct
    public void init() throws IOException {
        Files.createDirectories(outputDir);

        // Восстановление taskMap из уже существующих файлов
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(outputDir, "log_*.txt")) {
            for (Path path : stream) {
                String fileName = path.getFileName().toString(); // log_abc123.txt
                if (fileName.startsWith("log_") && fileName.endsWith(".txt")) {
                    String id = fileName.substring(4, fileName.length() - 4); // вырезаем "log_" и ".txt"
                    LogTask task = new LogTask();
                    task.setId(id);
                    task.setStatus(TaskStatus.DONE);
                    task.setFilePath(path);
                    taskMap.put(id, task);
                }
            }
        }
    }

    public String createLogTask(LocalDateTime from, LocalDateTime to) {
        String id = UUID.randomUUID().toString();
        LogTask task = new LogTask();
        task.setId(id);
        task.setFrom(from);
        task.setTo(to);
        task.setStatus(TaskStatus.PENDING);
        taskMap.put(id, task);

        new Thread(() -> generateLogAsync(task)).start();
        return id;
    }

    @Async
    public void generateLogAsync(LogTask task) {
        try {
            Thread.sleep(45000);

            if (!Files.exists(sourceLog)) {
                task.setStatus(TaskStatus.ERROR);
                return;
            }

            try (Stream<String> lines = Files.lines(sourceLog)) {
                List<String> filtered = lines
                        .filter(line -> {
                            try {
                                String timestamp = line.substring(0, 19); // "yyyy-MM-dd HH:mm:ss"
                                LocalDateTime entryTime = LocalDateTime.parse(timestamp, LOG_DATE_FORMAT);
                                return !entryTime.isBefore(task.getFrom()) && !entryTime.isAfter(task.getTo());
                            } catch (Exception e) {
                                return false;
                            }
                        })
                        .collect(Collectors.toList());

                Path resultFile = outputDir.resolve("log_" + task.getId() + ".txt");
                Files.write(resultFile, filtered);
                task.setFilePath(resultFile);
                task.setStatus(TaskStatus.DONE);
            }

        } catch (Exception e) {
            task.setStatus(TaskStatus.ERROR);
        }
    }

    public TaskStatus getStatus(String id) {
        LogTask task = taskMap.get(id);
        return task != null ? task.getStatus() : null;
    }

    public Path getFile(String id) {
        LogTask task = taskMap.get(id);
        return task != null && task.getStatus() == TaskStatus.DONE ? task.getFilePath() : null;
    }
}


