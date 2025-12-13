package org.sadik.agentbot.bot;



import jakarta.annotation.PostConstruct;
import org.sadik.agentbot.service.AiAgent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.username}")
    private String botUsername;

    private final AiAgent aiAgent;

    public TelegramBot(AiAgent aiAgent) {
        this.aiAgent = aiAgent;
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
        // Check if the update has a message and the message has text
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        String chatId = update.getMessage().getChatId().toString();
        String userText = update.getMessage().getText();

        try {
            // Send typing action to show bot is thinking
            SendChatAction chatAction = new SendChatAction();
            chatAction.setChatId(chatId);
            chatAction.setAction(org.telegram.telegrambots.meta.api.methods.ActionType.TYPING);
            execute(chatAction);

            // Process the message with AI agent
            String response = aiAgent.chat(userText);

            // Send the response back to the user
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText(response);
            execute(message);

        } catch (TelegramApiException e) {
            System.err.println("Error sending message: " + e.getMessage());
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
