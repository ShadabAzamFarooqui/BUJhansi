package com.example.hp.stickpick.activity;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hp.stickpick.R;
import com.example.hp.stickpick.bean.ListBean;
import com.example.hp.stickpick.utils.Conversion;
import com.example.hp.stickpick.utils.ParameterConstants;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserPicFragment extends DialogFragment {

    public UserPicFragment() {
        // Required empty public constructor
    }

    List<ListBean> listBeen,listCourseSem;
    int position;
    String mob,mail;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_user_pic, container, false);

        ImageView imageView = (ImageView) rootView.findViewById(R.id.imageFragment);
        TextView textView = (TextView) rootView.findViewById(R.id.textImageFragment);

        ShowAllRecordsActivity activity = (ShowAllRecordsActivity) getActivity();
        listBeen = activity.getList();
        listCourseSem=activity.getListCourseSem();
        position = activity.getPosition();

        if (HomeActivity.boolCourseSem){
            mob = listCourseSem.get(position).getMobile();
            mail = listCourseSem.get(position).getEmail();
            if (listCourseSem.get(position).getImage().toString().length() > 0) {
                textView.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(Conversion.BitMapfromString(listCourseSem.get(position).getImage().toString()));
            } else {
                imageView.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
                textView.setText("" + listCourseSem.get(position).getName().toString().charAt(0));
            }
        }
       else {
            mob = listBeen.get(position).getMobile();
            mail = listBeen.get(position).getEmail();
            if (listBeen.get(position).getImage().toString().length() > 0) {
                textView.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(Conversion.BitMapfromString(listBeen.get(position).getImage().toString()));
            } else {
                imageView.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
                textView.setText("" + listBeen.get(position).getName().toString().charAt(0));
            }
        }



        Button call = (Button) rootView.findViewById(R.id.call);
        Button sms = (Button) rootView.findViewById(R.id.sms);
        Button email = (Button) rootView.findViewById(R.id.email);
        Button remove = (Button) rootView.findViewById(R.id.remove);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mob));
                callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }

                getActivity().startActivity(callIntent);
            }
        });
        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSmsDialog(inflater);
            }
        });
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", mail, null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Suggestion/Query");
                startActivity(Intent.createChooser(emailIntent, null));
            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mobKey=listBeen.get(position).getMobile().toString();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = database.getReference(ParameterConstants.USERS);
                databaseReference.child(ParameterConstants.PROFILE).child(mobKey).child("myImage").setValue("");
                listBeen.get(position).setImage("");
            }
        });


        Window window = getDialog().getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void showSmsDialog(LayoutInflater inflater) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());

        final View dialogView = inflater.inflate(R.layout.sms_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText smsEdt = (EditText) dialogView.findViewById(R.id.smsEdt);
        dialogBuilder.setMessage("Enter message below");
        dialogBuilder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(listBeen.get(position).getMobile(), null,"B.U. Jhansi="+smsEdt.getText().toString() , null, null);
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

}
