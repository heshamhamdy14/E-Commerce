package com.example.e_commerce.Model;

public class Users {
    String name ;
    String phone ;
    String password;
    String address;
    String image;
    public Users(){}
    public Users(String username , String phone, String password , String address ,String image){
        this.name=username;
        this.phone=phone;
        this.password=password;
        this.address=address;
        this.image=image;
    }

    public String getname() {
        return name;
    }

    public void setname(String username) {
        this.name = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
