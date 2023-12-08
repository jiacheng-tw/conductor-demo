package com.example.conductordemo.config;

import io.orkes.conductor.client.ApiClient;
import io.orkes.conductor.client.EventClient;
import io.orkes.conductor.client.MetadataClient;
import io.orkes.conductor.client.OrkesClients;
import io.orkes.conductor.client.TaskClient;
import io.orkes.conductor.client.WorkflowClient;
import io.orkes.conductor.client.http.OrkesEventClient;
import io.orkes.conductor.client.http.OrkesMetadataClient;
import io.orkes.conductor.client.http.OrkesTaskClient;
import io.orkes.conductor.client.http.OrkesWorkflowClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class ConductorConfig {

    @Value("${conductor.root-url}")
    private String rootUri;

    @Bean
    @Profile("local")
    public ApiClient localApiClient() {
        return new ApiClient(rootUri);
    }

    @Bean
    public ApiClient apiClient(@Value("${conductor.api-key}") String apiKey,
                               @Value("${conductor.api-secret}") String apiSecret) {
        return new ApiClient(rootUri, apiKey, apiSecret);
    }

    @Bean
    public OrkesClients orkesClients(@Autowired ApiClient apiClient) {
        return new OrkesClients(apiClient);
    }

    @Bean
    public WorkflowClient workflowClient(@Autowired ApiClient apiClient) {
        // return orkesClients.getWorkflowClient();
        return new OrkesWorkflowClient(apiClient);
    }

    @Bean
    public TaskClient taskClient(@Autowired ApiClient apiClient) {
        // return orkesClients.getTaskClient();
        return new OrkesTaskClient(apiClient);
    }

    @Bean
    public MetadataClient metadataClient(@Autowired ApiClient apiClient) {
        // return orkesClients.getMetadataClient();
        return new OrkesMetadataClient(apiClient);
    }

    @Bean
    public EventClient eventClient(@Autowired ApiClient apiClient) {
        // return orkesClients.getEventClient();
        return new OrkesEventClient(apiClient);
    }
}
