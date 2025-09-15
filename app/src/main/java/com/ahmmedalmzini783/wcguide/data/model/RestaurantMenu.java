package com.ahmmedalmzini783.wcguide.data.model;

import java.util.Objects;

public class RestaurantMenu {
    private String itemName;
    private String description;
    private double price;
    private String category; // مقبلات، أطباق رئيسية، حلويات، مشروبات
    private String imageUrl;
    private boolean isAvailable;
    private boolean isSpecial; // طبق مميز
    private String allergens; // مسببات الحساسية

    public RestaurantMenu() {
        // Default constructor required for Firebase
    }

    public RestaurantMenu(String itemName, String description, double price, String category, 
                         String imageUrl, boolean isAvailable, boolean isSpecial, String allergens) {
        this.itemName = itemName;
        this.description = description;
        this.price = price;
        this.category = category;
        this.imageUrl = imageUrl;
        this.isAvailable = isAvailable;
        this.isSpecial = isSpecial;
        this.allergens = allergens;
    }

    // Getters and Setters
    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public boolean isSpecial() {
        return isSpecial;
    }

    public void setSpecial(boolean special) {
        isSpecial = special;
    }

    public String getAllergens() {
        return allergens;
    }

    public void setAllergens(String allergens) {
        this.allergens = allergens;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestaurantMenu that = (RestaurantMenu) o;
        return Objects.equals(itemName, that.itemName) && 
               Objects.equals(category, that.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemName, category);
    }

    @Override
    public String toString() {
        return "RestaurantMenu{" +
                "itemName='" + itemName + '\'' +
                ", category='" + category + '\'' +
                ", price=" + price +
                ", isAvailable=" + isAvailable +
                '}';
    }
}
