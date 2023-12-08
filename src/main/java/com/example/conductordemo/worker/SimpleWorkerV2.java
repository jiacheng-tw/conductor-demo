package com.example.conductordemo.worker;

import com.netflix.conductor.client.worker.Worker;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;

import java.util.Map;

public class SimpleWorkerV2 implements Worker {

    @Override
    public String getTaskDefName() {
        return "simple_task_example";
    }

    @Override
    public TaskResult execute(Task task) {
        Map<String, String> resultMap = Map.class.cast(task.getInputData().get("result"));
        task.setOutputData(Map.of("result", resultMap.get("random")));
        task.setStatus(Task.Status.COMPLETED);

        TaskResult taskResult = new TaskResult(task);
        taskResult.log(String.format("Task: %s,%nWorker: %s,%nDomain: %s",
                task.getTaskDefName(), getClass().getSimpleName(), task.getDomain()));
        return taskResult;
    }
}
