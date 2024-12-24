package com.example.myapplicationfirst;

public class Product {
    private int id;
    private String name;
    private double price;
    private String description;
    private String image_url;


    public Product(int id, String name, double price, String image_url, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.image_url = image_url;
        this.description = description;
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }
    public String getDescription() { return description; }

    public String getImage_url() {
        return image_url;
    }
}