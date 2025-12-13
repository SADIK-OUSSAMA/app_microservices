package org.sadik.agentbot.service;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AiAgent {

    private final ChatClient chatClient;
    private final RestTemplate restTemplate;

    public AiAgent(ChatClient.Builder chatClientBuilder) {
        this.restTemplate = new RestTemplate();
        this.chatClient = chatClientBuilder
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(new InMemoryChatMemory()))
                .build();
    }

    public String chat(String userQuery) {
        try {
            return chatClient.prompt()
                    .user(userQuery)
                    .call()
                    .content();
        } catch (Exception e) {
            return "Sorry, I encountered an error: " + e.getMessage();
        }
    }
}