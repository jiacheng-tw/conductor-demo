package com.example.conductordemo.worker;

import com.netflix.conductor.client.worker.Worker;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TimeoutWorker implements Worker {

    @Override
    public String getTaskDefName() {
        return "test_timeout_task";
    }

    @SneakyThrows
    @Override
    public TaskResult execute(Task task) {
        log.info("default callback time" + task.getCallbackAfterSeconds());
        task.setCallbackAfterSeconds(10L);
        Thread.sleep(10000L);
        log.info("NO1");
        Thread.sleep(10000L);
        log.info("NO2");
        task.setStatus(Task.Status.COMPLETED);
        return new TaskResult(task);
    }
}
