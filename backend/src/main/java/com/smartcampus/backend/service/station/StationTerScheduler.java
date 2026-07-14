package com.smartcampus.backend.service.station;

import com.smartcampus.backend.service.station.PassageTerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Prechauffe le cache de PassageTerService pour un nombre RESTREINT de gares
 * "surveillees" (ex: les gares affichees par defaut sur la carte du campus).
 *
 * Pourquoi ne pas boucler sur toutes les gares importees (26+) ?
 * -> Quota SNCF = 5000 requetes/mois. Avec 26 gares rafraichies toutes les
 *    5 minutes : 26 * 12/h * 24h * 30j = 224 640 requetes/mois. Bien trop.
 * Avec 5 gares toutes les 5 minutes : 5 * 12 * 24 * 30 = 43 200. Encore trop.
 * Avec 5 gares toutes les 30 minutes : 5 * 2 * 24 * 30 = 7 200. Toujours trop.
 * Avec 3 gares toutes les 30 minutes : 3 * 2 * 24 * 30 = 4 320. OK, sous 5000.
 *
 * Ajuste navitia.watched-stations et navitia.refresh-interval-ms dans
 * application.properties en fonction de tes besoins reels. Pour les gares
 * NON surveillees, le cache a la demande (TTL) dans PassageTerService suffit :
 * elles ne consomment du quota que quand un utilisateur les consulte vraiment.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StationTerScheduler {

    private final PassageTerService passageTerService;

    @Value("#{'${navitia.watched-stations:}'.split(',')}")
    private List<String> watchedStations;

    @Scheduled(fixedDelayString = "${navitia.refresh-interval-ms:1800000}") // defaut 30 min
    public void refreshWatchedStations() {
        for (String stopId : watchedStations) {
            String trimmed = stopId.trim();
            if (trimmed.isEmpty()) continue;

            try {
                passageTerService.refreshCache(trimmed);
            } catch (Exception e) {
                log.warn("Echec rafraichissement scheduler pour {} : {}", trimmed, e.getMessage());
            }
        }
    }
}