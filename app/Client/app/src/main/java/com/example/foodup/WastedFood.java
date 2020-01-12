package com.example.foodup;
public class WastedFood{
    private int id;
    private int wFood;

    public void setId(int id){
        this.id = id;
    }

    public void setWastedFood(int wFood){
        this.wFood = wFood;
    }

    public int getId(){
        return id;
    }

    public int getWastedFood(){
        return wFood; 
    }
}