package com.smartcampus.backend.service.weather;

import java.util.Map;

public class WeatherCodeResolver {

    public record WeatherCodeInfo(String description, String icon) {}

    private record CodeEntry(WeatherCodeInfo day, WeatherCodeInfo night) {}

    private static final String ICON_BASE = "http://openweathermap.org/img/wn/";

    private static CodeEntry entry(String dayDesc, String dayIcon, String nightDesc, String nightIcon) {
        return new CodeEntry(
                new WeatherCodeInfo(dayDesc, ICON_BASE + dayIcon + "@2x.png"),
                new WeatherCodeInfo(nightDesc, ICON_BASE + nightIcon + "@2x.png")
        );
    }

    // Table de correspondance WMO weathercode -> description/icone (jour/nuit)
    private static final Map<Integer, CodeEntry> CODES = Map.ofEntries(
            Map.entry(0,  entry("Ensoleillé", "01d", "Dégagé", "01n")),
            Map.entry(1,  entry("Principalement ensoleillé", "01d", "Principalement dégagé", "01n")),
            Map.entry(2,  entry("Partiellement nuageux", "02d", "Partiellement nuageux", "02n")),
            Map.entry(3,  entry("Nuageux", "03d", "Nuageux", "03n")),
            Map.entry(45, entry("Brumeux", "50d", "Brumeux", "50n")),
            Map.entry(48, entry("Brouillard givrant", "50d", "Brouillard givrant", "50n")),
            Map.entry(51, entry("Légère bruine", "09d", "Légère bruine", "09n")),
            Map.entry(53, entry("Bruine", "09d", "Bruine", "09n")),
            Map.entry(55, entry("Forte bruine", "09d", "Forte bruine", "09n")),
            Map.entry(56, entry("Légère bruine verglaçante", "09d", "Légère bruine verglaçante", "09n")),
            Map.entry(57, entry("Bruine verglaçante", "09d", "Bruine verglaçante", "09n")),
            Map.entry(61, entry("Légère pluie", "10d", "Légère pluie", "10n")),
            Map.entry(63, entry("Pluie", "10d", "Pluie", "10n")),
            Map.entry(65, entry("Forte pluie", "10d", "Forte pluie", "10n")),
            Map.entry(66, entry("Légère pluie verglaçante", "10d", "Légère pluie verglaçante", "10n")),
            Map.entry(67, entry("Pluie verglaçante", "10d", "Pluie verglaçante", "10n")),
            Map.entry(71, entry("Légère neige", "13d", "Légère neige", "13n")),
            Map.entry(73, entry("Neige", "13d", "Neige", "13n")),
            Map.entry(75, entry("Fortes chutes de neige", "13d", "Fortes chutes de neige", "13n")),
            Map.entry(77, entry("Grains de neige", "13d", "Grains de neige", "13n")),
            Map.entry(80, entry("Légères averses", "09d", "Légères averses", "09n")),
            Map.entry(81, entry("Averses", "09d", "Averses", "09n")),
            Map.entry(82, entry("Fortes averses", "09d", "Fortes averses", "09n")),
            Map.entry(85, entry("Légères averses de neige", "13d", "Légères averses de neige", "13n")),
            Map.entry(86, entry("Averses de neige", "13d", "Averses de neige", "13n")),
            Map.entry(95, entry("Orage", "11d", "Orage", "11n")),
            Map.entry(96, entry("Orages légers avec grêle", "11d", "Orages légers avec grêle", "11n")),
            Map.entry(99, entry("Orage avec grêle", "11d", "Orage avec grêle", "11n"))
    );

    private static final WeatherCodeInfo UNKNOWN_DAY = new WeatherCodeInfo("Inconnu", ICON_BASE + "01d@2x.png");
    private static final WeatherCodeInfo UNKNOWN_NIGHT = new WeatherCodeInfo("Inconnu", ICON_BASE + "01n@2x.png");

    // isDay = true -> variante jour, false -> variante nuit
    public static WeatherCodeInfo resolve(Integer code, boolean isDay) {
        if (code == null) return isDay ? UNKNOWN_DAY : UNKNOWN_NIGHT;
        CodeEntry codeEntry = CODES.get(code);
        if (codeEntry == null) return isDay ? UNKNOWN_DAY : UNKNOWN_NIGHT;
        return isDay ? codeEntry.day() : codeEntry.night();
    }
}