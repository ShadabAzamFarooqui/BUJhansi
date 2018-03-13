package com.example.hp.stickpick.bean;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by HP on 30-05-2016.
 */
public class UserBean implements Serializable {

    private Bitmap image;
    private String name;
    private String email;
    private String mobile;
    private String myImage;
    private String password;
    private int logo;
    private String nameNearestPlace;
    private String addressNearestPlace;
    private String openStatus;
    private String course;
    private String semester;
    private int notificationCount=0;
    private int notificationCountForSingleEvent=0;


    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String sememster) {
        this.semester = sememster;
    }



    public UserBean() {
    }

    public String getMyImage() {
        return myImage;
    }

    public void setMyImage(String myImage) {
        this.myImage = myImage;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public int getLogo() {
        return logo;
    }

    public void setLogo(int logo) {
        this.logo = logo;
    }

    public String getNameNearestPlace() {
        return nameNearestPlace;
    }

    public void setNameNearestPlace(String nameNearestPlace) {
        this.nameNearestPlace = nameNearestPlace;
    }

    public String getAddressNearestPlace() {
        return addressNearestPlace;
    }

    public void setAddressNearestPlace(String addressNearestPlace) {
        this.addressNearestPlace = addressNearestPlace;
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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOpenStatus() {
        return openStatus;
    }

    public void setOpenStatus(String openStatus) {
        this.openStatus = openStatus;
    }

    public int getNotificationCount() {
        return notificationCount;
    }

    public void setNotificationCount(int notificationCount) {
        this.notificationCount = notificationCount;
    }

    public int getNotificationCountForSingleEvent() {
        return notificationCountForSingleEvent;
    }

    public void setNotificationCountForSingleEvent(int notificationCountForSingleEvent) {
        this.notificationCountForSingleEvent = notificationCountForSingleEvent;
    }
}
