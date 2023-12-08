package com.example.conductordemo.worker;

import com.netflix.conductor.client.worker.Worker;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;

import java.util.Map;

public class SimpleWorkerV1 implements Worker {

    @Override
    public String getTaskDefName() {
        return "simple_task_example";
    }

    @Override
    public TaskResult execute(Task task) {
        Map<String, Object> inputData = task.getInputData();
        task.setOutputData(Map.of("result", inputData.get("result.random")));
        task.setStatus(Task.Status.COMPLETED);

        TaskResult taskResult = new TaskResult(task);
        taskResult.log(String.format("Task: %s,%nWorker: %s,%nDomain: %s",
                task.getTaskDefName(), getClass().getSimpleName(), task.getDomain()));
        return taskResult;
    }
}
