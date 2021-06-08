package com.sohel.bookmanagement.Model;

import com.sohel.bookmanagement.Model.ImageModel;
import com.sohel.bookmanagement.Model.TimeDateModel;

import java.util.ArrayList;

public class BookModel {
    String bookName,currentId;
    String bookId;
    ArrayList<ImageModel> imageList;
    TimeDateModel time;

    public BookModel(){

    }


    public BookModel(String bookName, String bookId, ArrayList<ImageModel> imageList, TimeDateModel time,String currentId) {
        this.bookName = bookName;
        this.bookId = bookId;
        this.imageList = imageList;
        this.time = time;
        this.currentId=currentId;
    }
    public BookModel(String currentId,String bookName) {
        this.currentId=currentId;
        this.bookName = bookName;
    }

    public String getCurrentId() {
        return currentId;
    }

    public void setCurrentId(String currentId) {
        this.currentId = currentId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public ArrayList<ImageModel> getImageList() {
        return imageList;
    }

    public void setImageList(ArrayList<ImageModel> imageList) {
        this.imageList = imageList;
    }

    public TimeDateModel getTime() {
        return time;
    }

    public void setTime(TimeDateModel time) {
        this.time = time;
    }
}
