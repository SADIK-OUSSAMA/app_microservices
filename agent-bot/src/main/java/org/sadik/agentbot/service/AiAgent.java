package org.sadik.agentbot.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.stereotype.Service;

@Service
public class AiAgent {

    private final ChatClient chatClient;
    private final RagService ragService;

    private static final String RAG_SYSTEM_PROMPT = """
            You are a helpful assistant that answers questions ONLY based on the provided context.
            If the context doesn't contain relevant information to answer the question, say:
            "I don't have information about that in the uploaded documents."
            Do not make up information. Only use what is provided in the context.

            Context:
            %s
            """;

    public AiAgent(ChatClient.Builder chatClientBuilder, RagService ragService) {
        this.ragService = ragService;
        this.chatClient = chatClientBuilder
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(new InMemoryChatMemory()))
                .build();
    }

    public String chat(String userQuery) {
        try {
            // Check if documents are uploaded
            if (!ragService.hasDocuments()) {
                return "📄 No documents uploaded yet. Please send me a document (PDF or TXT) first, then ask your questions!";
            }

            // Get relevant context from RAG
            String context = ragService.getContext(userQuery);
            if (context == null || context.isEmpty()) {
                return "I couldn't find relevant information in the uploaded documents for your question.";
            }

            // Build prompt with RAG context
            String systemPrompt = String.format(RAG_SYSTEM_PROMPT, context);

            return chatClient.prompt()
                    .system(systemPrompt)
                    .user(userQuery)
                    .call()
                    .content();
        } catch (Exception e) {
            return "Sorry, I encountered an error: " + e.getMessage();
        }
    }
}