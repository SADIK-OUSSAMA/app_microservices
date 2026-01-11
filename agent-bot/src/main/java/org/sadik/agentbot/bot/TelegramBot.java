package org.sadik.agentbot.bot;

import jakarta.annotation.PostConstruct;
import org.sadik.agentbot.service.AiAgent;
import org.sadik.agentbot.service.RagService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.InputStream;
import java.net.URL;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.username}")
    private String botUsername;

    private final AiAgent aiAgent;
    private final RagService ragService;

    public TelegramBot(AiAgent aiAgent, RagService ragService) {
        this.aiAgent = aiAgent;
        this.ragService = ragService;
    }

    @PostConstruct
    public void registerBot() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(this);
            System.out.println("✅ Telegram bot registered successfully: " + botUsername);
        } catch (TelegramApiException e) {
            System.err.println("❌ Failed to register Telegram bot: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage()) {
            return;
        }

        String chatId = update.getMessage().getChatId().toString();

        try {
            // Send typing action
            SendChatAction chatAction = new SendChatAction();
            chatAction.setChatId(chatId);
            chatAction.setAction(org.telegram.telegrambots.meta.api.methods.ActionType.TYPING);
            execute(chatAction);

            String response;

            // Handle document uploads
            if (update.getMessage().hasDocument()) {
                response = handleDocumentUpload(update.getMessage().getDocument());
            }
            // Handle text messages
            else if (update.getMessage().hasText()) {
                String userText = update.getMessage().getText();
                response = handleTextMessage(userText);
            } else {
                response = "Please send a text message or a document (PDF/TXT).";
            }

            // Send response
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText(response);
            execute(message);

        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
            e.printStackTrace();
            sendErrorMessage(chatId);
        }
    }

    private String handleTextMessage(String text) {
        // Handle commands
        if (text.startsWith("/")) {
            return handleCommand(text);
        }
        // Process with AI agent (RAG-enabled)
        return aiAgent.chat(text);
    }

    private String handleCommand(String command) {
        return switch (command.toLowerCase().split(" ")[0]) {
            case "/start" -> """
                    👋 Welcome! I'm a RAG-powered assistant.

                    📄 Send me a document (PDF or TXT) and I'll answer questions based on its content.

                    Commands:
                    /status - Show indexed document count
                    /clear - Clear all indexed documents
                    /help - Show this help message
                    """;
            case "/help" -> """
                    📚 How to use me:
                    1. Send a PDF or TXT file
                    2. Ask questions about the document
                    3. I'll answer based ONLY on the document content

                    Commands:
                    /status - Show indexed document count
                    /clear - Clear all indexed documents
                    """;
            case "/status" -> {
                int count = ragService.getDocumentCount();
                yield count > 0
                        ? "📊 Currently indexed: " + count + " document chunks"
                        : "📭 No documents indexed yet. Send me a PDF or TXT file!";
            }
            case "/clear" -> {
                ragService.clearDocuments();
                yield "🗑️ All documents cleared! Send me a new document to start fresh.";
            }
            default -> "Unknown command. Use /help to see available commands.";
        };
    }

    private String handleDocumentUpload(Document document) {
        String fileName = document.getFileName();
        String mimeType = document.getMimeType();

        // Validate file type
        if (!isValidFileType(fileName, mimeType)) {
            return "❌ Unsupported file type. Please send a PDF or TXT file.";
        }

        try {
            // Download file from Telegram
            GetFile getFile = new GetFile();
            getFile.setFileId(document.getFileId());
            org.telegram.telegrambots.meta.api.objects.File telegramFile = execute(getFile);

            String fileUrl = "https://api.telegram.org/file/bot" + botToken + "/" + telegramFile.getFilePath();

            byte[] fileContent;
            try (InputStream is = new URL(fileUrl).openStream()) {
                fileContent = is.readAllBytes();
            }

            // Ingest document
            int chunks = ragService.ingestDocument(fileContent, fileName);

            return String.format(
                    "✅ Document '%s' indexed successfully!\n📊 Created %d searchable chunks.\n\n💬 Now ask me questions about this document!",
                    fileName, chunks);

        } catch (Exception e) {
            System.err.println("Error processing document: " + e.getMessage());
            e.printStackTrace();
            return "❌ Failed to process document: " + e.getMessage();
        }
    }

    private boolean isValidFileType(String fileName, String mimeType) {
        if (fileName != null) {
            String lower = fileName.toLowerCase();
            if (lower.endsWith(".pdf") || lower.endsWith(".txt")) {
                return true;
            }
        }
        if (mimeType != null) {
            return mimeType.contains("pdf") || mimeType.contains("text");
        }
        return false;
    }

    private void sendErrorMessage(String chatId) {
        try {
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText("Sorry, something went wrong. Please try again.");
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
