package com.example.conductordemo.service;

import com.netflix.conductor.common.metadata.workflow.StartWorkflowRequest;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import com.netflix.conductor.common.metadata.workflow.WorkflowTask;
import io.orkes.conductor.client.MetadataClient;
import io.orkes.conductor.client.WorkflowClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class WorkflowService {

    private final WorkflowClient workflowClient;

    private final MetadataClient metadataClient;

    public WorkflowService(WorkflowClient workflowClient, MetadataClient metadataClient) {
        this.workflowClient = workflowClient;
        this.metadataClient = metadataClient;
    }

    public void registerWorkflow() {
        WorkflowDef workflowDef = new WorkflowDef();
        workflowDef.setName("simple_workflow_example");
        workflowDef.setDescription("This is a workflow definition example");
        workflowDef.setOwnerEmail("example@mail.com");
        workflowDef.setVersion(1);
        workflowDef.setTimeoutPolicy(WorkflowDef.TimeoutPolicy.ALERT_ONLY);
        workflowDef.setTimeoutSeconds(60L);
        workflowDef.setTasks(getWorkFlowTasks());
        workflowDef.setOutputParameters(Map.of("result", "${simple_task.output.result}"));
        workflowDef.setFailureWorkflow("failure_workflow_example");
        metadataClient.registerWorkflowDef(workflowDef, true);

        WorkflowDef failureWorkflowDef = new WorkflowDef();
        failureWorkflowDef.setName("failure_workflow_example");
        failureWorkflowDef.setDescription("This is a failure workflow example");
        failureWorkflowDef.setOwnerEmail("example@mail.com");
        failureWorkflowDef.setRestartable(true);
        failureWorkflowDef.setVersion(1);
        failureWorkflowDef.setTasks(getWorkFlowTasksForFailure());
        metadataClient.registerWorkflowDef(failureWorkflowDef, true);

        WorkflowDef timeoutWorkflowDef = new WorkflowDef();
        timeoutWorkflowDef.setName("test_timeout_workflow");
        timeoutWorkflowDef.setDescription("This is a workflow definition example");
        timeoutWorkflowDef.setOwnerEmail("example@mail.com");
        timeoutWorkflowDef.setVersion(1);
        timeoutWorkflowDef.setTimeoutPolicy(WorkflowDef.TimeoutPolicy.ALERT_ONLY);
        timeoutWorkflowDef.setTimeoutSeconds(60L);
        timeoutWorkflowDef.setTasks(getTimeoutWorkflowTasks());
        metadataClient.registerWorkflowDef(timeoutWorkflowDef, true);
    }

    public void startWorkflow() {
        StartWorkflowRequest request = new StartWorkflowRequest();
        request.setName("simple_workflow_example");
        request.setVersion(1);
        request.setTaskToDomain(Map.of("simple_task_example", "V1"));

        workflowClient.startWorkflow(request);
    }

    private List<WorkflowTask> getWorkFlowTasks() {
        WorkflowTask inlineTask = new WorkflowTask();
        inlineTask.setName("inline_task_example");
        inlineTask.setTaskReferenceName("inline_task");
        inlineTask.setType("INLINE");
        inlineTask.setInputParameters(Map.ofEntries(
                Map.entry("evaluatorType", "javascript"),
                Map.entry("expression", "function execute() {return {\"random\": Math.random()}} execute();")
        ));

        WorkflowTask simpleTask = new WorkflowTask();
        simpleTask.setName("simple_task_example");
        simpleTask.setTaskReferenceName("simple_task");
        simpleTask.setType("SIMPLE");
        simpleTask.setOptional(true);
        simpleTask.setInputParameters(Map.of("result", "${inline_task.output.result}"));

        return List.of(inlineTask, simpleTask);
    }

    private List<WorkflowTask> getTimeoutWorkflowTasks() {

        WorkflowTask simpleTask = new WorkflowTask();
        simpleTask.setName("test_timeout_task");
        simpleTask.setTaskReferenceName("test_timeout");
        simpleTask.setType("SIMPLE");
        simpleTask.setOptional(true);

        return List.of(simpleTask);
    }

    private List<WorkflowTask> getWorkFlowTasksForFailure() {
        WorkflowTask eventTask = new WorkflowTask();
        eventTask.setName("event_task_example");
        eventTask.setTaskReferenceName("event_task");
        eventTask.setType("EVENT");
        eventTask.setSink("conductor");
        eventTask.setInputParameters(Map.ofEntries(
                Map.entry("workflowId", "${workflow.input.workflowId}"),
                Map.entry("failureTaskId", "${workflow.input.failureTaskId}")
        ));

        return List.of(eventTask);
    }
}
