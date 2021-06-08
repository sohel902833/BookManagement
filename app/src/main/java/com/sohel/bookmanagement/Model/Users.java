package com.sohel.bookmanagement.Model;

public class Users {

    private String uid;
    private String image;
    private String name;
    private String email;
    private String passwrord;
    private String phone;

    public Users()
    {

    }

    public Users(String uid,String image, String name, String email,String phone, String passwrord) {
        this.uid=uid;
        this.image = image;
        this.name = name;
        this.email = email;
        this.phone=phone;
        this.passwrord = passwrord;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswrord() {
        return passwrord;
    }

    public void setPasswrord(String passwrord) {
        this.passwrord = passwrord;
    }
}
