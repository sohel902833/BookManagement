package com.sohel.bookmanagement.Model;

public class Users {

    private String Image;
    private String name;
    private String email;
    private String passwrord;

    public Users()
    {

    }

    public Users(String image, String name, String email, String passwrord) {
        Image = image;
        this.name = name;
        this.email = email;
        this.passwrord = passwrord;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
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
