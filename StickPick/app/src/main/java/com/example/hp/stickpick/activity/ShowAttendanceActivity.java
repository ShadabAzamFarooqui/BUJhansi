package com.example.hp.stickpick.activity;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.stickpick.R;
import com.example.hp.stickpick.utils.ParameterConstants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ShowAttendanceActivity extends AppCompatActivity {

    @Bind(R.id.show_attendance_textView)
    TextView show_attendance_textView;
    ProgressDialog progressDialog;
    List attendanceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_attendance);
        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(ShowAttendanceActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        getAllRecord();
    }

    void getAllRecord() {
        //use addListenerForSingleValueEvent for every hit
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(ParameterConstants.ATTENDANCE).child(ParameterConstants.ATTENDANCE_MCA_1);
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

        //iterate through each user, ignoring their UID
        if (users == null) {
            Toast.makeText(this, "No record found", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            finish();
        } else {
            List list = null;
            show_attendance_textView.setText("");
            for (Map.Entry<String, Object> entry : users.entrySet()) {
                //Get user map
                list = (List) entry.getValue();
                System.out.println("MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM  user " + users);
                System.out.println("MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM  entry " + entry);
                System.out.println("MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM  list " + list);
                show_attendance_textView.append("\n" + entry.getKey() + "\n");
                for (int i = 1; i < list.size(); i++) {
                    show_attendance_textView.append("\n" + list.get(i).toString());
                }
                show_attendance_textView.append("\n");
                list.clear();
            }


            progressDialog.dismiss();
        }
    }
}
