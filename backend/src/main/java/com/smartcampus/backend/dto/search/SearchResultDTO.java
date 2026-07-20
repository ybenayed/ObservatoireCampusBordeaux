package com.smartcampus.backend.dto.search;

/**
 * Représente un résultat de recherche de lieu (géocodage).
 * Miroir exact de SearchResultDto.kt côté Android :
 * - name      : titre court (ex: "Kedge Business School")
 * - subtitle  : complément d'adresse (ex: "Talence, Gironde")
 * - latitude / longitude : coordonnées pour positionner le marqueur
 */
public class SearchResultDTO {

    private String name;
    private String subtitle;
    private double latitude;
    private double longitude;

    public SearchResultDTO() {
    }

    public SearchResultDTO(String name, String subtitle, double latitude, double longitude) {
        this.name = name;
        this.subtitle = subtitle;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}