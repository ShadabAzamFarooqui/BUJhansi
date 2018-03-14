
package com.example.hp.stickpick.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;

import com.example.hp.stickpick.R;
import com.example.hp.stickpick.android_retrofit.activity.MapPlaceActivity;
import com.example.hp.stickpick.android_retrofit.api.ApiClient;
import com.example.hp.stickpick.android_retrofit.network.request.GetRequest;
import com.example.hp.stickpick.utils.Conversion;
import com.example.hp.stickpick.utils.Networking;
import com.example.hp.stickpick.bean.NoticeBean;
import com.example.hp.stickpick.utils.ParameterConstants;
import com.example.hp.stickpick.bean.ReferenceWrapper;
import com.example.hp.stickpick.bean.UserBean;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.LocationListener;

import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static Response<GetRequest> response;

    public static boolean boolNotification = true;
    public int count = 0;
    //MapPlaceActivity
    private GoogleMap mMap;
    double latitude;
    double longitude;
    private int PROXIMITY_RADIUS = 50;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    ProgressDialog pDialog;
    NoticeBean noticeBean;
    //boolean boolCheckMapVisibility=false;
    //====

    public static int checkBuFb = 1;
    public static boolean boolCourseSem = false;
    private ReferenceWrapper referenceWrapper;
    private UserBean userBean;
    private NavigationView navigationView;
    private DatabaseReference databaseReference;
    DrawerLayout drawer;
    private TextView hometitle;
    private boolean donestartup = false;
    private int firststartup = -1;
    Animation animBounce, animFadeIn, animSequential;

    public static String lats;
    public static String lngs;

    public static int intShowRecordTitle = 0;
    TextView teacherAccount;

    Bitmap bitmap;
    ImageView enterImageNotice;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final LinearLayout notificationLayout = (LinearLayout) findViewById(R.id.notification_layout);
        setSupportActionBar(toolbar);
        notificationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.fade_in);
                notificationLayout.startAnimation(animFadeIn);
                Intent intent = new Intent(HomeActivity.this, NoticeBoardActivity.class);
                startActivity(intent);
            }
        });

        referenceWrapper = ReferenceWrapper.getRefrenceWrapper(this);
        hometitle = (TextView) findViewById(R.id.hometitle);

        SignUpActivity.boolFromSignUp = false;
        //MapPlaceActivity
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        //Check if Google Play Services Available or not
        if (!CheckGooglePlayServices()) {
            Log.d("onCreate", "Finishing test case since Google Play Services are not available");
            finish();
        } else {
            Log.d("onCreate", "Google Play Services available.");
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);

//        handleMosqueRequest("mosque");

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        LocationManager locationManager;
        boolean checkGPS;
        locationManager = (LocationManager) this
                .getSystemService(LOCATION_SERVICE);
        checkGPS = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        // new TrackLocation(this).getMyLocation();
        if (!checkGPS) {
            //showSettingsAlert();
            android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(HomeActivity.this);
            alertDialog.setMessage("Please turn on your gps");
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                   /* pDialog.setMessage("Getting Location...");
                    pDialog.setCancelable(true);
                    pDialog.show();*/
                }
            });
            alertDialog.show();
        } else {
           /* pDialog.setMessage("Getting Location...");
            pDialog.setCancelable(false);
            pDialog.show();*/
        }

        //===
        animBounce = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.bounce);
        animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_in);
        animSequential = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.sequential);

        closeDrawerSubItemNearest();
        UserBean userBean = referenceWrapper.getUserBean();
        count = userBean.getNotificationCount();
        /*if (userBean == null) {
            DatabaseHandler databaseHandler = new DatabaseHandler(HomeActivity.this);
            databaseHandler.getMob();
            hometitle.setText("Welcome " + databaseHandler.getMob());
        } else {*/

        hometitle.setText("Welcome " + userBean.getName());
        /*}*/


        hometitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hometitle.startAnimation(animFadeIn);
               /* if (!boolCheckMapVisibility){
                    findViewById(R.id.relativeMap).setVisibility(View.VISIBLE);
                    boolCheckMapVisibility=true;
                }
                else {
                    findViewById(R.id.relativeMap).setVisibility(View.GONE);
                    boolCheckMapVisibility=false;
                }*/




               /* Intent intent = new Intent(HomeActivity.this, UserDetailActivity.class);
                startActivity(intent);*/
            }
        });


        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                closeDrawerSubItemNearest();
                closeDrawerSubItemCourse();
                closeDrawerSubItemMca();
                closeDrawerSubItemAccountSetting();
                invalidateOptionsMenu();
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                donestartup = true;
                if (firststartup == -1)
                    firststartup = 0;

                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);

        navigationView.setNavigationItemSelectedListener(this);

        if (!userBean.getCourse().equals("TEACHER")) {
            getSupportActionBar().setTitle("IT Department");
        } else {
            getSupportActionBar().setTitle("Administration Login");
        }


        //code for techer code;

        final EditText teacherCodeEdt = (EditText) findViewById(R.id.teacherCodeEdt);
        Button teacherCodeBtn = (Button) findViewById(R.id.teacherCodeBtn);
        teacherCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = teacherCodeEdt.getText().toString();
                final ProgressDialog p = new ProgressDialog(HomeActivity.this);
                p.setMessage("Please wait");
                p.show();
                DatabaseReference r = FirebaseDatabase.getInstance().getReference(ParameterConstants.TEACHER);
                r.child(ParameterConstants.CODE).setValue(code, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            Toast.makeText(HomeActivity.this, "code save", Toast.LENGTH_SHORT).show();
                            teacherCodeEdt.setText("");
                            p.dismiss();
                        } else {
                            Toast.makeText(HomeActivity.this, "error occurred", Toast.LENGTH_SHORT).show();
                            p.dismiss();
                        }
                    }

                });
            }
        });


        if (!ReferenceWrapper.getRefrenceWrapper(HomeActivity.this).getUserBean().getCourse().equalsIgnoreCase(ParameterConstants.TEACHER)) {
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.open_notice_dialog);
            // fab.setVisibility(View.GONE);
        }
        FloatingActionButton attendanceFab = (FloatingActionButton) findViewById(R.id.attendance_fab);
        attendanceFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, AttendanceActivity.class);
                startActivity(intent);
            }
        });
        FloatingActionButton show_attendance_fab = (FloatingActionButton) findViewById(R.id.show_attendance_fab);
        show_attendance_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ShowAttendanceActivity.class);
                startActivity(intent);
            }
        });
        FloatingActionButton findNearestFab = (FloatingActionButton) findViewById(R.id.find_nearest_fab);
        findNearestFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, FindNearest.class);
                startActivity(intent);
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.open_notice_dialog);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noticeBean = new NoticeBean();
                final Dialog openDialog = new Dialog(HomeActivity.this);
                openDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                openDialog.setContentView(R.layout.enter_notice_dailog);
                openDialog.setCanceledOnTouchOutside(false);

                final EditText notice = (EditText) openDialog.findViewById(R.id.notice);
                final Button save = (Button) openDialog.findViewById(R.id.save);
                enterImageNotice = (ImageView) openDialog.findViewById(R.id.enterImageNotice);

                Date today = new Date();
                final long key = today.getTime();

                final ReferenceWrapper referenceWrapper = ReferenceWrapper.getRefrenceWrapper(HomeActivity.this);

                final ProgressDialog progressDialog;

                progressDialog = new ProgressDialog(HomeActivity.this);
                progressDialog.setMessage("Saving notice.. Please wait..");
                progressDialog.setCancelable(false);

                enterImageNotice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.fade_in);
                        enterImageNotice.startAnimation(animFadeIn);
                        Intent intent = new Intent(
                                Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        startActivityForResult(
                                Intent.createChooser(intent, "Select File"),
                                1);

                    }
                });
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.fade_in);
                        save.startAnimation(animFadeIn);
                        if (!Networking.isConnected(HomeActivity.this)) {
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "Please check your internet connection", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                            toast.show();
                            return;
                        }
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference databaseReference = database.getReference(ParameterConstants.NOTICE);
                        final String notice_text = notice.getText().toString().trim();

//                        bitmap = ((BitmapDrawable) enterImageNotice.getDrawable()).getBitmap();

                        if (bitmap != null) {
                            noticeBean.setImage(Conversion.stringFromBitmap(bitmap));
                        } else {
                            noticeBean.setImage("");
                        }
                        UserBean userBean = referenceWrapper.getUserBean();
                        String name = userBean.getName();
                        //noticeBean = new NoticeBean();
                        noticeBean.setUser(name);
                        String dateTime = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                        noticeBean.setTime(dateTime);

                        if (notice_text.isEmpty()) {
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "Please write something", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                            toast.show();

                        } else {
                            noticeBean.setNoticeText(notice_text);
                            if (Networking.isConnected(HomeActivity.this)) {
                                if (userBean.getCourse().equals("TEACHER")) {
                                    progressDialog.show();
                                    databaseReference.child(key + "").setValue(noticeBean, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                            progressDialog.dismiss();
                                            if (databaseError == null) {
                                                notice.setText("");

                                                    /*Intent i = new Intent(HomeActivity.this, NoticeBoardActivity.class);
                                                    startActivity(i);*/
                                                openDialog.dismiss();
                                                Toast toast = Toast.makeText(getApplicationContext(),
                                                        "Notice saved", Toast.LENGTH_SHORT);
                                                toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                                toast.show();
                                                bitmap = null;
                                            } else {
                                                progressDialog.dismiss();
                                                Toast.makeText(getBaseContext(), "Error Occurred.. Please check your internet connection", Toast.LENGTH_LONG).show();
                                            }
                                        }

                                    });
                                } else {
                                    android.support.v7.app.AlertDialog.Builder dialogBuilder = new android.support.v7.app.AlertDialog.Builder(HomeActivity.this);
                                    dialogBuilder.setMessage("Only teacher can write the notice");
                                    dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            openDialog.dismiss();
                                        }
                                    });
                                    android.support.v7.app.AlertDialog b = dialogBuilder.create();
                                    b.show();
                                }
                            } else {
                                progressDialog.dismiss();
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "Please check your internet connection", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                toast.show();
                            }
                        }

                    }
                });
                bitmap = null;
                openDialog.show();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        hometitle.startAnimation(animBounce);
        userBean = referenceWrapper.getUserBean();
        if (userBean != null && navigationView != null) {
            View headerView = navigationView.getHeaderView(0);
            ImageView imageView = (ImageView) headerView.findViewById(R.id.userImage);
            TextView textView = (TextView) headerView.findViewById(R.id.imageTextView);
            if (!userBean.getMyImage().equals("")) {
                textView.setVisibility(View.GONE);
                imageView.setImageBitmap(Conversion.BitMapfromString(userBean.getMyImage()));
                imageView.setVisibility(View.VISIBLE);
               /* imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(HomeActivity.this, UserDetailActivity.class);
                        startActivity(intent);

                    }
                });*/

            } else {
                imageView.setVisibility(View.GONE);
                textView.setText("" + userBean.getName().charAt(0));
                textView.setVisibility(View.VISIBLE);
                /*textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(HomeActivity.this, UserDetailActivity.class);
                        startActivity(intent);

                    }
                });*/
            }

            TextView headerEmail = ((TextView) headerView.findViewById(R.id.userEmail));
            headerEmail.setText(userBean.getEmail());
            /*headerEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(HomeActivity.this, UserDetailActivity.class);
                    startActivity(intent);

                }
            });*/
            TextView headerUserName = ((TextView) headerView.findViewById(R.id.userName));
            headerUserName.setText(userBean.getName());

            teacherAccount = ((TextView) headerView.findViewById(R.id.teacherAccount));
            /*headerUserName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(HomeActivity.this, UserDetailActivity.class);
                    startActivity(intent);

                }
            });*/


        }

    }


    @Override
    public void onBackPressed() {
        bitmap = null;
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (!drawer.isDrawerOpen(GravityCompat.START)) {

            //this line is used to show the dialog to exit

           /* AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            //alertDialog.setTitle("Alert");
            alertDialog.setMessage("Are you sure to exit?");
            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                        super.onBackPressed();
                   }
            });
            alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alertDialog.show();*/

            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Press BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity input AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_showAllRecord) {
            boolCourseSem = false;
            intShowRecordTitle = 0;
            Intent intent = new Intent(HomeActivity.this, ShowAllRecordsActivity.class);
            intent.putExtra("key", userBean);
            startActivity(intent);
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        LinearLayout linearLayout;
        final Menu menu = navigationView.getMenu();
        int id = item.getItemId();
        Handler handler = new Handler();
        if (id == R.id.notice_board) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(HomeActivity.this, NoticeBoardActivity.class);
                    startActivity(intent);
                }
            }, 500);

        } else if (id == R.id.bundelkhand_university) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkBuFb = 1;
                    Intent intent = new Intent(HomeActivity.this, WebViewActivity.class);
                    startActivity(intent);

                }
            }, 500);

        } else if (id == R.id.nav_result) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkBuFb = 3;
                    Intent intent = new Intent(HomeActivity.this, WebViewActivity.class);
                    startActivity(intent);


                }
            }, 500);

        } else if (id == R.id.live_chat) {


            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(HomeActivity.this, HipmobCustomMessageLayoutLiveChatActivity.class);
                    startActivity(intent);
                }
            }, 500);


        } else if (id == R.id.logout) {

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    DatabaseHandler databaseHandler = new DatabaseHandler(HomeActivity.this);
                    databaseHandler.deleteTable();
                    Intent intent = new Intent(HomeActivity.this, SignInActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 500);

            //Toast.makeText(getBaseContext(), "Logged Out Successfully", Toast.LENGTH_LONG).show();
        } else if (id == R.id.contact_us) {

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(HomeActivity.this, ContactUs.class);
                    startActivity(intent);
                }
            }, 500);

        } else if (id == R.id.share) {

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {


                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Sent from m-Billing app");
                    shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Share the m-Billing App with your friends to spread goodness-Get the app-https://play.google.com/store/apps/details?id=com.lkintechnology.mBilling");
                    startActivity(Intent.createChooser(shareIntent, "Share via"));
                }
            }, 500);

        } else if (id == R.id.video_lectures) {

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(HomeActivity.this, VideoActivity.class);
                    startActivity(intent);
                }
            }, 500);

        } else if (id == R.id.stack_overflow) {

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    HomeActivity.checkBuFb = 4;
                    Intent intent = new Intent(HomeActivity.this, WebViewActivity.class);
                    startActivity(intent);


                }
            }, 500);


        } else if (id == R.id.show_all) {

            boolean boolCheckSubMenu = !menu.findItem(R.id.nav_mca).isVisible();
            if (boolCheckSubMenu) {
                openDrawerSubItemCourse();
            } else {
                closeDrawerSubItemCourse();
            }

        } else if (id == R.id.nav_mca) {
            boolean boolCheckSubMenu = !menu.findItem(R.id.nav_mca_1st).isVisible();
            if (boolCheckSubMenu) {
                openDrawerSubItemMCA();
            } else {
                closeDrawerSubItemMca();
            }
        } else if (id == R.id.nav_bca) {
            boolean boolCheckSubMenu = !menu.findItem(R.id.nav_bca_1st).isVisible();
            if (boolCheckSubMenu) {
                openDrawerSubItemBCA();
            } else {
                closeDrawerSubItemBCA();
            }
        } else if (id == R.id.nav_mca_1st) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    boolCourseSem = true;
                    intShowRecordTitle = 1;
                    Intent intent = new Intent(HomeActivity.this, ShowAllRecordsActivity.class);
                    intent.putExtra("cs", "MCA 1st");
                    startActivity(intent);

                }
            }, 500);

        } else if (id == R.id.nav_mca_2nd) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    boolCourseSem = true;
                    intShowRecordTitle = 2;
                    Intent intent = new Intent(HomeActivity.this, ShowAllRecordsActivity.class);
                    intent.putExtra("cs", "MCA 2nd");
                    startActivity(intent);

                }
            }, 500);


        } else if (id == R.id.nav_mca_3rd) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    boolCourseSem = true;
                    intShowRecordTitle = 3;
                    Intent intent = new Intent(HomeActivity.this, ShowAllRecordsActivity.class);
                    intent.putExtra("cs", "MCA 3rd");
                    startActivity(intent);

                }
            }, 500);


        } else if (id == R.id.nav_mca_4th) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    boolCourseSem = true;
                    intShowRecordTitle = 4;
                    Intent intent = new Intent(HomeActivity.this, ShowAllRecordsActivity.class);
                    intent.putExtra("cs", "MCA 4th");
                    startActivity(intent);
                }
            }, 500);


        } else if (id == R.id.nav_mca_5th) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    boolCourseSem = true;
                    intShowRecordTitle = 5;
                    Intent intent = new Intent(HomeActivity.this, ShowAllRecordsActivity.class);
                    intent.putExtra("cs", "MCA 5th");
                    startActivity(intent);

                }
            }, 500);


        } else if (id == R.id.nav_mca_6th) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    boolCourseSem = true;
                    intShowRecordTitle = 6;
                    Intent intent = new Intent(HomeActivity.this, ShowAllRecordsActivity.class);
                    intent.putExtra("cs", "MCA 6th");
                    startActivity(intent);
                }
            }, 500);


        } else if (id == R.id.nav_bca_1st) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    boolCourseSem = true;
                    intShowRecordTitle = 7;
                    Intent intent = new Intent(HomeActivity.this, ShowAllRecordsActivity.class);
                    intent.putExtra("cs", "BCA 1st");
                    startActivity(intent);

                }
            }, 500);


        } else if (id == R.id.nav_bca_2nd) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    boolCourseSem = true;
                    intShowRecordTitle = 8;
                    Intent intent = new Intent(HomeActivity.this, ShowAllRecordsActivity.class);
                    intent.putExtra("cs", "BCA 2nd");
                    startActivity(intent);

                }
            }, 500);

        } else if (id == R.id.nav_bca_3rd) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    boolCourseSem = true;
                    intShowRecordTitle = 9;
                    Intent intent = new Intent(HomeActivity.this, ShowAllRecordsActivity.class);
                    intent.putExtra("cs", "BCA 3rd");
                    startActivity(intent);
                }
            }, 500);

        } else if (id == R.id.nav_bca_4th) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    boolCourseSem = true;
                    intShowRecordTitle = 10;
                    Intent intent = new Intent(HomeActivity.this, ShowAllRecordsActivity.class);
                    intent.putExtra("cs", "BCA 4th");
                    startActivity(intent);

                }
            }, 500);

        } else if (id == R.id.nav_bca_5th) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    boolCourseSem = true;
                    intShowRecordTitle = 11;
                    Intent intent = new Intent(HomeActivity.this, ShowAllRecordsActivity.class);
                    intent.putExtra("cs", "BCA 5th");
                    startActivity(intent);

                }
            }, 500);

        } else if (id == R.id.nav_bca_6th) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    boolCourseSem = true;
                    intShowRecordTitle = 12;
                    Intent intent = new Intent(HomeActivity.this, ShowAllRecordsActivity.class);
                    intent.putExtra("cs", "BCA 6th");
                    startActivity(intent);

                }
            }, 500);

        } else if (id == R.id.teacher_list) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    boolCourseSem = true;
                    intShowRecordTitle = 13;
                    Intent intent = new Intent(HomeActivity.this, ShowAllRecordsActivity.class);
                    intent.putExtra("cs", "TEACHER IT");
                    startActivity(intent);

                }
            }, 500);


        } else if (id == R.id.nav_all_student) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    boolCourseSem = false;
                    intShowRecordTitle = 0;
                    Intent intent = new Intent(HomeActivity.this, ShowAllRecordsActivity.class);
                    startActivity(intent);
                }
            }, 500);

        } else if (id == R.id.nearest) {

            boolean boolCheckSubMenu = !menu.findItem(R.id.nav_atm).isVisible();
            if (boolCheckSubMenu) {
                openDrawerSubItemNearest();
            } else {
                closeDrawerSubItemNearest();
            }

        } else if (id == R.id.nav_atm) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (Networking.isConnected(HomeActivity.this)) {
                        Intent intent = new Intent(HomeActivity.this, ShowNearest.class);
                        intent.putExtra("input", "atm");
                        startActivity(intent);
                    } else {
                        Toast.makeText(HomeActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                    }
                }
            }, 500);

        } else if (id == R.id.nav_bank) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (Networking.isConnected(HomeActivity.this)) {
                        Intent intent = new Intent(HomeActivity.this, ShowNearest.class);
                        intent.putExtra("input", "bank");
                        startActivity(intent);
                    } else {
                        Toast.makeText(HomeActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                    }
                }
            }, 500);

        } else if (id == R.id.nav_doctor) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (Networking.isConnected(HomeActivity.this)) {
                        Intent intent = new Intent(HomeActivity.this, ShowNearest.class);
                        intent.putExtra("input", "doctor");
                        startActivity(intent);
                    } else {
                        Toast.makeText(HomeActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                    }
                }
            }, 500);

        } else if (id == R.id.nav_hospital) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (Networking.isConnected(HomeActivity.this)) {
                        Intent intent = new Intent(HomeActivity.this, ShowNearest.class);
                        intent.putExtra("input", "hospital");
                        startActivity(intent);
                    } else {
                        Toast.makeText(HomeActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                    }
                }
            }, 500);

        } else if (id == R.id.nav_police) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (Networking.isConnected(HomeActivity.this)) {
                        Intent intent = new Intent(HomeActivity.this, ShowNearest.class);
                        intent.putExtra("input", "police");
                        startActivity(intent);
                    } else {
                        Toast.makeText(HomeActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                    }
                }
            }, 500);

        } else if (id == R.id.nav_restra) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (Networking.isConnected(HomeActivity.this)) {
                        Intent intent = new Intent(HomeActivity.this, ShowNearest.class);
                        intent.putExtra("input", "restaurant");
                        startActivity(intent);
                    } else {
                        Toast.makeText(HomeActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                    }
                }
            }, 500);

        } else if (id == R.id.nav_company) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (Networking.isConnected(HomeActivity.this)) {
                        Intent intent = new Intent(HomeActivity.this, ShowNearest.class);
                        intent.putExtra("input", "company");
                        startActivity(intent);
                    } else {
                        Toast.makeText(HomeActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                    }
                }
            }, 500);

        } else if (id == R.id.map) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(HomeActivity.this, MapsActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 500);

        } else if (id == R.id.account_setting) {
            boolean boolCheckSubMenu = !menu.findItem(R.id.update_account).isVisible();
            if (boolCheckSubMenu) {
                openDrawerSubItemAccountSetting();
            } else {
                closeDrawerSubItemAccountSetting();
            }
        } else if (id == R.id.update_account) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(HomeActivity.this, UserDetailActivity.class);
                    startActivity(intent);
                }
            }, 500);


        } else if (id == R.id.delete_account) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {


                    if (Networking.isConnected(HomeActivity.this)) {

                        AlertDialog.Builder builder1 = new AlertDialog.Builder(HomeActivity.this);
                        builder1.setMessage("Are you sure to delete your account?");
                        builder1.setCancelable(true);

                        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                UserBean userBean = ReferenceWrapper.getRefrenceWrapper(HomeActivity.this).getUserBean();
                                ProgressDialog progressDialog2 = new ProgressDialog(HomeActivity.this);
                                progressDialog2.setMessage("Removing...");
                                progressDialog2.show();
                                databaseReference = FirebaseDatabase.getInstance().getReference(ParameterConstants.USERS);
                                databaseReference.child(ParameterConstants.PROFILE).child(userBean.getMobile()).removeValue();
                                Intent intent = new Intent(HomeActivity.this, SignUpActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                progressDialog2.dismiss();
                                startActivity(intent);
                                finish();
                            }
                        });

                        builder1.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                        AlertDialog alert11 = builder1.create();
                        alert11.show();


                    } else {
                        Toast.makeText(HomeActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                    }


                }
            }, 500);


        } else if (id == R.id.about) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(HomeActivity.this, AboutActivity.class);
                    startActivity(intent);
                }
            }, 500);

        }
        if (id != R.id.nearest && id != R.id.show_all && id != R.id.nav_mca && id != R.id.nav_bca && id != R.id.account_setting) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }

        return true;
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("GPS Not Enabled");
        alertDialog.setMessage("Do you wants to turn On GPS");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, 1);

            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Intent intent = new Intent(getApplicationContext(), FindNearest.class);
                startActivity(intent);
            }
        });
        alertDialog.show();
    }


    public void codeForAutoLogoutAfterFixTimeInterval() {
        /* Timer timer;
    @Override
    protected void onPause() {
        super.onPause();
        Toast.makeText(this, "timer stoped", Toast.LENGTH_SHORT).show();
        if (timer != null) {
            timer.cancel();
            Log.i("Main", "cancel timer");
            timer = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (drawer.isDrawerOpen(GravityCompat.START)){
            Toast.makeText(this, "timer stoped", Toast.LENGTH_SHORT).show();
            timer.cancel();
        }

        Toast.makeText(this, "timer start", Toast.LENGTH_SHORT).show();
        timer = new Timer();
        Log.i("Main", "Invoking logout timer");

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent i = new Intent(HomeActivity.this, SignInActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        }, 10 * 1000);
    }*/
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        hometitle.startAnimation(animBounce);
        UserBean userBean = referenceWrapper.getUserBean();
        if (userBean == null) {
            DatabaseHandler databaseHandler = new DatabaseHandler(HomeActivity.this);
            databaseHandler.getMob();
            hometitle.setText("Welcome " + databaseHandler.getMob());
        } else {

            hometitle.setText("Welcome " + userBean.getName());
        }
    }

    @Override
    protected void onResume() {

        Animation animClick2hike = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.its_free_blink);
        TextView click2hike = (TextView) findViewById(R.id.click2hike);
        click2hike.startAnimation(animClick2hike);

        super.onResume();
        //if (userBean.getCourse().equals("TEACHER")) {
        teacherAccount.setVisibility(View.GONE);
        Animation animBlink;
        animBlink = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.blink);
        teacherAccount.startAnimation(animBlink);

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


        ImageView logo_imageview = (ImageView) findViewById(R.id.logo);
        logo_imageview.startAnimation(animBlink);

        /*}*/
    }

    private void collectPhoneNumbers(Map<String, Object> users) {

        //iterate through each user, ignoring their UID
        if (users == null) {
            //finish();
        } else {
            for (Map.Entry<String, Object> entry : users.entrySet()) {
                //Get user map
                Map map = (Map) entry.getValue();
                Set<String> keyset = map.keySet();
                for (String key : keyset) {

                    if (key.equals("mobile")) {
                        if (map.get(key).toString().equals(userBean.getMobile())) {
                            TextView notificationCount = (TextView) findViewById(R.id.notificationCount);
                            notificationCount.setVisibility(View.INVISIBLE);
                            notificationCount.setText("" + (userBean.getNotificationCount() - userBean.getNotificationCountForSingleEvent()));
                            if (userBean.getNotificationCount() - userBean.getNotificationCountForSingleEvent() > 0) {
                                notificationCount.setVisibility(View.VISIBLE);
                                if (HomeActivity.boolNotification) {
                                    //sendNotification("" + (userBean.getNotificationCount() - userBean.getNotificationCountForSingleEvent()) + " Notice Posted");
                                    boolNotification = false;
                                }


                                return;
                            }

                        }
                    }
                }
            }
        }
    }


    private void sendNotification(String body) {

        Intent intent = new Intent(this, NoticeBoardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0/*Request code*/, intent, PendingIntent.FLAG_ONE_SHOT);
        //Set sound of notification
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notifiBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("B.U. Jhansi")
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(notificationSound)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0 /*ID of notification*/, notifiBuilder.build());
    }
    //close sub_item

    private void closeDrawerSubItemNearest() {
        NavigationView nv = (NavigationView) findViewById(R.id.nav_view);
        final Menu m = nv.getMenu();
        m.findItem(R.id.nav_atm).setVisible(false);
        m.findItem(R.id.nav_bank).setVisible(false);
        m.findItem(R.id.nav_doctor).setVisible(false);
        m.findItem(R.id.nav_hospital).setVisible(false);
        m.findItem(R.id.nav_police).setVisible(false);
        m.findItem(R.id.nav_restra).setVisible(false);
        m.findItem(R.id.nav_company).setVisible(false);

        LinearLayout linearLayout = (LinearLayout) m.findItem(R.id.nearest).getActionView();
        ((ImageView) linearLayout.findViewById(R.id.menu_icon)).setImageResource(R.drawable.d);
        SpannableString spannableString = new SpannableString(m.findItem(R.id.nearest).getTitle());
        m.findItem(R.id.nearest).setIcon(R.drawable.find_nearest_black);
        spannableString.setSpan(new ForegroundColorSpan(Color.BLACK), 0, spannableString.length(), 0);
        m.findItem(R.id.nearest).setTitle(spannableString);
    }

    private void closeDrawerSubItemCourse() {
        NavigationView nv = (NavigationView) findViewById(R.id.nav_view);
        final Menu m = nv.getMenu();
        m.findItem(R.id.nav_mca).setVisible(false);
        m.findItem(R.id.nav_bca).setVisible(false);
        m.findItem(R.id.nav_all_student).setVisible(false);

        //close mca semesters
        m.findItem(R.id.nav_mca_1st).setVisible(false);
        m.findItem(R.id.nav_mca_2nd).setVisible(false);
        m.findItem(R.id.nav_mca_3rd).setVisible(false);
        m.findItem(R.id.nav_mca_4th).setVisible(false);
        m.findItem(R.id.nav_mca_5th).setVisible(false);
        m.findItem(R.id.nav_mca_6th).setVisible(false);

        //close Bca semesters
        m.findItem(R.id.nav_bca_1st).setVisible(false);
        m.findItem(R.id.nav_bca_2nd).setVisible(false);
        m.findItem(R.id.nav_bca_3rd).setVisible(false);
        m.findItem(R.id.nav_bca_4th).setVisible(false);
        m.findItem(R.id.nav_bca_5th).setVisible(false);
        m.findItem(R.id.nav_bca_6th).setVisible(false);
        LinearLayout linearLayout2 = (LinearLayout) m.findItem(R.id.nav_bca).getActionView();
        ((ImageView) linearLayout2.findViewById(R.id.menu_icon)).setImageResource(R.drawable.d);

        LinearLayout linearLayout3 = (LinearLayout) m.findItem(R.id.nav_mca).getActionView();
        ((ImageView) linearLayout3.findViewById(R.id.menu_icon)).setImageResource(R.drawable.d);

        LinearLayout linearLayout = (LinearLayout) m.findItem(R.id.show_all).getActionView();
        ((ImageView) linearLayout.findViewById(R.id.menu_icon)).setImageResource(R.drawable.d);
        SpannableString spannableString = new SpannableString(m.findItem(R.id.show_all).getTitle());
        m.findItem(R.id.show_all).setIcon(R.drawable.userlist_black);
        spannableString.setSpan(new ForegroundColorSpan(Color.BLACK), 0, spannableString.length(), 0);
        m.findItem(R.id.show_all).setTitle(spannableString);
    }

    private void closeDrawerSubItemMca() {
        NavigationView nv = (NavigationView) findViewById(R.id.nav_view);
        final Menu m = nv.getMenu();
        m.findItem(R.id.nav_mca_1st).setVisible(false);
        m.findItem(R.id.nav_mca_2nd).setVisible(false);
        m.findItem(R.id.nav_mca_3rd).setVisible(false);
        m.findItem(R.id.nav_mca_4th).setVisible(false);
        m.findItem(R.id.nav_mca_5th).setVisible(false);
        m.findItem(R.id.nav_mca_6th).setVisible(false);

        LinearLayout linearLayout2 = (LinearLayout) m.findItem(R.id.nav_mca).getActionView();
        ((ImageView) linearLayout2.findViewById(R.id.menu_icon)).setImageResource(R.drawable.db);
        /*SpannableString spannableString = new SpannableString(m.findItem(R.id.nav_mca).getTitle());
        m.findItem(R.id.nav_mca).setIcon(R.drawable.userlist_icon);
        */
    }

    private void closeDrawerSubItemBCA() {
        NavigationView nv = (NavigationView) findViewById(R.id.nav_view);
        final Menu m = nv.getMenu();
        m.findItem(R.id.nav_bca_1st).setVisible(false);
        m.findItem(R.id.nav_bca_2nd).setVisible(false);
        m.findItem(R.id.nav_bca_3rd).setVisible(false);
        m.findItem(R.id.nav_bca_4th).setVisible(false);
        m.findItem(R.id.nav_bca_5th).setVisible(false);
        m.findItem(R.id.nav_bca_6th).setVisible(false);


        LinearLayout linearLayout = (LinearLayout) m.findItem(R.id.nav_bca).getActionView();
        ((ImageView) linearLayout.findViewById(R.id.menu_icon)).setImageResource(R.drawable.db);
    }

    private void closeDrawerSubItemAccountSetting() {
        NavigationView nv = (NavigationView) findViewById(R.id.nav_view);
        final Menu m = nv.getMenu();
        LinearLayout linearLayout = (LinearLayout) m.findItem(R.id.account_setting).getActionView();
        //side arrow down
        ((ImageView) linearLayout.findViewById(R.id.menu_icon)).setImageResource(R.drawable.d);
        //close sub item
        m.findItem(R.id.update_account).setVisible(false);
        m.findItem(R.id.delete_account).setVisible(false);
        //change Account Setting color to black
        SpannableString spannableAccountSetting = new SpannableString(m.findItem(R.id.account_setting).getTitle());
        m.findItem(R.id.account_setting).setIcon(R.drawable.setting_black);
        spannableAccountSetting.setSpan(new ForegroundColorSpan(Color.BLACK), 0, spannableAccountSetting.length(), 0);
        m.findItem(R.id.account_setting).setTitle(spannableAccountSetting);
    }

    //open sub_item

    private void openDrawerSubItemNearest() {


        LocationManager locationManager;
        boolean checkGPS;
        locationManager = (LocationManager) this
                .getSystemService(LOCATION_SERVICE);
        checkGPS = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        // new TrackLocation(this).getMyLocation();
        if (!checkGPS) {
            //showSettingsAlert();
            Toast.makeText(this, "Please turn on your gps", Toast.LENGTH_SHORT).show();
        }


        NavigationView nv = (NavigationView) findViewById(R.id.nav_view);
        final Menu m = nv.getMenu();
        m.findItem(R.id.nav_atm).setVisible(true);
        m.findItem(R.id.nav_bank).setVisible(true);
        m.findItem(R.id.nav_doctor).setVisible(true);
        m.findItem(R.id.nav_hospital).setVisible(true);
        m.findItem(R.id.nav_police).setVisible(true);
        m.findItem(R.id.nav_restra).setVisible(true);
        m.findItem(R.id.nav_company).setVisible(true);

        closeDrawerSubItemCourse();
        closeDrawerSubItemAccountSetting();


        LinearLayout linearLayout = (LinearLayout) m.findItem(R.id.nearest).getActionView();
        ((ImageView) linearLayout.findViewById(R.id.menu_icon)).setImageResource(R.drawable.u);


        SpannableString spannableString = new SpannableString(m.findItem(R.id.nearest).getTitle());
        SpannableString spannableAtm = new SpannableString(m.findItem(R.id.nav_atm).getTitle());
        SpannableString spannableBank = new SpannableString(m.findItem(R.id.nav_bank).getTitle());
        SpannableString spannableDoctor = new SpannableString(m.findItem(R.id.nav_doctor).getTitle());
        SpannableString spannableHospital = new SpannableString(m.findItem(R.id.nav_hospital).getTitle());
        SpannableString spannablePolice = new SpannableString(m.findItem(R.id.nav_police).getTitle());
        SpannableString spannableRestra = new SpannableString(m.findItem(R.id.nav_restra).getTitle());
        SpannableString spannableCompany = new SpannableString(m.findItem(R.id.nav_company).getTitle());

        m.findItem(R.id.nearest).setIcon(R.drawable.find_nearest_blue);

        spannableString.setSpan(new ForegroundColorSpan(Color.BLUE), 0, spannableString.length(), 0);
        m.findItem(R.id.nearest).setTitle(spannableString);

        spannableAtm.setSpan(new ForegroundColorSpan(Color.BLUE), 0, spannableAtm.length(), 0);
        m.findItem(R.id.nav_atm).setTitle(spannableAtm);
        spannableBank.setSpan(new ForegroundColorSpan(Color.BLUE), 0, spannableBank.length(), 0);
        m.findItem(R.id.nav_bank).setTitle(spannableBank);
        spannableDoctor.setSpan(new ForegroundColorSpan(Color.BLUE), 0, spannableDoctor.length(), 0);
        m.findItem(R.id.nav_doctor).setTitle(spannableDoctor);
        spannableHospital.setSpan(new ForegroundColorSpan(Color.BLUE), 0, spannableHospital.length(), 0);
        m.findItem(R.id.nav_hospital).setTitle(spannableHospital);
        spannablePolice.setSpan(new ForegroundColorSpan(Color.BLUE), 0, spannablePolice.length(), 0);
        m.findItem(R.id.nav_police).setTitle(spannablePolice);
        spannableRestra.setSpan(new ForegroundColorSpan(Color.BLUE), 0, spannableRestra.length(), 0);
        m.findItem(R.id.nav_restra).setTitle(spannableRestra);
        spannableCompany.setSpan(new ForegroundColorSpan(Color.BLUE), 0, spannableCompany.length(), 0);
        m.findItem(R.id.nav_company).setTitle(spannableCompany);

    }

    private void openDrawerSubItemCourse() {
        NavigationView nv = (NavigationView) findViewById(R.id.nav_view);
        final Menu m = nv.getMenu();
        m.findItem(R.id.nav_mca).setVisible(true);
        m.findItem(R.id.nav_bca).setVisible(true);
        m.findItem(R.id.nav_all_student).setVisible(true);

        closeDrawerSubItemNearest();
        closeDrawerSubItemAccountSetting();

        LinearLayout linearLayout = (LinearLayout) m.findItem(R.id.show_all).getActionView();
        ((ImageView) linearLayout.findViewById(R.id.menu_icon)).setImageResource(R.drawable.u);

        LinearLayout linearLayoutMCA = (LinearLayout) m.findItem(R.id.nav_mca).getActionView();
        ((ImageView) linearLayoutMCA.findViewById(R.id.menu_icon)).setImageResource(R.drawable.db);

        LinearLayout linearLayoutBCA = (LinearLayout) m.findItem(R.id.nav_bca).getActionView();
        ((ImageView) linearLayoutBCA.findViewById(R.id.menu_icon)).setImageResource(R.drawable.db);

        SpannableString spannableString = new SpannableString(m.findItem(R.id.show_all).getTitle());
        SpannableString spannableMca = new SpannableString(m.findItem(R.id.nav_mca).getTitle());
        SpannableString spannableBca = new SpannableString(m.findItem(R.id.nav_bca).getTitle());
        SpannableString spannableBsc = new SpannableString(m.findItem(R.id.nav_all_student).getTitle());

        m.findItem(R.id.show_all).setIcon(R.drawable.userlist_blue);
        spannableString.setSpan(new ForegroundColorSpan(Color.BLUE), 0, spannableString.length(), 0);
        m.findItem(R.id.show_all).setTitle(spannableString);
        spannableMca.setSpan(new ForegroundColorSpan(Color.BLUE), 0, spannableMca.length(), 0);
        m.findItem(R.id.nav_mca).setTitle(spannableMca);
        spannableBca.setSpan(new ForegroundColorSpan(Color.BLUE), 0, spannableBca.length(), 0);
        m.findItem(R.id.nav_bca).setTitle(spannableBca);
        spannableBsc.setSpan(new ForegroundColorSpan(Color.BLUE), 0, spannableBsc.length(), 0);
        m.findItem(R.id.nav_all_student).setTitle(spannableBsc);

    }

    private void openDrawerSubItemMCA() {
        NavigationView nv = (NavigationView) findViewById(R.id.nav_view);
        final Menu m = nv.getMenu();
        m.findItem(R.id.nav_mca_1st).setVisible(true);
        m.findItem(R.id.nav_mca_2nd).setVisible(true);
        m.findItem(R.id.nav_mca_3rd).setVisible(true);
        m.findItem(R.id.nav_mca_4th).setVisible(true);
        m.findItem(R.id.nav_mca_5th).setVisible(true);
        m.findItem(R.id.nav_mca_6th).setVisible(true);

        //close bca semester

        m.findItem(R.id.nav_bca_1st).setVisible(false);
        m.findItem(R.id.nav_bca_2nd).setVisible(false);
        m.findItem(R.id.nav_bca_3rd).setVisible(false);
        m.findItem(R.id.nav_bca_4th).setVisible(false);
        m.findItem(R.id.nav_bca_5th).setVisible(false);
        m.findItem(R.id.nav_bca_6th).setVisible(false);
        LinearLayout linearLayout2 = (LinearLayout) m.findItem(R.id.nav_bca).getActionView();
        ((ImageView) linearLayout2.findViewById(R.id.menu_icon)).setImageResource(R.drawable.db);

        LinearLayout linearLayout = (LinearLayout) m.findItem(R.id.nav_mca).getActionView();
        ((ImageView) linearLayout.findViewById(R.id.menu_icon)).setImageResource(R.drawable.u);

        SpannableString spannableString = new SpannableString(m.findItem(R.id.nav_mca).getTitle());
        SpannableString spannableMca1 = new SpannableString(m.findItem(R.id.nav_mca_1st).getTitle());
        SpannableString spannableMca2 = new SpannableString(m.findItem(R.id.nav_mca_2nd).getTitle());
        SpannableString spannableMca3 = new SpannableString(m.findItem(R.id.nav_mca_3rd).getTitle());
        SpannableString spannableMca4 = new SpannableString(m.findItem(R.id.nav_mca_4th).getTitle());
        SpannableString spannableMca5 = new SpannableString(m.findItem(R.id.nav_mca_5th).getTitle());
        SpannableString spannableMca6 = new SpannableString(m.findItem(R.id.nav_mca_6th).getTitle());


        //m.findItem(R.id.nav_mca).setIcon(R.drawable.userlist_icon);
        spannableString.setSpan(new ForegroundColorSpan(Color.BLUE), 0, spannableString.length(), 0);
        m.findItem(R.id.nav_mca).setTitle(spannableString);

        spannableMca1.setSpan(new ForegroundColorSpan(Color.BLUE), 0, spannableMca1.length(), 0);
        m.findItem(R.id.nav_mca_1st).setTitle(spannableMca1);

        spannableMca2.setSpan(new ForegroundColorSpan(Color.BLUE), 0, spannableMca2.length(), 0);
        m.findItem(R.id.nav_mca_2nd).setTitle(spannableMca2);

        spannableMca3.setSpan(new ForegroundColorSpan(Color.BLUE), 0, spannableMca3.length(), 0);
        m.findItem(R.id.nav_mca_3rd).setTitle(spannableMca3);

        spannableMca4.setSpan(new ForegroundColorSpan(Color.BLUE), 0, spannableMca4.length(), 0);
        m.findItem(R.id.nav_mca_4th).setTitle(spannableMca4);

        spannableMca5.setSpan(new ForegroundColorSpan(Color.BLUE), 0, spannableMca5.length(), 0);
        m.findItem(R.id.nav_mca_5th).setTitle(spannableMca5);

        spannableMca6.setSpan(new ForegroundColorSpan(Color.BLUE), 0, spannableMca6.length(), 0);
        m.findItem(R.id.nav_mca_6th).setTitle(spannableMca6);


    }

    private void openDrawerSubItemBCA() {
        NavigationView nv = (NavigationView) findViewById(R.id.nav_view);
        final Menu m = nv.getMenu();
        m.findItem(R.id.nav_bca_1st).setVisible(true);
        m.findItem(R.id.nav_bca_2nd).setVisible(true);
        m.findItem(R.id.nav_bca_3rd).setVisible(true);
        m.findItem(R.id.nav_bca_4th).setVisible(true);
        m.findItem(R.id.nav_bca_5th).setVisible(true);
        m.findItem(R.id.nav_bca_6th).setVisible(true);

        //close mca semester

        m.findItem(R.id.nav_mca_1st).setVisible(false);
        m.findItem(R.id.nav_mca_2nd).setVisible(false);
        m.findItem(R.id.nav_mca_3rd).setVisible(false);
        m.findItem(R.id.nav_mca_4th).setVisible(false);
        m.findItem(R.id.nav_mca_5th).setVisible(false);
        m.findItem(R.id.nav_mca_6th).setVisible(false);
        LinearLayout linearLayout = (LinearLayout) m.findItem(R.id.nav_mca).getActionView();
        ((ImageView) linearLayout.findViewById(R.id.menu_icon)).setImageResource(R.drawable.db);

        LinearLayout linearLayout2 = (LinearLayout) m.findItem(R.id.nav_bca).getActionView();
        ((ImageView) linearLayout2.findViewById(R.id.menu_icon)).setImageResource(R.drawable.u);
        SpannableString spannableBca = new SpannableString(m.findItem(R.id.nav_bca).getTitle());
        SpannableString spannableBca1 = new SpannableString(m.findItem(R.id.nav_bca_1st).getTitle());
        SpannableString spannableBca2 = new SpannableString(m.findItem(R.id.nav_bca_2nd).getTitle());
        SpannableString spannableBca3 = new SpannableString(m.findItem(R.id.nav_bca_3rd).getTitle());
        SpannableString spannableBca4 = new SpannableString(m.findItem(R.id.nav_bca_4th).getTitle());
        SpannableString spannableBca5 = new SpannableString(m.findItem(R.id.nav_bca_5th).getTitle());
        SpannableString spannableBca6 = new SpannableString(m.findItem(R.id.nav_bca_6th).getTitle());


        //m.findItem(R.id.nav_mca).setIcon(R.drawable.userlist_icon);
        spannableBca.setSpan(new ForegroundColorSpan(Color.BLUE), 0, spannableBca.length(), 0);
        m.findItem(R.id.nav_bca).setTitle(spannableBca);

        spannableBca1.setSpan(new ForegroundColorSpan(Color.BLUE), 0, spannableBca1.length(), 0);
        m.findItem(R.id.nav_bca_1st).setTitle(spannableBca1);

        spannableBca2.setSpan(new ForegroundColorSpan(Color.BLUE), 0, spannableBca2.length(), 0);
        m.findItem(R.id.nav_bca_2nd).setTitle(spannableBca2);

        spannableBca3.setSpan(new ForegroundColorSpan(Color.BLUE), 0, spannableBca3.length(), 0);
        m.findItem(R.id.nav_bca_3rd).setTitle(spannableBca3);

        spannableBca4.setSpan(new ForegroundColorSpan(Color.BLUE), 0, spannableBca4.length(), 0);
        m.findItem(R.id.nav_bca_4th).setTitle(spannableBca4);

        spannableBca5.setSpan(new ForegroundColorSpan(Color.BLUE), 0, spannableBca5.length(), 0);
        m.findItem(R.id.nav_bca_5th).setTitle(spannableBca5);

        spannableBca6.setSpan(new ForegroundColorSpan(Color.BLUE), 0, spannableBca6.length(), 0);
        m.findItem(R.id.nav_bca_6th).setTitle(spannableBca6);


    }

    private void openDrawerSubItemAccountSetting() {
        NavigationView nv = (NavigationView) findViewById(R.id.nav_view);
        final Menu m = nv.getMenu();
        m.findItem(R.id.update_account).setVisible(true);
        m.findItem(R.id.delete_account).setVisible(true);

        closeDrawerSubItemCourse();
        closeDrawerSubItemNearest();

        LinearLayout linearLayout = (LinearLayout) m.findItem(R.id.account_setting).getActionView();
        ((ImageView) linearLayout.findViewById(R.id.menu_icon)).setImageResource(R.drawable.u);

        SpannableString spannableAccount = new SpannableString(m.findItem(R.id.account_setting).getTitle());
        SpannableString spannableUpdate = new SpannableString(m.findItem(R.id.update_account).getTitle());
        SpannableString spannableDeleteAccount = new SpannableString(m.findItem(R.id.delete_account).getTitle());

        m.findItem(R.id.account_setting).setIcon(R.drawable.setting_blue);
        spannableAccount.setSpan(new ForegroundColorSpan(Color.BLUE), 0, spannableAccount.length(), 0);
        m.findItem(R.id.account_setting).setTitle(spannableAccount);
        spannableUpdate.setSpan(new ForegroundColorSpan(Color.BLUE), 0, spannableUpdate.length(), 0);
        m.findItem(R.id.update_account).setTitle(spannableUpdate);
        spannableDeleteAccount.setSpan(new ForegroundColorSpan(Color.BLUE), 0, spannableDeleteAccount.length(), 0);
        m.findItem(R.id.delete_account).setTitle(spannableDeleteAccount);

    }


    // code for MapPlaceActivity============================================>

  /*  private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }*/

    private boolean CheckGooglePlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        0).show();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("onLocationChanged", "entered");
        pDialog.dismiss();
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);
        HomeActivity.lats = String.valueOf(latitude);
        HomeActivity.lngs = String.valueOf(longitude);
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinateLayout);
        Snackbar.make(coordinatorLayout, "Your Location is: " + lats + "," + lngs, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();


        //Toast.makeText(this, ""+lats+","+lngs, Toast.LENGTH_SHORT).show();
        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
        Log.d("onLocationChanged", String.format("latitude:%.3f longitude:%.3f", latitude, longitude));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            Log.d("onLocationChanged", "Removing Location Updates");
        }
        Log.d("onLocationChanged", "Exit");
        // hidePDialog();
        handleMosqueRequest("mosque");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }
    //===MapPlaceActivity==================<!


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                onSelecFromGallary(data);
            }
        } else {
            bitmap = null;
        }
    }

    private void onSelecFromGallary(Intent data) {
      /*  Uri selectedImageUri = data.getData();
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
        bitmap = bm;
        enterImageNotice.setImageBitmap(bitmap);*/
        checkCode(data);
    }

    void checkCode(Intent data) {
        Uri uri = data.getData();
        try {
            Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            bitmap = bm;
            enterImageNotice.setImageBitmap(bm);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleMosqueRequest(String type) {

        Call<GetRequest> call = ApiClient.getApiInterface().getNearbyPlaces(type, HomeActivity.lats + "," + HomeActivity.lngs, PROXIMITY_RADIUS);
        call.enqueue(new Callback<GetRequest>() {
            @Override
            public void onResponse(Call<GetRequest> call, Response<GetRequest> response) {
                try {
                    HomeActivity.response = response;
                } catch (Exception e) {
                    Log.d("onResponse", "There is an error");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<GetRequest> call, Throwable t) {

            }

        });
    }
}

