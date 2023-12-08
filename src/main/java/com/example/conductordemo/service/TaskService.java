package com.example.conductordemo.service;

import com.example.conductordemo.worker.SimpleWorkerV1;
import com.example.conductordemo.worker.SimpleWorkerV2;
import com.example.conductordemo.worker.TimeoutWorker;
import com.netflix.conductor.common.metadata.tasks.TaskDef;
import io.orkes.conductor.client.MetadataClient;
import io.orkes.conductor.client.TaskClient;
import io.orkes.conductor.client.automator.TaskRunnerConfigurer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class TaskService {

    private final TaskClient taskClient;

    private final MetadataClient metadataClient;

    public TaskService(TaskClient taskClient, MetadataClient metadataClient) {
        this.taskClient = taskClient;
        this.metadataClient = metadataClient;
    }

    public void registerTaskDef() {
        TaskDef taskDefExample = new TaskDef("simple_task_example");
        taskDefExample.setDescription("This is a task definition example");
        taskDefExample.setOwnerEmail("example@mail.com");
        taskDefExample.setTimeoutPolicy(TaskDef.TimeoutPolicy.RETRY);
        taskDefExample.setTimeoutSeconds(60L);
        taskDefExample.setRetryLogic(TaskDef.RetryLogic.FIXED);
        taskDefExample.setRetryCount(3);
        taskDefExample.setRetryDelaySeconds(1);
        taskDefExample.setResponseTimeoutSeconds(40L);
        taskDefExample.setPollTimeoutSeconds(30);
        taskDefExample.setInputTemplate(Map.of("result", "${inline_task.output.result}"));
        taskDefExample.setOutputKeys(List.of("result"));


        TaskDef timeoutTaskDef = new TaskDef("test_timeout_task");
        timeoutTaskDef.setDescription("This is a task definition example");
        timeoutTaskDef.setOwnerEmail("example@mail.com");
        timeoutTaskDef.setTimeoutPolicy(TaskDef.TimeoutPolicy.RETRY);
        timeoutTaskDef.setTimeoutSeconds(30L);
        timeoutTaskDef.setRetryLogic(TaskDef.RetryLogic.FIXED);
        timeoutTaskDef.setRetryCount(3);
        timeoutTaskDef.setRetryDelaySeconds(1);
        timeoutTaskDef.setResponseTimeoutSeconds(12L);
        timeoutTaskDef.setPollTimeoutSeconds(30);
        log.info("Register tasks started.");
        metadataClient.registerTaskDefs(List.of(taskDefExample, timeoutTaskDef));
    }

    public void runTasks() {
        log.info("Tasks runner polling");
        
        TaskRunnerConfigurer taskRunnerV1 = new TaskRunnerConfigurer.Builder(taskClient,
                List.of(new SimpleWorkerV1()))
                .withTaskToDomain(Map.of("simple_task_example", "V1"))
                .withThreadCount(1)
                .build();
        taskRunnerV1.init();

        TaskRunnerConfigurer taskRunnerV2 = new TaskRunnerConfigurer.Builder(taskClient,
                List.of(new SimpleWorkerV2()))
                .withTaskToDomain(Map.of("simple_task_example", "V2"))
                .withThreadCount(1)
                .build();
        taskRunnerV2.init();

        TaskRunnerConfigurer timeoutTaskRunner = new TaskRunnerConfigurer.Builder(taskClient,
                List.of(new TimeoutWorker()))
                .withThreadCount(1)
                .build();
        timeoutTaskRunner.init();
    }
}
