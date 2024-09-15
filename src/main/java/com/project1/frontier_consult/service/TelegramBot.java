package com.project1.frontier_consult.service;

import com.project1.frontier_consult.config.BotConfig;
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
    final BotConfig config;
    @Setter
    @Getter
    private int flag;

    public TelegramBot(BotConfig config) {
        this.config = config;

        List<BotCommand> listOfCommands = new ArrayList();
        listOfCommands.add(new BotCommand("/citizens", "regulations on Republic of Moldova citizens"));
        listOfCommands.add(new BotCommand("/validity", "check validity of your travel document"));
        listOfCommands.add(new BotCommand("/foreigners", "regulations on foreign citizens"));
        listOfCommands.add(new BotCommand("/purposeDocs", "what are purpose of entry documents"));
        listOfCommands.add(new BotCommand("/visas", "who is requiring a visa for Republic of Moldova"));
        listOfCommands.add(new BotCommand("/acceptedDocs", "accepted travel documents for Republic of Moldova"));
        listOfCommands.add(new BotCommand("/calculator", "calculates period of staying"));
        listOfCommands.add(new BotCommand("/crossings", "obtain the information about your border crossings"));
        listOfCommands.add(new BotCommand("/permis", "obtain an electronic permit for access to border area"));
        listOfCommands.add(new BotCommand("/vehicles", "border crossing rules for means of transport"));
        listOfCommands.add(new BotCommand("/assurance", "how to buy an assurance"));
        listOfCommands.add(new BotCommand("/vinieta", "how to pay a road toll"));
        listOfCommands.add(new BotCommand("/language", "change the language"));
        listOfCommands.add(new BotCommand("/contacts", "how to contact us"));

        try{
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        }
        catch (TelegramApiException e) {
            log.error("Error setting bot`s command list: " + e.getMessage());
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

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            long id = update.getMessage().getChat().getId();
            String name = update.getMessage().getChat().getFirstName();

            switch (messageText) {
                case "/start":
                    registerUser(update.getMessage());
                    startCommandReceived(chatId, name);
                    break;

                case "/language":
                    startCommandReceived(chatId, name);
                    break;

                case "/eng":
                    setFlag(1);
                    languageCommandReceived(chatId);
                    break;

                case "/ro":
                    setFlag(2);
                    languageCommandReceived(chatId);
                    break;

                case "/ru":
                    setFlag(3);
                    languageCommandReceived(chatId);
                    break;

                case "/main":
                    languageCommandReceived(chatId);
                    break;

                case "/citizens":
                    citizensCommandReceived(chatId);
                    break;

                case "/foreigners":
                    foreignersCommandReceived(chatId);
                    break;

                case "/purposeDocs":
                    purposeDocsCommandReceived(chatId);
                    break;

                case "/validity":
                    validityCommandReceived(chatId);
                    break;

                case "/visas":
                    visasCommandReceived(chatId);
                    break;

                case "/acceptedDocs":
                    acceptedDocsCommandReceived(chatId);
                    break;

                case "/calculator":
                    calculatorCommandReceived(chatId);
                    break;

                case "/vehicles":
                    vehiclesCommandReceived(chatId);
                    break;

                case "/assurance":
                    assuranceCommandReceived(chatId);
                    break;

                case "/vinieta":
                    vinietaCommandReceived(chatId);
                    break;

                case "/permis":
                    permisCommandReceived(chatId);
                    break;

                case "/crossings":
                    crossingsCommandReceived(chatId);
                    break;

                case "/contacts":
                    contactsCommandReceived(chatId);
                    break;

                default:
                    defaultCommandReceived(chatId);
            }
        }
    }

    private void contactsCommandReceived(long chatId) {
        String answerEng = "We are glad that you are with us!!! " +
                "You can send any questions, clarifications, or need for additional consultations " +
                "to our email.: \npolitia.frontiera@border.gov.md\n" +
                "or you can contact us! Border Police Green Line: +37322259717\n” + " +
                "Thanks!!! Go on!!!\n\n" +

                "/main - back to main menu";

        String answerRo = "Ne bucurăm că ești alături de noi!!! Orice întrebare, clarificare sau nevoie de consultații " +
                "suplimentare, le puteți trimite la adresa electronica: \npolitia.frontiera@border.gov.md\n" +
                "sau puteți să contactați Linia Verde a Poliției de Frontieră la numărul de telefon : +37322259717\n" +
                "Mulțumesc!!! Continuați!!!\n\n" +

                "/main - înapoi la meniul principal";

        String answerRu = "Мы рады тому, что вы с нами!!!\n" +
                "Любые вопросы, уточнения, необходимость в дополнительных консультациях вы можете отправлять " +
                "письмом на наш email: \npolitia.frontiera@border.gov.md\n" +
                "либо позвонив на Зеленую линию Пограничной Полиции по номеру телефона: +37322259717\n" +
                "Спасибо!!! Продолжайте!!!\n\n" +

                "/main - в главное меню";

        chooseAnswerLanguage(chatId, answerRo, answerRu, answerEng);
    }

    private void registerUser(Message msg) {
        if(userRepository.findById(msg.getChatId()).isEmpty()){
            var chatId = msg.getChatId();
            var chat = msg.getChat();

            User user = new User();

            user.setChatID(chatId);
            user.setFirstName(chat.getFirstName());
            user.setLastName(chat.getLastName());
            user.setUserName(chat.getUserName());
            user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));

            userRepository.save(user);

            String infoMessage = "Registered user: " + user + " from " + chat.getLocation().toString() + "!\n" +
                    "At that moment, our community has " + userRepository.count() + " participants!";

            sendMessage(config.getOwnerId(), infoMessage);

            log.info("Registered user: " + user);
        }
    }

    /*private void myDataCommandReceived(long chatId, String name, long id) {
        String answerEng = "Your Chat ID is: " + chatId + "\n" +
                "Your Telegram ID is: " + id + "\n" +
                "Your Telegram Name is: " + name + "\n\n" +
                "/main - back to main menu";

        String answerRo = "Your Chat ID is: " + chatId + "\n" +
                "Your Telegram ID is: " + id + "\n" +
                "Your Telegram Name is: " + name + "\n\n" +
                "/main - înapoi la meniul principal";

        String answerRu = "Ваш Chat ID: " + chatId + "\n" +
                "Ваш Telegram ID: " + id + "\n" +
                "Ваше Telegram имя: " + name + "\n\n" +
                "/main - вернуться в главное меню";

        chooseAnswerLanguage(chatId, answerRo, answerRu, answerEng);
    }*/

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }
    }

    private InlineKeyboardMarkup setMarkupInline(){
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List <List<InlineKeyboardMarkup>> rowsInline = new ArrayList<>();
        List<InlineKeyboardMarkup> rowInline = new ArrayList<>();

        var mainButton = new InlineKeyboardButton();
        mainButton.setText("Main");
        mainButton.setCallbackData("/main");

        var languageButton = new InlineKeyboardButton();
        languageButton.setText("Language");
        languageButton.setCallbackData("/language");

        var engButton = new InlineKeyboardButton();
        engButton.setText("English");
        engButton.setCallbackData("/eng");

        var roButton = new InlineKeyboardButton();
        roButton.setText("Romanian");
        roButton.setCallbackData("/ro");

        var ruButton = new InlineKeyboardButton();
        ruButton.setText("Russian");
        ruButton.setCallbackData("/ru");

        var citizensButton = new InlineKeyboardButton();
        citizensButton.setText("Citizens");
        citizensButton.setCallbackData("/citizens");

        var foreignersButton = new InlineKeyboardButton();
        foreignersButton.setText("Foreigners");
        foreignersButton.setCallbackData("/foreigners");

        var purposeDocsButton = new InlineKeyboardButton();
        purposeDocsButton.setText("Entry purpose");
        purposeDocsButton.setCallbackData("/purposeDocs");

        var validityButton = new InlineKeyboardButton();
        validityButton.setText("Validity");
        validityButton.setCallbackData("/validity");

        var visasButton = new InlineKeyboardButton();
        visasButton.setText("Visas");
        visasButton.setCallbackData("/visas");

        var acceptedDocsButton = new InlineKeyboardButton();
        acceptedDocsButton.setText("Accepted Docs");
        acceptedDocsButton.setCallbackData("/acceptedDocs");

        var calculatorButton = new InlineKeyboardButton();
        calculatorButton.setText("Calculator");
        calculatorButton.setCallbackData("/calculator");

        var vehiclesButton = new InlineKeyboardButton();
        vehiclesButton.setText("Vehicles");
        vehiclesButton.setCallbackData("/vehicles");

        var assuranceButton = new InlineKeyboardButton();
        assuranceButton.setText("Assurance");
        assuranceButton.setCallbackData("/assurance");

        var vinietaButton = new InlineKeyboardButton();
        vinietaButton.setText("Vinieta");
        vinietaButton.setCallbackData("/vinieta");

        var permisButton = new InlineKeyboardButton();
        permisButton.setText("Permis");
        permisButton.setCallbackData("/permis");

        var crossingsButton = new InlineKeyboardButton();
        crossingsButton.setText("Crossings");
        crossingsButton.setCallbackData("/crossings");

        var contactsButton = new InlineKeyboardButton();
        contactsButton.setText("Contacts");
        contactsButton.setCallbackData("/contacts");



        return markupInline;
    }

    private void startCommandReceived(long chatId, String name) {
        String answer = "Hi, " + name + ", nice to meet you!\n\n" +
                "I was created to consult you about border crossing regulations in the Republic of Moldova!\n\n" +
                "You can choose the language to continue our conversation:\n\n" +
                "/eng - for english language;\n" +
                "/ro - pentru limba română;\n" +
                "/ru - для русского языка.";

        sendMessage(chatId, answer);
        log.info("Replied to user {}", name);
    }

    private void languageCommandReceived(long chatId) {
        String answerEng = "So let's go!!! " +
                "What border crossing information do you need to consult?\n\n" +
                "You can control me by sending these commands:\n\n" +
                "**PERSONS**\n" +
                "/citizens - regulations on Republic of Moldova citizens\n" +
                "/validity - check validity of your travel document\n" +
                "/foreigners - regulations on foreign citizens\n" +
                "/purposeDocs - what are purpose of entry documents\n" +
                "/visas - who is requiring a visa for RM\n" +
                "/acceptedDocs - accepted list of travel documents for RM\n" +
                "/calculator - calculates period of staying\n" +
                "/crossings – obtain the information about your border crossings\n" +
                "/permis – obtain the electronic permit of access to the border area\n\n" +
                "**VEHICLES**\n" +
                "/vehicles - border crossing rules for means of transport\n" +
                "/assurance - how to buy an assurance\n" +
                "/vinieta - how to pay a road toll\n" +
                "/validity - check validity of your vehicle's document\n" +
                "/crossings – obtain the information about your vehicle border crossings\n\n" +
                "**OTHER DATA**\n" +
                "/language - change the language\n" +
                "/contacts - how to contact us";

        String answerRo = "Noroc, am fost creat pentru oferirea consultațiilor privind regulile de trecere a frontierei RM!\n" +
                "Ce informații doriți să consultați?\n\n" +
                "Vă rog să alegeți:\n\n" +
                "**PERSOANE**\n" +
                "/citizens - regulile de trecere a cetățenilor RM\n" +
                "/validity - verifică valabilitate documentului de călătorie\n" +
                "/foreigners - regulile de trecere a străinilor\n" +
                "/purposeDocs - documente pentru argumentarea scopului călătoriei\n" +
                "/visas - cine are nevoie de o viză pentru RM\n" +
                "/acceptedDocs - lista actelor de călătorie acceptate pentru RM\n" +
                "/calculator - calcularea termenului de ședere\n" +
                "/crossings – obține informația privind traversarea frontierei\n\n" +
                "/permis – obține permisul electronic de acces în zona de frontieră\n\n" +
                "**VEHICULE**\n" +
                "/vehicles - regulile de trecere a mijloacelor de transport\n" +
                "/assurance - cum de procurat o asigurare\n" +
                "/vinieta - cum de achitat vinieta\n" +
                "/validity - verifică valabilitate unui document pentru vehiculul\n" +
                "/crossings – obține informația privind traversarea frontierei pentru vehiculul \n\n" +
                "**ALTE DATE**\n" +
                "/language - schimbă limba\n" +
                "/contacts - cum să ne contactați";

        String answerRu = "Привет! Я был создан для предоставления консультаций о правилах пересечения границы Республики Молдова!\n" +
                "О чем бы вы хотели узнать?\n\n" +
                "Пожалуйста выберите:\n\n" +
                "**ЛИЦА**\n" +
                "/citizens - правила пересечения граждан РМ\n" +
                "/validity - проверь действительность проездного документа\n" +
                "/foreigners - правила пересечение для иностранцев\n" +
                "/purposeDocs - документы для подтверждения цели поездки\n" +
                "/visas - кому требуется виза для въезда в РМ\n" +
                "/acceptedDocs - список признаваемых проездных документов для РМ\n" +
                "/calculator - калькулятор сроков пребывания\n" +
                "/crossings – получить информацию о пересечении границы \n" +
                "/permis – получить электронное разрешение для нахождения в пограничной зоне\n\n" +
                "**ТРАНСПОРТ**\n" +
                "/vehicles - правила пересечения для транспортных средств\n" +
                "/assurance - как приобрести страховку\n" +
                "/vinieta - как оплатить виньетку\n" +
                "/validity - проверь действительность своих проездных документов\n" +
                "/crossings – получить информацию о пересечении границы  транспортным средством\n\n" +
                "**ДРУГОЕ**\n" +
                "/language - поменять язык\n" +
                "/contacts - как с нами связаться";

        chooseAnswerLanguage(chatId, answerRo, answerRu, answerEng);
    }

    /*private void communityCommandReceived(long chatId) {
        long number = userRepository.count();
        String answerEng = "At that moment, our community has " + number + " participants!\n\n" +
                "/main - back to main menu";
        String answerRo = "La momentul actual, comunitatea noastră are " + number + " participanți!\n\n" +
                "/main - înapoi la meniul principal";
        String answerRu = "На данный момент в нашем сообществе " + number + " участников!\n\n" +
                "/main - вернуться в главное меню";

        chooseAnswerLanguage(chatId, answerRo, answerRu, answerEng);
    }*/

    private void calculatorCommandReceived(long chatId){
        String answerEng = "For calculating the period of staying of the foreign citizens in the " +
                "Republic of Moldova you can consult the official Schengen calculator following the link:\n" +
                "https://ec.europa.eu/assets/home/visa-calculator/calculator.htm?lang=en\n\n" +

                "/citizens - regulations on Republic of Moldova citizens\n" +
                "/foreigners - regulations on foreign citizens\n" +
                "/vehicles - border crossing rules for vehicles\n" +
                "/main - back to main menu";

        String answerRo = "Conform prevederilor art.84 alin.(3) al Legii nr.200/2010 privind regimul străinilor în Republica Moldova, " +
                "Străinii se pot afla pe teritoriul Republicii Moldova până la 90 de zile calendaristice în decursul oricărei perioade de 180 de zile " +
                "calendaristice, ceea ce implică luarea în considerare a ultimei perioade de 180 de zile precedente fiecărei zile de ședere.\n\n" +
                "Pentru calcularea termenului de ședere a cetățenilor străini în Republica Moldova " +
                "puteți folosi Calculatorul Schengen accesând următorul link:\n" +
                "https://ec.europa.eu/assets/home/visa-calculator/calculator.htm?lang=en\n\n" +

                "/citizens - regulile de trecere a cetățenilor RM\n" +
                "/foreigners - regulile de trecere a străinilor\n" +
                "/vehicles - regulile de trecere a vehiculelor\n" +
                "/main - înapoi la meniul principal";

        String answerRu = "В соответствии с положениями ст.84 пкт.(3) Закона №200/2010 о режиме иностранцев в Республике Молдова, " +
                "Иностранцы могут находиться на территории РМ не более 90 календарных дней в течение любого периода в 180 календарных дней, " +
                "что предполагает учет последнего 180-дневного периода, предшествующего каждому дню пребывания.\n\n" +
                "Для подсчета сроков пребывания иностранцев на территории РМ, можете воспользоваться " +
                "Официальным Шенгенским Калькулятором, пройдя по следующей ссылке:\n" +
                "https://ec.europa.eu/assets/home/visa-calculator/calculator.htm?lang=en\n\n" +

                "/citizens - правила пересечения граждан РМ\n" +
                "/foreigners - правила пересечение для иностранцев\n" +
                "/vehicles - правила пересечения для транспортных средств\n" +
                "/main - вернуться в главное меню";

        chooseAnswerLanguage(chatId, answerRo, answerRu, answerEng);
    }

    private void vehiclesCommandReceived(long chatId) {
        String answerEng = "Documents that are requested from the driver during crossing the border:\n\n" +
                "1) Driver's license valid for the category to which the driven vehicle belongs;\n" +
                "2) Vehicle registration certificate;\n" +
                "4) Documents relating to the nature and mass of the cargo, in the cases established by the legislation \n" +
                "5) Vehicles insurance valid for the Republic of Moldova or abroad, depending on the circumstances;\n" +
                "6) Authorization for the passengers transportation (regular or occasional services), in case of foreign transport operators;\n" +
                "7) Confirmation that attesting the payment of the vignette for the period of stay in the Republic of Moldova for vehicles " +
                "registered outside the Republic of Moldova, on exiting the country.\n\n" +

                "/assurance - how to buy an assurance\n" +
                "/vinieta - how to pay a road toll\n" +
                "/validity - check validity of your vehicle's document\n\n" +
                "/main - back to main menu";

        String answerRo = "Documente solicitate la traversarea frontierei de la conducătorul mijlocului de transport sunt următoarele:\n\n" +
                "1) Permisul de conducere valabil pentru categoria din care face parte vehiculul condus; \n" +
                "2) Certificatul de înmatriculare a vehiculului; \n" +
                "3) Actul care confirmă transmiterea în folosință a vehiculului, în cazul mijlocului de transport transmis în folosință având numărul de înmatriculare neutru; \n" +
                "4) Actele referitoare la natura și masa încărcăturii, în cazurile stabilite de legislație; \n" +
                "5) Asigurarea obligatorie de răspundere civilă auto valabilă pentru Republica Moldova sau pentru străinătate, după caz; \n" +
                "6) Autorizația de transport rutier de persoane prin servicii regulate sau ocazionale, în cazul operatorilor de transport străini; \n" +
                "7) Confirmarea ce atestă achitarea vinietei pentru perioada de aflare în Republica Moldova pentru vehicule înmatriculate în afară RM, la ieșire din țară.\n\n" +

                "/assurance - cum de procurat o asigurare\n" +
                "/vinieta - cum de achitat vinieta\n" +
                "/validity - verifică valabilitate unui document pentru vehiculul\n\n" +
                "/main - înapoi la meniul principal";

        String answerRu = "Документами, запрашиваемыми у водителя транспортного средства при пересечении границы, являются:\n" +
                "1) Водительское удостоверение, действительное для категории, к которой принадлежит управляемое транспортное средство;\n" +
                "2) Свидетельство о регистрации транспортного средства;\n" +
                "3) Акт, подтверждающий передачу транспортного средства в пользование, в случае, если транспортное средство, передаваемое в пользование, имеет нейтральный регистрационный номер;\n" +
                "4) Документы, касающиеся характера и массы груза, в случаях, установленных законодательством;\n" +
                "5) Обязательное страхование автогражданской ответственности, действительное на территории Республики Молдова или за рубежом, в зависимости от обстоятельств;\n" +
                "6) Разрешение на автомобильные перевозки людей регулярными или нерегулярными рейсами в случае транспортных операторов;\n" +
                "7) Подтверждение оплаты виньетки на период пребывания в Республике Молдова для транспортных средств, зарегистрированных за пределами Республики Молдова, при выезде из страны.\n\n" +

                "/assurance - как приобрести страховку\n" +
                "/vinieta - как оплатить виньетку\n" +
                "/validity - проверь действительность своих проездных документов\n" +
                "/main - вернуться в главное меню";

        chooseAnswerLanguage(chatId, answerRo, answerRu, answerEng);
    }

    private void foreignersCommandReceived(long chatId) {
        String answerEng = "Foreigners who meet the following conditions are allowed to enter the territory of the Republic of Moldova:\n" +
                "- possesses a valid travel document (expiry date is at least 3 month after intended day of departure and is issued in last 10 years period);\n" +
                "- has a visa;\n" +
                "- presents documents for justifying the purpose of entry, as well as the proof of means " +
                "for maintenance during the stay and for returning to the country of origin " +
                "or for transit to another state where there is a certainty that they will be allowed to enter;\n" +
                "- does not pose a danger to national security, public order and health.\n\n" +

                "More details are established in:\n" +
                "- art.6 of Law 200/2010: https://www.legis.md/cautare/getResults?doc_id=141517&lang=ro\n" +
                "- art.17 of Law 215/2011: https://www.legis.md/cautare/getResults?doc_id=139911&lang=ro#\n\n" +

                "/purposeDocs - what are the documents for justifying the purpose of entry\n" +
                "/visas - who is requiring a visa for RM\n" +
                "/acceptedDocs - accepted list of travel documents for RM\n" +
                "/calculator - calculates period of staying\n\n" +
                "/vehicles - border crossing rules for foreign vehicles\n" +
                "/main - back to main menu";

        String answerRo = "Intrarea pe teritoriul RM este permisă străinilor care întrunesc următoarele condiţii:\n" +
                "- posedă un document de călătorie valabil;\n" +
                "- posedă viză;\n" +
                "- prezintă documente care justifică scopul intrării, precum și dovada unor mijloace corespunzătoare " +
                "atât pentru întreţinere pe perioada şederii, cât şi pentru întoarcere în ţara de origine " +
                "sau pentru tranzit către un alt stat în care există siguranţa că li se va permite intrarea;\n" +
                "- nu prezintă pericol pentru securitatea naţională, ordinea şi sănătatea publică\n\n" +

                "Mai multe detalii sunt stabilite în:\n" +
                "- art.6 al Legii 200/2010: https://www.legis.md/cautare/getResults?doc_id=141517&lang=ro\n" +
                "- art.17 al Legii 215/2011: https://www.legis.md/cautare/getResults?doc_id=139911&lang=ro#\n\n" +

                "/purposeDocs - care sunt documente pentru justificarea scopului călătoriei\n" +
                "/visas - cine are nevoie de o viză pentru RM\n" +
                "/acceptedDocs - lista actelor de călătorie acceptate pentru RM\n" +
                "/calculator - calcularea termenului de ședere\n\n" +
                "/vehicles - regulile de trecere a vehiculelor din străinătate\n" +
                "/main - înapoi la meniul principal";

        String answerRu = "Въезд на территорию РМ разрешается иностранцам, если они удовлетворяют следующим условиям:\n" +
                "- обладают действительными проездными документами;\n" +
                "- имеют визу\n" +
                "- представляют документы, обосновывающие цель въезда, а также доказательства наличия соответствующих " +
                "средств как на содержание в период пребывания, так и для возвращения в страну происхождения " +
                "или для транзита в другую страну при наличии гарантии разрешения въезда на ее территорию;\n" +
                "- не представляют угрозы национальной безопасности, общественному порядку и общественному здоровью.\n\n" +

                "Больше деталей можно найти в:\n" +
                "- ст.6 Закона 200/2010: https://www.legis.md/cautare/getResults?doc_id=141517&lang=ru\n" +
                "- ст.17 Закона 215/2011: https://www.legis.md/cautare/getResults?doc_id=139911&lang=ru\n\n" +

                "/purposeDocs - какие документы для обоснования цели поездки\n" +
                "/visas - кому требуется виза для въезда в РМ\n" +
                "/acceptedDocs - список признаваемых проездных документов для РМ\n" +
                "/calculator - калькулятор сроков пребывания\n\n" +
                "/vehicles - правила пересечения для иностранных транспортных средств\n" +
                "/main - вернуться в главное меню";

        chooseAnswerLanguage(chatId, answerRo, answerRu, answerEng);
    }

    private void citizensCommandReceived(long chatId) {
        String answerEng = "Citizens of the Republic of Moldova have the right to leave and enter the Republic of Moldova based on their passport.\n" +
                "Residents of the border districts on the Moldovan-Ukrainian border have the right to leave and enter the Republic of Moldova based on their identity card " +
                "through the crossing point within the administrative district.\n" +
                "At the same time, for entry/exit to/from Turkey, citizens of the Republic of Moldova have the right to cross the border based on their identity card.\n\n" +
                "Minors have the right to leave and enter the Republic of Moldova only accompanied by ONE of their legal representatives " +
                "or by a companion, designated by declaration by the legal representative whose signature is legalized by the notary.\n\n" +
                "Minors (pupils and students) who have reached the age of 14 and are enrolled in studies " +
                "in educational institutions from other states, can present the document of enrollment at the educational institution " +
                "and the declaration issued by ONE of the parents, authenticated by a notary, " +
                "which contains his consent for the minor's exit and entry to the Republic of Moldova.\n\n" +

                "More details are established in:\n" +
                "- art.1 of Law 269/1994: https://www.legis.md/cautare/getResults?doc_id=131615&lang=ro\n\n" +

                "/validity - check validity of your travel document\n" +
                "/vehicles - border crossing rules for RM vehicles\n" +
                "/main - back to main menu";

        String answerRo = "Cetăţenii Republicii Moldova au dreptul de a ieşi şi intra în RM în baza paşaportului.\n" +
                "Locuitorii raioanelor de frontieră de la frontiera moldo-ucraineană au dreptul de a ieși și intra în RM în bază buletinului de identitate " +
                "prin punctul de trecere din raza raionului administrativ.\n" +
                "Totodată, la intrarea/ieșirea în/din Turcia, cetățenii RM au dreptul de a traversa frontiera în bază buletinului de identitate.\n\n" +
                "Minorii au dreptul de a ieşi şi de a intra în RM numai însoţiţi de UNUL dintre reprezentanţii lor legali " +
                "sau de un însoţitor, desemnat prin declaraţie de către reprezentantul legal a cărui semnătură se legalizează de notar.\n\n" +
                "Minorii (elevii şi studenţii) care au împlinit vîrsta de 14 ani şi sînt înmatriculaţi la studii " +
                "în instituţii de învăţămînt din alte state, pot prezenta actul de înmatriculare la instituţia de învăţămînt " +
                "şi declaraţia eliberată de UNUL dintre părinţi, autentificată de notar, " +
                "care conţine consimţămîntul acestuia pentru ieşirea şi intrarea minorului în RM.\n\n" +

                "Mai multe detalii sunt stabilite în:\n" +
                "- art.1 al Legii 269/1994: https://www.legis.md/cautare/getResults?doc_id=131615&lang=ro\n\n" +

                "/validity - verifică valabilitate documentului de călătorie\n" +
                "/vehicles - regulile de trecere pentru vehicule din RM\n" +
                "/main - înapoi la meniul principal";

        String answerRu = "Граждане Республики Молдова имеют право пересекать границу на основании паспортов. \n" +
                "Жители приграничных районов на молдо-украинской границе имеют право пересекать границу на основании внутреннего удостоверения личности " +
                "через пункты пропуска находящиеся в административном районе\n" +
                "В тоже время, на выезд/въезд в/из Турции, граждане РМ имеют право пересекать границу на основании внутреннего удостоверения личности.\n\n" +
                "Несовершеннолетние лица имеют право выезжать из РМ и въезжать в РМ только в сопровождении своего законного представителя " +
                "или сопровождающего лица, назначенного посредством декларации законным представителем, подпись которого заверяется нотариусом\n\n" +
                "Несовершеннолетние лица (учащиеся и студенты), достигшие возраста 14 лет, зачисленные на учебу в учебные заведения других государств, " +
                "при пересечении границы предъявляют акт о зачислении в соответствующее учебное заведение и удостоверенное нотариусом заявление одного из родителей, " +
                "содержащее его согласие о пересечении несовершеннолетним границы\n\n" +

                "Больше деталей можно найти в:\n" +
                "- ст.1 Закона 269/1994: https://www.legis.md/cautare/getResults?doc_id=131615&lang=ru\n\n" +

                "/validity - проверь действительность проездного документа\n" +
                "/vehicles - правила пересечения для транспортных средств из РМ\n" +
                "/main - вернуться обратно в основное меню";

        chooseAnswerLanguage(chatId, answerRo, answerRu, answerEng);
    }

    private void purposeDocsCommandReceived(long chatId) {
        String answerEng = "To justify the purpose of foreigners' entry into the Republic of Moldova, " +
                "the following documents must be presented:\n\n" +
                "a) for business trips:\n" +
                "- an invitation from a company, organization or institution;\n" +
                "- documents confirming an economic interest (for example: power of attorney, employment contract, " +
                "tickets or invitations to participate in exhibitions);\n\n" +
                "b) for trips for the purpose of study or other types of training:\n" +
                "- a document certifying enrollment in an educational institution or other forms of training;\n" +
                "- student card or course attendance certificate;\n\n" +
                "c) for tourist trips or personal reasons:\n" +
                "- supporting document regarding the accommodation at the hotel, the invitation from the host in which " +
                "the address of the guest's accommodation will be recorded;\n" +
                "- the confirmation regarding the seat reservation for an organized trip or any other appropriate document " +
                "indicating the plans of the trip to be undertaken;\n" +
                "- the return or circuit ticket;\n\n" +
                "d) for trips undertaken for political, scientific, cultural, sports purposes: invitations, entrance tickets, " +
                "programs in which, where possible, the name of the host organization and the duration of the visit should be entered\n\n" +

                "More details are established in:\n" +
                "- art.17 of Law 215/2011: https://www.legis.md/cautare/getResults?doc_id=139911&lang=ro#\n\n" +

                "/foreigners - regulations on foreign citizens\n" +
                "/visas - who is requiring a visa for RM\n" +
                "/acceptedDocs - accepted list of travel documents for RM\n" +
                "/calculator - calculates period of staying\n\n" +
                "/vehicles - border crossing rules for foreign vehicles\n" +
                "/main - back to main menu";

        String answerRo = "Pentru justificarea scopului intrării străinilor în RM, " +
                "urmează a fi prezentate următoarele documente:\n\n" +
                "a) pentru călătorii de afaceri:\n" +
                "- o invitaţie din partea unei întreprinderi, organizaţii sau a unei instituţii;\n" +
                "- documente ce confirmă un interes economic (de exemplu: procură, " +
                "contract de muncă, bilete  sau invitaţii de participare la expoziţii);\n\n" +
                "b) pentru călătorii în scop de studiu sau alte tipuri de instruire:\n" +
                "- un document ce atestă înscrierea la o instituţie de învăţămînt sau la alte forme de instruire;\n" +
                "- carnet de student sau certificat privind frecventarea cursului;\n\n" +
                "c) pentru călătorii turistice sau motive personale:\n" +
                "- document justificativ privind cazarea la hotel, invitaţia din partea gazdei " +
                "în care se va consemna adresa cazării invitatului;\n" +
                "- confirmarea privind rezervarea locului pentru o călătorie organizată sau orice alt document corespunzător indicînd planurile călătoriei ce va fi întreprinsă;\n" +
                "- biletul de întoarcere sau de circuit;\n\n" +
                "d) pentru călătoriile întreprinse în scopuri politice, ştiinţifice, culturale, " +
                "sportive: invitaţii, bilete de intrare, programe în care să fie înscris, " +
                "acolo unde este posibil, numele organizaţiei-gazdă şi durata vizitei\n\n" +

                "Mai multe detalii sunt stabilite în:\n" +
                "- art.17 al Legii 215/2011: https://www.legis.md/cautare/getResults?doc_id=139911&lang=ro#\n\n" +

                "/foreigners - regulile de trecere a străinilor\n" +
                "/visas - cine are nevoie de o viză pentru RM\n" +
                "/acceptedDocs - lista actelor de călătorie acceptate pentru RM\n" +
                "/calculator - calcularea termenului de ședere\n\n" +
                "/vehicles - regulile de trecere a vehiculelor din străinătate\n" +
                "/main - înapoi la meniul principal";

        String answerRu = "Для обоснования цели въезда в РМ иностранцев, необходимо представить следующие документы:\n\n" +
                " а) для деловых поездок:\n" +
                "- приглашение со стороны предприятия, организации или учреждения;\n" +
                "- документы, которые свидетельствуют об экономическом интересе (такие как доверенность, трудовой договор, " +
                "билеты или приглашения для участия в выставках);\n\n" +
                "b) для поездок, совершаемых в целях получения образования или иных видов обучения:\n" +
                "- документ о зачислении в учебное заведение или иное образовательное учреждение;\n" +
                "- студенческий билет или справка о посещаемом курсе;\n\n" +
                "c) для поездок, имеющих туристический или частный характер:\n" +
                "- подтвердительный документ относительно размещения в гостинице, приглашение со стороны принимающего лица, " +
                "в котором будет указан адрес размещения приглашенного;\n" +
                "- подтверждение бронирования места в случае организованной поездки или любой другой уместный документ, " +
                "указывающий программу планируемой поездки;\n" +
                "- обратный билет или билет на круговую поездку;\n\n" +
                "d) для поездок, предпринимаемых в политических, научных, культурных, спортивных целях, для участия в мероприятиях " +
                "религиозного характера либо по любым иным причинам, – приглашения, входные билеты, программы с указанием, " +
                "по возможности, наименования принимающей организации и срока пребывания\n\n" +

                "Больше деталей можно найти в:\n" +
                "- ст.17 Закона 215/2011: https://www.legis.md/cautare/getResults?doc_id=139911&lang=ru\n\n" +

                "/foreigners - правила пересечение для иностранцев\n" +
                "/visas - кому требуется виза для въезда в РМ\n" +
                "/acceptedDocs - список признаваемых проездных документов для РМ\n" +
                "/calculator - калькулятор сроков пребывания\n\n" +
                "/vehicles - правила пересечения для иностранных транспортных средств\n" +
                "/main - вернуться обратно в основное меню";

        chooseAnswerLanguage(chatId, answerRo, answerRu, answerEng);
    }

    private void validityCommandReceived(long chatId) {
        String answerEng = "The official Public Service Authority's service, that allows checking " +
                "online the status of the document in the database of the State Registry, " +
                "can be accessed via following link:\n" +
                "https://e-services.md/public/WebPublic/index.php?action=document&lang=md\n\n" +

                "/citizens - regulations on Republic of Moldova citizens\n" +
                "/foreigners - regulations on foreign citizens\n" +
                "/vehicles - border crossing rules for vehicles\n" +
                "/main - back to main menu";

        String answerRo = "Serviciul oficial al Agenției Servicii Publice care permite verificarea, în regim online, a statutului documentului " +
                "în baza de date a Registrului de stat, poate fi accesat la următorul link:\n" +
                "https://e-services.md/public/WebPublic/index.php?action=document&lang=md\n\n" +

                "/citizens - regulile de trecere a cetățenilor RM\n" +
                "/foreigners - regulile de trecere a străinilor\n" +
                "/vehicles - regulile de trecere a vehiculelor\n" +
                "/back - înapoi la meniul principal";

        String answerRu = "Официальный сервис Агентства государственных услуг, позволяющий проверить в режиме онлайн " +
                "статус документа в базе данных Госреестра, доступ к нему можно получить по следующей ссылке:\n" +
                "https://e-services.md/public/WebPublic/index.php?action=document&lang=md\n\n" +

                "/citizens - правила пересечения граждан РМ\n" +
                "/foreigners - правила пересечение для иностранцев\n" +
                "/vehicles - правила пересечения для транспортных средств\n" +
                "/main - вернуться в главное меню";

        chooseAnswerLanguage(chatId, answerRo, answerRu, answerEng);
    }

    private void visasCommandReceived(long chatId) {
        String answerEng = "List of foreigners who are required to have a visa for entry " +
                "into the Republic of Moldova is approved according to Annex no. 1 and 3 " +
                "to Law no.257/2013 and can be consulted by accessing the following link:" +
                "https://www.legis.md/cautare/getResults?doc_id=136362&lang=ro#\n\n" +

                "/citizens - regulations on Republic of Moldova citizens\n" +
                "/foreigners - regulations on foreign citizens\n" +
                "/vehicles - border crossing rules for vehicles\n" +
                "/main - back to main menu";

        String answerRo = "Lista străinilor care au obligația deținerii unei vize la intrarea, " +
                "ieșirea și tranzitarea teritoriului Republicii Moldova, este aprobată conform Anexei nr.1 și 3 " +
                "la Legea nr.257/2013 și poate fi consultată accesând următorul link: " +
                "https://www.legis.md/cautare/getResults?doc_id=136362&lang=ro#\n\n" +

                "/citizens - regulile de trecere a cetățenilor RM\n" +
                "/foreigners - regulile de trecere a străinilor\n" +
                "/vehicles - regulile de trecere a vehiculelor din RM\n" +
                "/main - înapoi la meniul principal";

        String answerRu = "Перечень иностранцев, обязанных иметь визу при въезде, выезде и " +
                "транзитном проезде по территории Республики Молдова утвержден Законом №257/2013 и " +
                "может быть проконсультирован по следующей ссылке:\n" +
                "https://www.legis.md/cautare/getResults?doc_id=136362&lang=ru\n\n" +

                "/citizens - правила пересечения граждан РМ\n" +
                "/foreigners - правила пересечение для иностранцев\n" +
                "/vehicles - правила пересечения для транспортных средств из РМ\n" +
                "/main - вернуться в главное меню";

        chooseAnswerLanguage(chatId, answerRo, answerRu, answerEng);
    }

    private void acceptedDocsCommandReceived(long chatId) {
        String answerEng = "List of travel documents that are accepted for the crossing of the border by foreigners" +
                "is approved according to Annex no. 1 " +
                "to Government Decision no.765/2014 and can be consulted by accessing the following link:" +
                " https://www.legis.md/cautare/getResults?doc_id=143523&lang=ro#\n\n" +

                "/citizens - regulations on Republic of Moldova citizens\n" +
                "/foreigners - regulations on foreign citizens\n" +
                "/vehicles - border crossing rules for vehicles\n" +
                "/main - back to main menu";

        String answerRo = "Lista documentelor de călătorie acceptate pentru traversarea " +
                "de către străini a frontierei de stat a Republicii Moldova, este aprobată " +
                "conform Anexei nr.1 la Hotărârea Guvernului nr.765/2014 și poate fi consultată accesând următorul link: " +
                " https://www.legis.md/cautare/getResults?doc_id=143523&lang=ro#\n\n" +

                "/citizens - regulile de trecere a cetățenilor RM\n" +
                "/foreigners - regulile de trecere a străinilor\n" +
                "/vehicles - regulile de trecere a vehiculelor\n" +
                "/main - înapoi la meniul principal";

        String answerRu = "Перечень проездных документов, необходимых иностранцам для пересечения " +
                "государственной границы Республики Молдова утвержден Постановлением Правительства №765/2014 и " +
                "может быть проконсультирован по следующей ссылке:\n" +
                " https://www.legis.md/cautare/getResults?doc_id=143523&lang=ro#\n\n" +

                "/citizens - правила пересечения граждан РМ\n" +
                "/foreigners - правила пересечение для иностранцев\n" +
                "/vehicles - правила пересечения для транспортных средств\n" +
                "/main - вернуться в главное меню";

        chooseAnswerLanguage(chatId, answerRo, answerRu, answerEng);
    }

    private void assuranceCommandReceived(long chatId) {
        String answerEng = "To purchase a green card, or an internal insurance for your vehicle, " +
                "as well as if necessary, life and health insurance for the trips abroad " +
                "you can use one of the insurance companies of the Republic of Moldova. " +
                "Here are some examples of links you can follow:\n" +
                "https://rapidasig.md/\n" +
                "https://omnis.md/\n\n" +

                "/citizens - regulations on Republic of Moldova citizens\n" +
                "/foreigners - regulations on foreign citizens\n" +
                "/vehicles - border crossing rules for vehicles\n" +
                "/main - back to main menu";

        String answerRo = "Pentru a achiziționa o carte verde sau o poliță de asigurare internă pentru un vehicul, " +
                "precum și, dacă este necesar, o asigurare de viață și de sănătate pentru călătoriile în străinătate " +
                "puteți folosi portalul companiilor de asigurări din Republica Moldova accesînd unul dintre linkuri:\n" +
                "https://rapidasig.md/\n" +
                "https://omnis.md/\n\n" +

                "/citizens - regulile de trecere a cetățenilor RM\n" +
                "/foreigners - regulile de trecere a străinilor\n" +
                "/vehicles - regulile de trecere a vehiculelor\n" +
                "/main - înapoi la meniul principal";

        String answerRu = "Для приобретения зеленой карты, либо внутреннего страхового полюса для вашего " +
                "транспортного средства, а также в случае необходимости страхования жизни и здоровья для заграничных поездок " +
                "вы можете воспользоваться порталом страховых компаний РМ пройдя по одной из ссылок:\n" +
                "https://rapidasig.md/ru\n" +
                "https://omnis.md/\n\n" +

                "/citizens - правила пересечения граждан РМ\n" +
                "/foreigners - правила пересечение для иностранцев\n" +
                "/vehicles - правила пересечения для транспортных средств\n" +
                "/main - вернуться в главное меню";

        chooseAnswerLanguage(chatId, answerRo, answerRu, answerEng);
    }

    private void vinietaCommandReceived(long chatId) {
        String answerEng = "To purchase a road toll (vignette) for Republic of Moldova " +
                "you can use the stat portal by accessing the following link:" +
                "https://evinieta.gov.md/\n\n" +

                "/citizens - regulations on Republic of Moldova citizens\n" +
                "/foreigners - regulations on foreign citizens\n" +
                "/vehicles - border crossing rules for vehicles\n" +
                "/main - back to main menu";

        String answerRo = "Pentru a achita taxa pentru folosirea drumurilor în Republica Moldova (vinieta) " +
                "puteți folosi portalul de stat accesînd următorul link:" +
                "https://evinieta.gov.md/\n\n" +

                "/citizens - regulile de trecere a cetățenilor RM\n" +
                "/foreigners - regulile de trecere a străinilor\n" +
                "/vehicles - regulile de trecere a vehiculelor\n" +
                "/main - înapoi la meniul principal";

        String answerRu = "Для оплаты виньетки за использование авто дорог Республики Молдова" +
                "вы можете воспользоваться государственным порталом пройдя по ссылке:\n" +
                "https://evinieta.gov.md/\n\n" +

                "/citizens - правила пересечения граждан РМ\n" +
                "/foreigners - правила пересечение для иностранцев\n" +
                "/vehicles - правила пересечения для транспортных средств\n" +
                "/main - вернуться в главное меню";

        chooseAnswerLanguage(chatId, answerRo, answerRu, answerEng);
    }

    private void defaultCommandReceived(long chatId) {
        String answerEng = "Sorry, the command is not implemented at that moment..\n" +
                "Please choose other proposed command!\n\n" +
                "Or white us an email on: \npolitia.frontiera@border.gov.md\n" +
                "about your opinion / proposal / necessity we should implement!\n" +
                "Thanks!!! Go on!!!\n\n" +

                "/main - back to main menu";

        String answerRo = "Cerem scuze comanda nu este implementată la moment..\n" +
                "Vă rog alegeți altă comandă propusă\n\n" +
                "Sau scrieți un mesaj pe adresa electronică: \npolitia.frontiera@border.gov.md\n" +
                "despre opinia / propunerea / necesarul care noi trebuie să implementăm!\n" +
                "Mulțumesc!!! Continuați!!!\n\n" +

                "/main - înapoi la meniul principal";

        String answerRu = "Простите, данная команда не реализована на данный момент..\n" +
                "Пожалуйста, выберете другую предложенную команду!\n" +
                "Или напишите письмо на наш email: \n politia.frontiera@border.gov.md \n" +
                "о вашем мнении / предложении / необходимости, которую мы должны реализовать!\n" +
                "Спасибо!!! Продолжайте!!!\n\n" +

                "/main - вернуться в главное меню ";

        chooseAnswerLanguage(chatId, answerRo, answerRu, answerEng);
    }

    private void crossingsCommandReceived(long chatId) {
        String answerEng = " To obtain an extract from the Integrated Information System of the Border Police regarding the crossing of the state border by the individual or transport unit " +
                "you can use the state portal by accessing the following link:" +
                " https://servicii.gov.md/ro/service/021001121\n\n" +

                "/citizens - regulations on Republic of Moldova citizens\n" +
                "/foreigners - regulations on foreign citizens\n" +
                "/vehicles - border crossing rules for vehicles\n" +
                "/main - back to main menu";

        String answerRo = "Pentru a obține extras din sistemul informațional integrat al Poliției de Frontieră cu privire la traversarea frontierei de stat de către persoana fizică sau unitatea de transport " +
                "puteți folosi portalul de stat accesînd următorul link:" +
                " https://servicii.gov.md/ro/service/021001121\n\n" +

                "/citizens - regulile de trecere a cetățenilor RM\n" +
                "/foreigners - regulile de trecere a străinilor\n" +
                "/vehicles - regulile de trecere a vehiculelor\n" +
                "/main - înapoi la meniul principal";

        String answerRu = "Для получения информации из Интегрированной системы Пограничной Полиции о пересечении государственной границы Республики Молдова лицом или транспортным средством " +
                "вы можете воспользоваться государственным порталом пройдя по ссылке:\n" +
                " https://servicii.gov.md/ru/service/021001121\n\n" +

                "/citizens - правила пересечения граждан РМ\n" +
                "/foreigners - правила пересечение для иностранцев\n" +
                "/vehicles - правила пересечения для транспортных средств\n" +
                "/main - вернуться в главное меню";

        chooseAnswerLanguage(chatId, answerRo, answerRu, answerEng);
    }

    private void permisCommandReceived(long chatId) {
        String answerEng = " To obtain the electronic permit of access to the border area or a state border crossing point " +
                "you can use the official Border Police portal by accessing the following link:" +
                " https://epermis.border.gov.md/en\n\n" +

                "/citizens - regulations on Republic of Moldova citizens\n" +
                "/foreigners - regulations on foreign citizens\n" +
                "/vehicles - border crossing rules for vehicles\n" +
                "/main - back to main menu";

        String answerRo = "Pentru a obține permisul de aflare în zonă de frontieră sau accesul temporar în punctul de trecere " +
                "puteți folosi portalul de stat accesînd următorul link:" +
                " https://epermis.border.gov.md/ \n\n" +

                "/citizens - regulile de trecere a cetățenilor RM\n" +
                "/foreigners - regulile de trecere a străinilor\n" +
                "/vehicles - regulile de trecere a vehiculelor\n" +
                "/main - înapoi la meniul principal";

        String answerRu = "Для получения разрешения на доступ в приграничную зону либо на территорию пункта пропуска через государственную границу " +
                "вы можете воспользоваться официальным порталом Пограничной Полиции пройдя по ссылке:\n" +
                " https://epermis.border.gov.md/ru \n\n" +

                "/citizens - правила пересечения граждан РМ\n" +
                "/foreigners - правила пересечение для иностранцев\n" +
                "/vehicles - правила пересечения для транспортных средств\n" +
                "/main - вернуться в главное меню";

        chooseAnswerLanguage(chatId, answerRo, answerRu, answerEng);
    }

    private void chooseAnswerLanguage(long chatId, String answerRo, String answerRu, String answerEng){
        if (flag == 2) {
            sendMessage(chatId, answerRo);
        } else if (flag == 3) {
            sendMessage(chatId, answerRu);
        } else {
            sendMessage(chatId, answerEng);
        }
    }
}