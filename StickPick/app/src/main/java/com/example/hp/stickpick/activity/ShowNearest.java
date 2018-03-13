package com.example.hp.stickpick.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.hp.stickpick.R;
import com.example.hp.stickpick.utils.Networking;
import com.example.hp.stickpick.bean.UserBean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ShowNearest extends AppCompatActivity {


    ProgressDialog dialog;
    String a=HomeActivity.lats;
    String b=HomeActivity.lngs;
    String dataUrl ;
    String input;
    ListView listView;

    List<UserBean> userBeanArrayList = new ArrayList<>();

    int[] img={R.drawable.atm,R.drawable.bank,R.drawable.doctor,R.drawable.hospital,R.drawable.police,R.drawable.restaurant,R.drawable.company};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_nearest);
        listView=(ListView) findViewById(R.id.list);

        LocationManager locationManager;
        boolean checkGPS;
        locationManager = (LocationManager) this
                .getSystemService(LOCATION_SERVICE);
        checkGPS = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!checkGPS) {
            android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(this);
            alertDialog.setCancelable(false);
            alertDialog.setMessage("Please turn on your gps");
            alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            alertDialog.show();

        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                dailog(i);
            }
        });
        Intent intent=getIntent();
        input =intent.getStringExtra("input");
        dataUrl= "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+a+","+b+"&radius=1000&types="+ input +"&sensor=false&key=AIzaSyBYim7RY_8oUxXfBxnJCb0jjgPCDrFr790";
        makeRequest();
    }
    public void dailog(final int i){

        final CharSequence[] dailogItems = { "Yes", "No"};

        AlertDialog.Builder dailogBuilder = new AlertDialog.Builder(ShowNearest.this);
        dailogBuilder.setTitle("Would you like to open map application?");
        dailogBuilder.setItems(dailogItems,   new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (dailogItems[item].equals("Yes")) {
                    String uri = "http://maps.google.com/maps?saddr=" +  "" +  "&daddr=" + userBeanArrayList.get(i).getAddressNearestPlace();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    intent.setPackage("com.google.android.apps.maps");
                    startActivity(intent);
                }
                else if (dailogItems[item].equals("No")){
                    dialog.dismiss();
                }
            }
        });
        dailogBuilder.show();
    }
    private void makeRequest() {
        if (Networking.isConnected(this)) {
            new GetData().execute();


        } else {
            android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(this);
            alertDialog.setCancelable(false);
            alertDialog.setMessage("Please check your internet connection");
            alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            alertDialog.show();
        }
    }



    class GetData extends AsyncTask<Void, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(ShowNearest.this);
            dialog.setMessage("Please wait ...");
            dialog.setCancelable(true);
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            Networking networking = new Networking();
            String data = null;
            try {
                data = networking.getDataFromCloud(dataUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return data;
        }


        @Override
        protected void onPostExecute(String data) {


            dialog.dismiss();
            try {

                JSONObject jsonObject = new JSONObject(data);

                JSONArray resArr=jsonObject.getJSONArray("results");

                for (int i = 0; i < resArr.length(); i++) {

                    UserBean bean = new UserBean();
                    JSONObject resJsonObject = resArr.getJSONObject(i);
                    String name = resJsonObject.getString("name");
                    String add = resJsonObject.getString("vicinity");
                    /*JSONObject opening_hours=resJsonObject.getJSONObject("opening_hours");
                    String openStatus=opening_hours.getString("open_now");*/
                    bean.setNameNearestPlace(name);
                    bean.setAddressNearestPlace(add);
                    //bean.setOpenStatus(openStatus);

                    if (input.equals("atm")) {
                        bean.setLogo(img[0]);
                        getSupportActionBar().setTitle("ATM");
                    } else if (input.equals("bank")) {
                        bean.setLogo(img[1]);
                        getSupportActionBar().setTitle("BANK");
                    } else if (input.equals("doctor")) {
                        bean.setLogo(img[2]);
                        getSupportActionBar().setTitle("DOCTOR");
                    } else if (input.equals("hospital")) {
                        bean.setLogo(img[3]);
                        getSupportActionBar().setTitle("HOSPITAL");
                    } else if (input.equals("police")) {
                        bean.setLogo(img[4]);
                        getSupportActionBar().setTitle("POLICE STATION");
                    } else if (input.equals("restaurant")) {
                        bean.setLogo(img[5]);
                        getSupportActionBar().setTitle("RESTAURANT");
                    }
                    else if (input.equals("company")) {
                        bean.setLogo(img[6]);
                        getSupportActionBar().setTitle("OTHER PLACE");
                    }
                    userBeanArrayList.add(bean);
                }

            }catch (Exception e) {
                // TODO: handle exception
            }
            ListViewAdapter listViewAdapter = new ListViewAdapter(ShowNearest.this, userBeanArrayList);
            listView.setAdapter(listViewAdapter);

        }
    }


}
