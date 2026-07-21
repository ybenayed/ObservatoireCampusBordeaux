package com.smartcampus.backend.dto.search;

/**
 * Corps de la requête POST /api/itinerary : les 2 points choisis côté Android
 * (origine + destination), chacun au même format que SearchResultDTO.
 */
public class ItineraryRequestDTO {

    private SearchResultDTO origin;
    private SearchResultDTO destination;

    public ItineraryRequestDTO() {
    }

    public ItineraryRequestDTO(SearchResultDTO origin, SearchResultDTO destination) {
        this.origin = origin;
        this.destination = destination;
    }

    public SearchResultDTO getOrigin() {
        return origin;
    }

    public void setOrigin(SearchResultDTO origin) {
        this.origin = origin;
    }

    public SearchResultDTO getDestination() {
        return destination;
    }

    public void setDestination(SearchResultDTO destination) {
        this.destination = destination;
    }
}