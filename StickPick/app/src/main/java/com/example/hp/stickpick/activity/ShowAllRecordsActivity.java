package com.example.hp.stickpick.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.stickpick.R;
import com.example.hp.stickpick.bean.ListBean;
import com.example.hp.stickpick.utils.Conversion;
import com.example.hp.stickpick.utils.Networking;
import com.example.hp.stickpick.utils.ParameterConstants;
import com.example.hp.stickpick.bean.ReferenceWrapper;
import com.example.hp.stickpick.bean.UserBean;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ShowAllRecordsActivity extends AppCompatActivity {

    DatabaseReference databaseReference;
    ListView listView;
    AutoCompleteTextView autoCompleteTextView;
    int i;
    int position;
    EditText mSearch;
    ArrayList<String> listName;
    ArrayList<String> listMobile;
    ArrayList<String> listEmail;
    ArrayList<String> listPassword;
    ArrayList<String> listImage;
    ArrayList<String> listCourse;
    ArrayList<String> listSemester;
    List<ListBean> listBean;
    private ProgressDialog progressDialog;
    String cs;

    boolean expendListViewBool = true;
    List<ListBean> listCourseSemester;
    List<ListBean> listTeacher;

    public List<ListBean> getList() {
        return listBean;
    }

    public List<ListBean> getListCourseSem() {
        return listCourseSemester;
    }

    public int getPosition() {
        return position;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_record);
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autocomplete);
        Intent i = getIntent();

        if (HomeActivity.intShowRecordTitle == 0) {
            getSupportActionBar().setTitle("List of all Student");
        } else if (HomeActivity.intShowRecordTitle == 1) {
            getSupportActionBar().setTitle("M.C.A 1st");
        } else if (HomeActivity.intShowRecordTitle == 2) {
            getSupportActionBar().setTitle("M.C.A 2st");
        } else if (HomeActivity.intShowRecordTitle == 3) {
            getSupportActionBar().setTitle("M.C.A 3rd");
        } else if (HomeActivity.intShowRecordTitle == 4) {
            getSupportActionBar().setTitle("M.C.A 4th");
        } else if (HomeActivity.intShowRecordTitle == 5) {
            getSupportActionBar().setTitle("M.C.A 5th");
        } else if (HomeActivity.intShowRecordTitle == 6) {
            getSupportActionBar().setTitle("M.C.A 6th");
        } else if (HomeActivity.intShowRecordTitle == 7) {
            getSupportActionBar().setTitle("B.C.A 1st");
        } else if (HomeActivity.intShowRecordTitle == 8) {
            getSupportActionBar().setTitle("B.C.A 2st");
        } else if (HomeActivity.intShowRecordTitle == 9) {
            getSupportActionBar().setTitle("B.C.A 3rd");
        } else if (HomeActivity.intShowRecordTitle == 10) {
            getSupportActionBar().setTitle("B.C.A 4th");
        } else if (HomeActivity.intShowRecordTitle == 11) {
            getSupportActionBar().setTitle("B.C.A 5th");
        } else if (HomeActivity.intShowRecordTitle == 12) {
            getSupportActionBar().setTitle("B.C.A 6th");
        } else if (HomeActivity.intShowRecordTitle == 13) {
            getSupportActionBar().setTitle("List Of Teachers");
        }
       /* if (Networking.isConnected(ShowAllRecordsActivity.this)) {*/
        listView = (ListView) findViewById(R.id.listView);
        mSearch= (EditText) findViewById(R.id.search);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Shaking hands with server");
        progressDialog.show();
        getAllRecord();

        /*} else {
            Toast.makeText(ShowAllRecordsActivity.this, "Please check you internet connection", Toast.LENGTH_SHORT).show();
            finish();
        }*/

    }

    private void autoCompleteTextView(List list) {
        autoCompleteTextView.setThreshold(1);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, list);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(ShowAllRecordsActivity.this, adapter.getItem(i), Toast.LENGTH_SHORT).show();
                autoCompleteTextView.setText("");
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    void getAllRecord() {
//use addListenerForSingleValueEvent for every hit

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(ParameterConstants.USERS).child(ParameterConstants.PROFILE);
        ref.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        collectPhoneNumbers((Map<String, Object>) dataSnapshot.getValue());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }

    private void collectPhoneNumbers(Map<String, Object> users) {

        listName = new ArrayList<>();
        listMobile = new ArrayList<>();
        listEmail = new ArrayList<>();
        listPassword = new ArrayList<>();
        listImage = new ArrayList<>();
        listCourse = new ArrayList<>();
        listSemester = new ArrayList<>();

        listCourseSemester = new ArrayList<>();
        listTeacher = new ArrayList<>();


        //iterate through each user, ignoring their UID
        if (users == null) {
            Toast.makeText(this, "No record found", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            finish();
        } else {
            for (Map.Entry<String, Object> entry : users.entrySet()) {
                //Get user map
                Map map = (Map) entry.getValue();
                Set<String> keyset = map.keySet();
                for (String key : keyset) {

                    if (key.equals("name")) {
                        listName.add(map.get(key).toString().toUpperCase());
                        //showAllText.append("" + userName + "\n");
                    }
                    if (key.equals("mobile")) {

                        listMobile.add(map.get(key).toString());

                    }
                    if (key.equals("email")) {

                        listEmail.add(map.get(key).toString());

                    }
                    if (key.equals("password")) {

                        listPassword.add(map.get(key).toString());

                    }
                    if (key.equals("myImage")) {
                        listImage.add(map.get(key).toString());

                    }
                    if (key.equals("course")) {
                        listCourse.add(map.get(key).toString());

                    }
                    if (key.equals("semester")) {
                        listSemester.add(map.get(key).toString());

                    }

                }
            }
            progressDialog.dismiss();
        }

        listBean = new ArrayList();
        for (int i = 0; i < listName.size(); i++) {
            ListBean bean = new ListBean();
            bean.setName(listName.get(i));
            bean.setEmail(listEmail.get(i));
            bean.setMobile(listMobile.get(i));
            bean.setPassword(listPassword.get(i));
            bean.setImage(listImage.get(i));
            bean.setCourse(listCourse.get(i));
            bean.setSemester(listSemester.get(i));
            listBean.add(bean);
        }
        Collections.sort(listBean);

        if (HomeActivity.boolCourseSem) {
            listCourseSemester.clear();
            cs = getIntent().getStringExtra("cs");
            String[] strCs = cs.split(" ");
            for (int a = 0; a < listBean.size(); a++) {
                if (listBean.get(a).getCourse().equals(strCs[0]) && listBean.get(a).getSemester().equals(strCs[1])) {
                    listCourseSemester.add(listBean.get(a));
                }
            }
            MyAdapter myAdapter = new MyAdapter(ShowAllRecordsActivity.this, listCourseSemester);
            listView.setAdapter(myAdapter);

            List autoCompleteList = new ArrayList();
            for (int i = 0; i < listCourseSemester.size(); i++) {
                autoCompleteList.add(listCourseSemester.get(i).getName() + "\n" + listCourseSemester.get(i).getMobile());
            }
            autoCompleteTextView(autoCompleteList);
        } else {
            for (int a = 0; a < listBean.size(); a++) {
                if (listBean.get(a).getCourse().equals("TEACHER")) {
                    listTeacher.add(listBean.get(a));
                }
            }
            listBean.removeAll(listTeacher);
            MyAdapter myAdapter = new MyAdapter(ShowAllRecordsActivity.this, listBean);
            listView.setAdapter(myAdapter);

            List autoCompleteList = new ArrayList();
            for (int i = 0; i < listBean.size(); i++) {
                autoCompleteList.add(listBean.get(i).getName() + "\n" + listBean.get(i).getMobile());
            }
            autoCompleteTextView(autoCompleteList);
        }

        mSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
//                MyAdapter myAdapter = new MyAdapter(ShowAllRecordsActivity.this, listBean);
//                myAdapter.getFilter().filter(s.toString());
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (!HomeActivity.boolCourseSem) {
                    a(listBean, i);
                } else {
                    a(listCourseSemester, i);
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity input AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.action_deleteAllRecord) {

            android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);
            alertDialog.setTitle("Are you sure to clear whole record?");
            alertDialog.setMessage("Please think before doing...");
            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    databaseReference = database.getReference(ParameterConstants.USERS);
                    databaseReference.child(ParameterConstants.PROFILE).setValue(null);
                    //NoticeBoardActivity.clearNoticeBoardContent();
                    databaseReference = database.getReference(ParameterConstants.NOTICE);
                    databaseReference.setValue(null);
                    finish();

                }
            });
            alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();

                }
            });
            alertDialog.show();
        }

        return super.onOptionsItemSelected(item);
    }


    class MyAdapter extends BaseAdapter implements Filterable{

        Context context;


        List<ListBean> listBean;


        MyAdapter(Context context, List listBean) {
            this.context = context;

            this.listBean = listBean;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup viewGroup) {


            LayoutInflater inflater = ((Activity) context).getLayoutInflater();

            // well set up the ViewHolder

            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.userlistitem, null);
                holder = new ViewHolder();
                holder.name = (TextView) convertView.findViewById(R.id.nameUserList);
                holder.mobile = (TextView) convertView.findViewById(R.id.mobileUserList);
                holder.email = (TextView) convertView.findViewById(R.id.emailUserList);
                holder.password = (TextView) convertView.findViewById(R.id.passwordUserList);
                holder.courseSem = (TextView) convertView.findViewById(R.id.courseSemUserList);
                holder.linearEmail = (LinearLayout) convertView.findViewById(R.id.linearEmail);
                holder.linearPass = (LinearLayout) convertView.findViewById(R.id.linearPass);
                holder.image = (ImageView) convertView.findViewById(R.id.imageUserList);
                holder.textViewimageDetail = (TextView) convertView.findViewById(R.id.textViewimageDetail);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            holder.name.setText(listBean.get(position).getName());
            holder.mobile.setText(listBean.get(position).getMobile());
            holder.email.setText(listBean.get(position).getEmail());
            holder.password.setText(listBean.get(position).getPassword());
            holder.courseSem.setText(listBean.get(position).getCourse() + " " + listBean.get(position).getSemester());
            holder.linearEmail.setVisibility(View.VISIBLE);
            holder.linearEmail.setVisibility(View.VISIBLE);
            holder.linearPass.setVisibility(View.GONE);

            if (listBean.get(position).getImage().length() > 0) {
                holder.textViewimageDetail.setVisibility(View.GONE);
                holder.image.setVisibility(View.VISIBLE);
                holder.image.setImageBitmap(Conversion.BitMapfromString(listBean.get(position).getImage().toString()));
            } else {
                holder.image.setVisibility(View.GONE);
                holder.textViewimageDetail.setText("" + listBean.get(position).getName().charAt(0));
                holder.textViewimageDetail.setVisibility(View.VISIBLE);
            }

            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ShowAllRecordsActivity.this.position = position;
                    android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
                    android.support.v4.app.DialogFragment dFragment = new UserPicFragment();
                    dFragment.show(fm, "Dialog Fragment");
                }
            });
            holder.textViewimageDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ShowAllRecordsActivity.this.position = position;
                    android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
                    android.support.v4.app.DialogFragment dFragment = new UserPicFragment();
                    dFragment.show(fm, "Dialog Fragment");
                }
            });

            return convertView;
        }

        @Override
        public int getCount() {
            return listBean.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public Filter getFilter() {
            return null;
        }
    }

    static class ViewHolder {
        TextView name, mobile, email, password, courseSem;
        ImageView image;
        TextView textViewimageDetail;
        LinearLayout linearEmail, linearPass;
    }










    void a(final List<ListBean> list, int i) {

        ShowAllRecordsActivity.this.i = i;
        final CharSequence[] items = {"Call", "Sms", "Email", "Remove"};
        AlertDialog.Builder builder = new AlertDialog.Builder(ShowAllRecordsActivity.this);
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + list.get(ShowAllRecordsActivity.this.i).getMobile().toString()));
                    callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    try {
                        ShowAllRecordsActivity.this.startActivity(callIntent);
                    } catch (Exception e) {
                        Toast.makeText(ShowAllRecordsActivity.this, "Please give permission", Toast.LENGTH_SHORT).show();
                    }
                } else if (item == 1) {

                    final Dialog openDialog = new Dialog(ShowAllRecordsActivity.this);
                    openDialog.setContentView(R.layout.message_dailog);
                    openDialog.setTitle("Enter message");
                    final EditText msgEdt = (EditText) openDialog.findViewById(R.id.msgEdt);
                    Button msgBtn = (Button) openDialog.findViewById(R.id.msgBtn);
                    final String mobileNumber = list.get(ShowAllRecordsActivity.this.i).getMobile().toString();
                    msgBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (msgEdt.getText().toString().trim().isEmpty()) {
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "Please write something", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                toast.show();

                                return;
                            }
                            SmsManager smsManager = SmsManager.getDefault();
                            try {
                                smsManager.sendTextMessage(mobileNumber, null, "B.U.Jhansi=\n" + msgEdt.getText().toString(), null, null);
                            } catch (Exception e) {
                                Toast.makeText(ShowAllRecordsActivity.this, "Please give permission", Toast.LENGTH_SHORT).show();
                            }
                            openDialog.dismiss();
                        }
                    });
                    openDialog.show();


                } else if (item == 2) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", list.get(position).getEmail().toString(), null));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "B.U.Jhansi");
                    startActivity(Intent.createChooser(emailIntent, null));
                } else if (item == 3) {
                    android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(ShowAllRecordsActivity.this);
                    alertDialog.setMessage("Are you sure?");
                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            UserBean userBean = ReferenceWrapper.getRefrenceWrapper(ShowAllRecordsActivity.this).getUserBean();
                            if (userBean.getCourse().equals("TEACHER")) {
                                ProgressDialog progressDialog2 = new ProgressDialog(ShowAllRecordsActivity.this);
                                progressDialog2.setMessage("Removing...");
                                progressDialog2.show();
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                databaseReference = database.getReference(ParameterConstants.USERS);
                                databaseReference.child(ParameterConstants.PROFILE).child(list.get(ShowAllRecordsActivity.this.i).getMobile()).removeValue();
                                Intent intent = new Intent(ShowAllRecordsActivity.this, ShowAllRecordsActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                if (HomeActivity.boolCourseSem){
                                    intent.putExtra("cs", cs);
                                }
                                startActivity(intent);

                                finish();
                                progressDialog2.dismiss();
                            } else {
                                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ShowAllRecordsActivity.this);
                                dialogBuilder.setMessage("Only teacher can delete the record");
                                dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        dialog.dismiss();
                                    }
                                });

                                AlertDialog b = dialogBuilder.create();
                                b.show();
                            }

                        }
                    });
                    alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertDialog.show();
                }
            }

        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }


    void b() {


        ShowAllRecordsActivity.this.i = i;
        final CharSequence[] items = {"Call", "Sms", "Email", "Remove"};
        ShowAllRecordsActivity.this.i = i;
        AlertDialog.Builder builder = new AlertDialog.Builder(ShowAllRecordsActivity.this);
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + listCourseSemester.get(ShowAllRecordsActivity.this.i).getMobile().toString()));
                    callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    try {
                        ShowAllRecordsActivity.this.startActivity(callIntent);

                    } catch (Exception e) {
                        Toast.makeText(ShowAllRecordsActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } else if (item == 1) {
                    final Dialog openDialog = new Dialog(ShowAllRecordsActivity.this);
                    openDialog.setContentView(R.layout.message_dailog);
                    openDialog.setTitle("Enter message");
                    final EditText msgEdt = (EditText) openDialog.findViewById(R.id.msgEdt);
                    Button msgBtn = (Button) openDialog.findViewById(R.id.msgBtn);
                    final String mobileNumber = listCourseSemester.get(ShowAllRecordsActivity.this.i).getMobile().toString();
                    msgBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (msgEdt.getText().toString().trim().isEmpty()) {
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "Please write something", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                toast.show();

                                return;
                            }
                            SmsManager smsManager = SmsManager.getDefault();
                            try {
                                smsManager.sendTextMessage(mobileNumber, null, "B.U.Jhansi=\n" + msgEdt.getText().toString(), null, null);

                            } catch (Exception e) {
                                Toast.makeText(ShowAllRecordsActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            openDialog.dismiss();
                        }
                    });
                    openDialog.show();


                } else if (item == 2) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", listCourseSemester.get(ShowAllRecordsActivity.this.i).getEmail().toString(), null));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "B.U.Jhansi");
                    startActivity(Intent.createChooser(emailIntent, null));
                } else if (item == 3) {
                    android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(ShowAllRecordsActivity.this);
                    alertDialog.setMessage("Are you sure?");
                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            UserBean userBean = ReferenceWrapper.getRefrenceWrapper(ShowAllRecordsActivity.this).getUserBean();
                            if (userBean.getCourse().equals("TEACHER")) {
                                ProgressDialog progressDialog2 = new ProgressDialog(ShowAllRecordsActivity.this);
                                progressDialog2.setMessage("Removing...");
                                progressDialog2.show();
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                databaseReference = database.getReference(ParameterConstants.USERS);

                                databaseReference.child(ParameterConstants.PROFILE).child(listCourseSemester.get(ShowAllRecordsActivity.this.i).getMobile()).removeValue();
                                Intent intent = new Intent(ShowAllRecordsActivity.this, ShowAllRecordsActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                intent.putExtra("cs", cs);
                                startActivity(intent);
                                finish();
                                progressDialog2.dismiss();
                            } else {
                                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ShowAllRecordsActivity.this);
                                dialogBuilder.setMessage("Only teacher can delete the record");
                                dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        dialog.dismiss();
                                    }
                                });

                                AlertDialog b = dialogBuilder.create();
                                b.show();
                            }

                        }
                    });
                    alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertDialog.show();
                }
            }

        });
        AlertDialog dialog = builder.create();
        dialog.show();


    }
}
