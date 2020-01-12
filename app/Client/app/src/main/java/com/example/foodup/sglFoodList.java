package com.example.foodup;

import java.util.ArrayList;

public class sglFoodList {

    private int id;
    private String name;
    private int posts;
    private ArrayList<Product> productList = new ArrayList<Product>();

    public void setName(String set_name){
        this.name = set_name;
    }

    public String getName (){
        return this.name;
    }

    public void setID(Integer set_id){
        this.id = set_id;
    }

    public Integer getID(){
        return this.id;
    }

    public void setPosts(Integer posts){
        this.posts = posts;
    }

    public Integer getPosts(){
        return this.posts;
    }

    public void setProductList(ArrayList<Product> list){
        this.productList = list;
    }

    public ArrayList<Product> getProductList(){
        return productList;
    }

}
