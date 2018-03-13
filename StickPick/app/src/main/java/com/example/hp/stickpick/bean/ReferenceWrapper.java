package com.example.hp.stickpick.bean;

import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by HP on 31-05-2016.
 */
public class ReferenceWrapper {


    public static ReferenceWrapper referenceWrapper;
    public String device_name = "";
    public String timeZone;
    public String locale;
    private CharSequence currency = "\u20B9";
    private UserBean userBean;

    private Typeface typeface_light, rupeeTypeFace, typeface_heavy;


    private Context context;


    public static ReferenceWrapper getRefrenceWrapper(Context context) {
        if (referenceWrapper == null) {
            referenceWrapper = new ReferenceWrapper(context);
        }
        return referenceWrapper;
    }

    private ReferenceWrapper(Context activity) {
        context = activity;

    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void hideKeyboard(EditText editText, Context activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && editText != null)
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }

    public UserBean getUserBean() {

        return this.userBean;

    }

}
