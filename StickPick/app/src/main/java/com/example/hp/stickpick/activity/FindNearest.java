package com.example.hp.stickpick.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.hp.stickpick.R;

public class FindNearest extends AppCompatActivity {

    TextView atm;
    TextView bank;
    TextView doctor;
    TextView hospital;
    TextView policeStation;
    TextView restaurant;
    TextView company;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_nearest);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
       // new TrackLocation(FindNearest.this).getMyLocation();

        atm= (TextView) findViewById(R.id.atm);
        atm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(FindNearest.this,ShowNearest.class);
                intent.putExtra("input","atm");
                startActivity(intent);
            }
        });
        bank= (TextView) findViewById(R.id.bank);
        bank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(FindNearest.this,ShowNearest.class);
                intent.putExtra("input","bank");
                startActivity(intent);
            }
        });
        doctor= (TextView) findViewById(R.id.doctor);
        doctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(FindNearest.this,ShowNearest.class);
                intent.putExtra("input","doctor");
                startActivity(intent);
            }
        });
        hospital= (TextView) findViewById(R.id.hospital);
        hospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(FindNearest.this,ShowNearest.class);
                intent.putExtra("input","hospital");
                startActivity(intent);
            }
        });
        policeStation= (TextView) findViewById(R.id.police);
        policeStation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(FindNearest.this,ShowNearest.class);
                intent.putExtra("input","police");
                startActivity(intent);
            }
        });
        restaurant= (TextView) findViewById(R.id.restra);
        restaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(FindNearest.this,ShowNearest.class);
                intent.putExtra("input","restaurant");
                startActivity(intent);
            }
        });
        company= (TextView) findViewById(R.id.company);
        company.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(FindNearest.this,ShowNearest.class);
                intent.putExtra("input","company");
                startActivity(intent);
            }
        });



    }
}
