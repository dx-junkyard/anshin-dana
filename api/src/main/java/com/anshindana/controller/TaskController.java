package com.anshindana.controller;

import com.anshindana.domain.TodayTasks;
import com.anshindana.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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
    public ResponseEntity<TodayTasks> today(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(taskService.getTodayTasks(parseUserId(jwt)));
    }

    private Long parseUserId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }
}
