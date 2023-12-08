package com.example.conductordemo.controller;

import com.example.conductordemo.service.EventService;
import com.example.conductordemo.service.TaskService;
import com.example.conductordemo.service.WorkflowService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class ConductorController {

    private final TaskService taskService;

    private final WorkflowService workflowService;

    private final EventService eventService;

    public ConductorController(TaskService taskService, WorkflowService workflowService, EventService eventService) {
        this.taskService = taskService;
        this.workflowService = workflowService;
        this.eventService = eventService;
    }

    @PostMapping("tasks/register")
    public ResponseEntity<Void> registerTasks() {
        taskService.registerTaskDef();
        return ResponseEntity.created(URI.create("/tasks")).build();
    }

    @PostMapping("tasks/poll")
    public ResponseEntity<Void> pollTasks() {
        taskService.runTasks();
        return ResponseEntity.ok().build();
    }

    @PostMapping("workflows/register")
    public ResponseEntity<Void> registerWorkflows() {
        workflowService.registerWorkflow();
        return ResponseEntity.created(URI.create("/workflows")).build();
    }

    @PostMapping("workflows/start")
    public ResponseEntity<Void> startWorkflows() {
        workflowService.startWorkflow();
        return ResponseEntity.ok().build();
    }

    @PostMapping("events/register")
    public ResponseEntity<Void> registerEventHandlers() {
        eventService.registerEventHandler();
        return ResponseEntity.created(URI.create("/events")).build();
    }
}
