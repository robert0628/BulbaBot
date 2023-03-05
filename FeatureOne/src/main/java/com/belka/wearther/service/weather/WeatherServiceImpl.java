package com.belka.wearther.service.weather;

import com.belka.wearther.models.weather.WeatherNow;
import com.belka.wearther.service.geo.GeoFromIPService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@Data
public class WeatherServiceImpl implements WeatherService {
    private static final String ERROR_TEXT = "Error occurred: ";
    @Value("${weather.key}")
    private String key;
    @Value("${weather.link}")
    private String link;
    private final RestTemplate restTemplate;
    private final GeoFromIPService geoFromIPService;

    @Autowired
    public WeatherServiceImpl(RestTemplate restTemplate, GeoFromIPService geoFromIPService) {
        this.restTemplate = restTemplate;
        this.geoFromIPService = geoFromIPService;
    }

    public String getWeather(String city) {
        WeatherNow weatherNow = restTemplate.getForObject(getWeatherLink(city), WeatherNow.class);
        if (weatherNow == null || weatherNow.getMain() == null) {
            throw new RuntimeException("couldn't get weather data from remote server");
        }

        String text = "temp is " + weatherNow.getMain().getTemp() + ", filling like " + weatherNow.getMain().getFeelsLike();
        if (weatherNow.getMain().getTemp() < 0) {
            return "⛄" + text + "⛄";
        }
        return text;

    }

    private String getWeatherLink(String city) {
        return String.format(link, city);
    }

    public String findCity() {
        return geoFromIPService.getCityName();
    }

   /* public void chooseCity(Long chatId) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("do you need more information?")
                .build();
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        InlineKeyboardButton yesButton = new InlineKeyboardButton();
        yesButton.setText("give me a forecast for 10 days");
        yesButton.setCallbackData(YES_BUTTON);

        InlineKeyboardButton noButton = new InlineKeyboardButton();
        noButton.setText("No");
        noButton.setCallbackData(NO_BUTTON);

        rowInline.add(yesButton);
        rowInline.add(noButton);
        rowsInLine.add(rowInline);
        markupInLine.setKeyboard(rowsInLine);

        message.setReplyMarkup(markupInLine);

        executeMessage(message);
    }

    private void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }*/

}
