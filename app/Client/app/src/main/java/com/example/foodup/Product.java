package com.example.foodup;

public class Product {

    private int id;
    private String name;
    private String brand;
    private int amount;
    private String imagePath;

    public void setID(int id){
        this.id = id;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setBrand(String brand){
        this.brand = brand;
    }

    public void setAmount(int amount){
        this.amount = amount;
    }

    public void setImagePath(String imagePath){
        this.imagePath = imagePath;
    }

    public int getID(){
        return id;
    }

    public String getName(){
        return name;
    }

    public String getBrand(){
        return brand;
    }

    public int getAmount(){
        return amount;
    }

    public String getImagePath(){
        return imagePath;
    }

}
