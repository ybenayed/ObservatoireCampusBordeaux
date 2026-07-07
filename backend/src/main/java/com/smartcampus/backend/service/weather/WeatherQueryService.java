package com.smartcampus.backend.service.weather;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcampus.backend.dto.weather.WeatherAtDTO;
import com.smartcampus.backend.dto.weather.WeatherRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
public class WeatherQueryService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String WEATHER_URL = "https://api.open-meteo.com/v1/forecast";

    // Meme fenetre que le cache campus : 7 jours passes + aujourd'hui + 7 jours a venir
    private static final int PAST_DAYS = 7;
    private static final int FORECAST_DAYS = 8;

    public WeatherQueryService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public WeatherAtDTO getWeatherAt(WeatherRequestDTO request) {
        if (request.getLatitude() == null || request.getLongitude() == null || request.getDate() == null) {
            throw new IllegalArgumentException("latitude, longitude et date sont obligatoires");
        }

        LocalDate today = LocalDate.now();
        LocalDate requestedDate = request.getDate();
        long diffDays = ChronoUnit.DAYS.between(today, requestedDate);

        if (diffDays < -PAST_DAYS || diffDays > FORECAST_DAYS - 1) {
            throw new IllegalArgumentException(
                    "La date doit etre comprise entre " + today.minusDays(PAST_DAYS)
                            + " et " + today.plusDays(FORECAST_DAYS - 1));
        }

        try {
            String url = WEATHER_URL +
                    "?latitude=" + request.getLatitude() + "&longitude=" + request.getLongitude() +
                    "&hourly=temperature_2m,weathercode,precipitation_probability,is_day" +
                    "&daily=temperature_2m_max,temperature_2m_min,weathercode,precipitation_sum,windspeed_10m_max" +
                    "&timezone=auto&forecast_days=" + FORECAST_DAYS + "&past_days=" + PAST_DAYS;

            String rawJson = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(rawJson);

            return buildWeatherAt(root, request);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur lors de la recuperation meteo pour lat={}, lon={}, date={}",
                    request.getLatitude(), request.getLongitude(), request.getDate(), e);
            return null;
        }
    }

    private WeatherAtDTO buildWeatherAt(JsonNode root, WeatherRequestDTO request) {
        LocalDate requestedDate = request.getDate();
        LocalTime requestedTime = request.getTime();

        // ─── Recherche du jour demande dans le tableau "daily"
        JsonNode daily = root.path("daily");
        JsonNode dailyTime = daily.path("time");
        int dayIndex = -1;
        for (int i = 0; i < dailyTime.size(); i++) {
            if (LocalDate.parse(dailyTime.get(i).asText()).equals(requestedDate)) {
                dayIndex = i;
                break;
            }
        }
        if (dayIndex == -1) {
            throw new IllegalArgumentException("Aucune donnee meteo disponible pour la date " + requestedDate);
        }

        Integer dayCode = daily.path("weathercode").get(dayIndex).asInt();
        WeatherCodeResolver.WeatherCodeInfo dayInfo = WeatherCodeResolver.resolve(dayCode, true);

        WeatherAtDTO.WeatherAtDTOBuilder builder = WeatherAtDTO.builder()
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .date(requestedDate)
                .time(requestedTime)
                .temperatureMax(daily.path("temperature_2m_max").get(dayIndex).asDouble())
                .temperatureMin(daily.path("temperature_2m_min").get(dayIndex).asDouble())
                .precipitationSum(daily.path("precipitation_sum").get(dayIndex).asDouble())
                .windspeedMax(daily.path("windspeed_10m_max").get(dayIndex).asDouble())
                .weathercode(dayCode)
                .description(dayInfo.description())
                .icon(dayInfo.icon());

        // ─── Si une heure est fournie, on affine avec le point horaire correspondant
        if (requestedTime != null) {
            JsonNode hourly = root.path("hourly");
            JsonNode hourlyTime = hourly.path("time");
            int hourIndex = -1;
            for (int i = 0; i < hourlyTime.size(); i++) {
                String iso = hourlyTime.get(i).asText(); // ex: 2026-07-07T14:00
                LocalDate d = LocalDate.parse(iso.substring(0, 10));
                int hour = Integer.parseInt(iso.substring(11, 13));
                if (d.equals(requestedDate) && hour == requestedTime.getHour()) {
                    hourIndex = i;
                    break;
                }
            }

            if (hourIndex != -1) {
                Integer hourCode = hourly.path("weathercode").get(hourIndex).asInt();
                boolean isDay = hourly.path("is_day").has(hourIndex) && hourly.path("is_day").get(hourIndex).asInt(1) == 1;
                WeatherCodeResolver.WeatherCodeInfo hourInfo = WeatherCodeResolver.resolve(hourCode, isDay);

                builder.temperature(hourly.path("temperature_2m").get(hourIndex).asDouble())
                        .precipitationProbability(
                                hourly.path("precipitation_probability").has(hourIndex)
                                        ? hourly.path("precipitation_probability").get(hourIndex).asInt()
                                        : null)
                        .weathercode(hourCode)
                        .description(hourInfo.description())
                        .icon(hourInfo.icon());
            } else {
                log.warn("Heure {} non trouvee pour la date {}, retour du resume journalier uniquement",
                        requestedTime, requestedDate);
            }
        }

        return builder.build();
    }
}