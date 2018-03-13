package com.example.hp.stickpick.bean;

import android.support.annotation.NonNull;

import java.util.Comparator;

/**
 * Created by Shadab Azam Farooqui on 9/16/2017.
 */

public class ListBean implements Comparable {

    private String name,email,mobile,password,image,course,semester;

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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        ListBean b=(ListBean)o;
        if (this.getName().compareTo(b.getName())>0){
            return 10;
        }
        else if (this.getName().compareTo(b.getName())<0){
            return -10;
        }
        else {
            return 0;
        }
    }
}
