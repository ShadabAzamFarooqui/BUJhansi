package com.example.hp.stickpick.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by pc on 10/7/2016.
 */
public class Preferences {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "Bahikhata";
    private static Preferences instance;
    private static final String IS_LOGIN = "Login";
    private static final String name="name";
    private static final String mobile="mobile";
    private static final String email="email";


    private Preferences(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    public static Preferences getInstance(Context context) {
        if (instance == null) {
            instance = new Preferences(context);
        }
        return instance;
    }

    public void setLogin(Boolean isLogin) {
        editor.putBoolean(IS_LOGIN, isLogin);
        editor.commit();
    }

    public Boolean getLogin() {
        return pref.getBoolean(IS_LOGIN, false);
    }


    public void setName(String userName) {
        editor.putString(name, userName);
        editor.commit();
    }
    public String getName() {
        return pref.getString(name, "");
    }

    public void setMobile(String userMobile) {
        editor.putString(mobile, userMobile);
        editor.commit();
    }
    public String getMobile() {
        return pref.getString(mobile, "");
    }


    public void setEmail(String userEmail) {
        editor.putString(email, userEmail);
        editor.commit();
    }
    public String getEmail() {
        return pref.getString(email, "");
    }

}
