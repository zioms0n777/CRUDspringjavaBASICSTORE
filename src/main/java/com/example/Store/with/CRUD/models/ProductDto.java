package com.example.Store.with.CRUD.models;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public class ProductDto {

    @NotEmpty(message = "Wpisz nazwe")
    private String name;

    @NotEmpty(message = "Wpisz marke")
    private String brand;

    @NotEmpty(message = "Wpisz nazwe kategorii")
    private String category;

    @Min(0)
    private double price;

    @Size(min = 10, message = "Opis powinien miec przynajmniej 10 słów")
    @Size(max = 500, message = "Opis nie powinien być dłuższy niż 500 słów.")
    private String description;

    private MultipartFile image;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }
}
