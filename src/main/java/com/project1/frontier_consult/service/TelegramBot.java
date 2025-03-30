package com.project1.frontier_consult.service;

import com.project1.frontier_consult.config.BotConfig;
import com.project1.frontier_consult.model.PredefinedResponseRepository;
import com.project1.frontier_consult.model.User;
import com.project1.frontier_consult.model.UserRepository;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PredefinedResponseRepository predefinedResponseRepository;
    final BotConfig config;
    @Setter
    @Getter
    private int flag;

    public TelegramBot(BotConfig config) {
        this.config = config;

        List<BotCommand> listOfCommands = new ArrayList();
        listOfCommands.add(new BotCommand("/citizens", "Regulations on Republic of Moldova citizens"));
        listOfCommands.add(new BotCommand("/minors", "Regulations on minors"));
        listOfCommands.add(new BotCommand("/validity", "Check validity of your travel document"));
        listOfCommands.add(new BotCommand("/foreigners", "Regulations on foreign citizens"));
        listOfCommands.add(new BotCommand("/purpose", "What are purpose of entry documents"));
        listOfCommands.add(new BotCommand("/visas", "Who is requiring a visa for Republic of Moldova"));
        listOfCommands.add(new BotCommand("/accepted", "Accepted travel documents for Republic of Moldova"));
        listOfCommands.add(new BotCommand("/calculator", "Calculates period of staying"));
        listOfCommands.add(new BotCommand("/crossings", "Obtain the information about your border crossings"));
        listOfCommands.add(new BotCommand("/permis", "Obtain an electronic permit for access to border area"));
        listOfCommands.add(new BotCommand("/vehicles", "Border crossing rules for means of transport"));
        listOfCommands.add(new BotCommand("/assurance", "How to buy an assurance?"));
        listOfCommands.add(new BotCommand("/vinieta", "How to pay a road toll (vinieta)?"));
        listOfCommands.add(new BotCommand("/language", "Change the language"));
        listOfCommands.add(new BotCommand("/contacts", "How to contact us"));

        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot`s command list: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasCallbackQuery()) {
            String callBackData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            String name = update.getCallbackQuery().getMessage().getChat().getFirstName();

            switch (callBackData) {
                case "language":
                    startCommandReceived(chatId, name);
                    break;

                case "eng":
                    setFlag(1);
                    predefinedCommandRecieved(chatId, "main");
                    break;

                case "ro":
                    setFlag(2);
                    predefinedCommandRecieved(chatId, "main");
                    break;

                case "ru":
                    setFlag(3);
                    predefinedCommandRecieved(chatId, "main");
                    break;

                default:
                    predefinedCommandRecieved(chatId, callBackData);
            }
        }
        else if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            String name = update.getMessage().getChat().getFirstName();

            if (messageText.startsWith("/")) {
                switch (messageText) {
                    case "/start":
                        registerUser(update.getMessage());
                        startCommandReceived(chatId, name);
                        break;

                    case "/language":
                        startCommandReceived(chatId, name);
                        break;

                    default:
                        predefinedCommandRecieved(chatId, messageText.substring(1));
                }
            }
        }
    }

    private void registerUser(Message msg) {
        if (userRepository.findById(msg.getChatId()).isEmpty()) {
            var chatId = msg.getChatId();
            var chat = msg.getChat();

            User user = new User();

            user.setChatID(chatId);
            user.setFirstName(chat.getFirstName());
            user.setLastName(chat.getLastName());
            user.setUserName(chat.getUserName());
            user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));

            userRepository.save(user);

            String infoMessage = "Registered user: " + user + " from " + chat.getLocation() + "!\n\n" +
                    "At that moment, our community has " + userRepository.count() + " participants!";

            sendMessage(config.getOwnerId(), infoMessage, "test");

            log.info("Registered user: " + user);
        }
    }

    private void sendMessage(long chatId, String textToSend, String inlineMarkupOption) {
        SendMessage message = new SendMessage();
        InlineKeyboardMarkup markupInline = setMarkupInline(inlineMarkupOption);

        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        message.setReplyMarkup(markupInline);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }
    }

    private InlineKeyboardMarkup setMarkupInline(String inlineMarkupOption) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline3 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline4 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline5 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline6 = new ArrayList<>();
        List<InlineKeyboardButton> rowInline7 = new ArrayList<>();

        var mainButton = new InlineKeyboardButton();
        mainButton.setText("Main");
        mainButton.setCallbackData("main");

        var languageButton = new InlineKeyboardButton();
        languageButton.setText("Language");
        languageButton.setCallbackData("language");

        var engButton = new InlineKeyboardButton();
        engButton.setText("English");
        engButton.setCallbackData("eng");

        var roButton = new InlineKeyboardButton();
        roButton.setText("Romanian");
        roButton.setCallbackData("ro");

        var ruButton = new InlineKeyboardButton();
        ruButton.setText("Russian");
        ruButton.setCallbackData("ru");

        var citizensButton = new InlineKeyboardButton();
        citizensButton.setText("Citizens");
        citizensButton.setCallbackData("citizens");

        var foreignersButton = new InlineKeyboardButton();
        foreignersButton.setText("Foreigners");
        foreignersButton.setCallbackData("foreigners");

        var purposeDocsButton = new InlineKeyboardButton();
        purposeDocsButton.setText("Entry purpose");
        purposeDocsButton.setCallbackData("purposeDocs");

        var validityButton = new InlineKeyboardButton();
        validityButton.setText("Validity");
        validityButton.setCallbackData("validity");

        var visasButton = new InlineKeyboardButton();
        visasButton.setText("Visas");
        visasButton.setCallbackData("visas");

        var acceptedDocsButton = new InlineKeyboardButton();
        acceptedDocsButton.setText("Accepted Docs");
        acceptedDocsButton.setCallbackData("acceptedDocs");

        var calculatorButton = new InlineKeyboardButton();
        calculatorButton.setText("Calculator");
        calculatorButton.setCallbackData("calculator");

        var vehiclesButton = new InlineKeyboardButton();
        vehiclesButton.setText("Vehicles");
        vehiclesButton.setCallbackData("vehicles");

        var assuranceButton = new InlineKeyboardButton();
        assuranceButton.setText("Assurance");
        assuranceButton.setCallbackData("assurance");

        var vinietaButton = new InlineKeyboardButton();
        vinietaButton.setText("Vinieta");
        vinietaButton.setCallbackData("vinieta");

        var permisButton = new InlineKeyboardButton();
        permisButton.setText("Permis");
        permisButton.setCallbackData("permis");

        var crossingsButton = new InlineKeyboardButton();
        crossingsButton.setText("Crossings");
        crossingsButton.setCallbackData("crossings");

        var contactsButton = new InlineKeyboardButton();
        contactsButton.setText("Contacts");
        contactsButton.setCallbackData("contacts");

        var minorsButton = new InlineKeyboardButton();
        minorsButton.setText("Minors");
        minorsButton.setCallbackData("minors");

        if (inlineMarkupOption.equals("contacts")) {
            rowInline1.add(mainButton);
            rowsInline.add(rowInline1);

        } else if (inlineMarkupOption.equals("main")) {
            rowInline1.add(citizensButton);
            rowInline1.add(crossingsButton);

            rowInline2.add(minorsButton);
            rowInline2.add(permisButton);

            rowInline3.add(validityButton);
            rowInline3.add(vehiclesButton);

            rowInline4.add(foreignersButton);
            rowInline4.add(assuranceButton);

            rowInline5.add(visasButton);
            rowInline5.add(vinietaButton);

            rowInline6.add(acceptedDocsButton);
            rowInline6.add(purposeDocsButton);

            rowInline7.add(languageButton);
            rowInline7.add(contactsButton);

            rowsInline.add(rowInline1);
            rowsInline.add(rowInline2);
            rowsInline.add(rowInline3);
            rowsInline.add(rowInline4);
            rowsInline.add(rowInline5);
            rowsInline.add(rowInline6);
            rowsInline.add(rowInline7);

        } else if (inlineMarkupOption.equals("vehicles")) {
            rowInline1.add(assuranceButton);
            rowInline1.add(vinietaButton);

            rowInline2.add(validityButton);
            rowInline2.add(crossingsButton);

            rowInline3.add(mainButton);

            rowsInline.add(rowInline1);
            rowsInline.add(rowInline2);
            rowsInline.add(rowInline3);

        } else if (inlineMarkupOption.equals("foreigners")) {
            rowInline1.add(purposeDocsButton);
            rowInline1.add(visasButton);

            rowInline2.add(acceptedDocsButton);
            rowInline2.add(calculatorButton);

            rowInline3.add(mainButton);

            rowsInline.add(rowInline1);
            rowsInline.add(rowInline2);
            rowsInline.add(rowInline3);

        } else if (inlineMarkupOption.equals("citizens")) {
            rowInline1.add(validityButton);
            rowInline1.add(vehiclesButton);

            rowInline2.add(minorsButton);

            rowInline3.add(mainButton);

            rowsInline.add(rowInline1);
            rowsInline.add(rowInline2);
            rowsInline.add(rowInline3);

        } else if (inlineMarkupOption.equals("purposeDocs")) {
            rowInline1.add(foreignersButton);
            rowInline1.add(visasButton);

            rowInline2.add(acceptedDocsButton);
            rowInline2.add(calculatorButton);

            rowInline3.add(mainButton);

            rowsInline.add(rowInline1);
            rowsInline.add(rowInline2);
            rowsInline.add(rowInline3);

        } else if (inlineMarkupOption.equals("start") || inlineMarkupOption.equals("language")) {
            rowInline1.add(engButton);
            rowInline2.add(roButton);
            rowInline3.add(ruButton);

            rowsInline.add(rowInline1);
            rowsInline.add(rowInline2);
            rowsInline.add(rowInline3);

        } else {
            rowInline1.add(citizensButton);
            rowInline1.add(vehiclesButton);

            rowInline2.add(foreignersButton);
            rowInline2.add(minorsButton);

            rowInline3.add(mainButton);

            rowsInline.add(rowInline1);
            rowsInline.add(rowInline2);
            rowsInline.add(rowInline3);
        }

        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }

    private String chooseAnswerLanguage(int flag) {
        if (flag == 2) {
            return "ro";
        } else if (flag == 3) {
            return "ru";
        } else {
            return "eng";
        }
    }

    private void startCommandReceived(long chatId, String name) {
        String answer = "Hi, " + name + ", nice to meet you!\n\n" +
                "I was created to consult you about border crossing regulations in the Republic of Moldova!\n\n" +
                "You can choose the language to continue our conversation:\n\n";

        sendMessage(chatId, answer, "start");
        log.info("Replied to user {}", name);
    }

    private void predefinedCommandRecieved(long chatID, String predifinedCommand) {
        String language = chooseAnswerLanguage(flag);

        String answer = predefinedResponseRepository
                .findByCommandAndLanguage(predifinedCommand, language)
                .get()
                .getResponseText();

        if (!answer.isEmpty()){
            sendMessage(chatID, answer, predifinedCommand);
        } else {
            String defaultAnswer = predefinedResponseRepository
                    .findByCommandAndLanguage("default", language)
                    .get()
                    .getResponseText();
            sendMessage(chatID, defaultAnswer, "main");
        }
    }
}