package com.example.hp.stickpick.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.stickpick.R;
import com.example.hp.stickpick.utils.Networking;
import com.example.hp.stickpick.utils.ParameterConstants;
import com.example.hp.stickpick.bean.ReferenceWrapper;
import com.example.hp.stickpick.bean.UserBean;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity implements View.OnClickListener {

    Button signIn;
    TextView signUp, goToVarificationPage,itsFreeTxt;
    ImageView logo_imageview;
    Animation animBlink,animItsFreeBlink;
    ProgressDialog progressDialog;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference(ParameterConstants.USERS);
    private DatabaseReference firebaseDatabase;
    private ReferenceWrapper referenceWrapper = ReferenceWrapper.getRefrenceWrapper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.splash_page);


        signIn = (Button) findViewById(R.id.signin);
        signUp = (TextView) findViewById(R.id.signup);
        goToVarificationPage = (TextView) findViewById(R.id.tagline);
        itsFreeTxt= (TextView) findViewById(R.id.its_free);
       // logo_imageview= (ImageView) findViewById(R.id.logo_imageview);

        animation();
        signIn.setOnClickListener(this);
        signUp.setOnClickListener(this);
        goToVarificationPage.setOnClickListener(this);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("                    B. U. Jhansi");
        //actionBar.setTitle("B. U. Jhansi");
        //actionBar.setDisplayShowTitleEnabled(true);
        //actionBar.setDisplayHomeAsUpEnabled(true);

        SignUpActivity.boolFromSignUp=false;

        errorDialog();

           /* CoordinatorLayout coordinatorLayout= (CoordinatorLayout) findViewById(R.id.coordinateLayout);
            Snackbar.make(coordinatorLayout, "", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();*/
            //showDialogForNetworkChecking();

    }

    private void errorDialog() {
        if (Networking.isConnected(this)) {
            DatabaseHandler databaseHandler = new DatabaseHandler(SplashActivity.this);
            if (databaseHandler.isAlreadyLoggedIn()) {
                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Please wait...");
                progressDialog.show();
                progressDialog.setCancelable(false);
                retrieveKey(databaseHandler.getMob());
            }
        } else {
            android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(this);
            alertDialog.setCancelable(false);
            alertDialog.setMessage("Please check your internet connection");
            alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    errorDialog();
                }
            });
            alertDialog.show();

        }
    }

    void showDialogForNetworkChecking(){
        android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(this);
        alertDialog.setCancelable(false);
        alertDialog.setMessage("Please check your internet connection");
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (Networking.isConnected(SplashActivity.this)){
                    showDialogForNetworkChecking();
                }else {
                    DatabaseHandler databaseHandler = new DatabaseHandler(SplashActivity.this);
                    if (databaseHandler.isAlreadyLoggedIn()) {
                        progressDialog = new ProgressDialog(SplashActivity.this);
                        progressDialog.setMessage("Please wait...");
                        progressDialog.show();
                        progressDialog.setCancelable(false);
                        retrieveKey(databaseHandler.getMob());
                    }
                }
            }
        });
        alertDialog.show();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signin:
                Intent intent = new Intent(this, SignInActivity.class);
                startActivity(intent);
                break;
            case R.id.signup:
                Intent intent1 = new Intent(this, SignUpActivity.class);
                startActivity(intent1);
                break;


        }
    }

    void animation() {

        animBlink = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.blink);
        //goToVarificationPage.startAnimation(animBlink);

        animItsFreeBlink=AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.its_free_blink);
        itsFreeTxt.startAnimation(animItsFreeBlink);
        //logo_imageview.startAnimation(animBlink);

    }

    private void retrieveKey(String mobile) {

        databaseReference.child(ParameterConstants.PROFILE).child(mobile).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                UserBean userBean = dataSnapshot.getValue(UserBean.class);
                progressDialog.dismiss();
                if (userBean != null) {
                    loginPic(userBean.getName());
                    referenceWrapper.setUserBean(userBean);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginPic(String name) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(ParameterConstants.NOTICE);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                UserBean userBean = ReferenceWrapper.getRefrenceWrapper(SplashActivity.this).getUserBean();
                int iteratorCount = (int) dataSnapshot.getChildrenCount();
                userBean.setNotificationCount(iteratorCount);
                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference(ParameterConstants.USERS);
                databaseReference1.child(ParameterConstants.PROFILE).child(userBean.getMobile()).setValue(userBean);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("dbName", "" + firebaseDatabase);
        startActivity(intent);
    }

}
