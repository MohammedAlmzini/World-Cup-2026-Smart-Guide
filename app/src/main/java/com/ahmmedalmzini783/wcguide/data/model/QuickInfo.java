package com.ahmmedalmzini783.wcguide.data.model;

import java.util.List;
import java.util.Objects;

public class QuickInfo {
    private String countryCode; // US, CA, MX
    private String currency; // USD, CAD, MXN
    private List<String> languages; // ["English"], ["English", "French"], ["Spanish"]
    private String transportTips; // Metro, Uber, buses
    private String weatherTip; // Summer: warm, Check rain, Sunblock

    public QuickInfo() {
        // Default constructor required for Firebase
    }

    public QuickInfo(String countryCode, String currency, List<String> languages,
                     String transportTips, String weatherTip) {
        this.countryCode = countryCode;
        this.currency = currency;
        this.languages = languages;
        this.transportTips = transportTips;
        this.weatherTip = weatherTip;
    }

    // Getters and Setters
    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public List<String> getLanguages() { return languages; }
    public void setLanguages(List<String> languages) { this.languages = languages; }

    public String getTransportTips() { return transportTips; }
    public void setTransportTips(String transportTips) { this.transportTips = transportTips; }

    public String getWeatherTip() { return weatherTip; }
    public void setWeatherTip(String weatherTip) { this.weatherTip = weatherTip; }

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
                ", currency='" + currency + '\'' +
                '}';
    }
}