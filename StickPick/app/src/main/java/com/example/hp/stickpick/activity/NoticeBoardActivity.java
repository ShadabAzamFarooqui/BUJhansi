package com.example.hp.stickpick.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.stickpick.R;
import com.example.hp.stickpick.bean.NoticeBean;
import com.example.hp.stickpick.bean.ReferenceWrapper;
import com.example.hp.stickpick.bean.UserBean;
import com.example.hp.stickpick.utils.Conversion;
import com.example.hp.stickpick.utils.Networking;
import com.example.hp.stickpick.utils.ParameterConstants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NoticeBoardActivity extends AppCompatActivity implements View.OnTouchListener {

    private LinearLayout parentLayout;
    ProgressDialog progressDialog;


    TextView nameTV;
    TextView dateTV;
    TextView noticeTV;
    ImageView noticeImg;
    LinearLayout mainLayout;
    View convertView;
    List<NoticeBean> listOfNoticeBean;
    DatabaseReference databaseReference;
    ValueEventListener secondValueListener;
    ValueEventListener firstValueListener;
    Bitmap bitmap;
    ImageView enterImageNotice;
    NoticeBean noticeBean;
    public int position;
    //public static int notificationCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_board);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setTitle("Notice Board");
        //collapsingToolbarLayout.setBackgroundDrawable(new ColorDrawable(Color.RED));
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.parseColor("#ffffff"));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    /*Intent intent = new Intent(NoticeBoardActivity.this, NoticeEnterActivity.class);
                    startActivity(intent);
                    finish();*/
                noticeBean = new NoticeBean();
                final Dialog openDialog = new Dialog(NoticeBoardActivity.this);
                openDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                openDialog.setContentView(R.layout.enter_notice_dailog);
                openDialog.setCanceledOnTouchOutside(false);

                final EditText notice = (EditText) openDialog.findViewById(R.id.notice);
                final Button save = (Button) openDialog.findViewById(R.id.save);
                enterImageNotice = (ImageView) openDialog.findViewById(R.id.enterImageNotice);
                final long id;
                final ReferenceWrapper referenceWrapper;
                final ProgressDialog progressDialog;
                referenceWrapper = ReferenceWrapper.getRefrenceWrapper(NoticeBoardActivity.this);
                progressDialog = new ProgressDialog(NoticeBoardActivity.this);
                progressDialog.setMessage("Saving notice.. Please wait..");
                progressDialog.setCancelable(false);
                Date today = new Date();
                id = today.getTime();
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
                        //save.setClickable(false);
                        Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.fade_in);
                        save.startAnimation(animFadeIn);
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
                        if (userBean != null) {
                            String name = userBean.getName();
                            //noticeBean = new NoticeBean();
                            //noticeBean.setImage("");
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
                                if (Networking.isConnected(NoticeBoardActivity.this)) {
                                    if (userBean.getCourse().equals("TEACHER")) {
                                        progressDialog.show();
                                        databaseReference.child(id + "").setValue(noticeBean, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                                if (databaseError == null) {
                                                    notice.setText("");
                                                    progressDialog.dismiss();
                                                    Toast toast = Toast.makeText(getApplicationContext(),
                                                            "Notice saved", Toast.LENGTH_SHORT);
                                                    toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                                    toast.show();
                                                    finish();
                                                    Intent intent = new Intent(NoticeBoardActivity.this, NoticeBoardActivity.class);
                                                    startActivity(intent);
                                                    openDialog.dismiss();

                                                } else {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(getBaseContext(), "Error Occurred.. Please check your internet connection", Toast.LENGTH_LONG).show();
                                                }
                                            }

                                        });
                                    } else {
                                        android.support.v7.app.AlertDialog.Builder dialogBuilder = new android.support.v7.app.AlertDialog.Builder(NoticeBoardActivity.this);
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
                    }
                });
                bitmap=null;
                openDialog.show();
            }
        });


        progressDialog = new ProgressDialog(NoticeBoardActivity.this);
        progressDialog.setMessage("Loading...");

        parentLayout = (LinearLayout) findViewById(R.id.notice_board);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference(ParameterConstants.NOTICE);
        progressDialog.show();
        progressDialog.setCancelable(false);
        listOfNoticeBean = new ArrayList<>();


        firstValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listOfNoticeBean.clear();
                if (dataSnapshot.getValue() != null) {
                    Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                    Iterator<DataSnapshot> iterator = snapshotIterator.iterator();

                    while (iterator.hasNext()) {
                        DataSnapshot snapshot = iterator.next();
                        final NoticeBean noticeBean = (NoticeBean) snapshot.getValue(NoticeBean.class);
                        listOfNoticeBean.add(noticeBean);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        secondValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //notificationCount = 0;
                //have to call removerView() to remove the previous view;
                convertView = getLayoutInflater().inflate(R.layout.notice_item_layout, null);
                nameTV = ((TextView) convertView.findViewById(R.id.name));
                dateTV = ((TextView) convertView.findViewById(R.id.date));
                noticeTV = ((TextView) convertView.findViewById(R.id.notice));
                noticeImg = (ImageView) convertView.findViewById(R.id.noticeImg);
                ((ViewGroup) parentLayout).removeAllViews();

                UserBean userBean = ReferenceWrapper.getRefrenceWrapper(NoticeBoardActivity.this).getUserBean();
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(ParameterConstants.USERS);
                dbRef.child(ParameterConstants.PROFILE).child(userBean.getMobile()).setValue(userBean);
                if (dataSnapshot.getValue() != null) {
                    Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                    Collections.reverse(listOfNoticeBean);
                    for (int i = 0; i < listOfNoticeBean.size(); i++) {
                        position=i;
                        convertView = getLayoutInflater().inflate(R.layout.notice_item_layout, null);
                        nameTV = ((TextView) convertView.findViewById(R.id.name));
                        mainLayout = ((LinearLayout) convertView.findViewById(R.id.mainLayout));
                        dateTV = ((TextView) convertView.findViewById(R.id.date));
                        noticeTV = ((TextView) convertView.findViewById(R.id.notice));
                        noticeImg = (ImageView) convertView.findViewById(R.id.noticeImg);

                        nameTV.setText(listOfNoticeBean.get(i).getUser());
                        dateTV.setText(listOfNoticeBean.get(i).getTime());
                        noticeTV.setText(listOfNoticeBean.get(i).getNoticeText());
                        if (listOfNoticeBean.get(i).getImage().equals("")) {
                            noticeImg.setVisibility(View.GONE);
                        } else {
                            noticeImg.setVisibility(View.VISIBLE);
                            noticeImg.setImageBitmap(Conversion.BitMapfromString(listOfNoticeBean.get(i).getImage()));
                            noticeImg.setOnTouchListener(NoticeBoardActivity.this);
                        }
                        ((ViewGroup) parentLayout).addView(convertView);
                    }
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        databaseReference.addValueEventListener(firstValueListener);
        databaseReference.addValueEventListener(secondValueListener);
        // single event notification is being getting over here to subtract it from value event listener
        // in order to get exact notice number.
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int iteratorCount = (int) dataSnapshot.getChildrenCount();
                UserBean userBean = ReferenceWrapper.getRefrenceWrapper(NoticeBoardActivity.this).getUserBean();
                userBean.setNotificationCountForSingleEvent(iteratorCount);
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(ParameterConstants.USERS);
                dbRef.child(ParameterConstants.PROFILE).child(userBean.getMobile()).setValue(userBean);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.notice, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity input AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.action_clearNotice) {

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Are you sure to erase the notice board?");
            alertDialog.setMessage("Please think before doing...");
            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    UserBean userBean = ReferenceWrapper.getRefrenceWrapper(NoticeBoardActivity.this).getUserBean();
                    if (userBean.getCourse().equals("TEACHER")) {
                        ProgressDialog progressDialog = new ProgressDialog(NoticeBoardActivity.this);
                        progressDialog.show();
                        clearNoticeBoardContent();
                        progressDialog.dismiss();
                        finish();
                        Toast.makeText(NoticeBoardActivity.this, "Notice Board hase been cleared", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(NoticeBoardActivity.this, NoticeBoardActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                    } else {
                        android.support.v7.app.AlertDialog.Builder dialogBuilder = new android.support.v7.app.AlertDialog.Builder(NoticeBoardActivity.this);
                        dialogBuilder.setMessage("Only teacher can clear the notice");
                        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        });

                        android.support.v7.app.AlertDialog b = dialogBuilder.create();
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

        return super.onOptionsItemSelected(item);
    }

    public void clearNoticeBoardContent() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference(ParameterConstants.NOTICE);
        databaseReference.setValue(null);
        UserBean userBean = ReferenceWrapper.getRefrenceWrapper(NoticeBoardActivity.this).getUserBean();
        userBean.setNotificationCount(0);
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(ParameterConstants.USERS);
        dbRef.child(ParameterConstants.PROFILE).child(userBean.getMobile()).setValue(userBean);
        getAllRecord();
    }

    void getAllRecord() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(ParameterConstants.USERS).child(ParameterConstants.PROFILE);
        ref.addListenerForSingleValueEvent(
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
        for (Map.Entry<String, Object> entry : users.entrySet()) {
            //Get user map
            final Map map = (Map) entry.getValue();
            Set<String> keyset = map.keySet();
            for (final String key : keyset) {
                if (key.equals("mobile")) {
                    FirebaseDatabase.getInstance().getReference(ParameterConstants.USERS).child(ParameterConstants.PROFILE).child(map.get(key).toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            UserBean userBean = dataSnapshot.getValue(UserBean.class);
                            if (userBean != null) {
                                userBean.setNotificationCountForSingleEvent(0);
                                FirebaseDatabase.getInstance().getReference(ParameterConstants.USERS).child(ParameterConstants.PROFILE).child(map.get(key).toString()).setValue(userBean);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_SHORT).show();

                        }
                    });
                }

            }
        }
        progressDialog.dismiss();
    }


    @Override
    protected void onResume() {
        super.onResume();
        HomeActivity.boolNotification = false;
        //MyFirebaseMessagingService.boolNoticeNotification=false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        databaseReference.removeEventListener(firstValueListener);
        databaseReference.removeEventListener(secondValueListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        databaseReference.removeEventListener(secondValueListener);
        databaseReference.removeEventListener(firstValueListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                onSelectFromGalleryResult(data);
            }
        }else {
            bitmap=null;
        }
    }

    private void onSelectFromGalleryResult(Intent data) {
       /* Uri selectedImageUri = data.getData();
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
    void checkCode(Intent data){
        Uri uri = data.getData();
        try {
            Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            bitmap = bm;
            enterImageNotice.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ImageView view = (ImageView) v;
        view.setScaleType(ImageView.ScaleType.MATRIX);
        float scale;

        dumpEvent(event);
        // Handle touch events here...

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:   // first finger down only
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                Log.d(TAG, "mode=DRAG"); // write to LogCat
                mode = DRAG;
                break;

            case MotionEvent.ACTION_UP: // first finger lifted

            case MotionEvent.ACTION_POINTER_UP: // second finger lifted

                mode = NONE;
                Log.d(TAG, "mode=NONE");
                break;

            case MotionEvent.ACTION_POINTER_DOWN: // first and second finger down

                oldDist = spacing(event);
                Log.d(TAG, "oldDist=" + oldDist);
                if (oldDist > 5f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                    Log.d(TAG, "mode=ZOOM");
                }
                break;

            case MotionEvent.ACTION_MOVE:

                if (mode == DRAG) {
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x, event.getY() - start.y); // create the transformation in the matrix  of points
                } else if (mode == ZOOM) {
                    // pinch zooming
                    float newDist = spacing(event);
                    Log.d(TAG, "newDist=" + newDist);
                    if (newDist > 5f) {
                        matrix.set(savedMatrix);
                        scale = newDist / oldDist; // setting the scaling of the
                        // matrix...if scale > 1 means
                        // zoom in...if scale < 1 means
                        // zoom out
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;
        }

        view.setImageMatrix(matrix); // display the transformation on screen

        return true; // indicate event was handled
    }

    /*
     * --------------------------------------------------------------------------
     * Method: spacing Parameters: MotionEvent Returns: float Description:
     * checks the spacing between the two fingers on touch
     * ----------------------------------------------------
     */

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /*
     * --------------------------------------------------------------------------
     * Method: midPoint Parameters: PointF object, MotionEvent Returns: void
     * Description: calculates the midpoint between the two fingers
     * ------------------------------------------------------------
     */

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    /**
     * Show an event in the LogCat view, for debugging
     */
    private void dumpEvent(MotionEvent event) {
        String names[] = {"DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE", "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?"};
        StringBuilder sb = new StringBuilder();
        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        sb.append("event ACTION_").append(names[actionCode]);

        if (actionCode == MotionEvent.ACTION_POINTER_DOWN || actionCode == MotionEvent.ACTION_POINTER_UP) {
            sb.append("(pid ").append(action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
            sb.append(")");
        }

        sb.append("[");
        for (int i = 0; i < event.getPointerCount(); i++) {
            sb.append("#").append(i);
            sb.append("(pid ").append(event.getPointerId(i));
            sb.append(")=").append((int) event.getX(i));
            sb.append(",").append((int) event.getY(i));
            if (i + 1 < event.getPointerCount())
                sb.append(";");
        }

        sb.append("]");
        Log.d("Touch Events ---------", sb.toString());
    }

    private static final String TAG = "Touch";
    @SuppressWarnings("unused")
    private static final float MIN_ZOOM = 1f, MAX_ZOOM = 1f;

    // These matrices will be used to scale points of the image
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    // The 3 states (events) which the user is trying to perform
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    // these PointF objects are used to record the point(s) the user is touching
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;


    class MyAdapter extends BaseAdapter {

        Context context;
        List<NoticeBean> list;

        public MyAdapter(Context context, List<NoticeBean> list) {
            this.context = context;
            this.list = list;
        }


        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int i, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.notice_item_layout, null);
            nameTV = ((TextView) convertView.findViewById(R.id.name));
            dateTV = ((TextView) convertView.findViewById(R.id.date));
            noticeTV = ((TextView) convertView.findViewById(R.id.notice));
            noticeImg = (ImageView) convertView.findViewById(R.id.noticeImg);

            nameTV.setText(list.get(i).getUser());
            dateTV.setText(list.get(i).getTime());
            noticeTV.setText(list.get(i).getNoticeText());
            if (list.get(i).getImage().equals("")) {
                noticeImg.setVisibility(View.GONE);
            } else {
                noticeImg.setVisibility(View.VISIBLE);
                noticeImg.setImageBitmap(Conversion.BitMapfromString(list.get(i).getImage()));

                noticeImg.setOnTouchListener(NoticeBoardActivity.this);
            }

            return convertView;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        bitmap=null;
    }
}
