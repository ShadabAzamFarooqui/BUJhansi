package com.example.hp.stickpick.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.hp.stickpick.R;
import com.example.hp.stickpick.bean.UserBean;

import java.util.List;


public class ListViewAdapter extends BaseAdapter {

    Context context;
    List<UserBean> userBeanList;

    ListViewAdapter(Context context, List<UserBean> userBeanList) {
        this.context = context;
        this.userBeanList = userBeanList;
    }


    @Override
    public int getCount() {
        return userBeanList.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.rowlist, null);
        TextView rank = (TextView) convertView.findViewById(R.id.nameNearestPlace);
        TextView contry = (TextView) convertView.findViewById(R.id.addressNearestPlace);
        TextView openStatus = (TextView) convertView.findViewById(R.id.openStatusNearestPlace);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.img);


        rank.setText("" + userBeanList.get(position).getNameNearestPlace());
        contry.setText(userBeanList.get(position).getAddressNearestPlace());
        imageView.setImageResource(userBeanList.get(position).getLogo());
        //openStatus.setText(userBeanList.get(position).getOpenStatus());
        return convertView;
    }
}