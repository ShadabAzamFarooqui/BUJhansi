package com.example.hp.stickpick.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.hp.stickpick.R;
import com.example.hp.stickpick.utils.Conversion;
import com.example.hp.stickpick.utils.ParameterConstants;
import com.example.hp.stickpick.bean.ReferenceWrapper;
import com.example.hp.stickpick.bean.UserBean;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SignUpActivity extends AppCompatActivity {

    private TextInputLayout inputLayoutName, inputLayoutPassword, inputLayoutEmail, inputLayoutMobile, inputLayoutConfirmPassword, inputLayoutEditCode;
    private Button signUp;
    private EditText name, email, password, confirmPassword, mobile;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;
    private ReferenceWrapper referenceWrapper;
    CheckBox checkbox;
    private ImageView uploadPic;
    Bitmap myBitmap;
    //String stringFromBitmap = null;
    int REQUEST_CAMERA = 0, SELECT_FILE = 1;

    String course, semester;
    boolean spinnerCourseCheck;
    boolean spinnerSemCheck;


    //verification copy
    EditText OTPEditText;
    Button OTPButton;
    RelativeLayout relativeSignUp;
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    boolean mVerificationInProgress = false;
    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    ScrollView scrollingView;
    LinearLayout linearLayout;
    EditText editCode;
    public static boolean boolFromSignUp = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_fragment);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);
        referenceWrapper = ReferenceWrapper.getRefrenceWrapper(this);
        progressDialog.setMessage("Registering.. Please wait");
        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        inputLayoutConfirmPassword = (TextInputLayout) findViewById(R.id.input_layout_confirm_password);
        inputLayoutMobile = (TextInputLayout) findViewById(R.id.input_layout_mobile);
        inputLayoutEditCode = (TextInputLayout) findViewById(R.id.input_layout_editCode);

        uploadPic = (ImageView) findViewById(R.id.uploadPic);
        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        mobile = (EditText) findViewById(R.id.mobile);
        confirmPassword = (EditText) findViewById(R.id.confirmPassword);
        checkbox = (CheckBox) findViewById(R.id.checkbox);

        editCode = (EditText) findViewById(R.id.editCode);

        spinnerClass();
        spinnerSem();
        uploadPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
        signUp = (Button) findViewById(R.id.signupbutton);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* if (Networking.isConnected(SignUpActivity.this)) {*/
                submitForm(v);
                /*} else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Please check your internet connection", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();

                }*/

            }
        });
//        getSupportActionBar().setTitle("User Registration");


        //verification Part

        scrollingView = (ScrollView) findViewById(R.id.scrollSignUp);
        linearLayout = (LinearLayout) findViewById(R.id.linearSignUp);
        mAuth = FirebaseAuth.getInstance();


        OTPButton = (Button) findViewById(R.id.submitOpt);
        OTPEditText = (EditText) findViewById(R.id.enterOptEdt);


        OTPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (OTPEditText.getText().toString().length() > 5) {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, OTPEditText.getText().toString());
                    signInWithPhoneAuthCredential(credential);
                } else {
                    Toast.makeText(SignUpActivity.this, "invalid OTP", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCredential:success");
                            Toast.makeText(SignUpActivity.this, "Verification done line No :227", Toast.LENGTH_LONG).show();
                            FirebaseUser user = task.getResult().getUser();
                            databaseReference.child(ParameterConstants.PROFILE).child(userBean.getMobile()).setValue(userBean, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if (databaseError == null) {
                                        progressDialog.dismiss();
                                        referenceWrapper.setUserBean(userBean);
                                        Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                                        boolFromSignUp = true;
                                        intent.putExtra("mobPass", userBean.getMobile() + " " + userBean.getPassword());
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    } else {
                                        progressDialog.dismiss();
                                    }
                                }
                            });

                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(SignUpActivity.this, "Verification failed code invalid", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    private void submitForm(View view ) {
        if (!validateName()) {
            return;
        }
        if (!validateEmail()) {
            return;
        }
        if (!validatePassword()) {
            return;
        }
        if (!validateConfirmPassword()) {
            return;
        }
        if (!validateMobile()) {
            return;
        }
        /*if (course.equals("TEACHER")) {
            Toast.makeText(this, "Teacher panel is under development...", Toast.LENGTH_SHORT).show();
            return;
        }*/
        if (!checkbox.isChecked()) {
            Snackbar.make(view, "Please accept terms and conditions", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

            return;
        }
        if (!ReferenceWrapper.isNetworkAvailable(this)) {

            Snackbar.make(view, "Please check your network connection", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

        } else {

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            databaseReference = database.getReference(ParameterConstants.USERS);
            if (spinnerCourseCheck && spinnerSemCheck) {
                if (course.equals("TEACHER") && !semester.equals("IT") || !course.equals("TEACHER") && semester.equals("IT") || !course.equals("TEACHER") && semester.equals("FACULTY")) {

                    Snackbar.make(view, "Please select the correct option from spinner", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();



                } else if (course.equals("TEACHER")) {
                    final ProgressDialog p = new ProgressDialog(this);
                    p.setMessage("Matching Teacher code:");
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference(ParameterConstants.TEACHER);
                    ref.child(ParameterConstants.CODE).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            String code = dataSnapshot.getValue(String.class);
                            if (!code.equals(editCode.getText().toString())) {
                                p.dismiss();
                                inputLayoutEditCode.setError("invalid teacher code");
                                requestFocus(OTPEditText);
                            } else {
                                p.dismiss();
                                retrieveKey(mobile.getText().toString().trim());
                            }
                        }


                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });

                } else {
                    retrieveKey(mobile.getText().toString().trim());
                }
            } else {

                Snackbar.make(view, "Please select the correct option from spinner", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();


            }
        }
    }

    private void retrieveKey(String mobile) {
        progressDialog.show();

        databaseReference.child(ParameterConstants.PROFILE).child(mobile).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                UserBean userBean = dataSnapshot.getValue(UserBean.class);
                if (userBean == null) {
                    register();
                } else {
                    progressDialog.dismiss();
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "User with this mobile number already registered, Please login", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }


    private void register() {
        registerUser(name.getText().toString().trim(),
                email.getText().toString().trim(),
                password.getText().toString().trim(),
                mobile.getText().toString().trim(),
                myBitmap);
    }

    public UserBean userBean;

    private void registerUser(String name, String email, String password, String mobile, final Bitmap myBitmap) {
        final UserBean userBean = new UserBean();
        userBean.setName(name);
        userBean.setPassword(password);
        userBean.setMobile(mobile);
        userBean.setEmail(email);
        userBean.setMyImage(Conversion.stringFromBitmap(myBitmap));
        //userBean.setNotificationCount(0);
        if (!course.equals("")) {
            userBean.setCourse(course);
        }
        if (!course.equals("")) {
            userBean.setSemester(semester);
        }
        this.userBean = userBean;

        /*progressDialog.dismiss();
        scrollingView.setVisibility(View.GONE);
        linearLayout.setVisibility(View.VISIBLE);*/

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Toast.makeText(SignUpActivity.this, "verification done line No :153", Toast.LENGTH_LONG).show();

                databaseReference.child(ParameterConstants.PROFILE).child(userBean.getMobile()).setValue(userBean, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            progressDialog.dismiss();
                            referenceWrapper.setUserBean(userBean);
                            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                            boolFromSignUp = true;
                            intent.putExtra("mobPass", userBean.getMobile() + " " + userBean.getPassword());
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } else {
                            progressDialog.dismiss();
                        }
                    }
                });
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                progressDialog.dismiss();
                Toast.makeText(SignUpActivity.this, ""+e, Toast.LENGTH_SHORT).show();
//                Toast.makeText(SignUpActivity.this, "verification fail", Toast.LENGTH_LONG).show();
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // [START_EXCLUDE]
                    Toast.makeText(SignUpActivity.this, "invalid mob no", Toast.LENGTH_LONG).show();
                    // [END_EXCLUDE]
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // [START_EXCLUDE]
                    Toast.makeText(SignUpActivity.this, "quota over", Toast.LENGTH_LONG).show();
                    // [END_EXCLUDE]
                }
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                //Log.d(TAG, "onCodeSent:" + verificationId);
                progressDialog.dismiss();
                Toast.makeText(SignUpActivity.this, "Verification code sent to mobile", Toast.LENGTH_LONG).show();
                // Save verification ID and resending token so we can use them later

                mVerificationId = verificationId;
                mResendToken = token;

                scrollingView.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);

            }
        };
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" +mobile,             // Phone number to verify
                60,                      // Timeout duration
                TimeUnit.SECONDS,        // Unit of timeout
                SignUpActivity.this,   // Activity (for callback binding)
                mCallbacks);         // OnVerificationStateChangedCallbacks
    }
    // storing is being done here
        /*databaseReference.child(ParameterConstants.PROFILE).child(mobile).setValue(userBean, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    progressDialog.dismiss();
                    refrenceWrapper.setUserBean(userBean);
                    Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    progressDialog.dismiss();
                }
            }
        });*/

    private boolean validateName() {
        if (name.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError("Name cannot be left blank");
            requestFocus(name);
            return false;
        } else if (name.getText().toString().trim().length() < 5) {
            inputLayoutName.setError("Name should not be less than 5 character");
            requestFocus(name);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }
        return true;
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

    private boolean validateEmail() {
        String emailAdd = email.getText().toString().trim();

        if (emailAdd.isEmpty() || !isValidEmail(emailAdd)) {
            inputLayoutEmail.setError("Invalid Email");
            requestFocus(email);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        if (password.getText().toString().trim().isEmpty()) {
            inputLayoutPassword.setError("Password cannot be left blank");
            requestFocus(password);
            return false;
        } else if (password.getText().toString().trim().length() < 5) {
            inputLayoutPassword.setError("Password should not be less than 5 character");
            requestFocus(password);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateConfirmPassword() {
        if (!confirmPassword.getText().toString().trim().equals(password.getText().toString().trim())) {
            inputLayoutConfirmPassword.setError("Passwords does not match");
            requestFocus(confirmPassword);
            return false;
        } else {
            inputLayoutConfirmPassword.setErrorEnabled(false);
        }
        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    //camera
    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        myBitmap = thumbnail;
        uploadPic.setImageBitmap(thumbnail);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        /*Uri selectedImageUri = data.getData();
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = managedQuery(selectedImageUri, projection, null, null,
                null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();

        String selectedImagePath = cursor.getString(column_index);

        Bitmap bm;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(selectedImagePath, options);
        final int REQUIRED_SIZE = 200;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(selectedImagePath, options);
        myBitmap = bm;
        uploadPic.setImageBitmap(bm);*/
        checkCode(data);
    }
    void checkCode(Intent data){
        Uri uri = data.getData();
        try {
            Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            myBitmap = bm;
            uploadPic.setImageBitmap(bm);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Spinner
    public void spinnerClass() {

        final Spinner spinner = (Spinner) findViewById(R.id.spinner_class);

        final List<String> list = new ArrayList<>();
        list.add("SELECT");
        list.add("TEACHER");
        list.add("MCA");
        list.add("BCA");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    course = list.get(i);
                    semList.clear();
                    semList.add("SELECT");
                    spinnerCourseCheck = true;
                    inputLayoutEditCode.setVisibility(View.VISIBLE);
                    editCode.setVisibility(View.VISIBLE);
                    if (i == 1) {
                        semList.clear();
                        semList.add("FACULTY");
                        semList.add("IT");
                        inputLayoutEditCode.setVisibility(View.VISIBLE);
                        editCode.setVisibility(View.VISIBLE);
                        spinnerSem();
                    }
                    if (i == 2) {
                        semList.clear();
                        semList.add("SEMESTER");
                        semList.add("1st");
                        semList.add("2nd");
                        semList.add("3rd");
                        semList.add("4th");
                        semList.add("5th");
                        semList.add("6th");
                        inputLayoutEditCode.setVisibility(View.GONE);
                        editCode.setVisibility(View.GONE);
                        spinnerSem();
                    }
                    if (i == 3) {
                        semList.clear();
                        semList.add("SEMESTER");
                        semList.add("1st");
                        semList.add("2nd");
                        semList.add("3rd");
                        semList.add("4th");
                        semList.add("5th");
                        semList.add("6th");
                        inputLayoutEditCode.setVisibility(View.GONE);
                        editCode.setVisibility(View.GONE);
                        spinnerSem();
                    }
                    if (i == 4) {
                        semList.add("1st");
                        semList.add("2nd");
                        semList.add("3rd");
                        semList.add("4th");
                        semList.add("5th");
                        semList.add("6th");
                        editCode.setVisibility(View.GONE);
                        spinnerSem();
                    }

                } else {
                    spinnerCourseCheck = false;
                    course = "";
                    semList.clear();
                    semList.add("SELECT");
                    spinnerSem();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


    }

    final List<String> semList = new ArrayList<>();

    public void spinnerSem() {

        final Spinner spinner = (Spinner) findViewById(R.id.spinner_sem);
//        semList.add("SELECT");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, semList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    semester = semList.get(i);
                    spinnerSemCheck = true;
                } else {
                    spinnerSemCheck = false;
                    semester = "";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (scrollingView.getVisibility() != View.VISIBLE) {
            scrollingView.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }
}
