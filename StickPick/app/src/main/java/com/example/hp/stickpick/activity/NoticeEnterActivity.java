package com.example.hp.stickpick.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hp.stickpick.R;
import com.example.hp.stickpick.utils.Networking;
import com.example.hp.stickpick.bean.NoticeBean;
import com.example.hp.stickpick.utils.ParameterConstants;
import com.example.hp.stickpick.bean.ReferenceWrapper;
import com.example.hp.stickpick.bean.UserBean;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import timber.log.Timber;

public class NoticeEnterActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText notice;
    private Button save;
    String date = "";
    Long time;
    long id = 0;
    private ReferenceWrapper referenceWrapper;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_notice);
        referenceWrapper = ReferenceWrapper.getRefrenceWrapper(this);
        notice = (EditText) findViewById(R.id.notice);
        save = (Button) findViewById(R.id.save);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving notice.. Please wait..");
        progressDialog.setCancelable(false);

        save.setOnClickListener(this);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        Date today = new Date();
        id = today.getTime();
        Timber.i("AAAAAAAAAAAAAAAAAAAAA "+id);
        time = today.getTime();
        date = simpleDateFormat.format(today);
    }

    @Override
    public void onClick(View v) {
        //save.setClickable(false);
        Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_in);
        save.startAnimation(animFadeIn);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference(ParameterConstants.NOTICE);

        final String notice_text = notice.getText().toString().trim();

        UserBean userBean = referenceWrapper.getUserBean();
        if (userBean != null) {
            String name = userBean.getName();
            final NoticeBean noticeBean = new NoticeBean();
            //noticeBean.setDate(date);
            noticeBean.setUser(name);
            String dateTime = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
            noticeBean.setTime(dateTime);

            if (notice_text.isEmpty()) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Please write something", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
                //finish();
            } else {
                noticeBean.setNoticeText(notice_text);
                noticeBean.setMobile(userBean.getMobile().toString());


                if (Networking.isConnected(NoticeEnterActivity.this)) {
                    //UserBean bean = RefrenceWrapper.getRefrenceWrapper(NoticeEnterActivity.this).getUserBean();
                    if (userBean.getCourse().equals("TEACHER")) {
                        progressDialog.show();
                        databaseReference.child(id + "").setValue(noticeBean, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                progressDialog.dismiss();
                                if (databaseError == null) {

                                    /*Intent intent2 = new Intent(NoticeEnterActivity.this, NoticeBoardActivity.class);
                                     Toast.makeText(getBaseContext(), "Notice Saved", Toast.LENGTH_LONG).show();
                                    startActivity(intent2);*/
                                    notice.setText("");
                                    Toast toast = Toast.makeText(getApplicationContext(),
                                            "Notice saved", Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                    toast.show();
                                    /*MyFirebaseMessagingService.boolNoticeNotification=true;
                                    RemoteMessage msg=null;
                                    new MyFirebaseMessagingService().onMessageReceived(msg);*/

                                    Intent i = new Intent(NoticeEnterActivity.this, NoticeBoardActivity.class);
                                    startActivity(i);
                                    finish();


                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(getBaseContext(), "Error Occurred.. Please check your internet connection", Toast.LENGTH_LONG).show();
                                }
                            }

                        });
                    } else {
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(NoticeEnterActivity.this);
                        dialogBuilder.setMessage("Only teacher can write the notice");
                        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Intent intent = new Intent(NoticeEnterActivity.this, NoticeBoardActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                        AlertDialog b = dialogBuilder.create();
                        b.show();
                    }

                } else {
                    progressDialog.dismiss();
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Please check your internet connection", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();

                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        progressDialog.dismiss();
        Intent intent = new Intent(this, NoticeBoardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }

}
