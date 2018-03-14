package com.example.hp.stickpick.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.stickpick.R;
import com.example.hp.stickpick.utils.ParameterConstants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AboutActivity extends AppCompatActivity {
    boolean boolExpandCollapse = true;
    @Bind(R.id.btnAbout)
    Button aboutBtn;
    @Bind(R.id.txtAbout)
    TextView aboutTxt;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    boolean boolShowDeveloperOption = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        toolbar.setTitle("About Application");
        setSupportActionBar(toolbar);


        aboutBtn = (Button) findViewById(R.id.btnAbout);
        aboutTxt = (TextView) findViewById(R.id.txtAbout);
        aboutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (boolExpandCollapse) {
                    aboutTxt.setMaxLines(aboutTxt.getText().length());
                    aboutBtn.setText("...Less");
                    boolExpandCollapse = false;
                } else {
                    aboutTxt.setMaxLines(20);
                    aboutBtn.setText("More...");
                    boolExpandCollapse = true;
                }
            }
        });



       /* LinearLayout showDeveloper= (LinearLayout) findViewById(R.id.showDeveloper);
        final LinearLayout developerOption= (LinearLayout) findViewById(R.id.developerOption);
        showDeveloper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (boolShowDeveloperOption){
                   developerOption.setVisibility(View.VISIBLE);
                    boolShowDeveloperOption=false;
               }
               else {
                    developerOption.setVisibility(View.GONE);
                    boolShowDeveloperOption=true;
                }
            }
        });
        findViewById(R.id.callUsImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+"+9195062228028"));
                startActivity(intent);
            }
        });
        findViewById(R.id.emailUsImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "shadabazam.it@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Suggestion/Query");
                startActivity(Intent.createChooser(emailIntent, null));
            }
        });
        findViewById(R.id.connectFacebookImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeActivity.checkBuFb=2;
                Intent intent=new Intent(AboutActivity.this,FacebookActivity.class);
                startActivity(intent);
            }
        });*/

    }



}
