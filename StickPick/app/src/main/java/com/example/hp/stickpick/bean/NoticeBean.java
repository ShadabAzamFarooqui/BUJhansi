package com.example.hp.stickpick.bean;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by HP on 02-06-2016.
 */
public class NoticeBean implements Serializable, Comparable {

    private String noticeText;
    private String dateTime;
    private String date;
    private String user;
    private String image;
    private String mobile;


    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTime() {

        return dateTime;
    }

    public void setTime(String time) {
        this.dateTime = time;
    }

    public String getNoticeText() {
        return noticeText;
    }

    public void setNoticeText(String noticeText) {
        this.noticeText = noticeText;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        NoticeBean b=(NoticeBean)o;

        if (this.getTime().compareTo(b.getTime())>0){
            return 10;
        }
        else if (this.getTime().compareTo(b.getTime())<0){
            return -10;
        }
        else {
            return 0;
        }

    }
}
