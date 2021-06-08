package com.sohel.bookmanagement.Model;

public class Agent {
    String uID,name,phone,password,profileImage;
    int coin;


    public Agent() {
    }

    public Agent(String uID, String name, String phone, String password, String profileImage, int coin) {
        this.uID = uID;
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.profileImage = profileImage;
        this.coin = coin;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }
}
