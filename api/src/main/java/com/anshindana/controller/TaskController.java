package com.anshindana.controller;

import com.anshindana.domain.TodayTasks;
import com.anshindana.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/today")
    public ResponseEntity<TodayTasks> today() {
        return ResponseEntity.ok(taskService.getTodayTasks(1L));
    }
}
