package com.anshindana.controller;

import com.anshindana.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/plan")
public class PlanController {

    private final TaskService taskService;

    public PlanController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/emergency")
    public ResponseEntity<Object> emergency(@RequestParam(defaultValue = "4") int people,
                                            @RequestParam(defaultValue = "3") int days) {
        return ResponseEntity.ok(taskService.emergencyPlan(people, days));
    }
}
