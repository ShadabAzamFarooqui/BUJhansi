package com.example.hp.stickpick.fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.stickpick.R;
import com.example.hp.stickpick.bean.ListBean;
import com.example.hp.stickpick.utils.ParameterConstants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class MCA2 extends Fragment {


    public MCA2() {
        // Required empty public constructor
    }

    ListView listView;
    Button summit;
    CheckBox mainCheckBox;
    List listCourseSemester;
    MyAdapter myAdapter;

    ProgressDialog progressDialog;
    boolean[] br;
    List attendanceList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mca2, container, false);

        progressDialog = new ProgressDialog(getActivity());
        attendanceList = new ArrayList();
        getAllRecord();
        listView = view.findViewById(R.id.AttendanceMca2);
        summit = view.findViewById(R.id.summit);
        mainCheckBox = view.findViewById(R.id.mainCheckBox);

        mainCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if (isChecked == true) {
                    MyAdapter.map.clear();
                    boolean[] bArray = new boolean[listCourseSemester.size()];
                    for (int i = 0; i < listCourseSemester.size(); i++) {
                        bArray[i] = true;
                    }
                    MyAdapter.map.put(0, bArray);
                    myAdapter.notifyDataSetChanged();
                    listView.setAdapter(myAdapter);
                } else {
                    MyAdapter.map.clear();
                    boolean[] bArray = new boolean[listCourseSemester.size()];
                    for (int i = 0; i < listCourseSemester.size(); i++) {
                        bArray[i] = false;
                    }
                    MyAdapter.map.put(0, bArray);
                    myAdapter.notifyDataSetChanged();
                    listView.setAdapter(myAdapter);
                }
            }
        });
        summit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setMessage("Loading...");
                progressDialog.show();
                attendanceList.clear();
                attendanceList.add("Not Found");
                for (int j = 0; j < MyAdapter.map.get(0).length; j++) {
                    br = MyAdapter.map.get(0);
                    System.out.println("DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD   " + (j + 1) + " " + br[j]);
                    attendanceList.add("" + (j + 1) + "," + br[j]);
                }

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(ParameterConstants.ATTENDANCE);
                String strDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                databaseReference.child(ParameterConstants.ATTENDANCE_MCA_2).child("" + strDate).setValue(attendanceList, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            progressDialog.dismiss();
                            mainCheckBox.setChecked(false);
                            MyAdapter.map.clear();
                            myAdapter.notifyDataSetChanged();
                            Toast.makeText(getActivity(), "attendance saved", Toast.LENGTH_SHORT).show();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "attendance not saved", Toast.LENGTH_SHORT).show();

                        }
                    }
                });



            }
        });
        return view;
    }


    void getAllRecord() {
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

        List listName = new ArrayList<>();
        List listMobile = new ArrayList<>();
        List listEmail = new ArrayList<>();
        List listPassword = new ArrayList<>();
        List listImage = new ArrayList<>();
        List listCourse = new ArrayList<>();
        List listSemester = new ArrayList<>();
        List listCourseSemester = new ArrayList<>();

        //iterate through each user, ignoring their UID
        if (users == null) {
            Toast.makeText(getContext(), "No record found", Toast.LENGTH_SHORT).show();

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
        }

        List<ListBean> listBean = new ArrayList();
        for (int i = 0; i < listName.size(); i++) {
            ListBean bean = new ListBean();
            bean.setName(listName.get(i).toString());
            bean.setEmail(listEmail.get(i).toString());
            bean.setMobile(listMobile.get(i).toString());
            bean.setPassword(listPassword.get(i).toString());
            bean.setImage(listImage.get(i).toString());
            bean.setCourse(listCourse.get(i).toString());
            bean.setSemester(listSemester.get(i).toString());
            listBean.add(bean);
        }
        Collections.sort(listBean);
        listCourseSemester.clear();
        for (int i = 0; i < listBean.size(); i++) {
            if (listBean.get(i).getCourse().equals("MCA") && listBean.get(i).getSemester().equals("2nd")) {
                listCourseSemester.add(listBean.get(i));
            }
        }
        this.listCourseSemester = listCourseSemester;
        myAdapter = new MyAdapter(getActivity(), listCourseSemester);
        listView.setAdapter(myAdapter);

    }

    static class MyAdapter extends BaseAdapter {

        List<ListBean> listBean;
        Context context;
        boolean bool;
        public static HashMap<Integer, boolean[]> map;


        MyAdapter(Context context, List listBean) {
            this.context = context;
            this.listBean = listBean;
            this.bool = bool;
            map = new HashMap<Integer, boolean[]>();
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
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            ViewHolder holder = null;
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.row_fragment, null);
                holder = new ViewHolder();
                holder.name = (TextView) convertView.findViewById(R.id.nameUserList);
                holder.mobile = (TextView) convertView.findViewById(R.id.mobileUserList);
                holder.email = (TextView) convertView.findViewById(R.id.emailUserList);
                holder.password = (TextView) convertView.findViewById(R.id.passwordUserList);
                holder.courseSem = (TextView) convertView.findViewById(R.id.courseSemUserList);
                holder.textViewimageDetail = (TextView) convertView.findViewById(R.id.textViewimageDetail);
                holder.checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.name.setText(listBean.get(position).getName());
            holder.mobile.setText(listBean.get(position).getMobile());
            holder.email.setText(listBean.get(position).getEmail());
            holder.password.setText(listBean.get(position).getPassword());
            holder.courseSem.setText(listBean.get(position).getCourse() + " " + listBean.get(position).getSemester());
            holder.textViewimageDetail.setText("" + (position + 1));
            holder.textViewimageDetail.setVisibility(View.VISIBLE);

            final int mChildPosition = position;
            holder.checkbox.setOnCheckedChangeListener(null);
            if (map.containsKey(0)) {
                boolean getChecked[] = map.get(0);
                holder.checkbox.setChecked(getChecked[mChildPosition]);
            } else {
                boolean getChecked[] = new boolean[getCount()];
                map.put(0, getChecked);
                holder.checkbox.setChecked(false);
            }

            holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (isChecked) {
                        boolean getChecked[] = map.get(0);
                        getChecked[mChildPosition] = isChecked;
                        map.put(0, getChecked);
                    } else {
                        boolean getChecked[] = map.get(0);
                        getChecked[mChildPosition] = isChecked;
                        map.put(0, getChecked);

                    }
                }
            });

            return convertView;
        }
    }

    static class ViewHolder {
        TextView name, mobile, email, password, courseSem, textViewimageDetail;
        CheckBox checkbox;
    }
}
