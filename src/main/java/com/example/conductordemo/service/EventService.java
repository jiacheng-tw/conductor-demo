package com.example.conductordemo.service;

import com.netflix.conductor.common.metadata.events.EventHandler;
import io.orkes.conductor.client.EventClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class EventService {

    private final EventClient eventClient;

    public EventService(EventClient eventClient) {
        this.eventClient = eventClient;
    }

    public void registerEventHandler() {
        EventHandler eventHandler = new EventHandler();
        eventHandler.setName("complete_task_event_example");
        eventHandler.setEvent("conductor:failure_workflow_example:event_task");
        eventHandler.setActive(true);
        eventHandler.setActions(getActions());

        eventClient.registerEventHandler(eventHandler);
    }

    private List<EventHandler.Action> getActions() {
        EventHandler.StartWorkflow startWorkflow = new EventHandler.StartWorkflow();
        startWorkflow.setName("simple_workflow_example");
        startWorkflow.setVersion(1);
        startWorkflow.setTaskToDomain(Map.of("simple_task_example", "V2"));

        EventHandler.Action action = new EventHandler.Action();
        action.setAction(EventHandler.Action.Type.start_workflow);
        action.setStart_workflow(startWorkflow);

        return List.of(action);
    }
}
