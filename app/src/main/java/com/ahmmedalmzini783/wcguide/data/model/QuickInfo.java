package com.ahmmedalmzini783.wcguide.data.model;

import java.util.List;
import java.util.Objects;

public class QuickInfo {
    private String countryCode; // US, CA, MX
    private String countryName; // United States, Canada, Mexico
    private String currency; // USD, CAD, MXN
    private String languages; // English, English/French, Spanish
    private String transport; // Metro, Uber, buses
    private String weather; // Summer: warm, Check rain, Sunblock

    public QuickInfo() {
        // Default constructor required for Firebase
    }

    public QuickInfo(String countryCode, String countryName, String currency, String languages,
                     String transport, String weather) {
        this.countryCode = countryCode;
        this.countryName = countryName;
        this.currency = currency;
        this.languages = languages;
        this.transport = transport;
        this.weather = weather;
    }

    // Getters and Setters
    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public String getCountryName() { return countryName; }
    public void setCountryName(String countryName) { this.countryName = countryName; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getLanguages() { return languages; }
    public void setLanguages(String languages) { this.languages = languages; }

    public String getTransport() { return transport; }
    public void setTransport(String transport) { this.transport = transport; }

    public String getWeather() { return weather; }
    public void setWeather(String weather) { this.weather = weather; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuickInfo quickInfo = (QuickInfo) o;
        return Objects.equals(countryCode, quickInfo.countryCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(countryCode);
    }

    @Override
    public String toString() {
        return "QuickInfo{" +
                "countryCode='" + countryCode + '\'' +
                ", countryName='" + countryName + '\'' +
                ", currency='" + currency + '\'' +
                ", languages='" + languages + '\'' +
                ", transport='" + transport + '\'' +
                ", weather='" + weather + '\'' +
                '}';
    }
}