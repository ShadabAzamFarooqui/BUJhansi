package com.example.hp.stickpick.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.stickpick.R;
import com.example.hp.stickpick.utils.Conversion;
import com.example.hp.stickpick.utils.HelperActivity;
import com.example.hp.stickpick.utils.Networking;
import com.example.hp.stickpick.utils.ParameterConstants;
import com.example.hp.stickpick.bean.ReferenceWrapper;
import com.example.hp.stickpick.bean.UserBean;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserDetailActivity extends AppCompatActivity {

    EditText name;
    EditText email;
    EditText mobile;
    EditText password;
    ImageView imageView;
    ReferenceWrapper referenceWrapper;
    UserBean userBean;
    boolean check = true;
    int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    Bitmap myBitmap;
    String u_n;
    TextView imageTextView;
    Button saveButton;
    private DatabaseReference databaseReference;
    private TextInputLayout inputLayoutName, inputLayoutPassword, inputLayoutEmail, inputLayoutMobile, inputLayoutConfirmPassword;


    //for spinner
    String course, semester;
    boolean spinnerCourseCheck;
    boolean spinnerSemCheck;
    Spinner spinnerClass;
    Spinner spinnerSem;
    //final List<String> semList = new ArrayList<>();


    // List<UserBean> list
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        HelperActivity.setStatusBar(this);
        HelperActivity.closeKeyPad(this);

        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name_details);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email_details);
        inputLayoutMobile = (TextInputLayout) findViewById(R.id.input_layout_mobile_detail);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password_detail);


        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        mobile = (EditText) findViewById(R.id.mobile);
        imageView = (ImageView) findViewById(R.id.iv);
        imageTextView = (TextView) findViewById(R.id.textViewimageDetail);
        saveButton = (Button) findViewById(R.id.save_button);

        /*if (fromUserListPage()) {

            fromUserListPage();
        } else {*/
        fromHomePage();
        name.setEnabled(true);
        email.setEnabled(true);
        mobile.setEnabled(true);
        password.setEnabled(true);


        spinnerClass();
        spinnerSem();

        imageTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView.setVisibility(View.GONE);
                selectImage();

            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageTextView.setVisibility(View.GONE);
                selectImage();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Networking.isConnected(UserDetailActivity.this)) {
                    ReferenceWrapper referenceWrapper = ReferenceWrapper.getRefrenceWrapper(UserDetailActivity.this);
                    UserBean userBean = referenceWrapper.getUserBean();
                    //userBean.setName(name.getText().toString());
                    submitForm(view);
                    if (imageBool) {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference databaseReference = database.getReference(ParameterConstants.USERS);
                        databaseReference.child(ParameterConstants.PROFILE).child(userBean.getMobile()).child("myImage").setValue("");
                        userBean.setMyImage("");
                    }
                } else {
                    Toast.makeText(UserDetailActivity.this, "Please check you internet connection", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }


    /*}*/
    private void fromHomePage() {

        referenceWrapper = ReferenceWrapper.getRefrenceWrapper(this);
        userBean = referenceWrapper.getUserBean();
        //userBean.setName(name.getText().toString());
        if (!userBean.getMyImage().equals("")) {
            imageTextView.setVisibility(View.GONE);
            imageView.setImageBitmap(Conversion.BitMapfromString(userBean.getMyImage()));
            imageView.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.GONE);
            imageTextView.setVisibility(View.VISIBLE);
            imageTextView.setText("" + userBean.getName().charAt(0));

        }

        name.setText(userBean.getName());
        email.setText(userBean.getEmail());
        mobile.setText(userBean.getMobile());
        password.setText(userBean.getPassword());

    }

    private void submitForm(final View view) {
        if (!validateName()) {
            return;
        }


        if (!validateEmail()) {
            return;
        }

        if (!validatePassword()) {
            return;
        }


        if (!validateMobile()) {
            return;
        }
        /*if (course.equals("TEACHER")){
            Toast.makeText(this, "Teacher panel is under development...", Toast.LENGTH_SHORT).show();
            return;
        }*/
        else {
            final ProgressDialog progressDialog = new ProgressDialog(UserDetailActivity.this);
            progressDialog.setMessage("Please wait...");

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            databaseReference = database.getReference(ParameterConstants.USERS);

            userBean.setName(name.getText().toString());
            userBean.setEmail(email.getText().toString());
            userBean.setMobile(mobile.getText().toString());
            userBean.setPassword(password.getText().toString());

            if (myBitmap != null) {
                userBean.setMyImage(Conversion.stringFromBitmap(myBitmap));
            }

            if (!semester.equals("") && !course.equals("")) {
                progressDialog.show();
                userBean.setCourse(course);
                userBean.setSemester(semester);
                new DatabaseHandler(this).updateStudent(userBean);
                databaseReference.child(ParameterConstants.PROFILE).child(userBean.getMobile()).setValue(userBean);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();

                        Snackbar.make(view, "Your changes have been updated successfully", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 2000);
                    }
                }, 1000);

            } else {
                Toast.makeText(this, "Select correct option from both spinner", Toast.LENGTH_SHORT).show();
            }
        }
    }


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


    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    boolean imageBool = false;

    private void selectImage() {
        final CharSequence[] items = {"Add/Change photo", "Remove photo"};

        AlertDialog.Builder builder = new AlertDialog.Builder(UserDetailActivity.this);
        builder.setTitle("What you want to do?");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Remove photo")) {
                    imageBool = true;
                    imageView.setVisibility(View.GONE);
                    imageTextView.setText("" + userBean.getName().charAt(0));
                    imageTextView.setVisibility(View.VISIBLE);
                } else if (items[item].equals("Add/Change photo")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                    imageBool = false;
                    if (imageView.getVisibility() == View.VISIBLE) {
                        imageTextView.setVisibility(View.GONE);
                    }

                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                onSelectFromGalleryResult(data);
                imageTextView.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
            } else if (requestCode == REQUEST_CAMERA) {
                onCaptureImageResult(data);
                imageTextView.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);

            }


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
        imageView.setImageBitmap(thumbnail);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
//        Uri selectedImageUri = data.getData();
//        String[] projection = {MediaStore.MediaColumns.DATA};
//        Cursor cursor = managedQuery(selectedImageUri, projection, null, null,
//                null);
//        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
//        cursor.moveToFirst();
//
//        String selectedImagePath = cursor.getString(column_index);
//
//        Bitmap bm;
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(selectedImagePath, options);
//        final int REQUIRED_SIZE = 200;
//        int scale = 1;
//        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
//                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
//            scale *= 2;
//        options.inSampleSize = scale;
//        options.inJustDecodeBounds = false;
//        bm = BitmapFactory.decodeFile(selectedImagePath, options);

//        myBitmap = bm;
//        imageView.setImageBitmap(bm);
        checkCode(data);
    }
    void checkCode(Intent data){
        Uri uri = data.getData();
        try {
            Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            myBitmap = bm;
            imageView.setImageBitmap(bm);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void spinnerClass() {
        spinnerClass = (Spinner) findViewById(R.id.spinner_class_detail);
        final List<String> list = new ArrayList<>();

        UserBean userBean = ReferenceWrapper.getRefrenceWrapper(this).getUserBean();
        if (userBean.getCourse().equals("TEACHER")) {
            list.clear();
            list.add("SELECT");
            list.add("TEACHER");
        } else {
            list.clear();
            list.add("SELECT");
            list.add("MCA");
            list.add("BCA");
        }


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClass.setAdapter(dataAdapter);
        spinnerClass.setSelection(dataAdapter.getPosition(userBean.getCourse()));
        spinnerClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    course = list.get(i);
                } else {
                    course = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    public void spinnerSem() {
        spinnerSem = (Spinner) findViewById(R.id.spinner_sem_detail);
        final List<String> list = new ArrayList<>();


        UserBean userBean = ReferenceWrapper.getRefrenceWrapper(this).getUserBean();
        if (userBean.getCourse().equals("TEACHER")) {
            list.clear();
            list.add("FACULTY");
            list.add("IT");
        } else {
            list.clear();
            list.add("SEMESTER");
            list.add("1st");
            list.add("2nd");
            list.add("3rd");
            list.add("4th");
            list.add("5th");
            list.add("6th");
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSem.setAdapter(dataAdapter);
        spinnerSem.setSelection(dataAdapter.getPosition(userBean.getSemester()));
        spinnerSem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    semester = list.get(i);
                } else {
                    semester = "";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

}
