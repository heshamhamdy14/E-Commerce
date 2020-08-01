package com.example.e_commerce.Model;

public class Cart {
    String pName , price , quantity , pid ;
    public Cart(){}

    public Cart(String pName, String price, String quantity, String pid) {
        this.pName = pName;
        this.price = price;
        this.quantity = quantity;
        this.pid = pid;
    }

    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }
}
