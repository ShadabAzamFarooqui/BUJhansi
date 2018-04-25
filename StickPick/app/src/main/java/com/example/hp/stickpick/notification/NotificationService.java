package com.example.hp.stickpick.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.example.hp.stickpick.R;
import com.example.hp.stickpick.activity.HomeActivity;
import com.example.hp.stickpick.activity.NoticeBoardActivity;
import com.example.hp.stickpick.activity.SplashActivity;
import com.example.hp.stickpick.bean.ReferenceWrapper;
import com.example.hp.stickpick.utils.ParameterConstants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class NotificationService extends Service {

    NotificationDatabase notificationDatabase;

    public NotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        notificationDatabase = new NotificationDatabase(this);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(ParameterConstants.NOTICE);
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

        return START_STICKY;
    }

    private void collectPhoneNumbers(Map<String, Object> users) {

        String events = new NotificationDatabase(getApplicationContext()).getEvents();
        String[] eventsAr = events.split(",");
        if (Integer.valueOf(eventsAr[1]) - Integer.valueOf(eventsAr[0]) > 0) {
            try {
                if (ReferenceWrapper.getRefrenceWrapper(getApplicationContext()).getUserBean().getMobile().equals(notificationDatabase.getMob())){

                }
            }catch (NullPointerException e){

            }
            sendNotification("" + (Integer.valueOf(eventsAr[1]) - Integer.valueOf(eventsAr[0])) + " Notice Posted");
        } else {
            Toast.makeText(this, "no new notice", Toast.LENGTH_SHORT).show();
//            sendNotification("notice cleared");
        }
    }

    private void sendNotification(String body) {
        Intent intent;
        if (HomeActivity.notificationHomeHandler){
            intent = new Intent(this, NoticeBoardActivity.class);
        }else {
            HomeActivity.notificationNoticeHandler=true;
            intent = new Intent(this, SplashActivity.class);

        }

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notifiBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("B.U. Jhansi")
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(notificationSound)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notifiBuilder.build());

    }


}
