package com.example.hp.stickpick.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.stickpick.R;

public class ContactUs extends AppCompatActivity {

    boolean aBoolean=true;
    ImageView connectFacebookImage;
    LinearLayout connectFacebookLayout;
    LinearLayout callUs,emailUs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.live_support_fragment);
        getSupportActionBar().setTitle("Connect Me");
        connectFacebookImage=(ImageView) findViewById(R.id.connectFacebookImage);
        connectFacebookLayout=(LinearLayout) findViewById(R.id.connectFacebookLayout);
        final TextView textView= (TextView) findViewById(R.id.textview);

        callUs= (LinearLayout) findViewById(R.id.callUs);
        emailUs= (LinearLayout) findViewById(R.id.emailUs);
        final TextView callText= (TextView) findViewById(R.id.textViewCall);

        callUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+callText.getText().toString()));
                startActivity(intent);
            }
        });
        emailUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "shadabazam.it@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Suggestion/Query");
                startActivity(Intent.createChooser(emailIntent, null));
            }
        });
        LinearLayout facebook= (LinearLayout) findViewById(R.id.linearFacebook);
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ContactUs.this, "write code", Toast.LENGTH_SHORT).show();
            }
        });

        final LinearLayout about=(LinearLayout) findViewById(R.id.dev1);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (aBoolean){
                    textView.setVisibility(View.VISIBLE);
                    aBoolean=false;
                }
                else {
                    textView.setVisibility(View.GONE);
                    aBoolean=true;
                }

                textView.setText("  Shadab Azam Farooqui\n  M.C.A. from \n  Bundelkhand University, Jhansi\n");
            }
        });
        connectFacebookImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeActivity.checkBuFb=2;
                Intent intent=new Intent(ContactUs.this,FacebookActivity.class);
                startActivity(intent);
            }
        });
        connectFacebookLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeActivity.checkBuFb=2;
                Intent intent=new Intent(ContactUs.this,FacebookActivity.class);
                startActivity(intent);
            }
        });


    }
}
