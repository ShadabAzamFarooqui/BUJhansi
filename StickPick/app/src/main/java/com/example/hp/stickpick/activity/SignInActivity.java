package com.example.hp.stickpick.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hp.stickpick.R;
import com.example.hp.stickpick.utils.HelperActivity;
import com.example.hp.stickpick.utils.Networking;
import com.example.hp.stickpick.utils.ParameterConstants;
import com.example.hp.stickpick.bean.ReferenceWrapper;
import com.example.hp.stickpick.bean.UserBean;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignInActivity extends AppCompatActivity {
    private TextInputLayout inputLayoutPassword, inputLayoutMobile;
    private Button signIn;
    private EditText password, mobile;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;
    private ReferenceWrapper referenceWrapper;
    private DatabaseReference firebaseDatabase;
    private CheckBox checkBox;
    UserBean bean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_fragment);
        HelperActivity.setStatusBar(this);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Shaking hands with server");
        referenceWrapper = ReferenceWrapper.getRefrenceWrapper(this);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);

        inputLayoutMobile = (TextInputLayout) findViewById(R.id.input_layout_mobile);
        password = (EditText) findViewById(R.id.password);
        mobile = (EditText) findViewById(R.id.mobile);
        signIn = (Button) findViewById(R.id.signinbutton);
        checkBox = (CheckBox) findViewById(R.id.checkbox);

        if (SignUpActivity.boolFromSignUp){
            Intent intent=getIntent();
            String mobPass =intent.getStringExtra("mobPass");
            String[] strAr=mobPass.split(" ");
            mobile.setText(strAr[0]);
            password.setText(strAr[1]);
        }

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(password.getWindowToken(), 0);

                if (Networking.isConnected(SignInActivity.this)) {
                    submitForm();
                } else {
                    Toast.makeText(SignInActivity.this, "Please check you internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //getSupportActionBar().setTitle("Sign In");
    }

    private void submitForm() {

        if (!validateMobile()) {
            return;
        }


        if (!validatePassword()) {
            return;
        }


        if (!ReferenceWrapper.isNetworkAvailable(this)) {
            Toast.makeText(getApplicationContext(), "Please check your network connection", Toast.LENGTH_SHORT).show();
        } else {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            databaseReference = database.getReference(ParameterConstants.USERS);

            progressDialog.show();
            retrieveKey(mobile.getText().toString().trim());

        }
    }


    private void retrieveKey(final String mobile) {

        databaseReference.child(ParameterConstants.PROFILE).child(mobile).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                UserBean userBean = dataSnapshot.getValue(UserBean.class);
                if (userBean != null) {
                    bean = userBean;
                    if (password.getText().toString().trim().equals(userBean.getPassword())) {
                        DatabaseHandler databaseHandler = new DatabaseHandler(SignInActivity.this);
                        if (checkBox.isChecked()) {
                            if (!databaseHandler.isAlreadyLoggedIn()) {
                                databaseHandler.insertStudent(userBean);
                            }

                        }
                        if (databaseHandler.isAlreadyLoggedIn()&&!databaseHandler.getMob().equals(userBean.getMobile())){
                            databaseHandler.deleteTable();
                            if (checkBox.isChecked()) {
                                if (!databaseHandler.isAlreadyLoggedIn()) {
                                    databaseHandler.insertStudent(userBean);
                                }
                            }
                        }
                        login(userBean.getName());
                        referenceWrapper.setUserBean(userBean);

                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(getBaseContext(), "Invalid Credentials, Please Enter Correct Password", Toast.LENGTH_LONG).show();
                    }

                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getBaseContext(), "Unregistered Number, Please Register using Sign Up", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void login(String name) {
        DatabaseReference databaseReferenceNotice = FirebaseDatabase.getInstance().getReference(ParameterConstants.NOTICE);
        databaseReferenceNotice.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                UserBean userBean = ReferenceWrapper.getRefrenceWrapper(SignInActivity.this).getUserBean();
                int iteratorCount = (int) dataSnapshot.getChildrenCount();
                userBean.setNotificationCount(iteratorCount);
                DatabaseReference databaseReferenceUser = FirebaseDatabase.getInstance().getReference(ParameterConstants.USERS);
                databaseReferenceUser.child(ParameterConstants.PROFILE).child(userBean.getMobile()).setValue(userBean);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("dbName", "" + firebaseDatabase);
        startActivity(intent);
    }

    private boolean validateMobile() {
        if (mobile.getText().toString().trim().isEmpty()) {
            inputLayoutMobile.setError("Mobile cannot be left blank");
            requestFocus(mobile);
            return false;
        } else if (mobile.getText().toString().length() != 10) {
            inputLayoutMobile.setError("Mobile should be of 10 digits");
            requestFocus(mobile);
            return false;
        } else {
            inputLayoutMobile.setErrorEnabled(false);
        }
        return true;
    }


    private boolean validatePassword() {
        if (password.getText().toString().trim().isEmpty()) {
            inputLayoutPassword.setError("Password cannot be left blank");
            requestFocus(password);
            return false;
        }/*else if (password.getText().toString().trim().length() < 5) {
            inputLayoutPassword.setError("Password should not be less than 5 character");
            requestFocus(password);
            return false;
        } */ else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}

