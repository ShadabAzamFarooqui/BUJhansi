package com.example.hp.stickpick.activity;


import android.app.ActionBar;

import android.app.Activity;

import android.content.Intent;

import android.content.SharedPreferences;

import android.graphics.Color;

import android.os.Bundle;

import android.preference.PreferenceManager;

import android.view.Menu;

import android.view.MenuItem;

import android.view.View;

import android.view.View.OnClickListener;

import android.widget.Button;

import android.widget.ImageView;

import android.widget.LinearLayout;

import android.widget.RelativeLayout;

import android.widget.TextView;

import android.widget.Toast;


import com.example.hp.stickpick.R;
import com.example.hp.stickpick.utils.ParameterConstants;
import com.example.hp.stickpick.bean.ReferenceWrapper;
import com.example.hp.stickpick.bean.UserBean;
import com.hipmob.android.ChatMessage;

import com.hipmob.android.HipmobChatView;

import com.hipmob.android.HipmobChatView.ErrorListener;

import com.hipmob.android.HipmobChatView.ExtendedStatusListener;


import java.util.UUID;


public class HipmobCustomMessageLayoutLiveChatActivity extends Activity implements OnClickListener, HipmobChatView.StatusListener, ErrorListener, ExtendedStatusListener {

    private static String HIPMOB_APP_ID = ParameterConstants.HIPMOB_KEY_APPID;

    private boolean backPress = true;

    private CustomHipmobChatView chatView;

    private ReferenceWrapper referenceWrapper;

    private ActionBar actionBar;

    private SharedPreferences prefs;


    private LinearLayout backButtonLayout, parentLinearLayout;

    private RelativeLayout attachmentLayout, title_relative_layout;

    private TextView actionBarTitle, status, errors, extended;

    private ImageView attachment;

    private boolean connectionFlag, chatSessionFlag;

    private static final int MENU_ATTACHMENT = Menu.FIRST + 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.hipmob_custom_message_layout);

        referenceWrapper = ReferenceWrapper.getRefrenceWrapper(this);

		 /*setTitle("Moneygizer Chat Support");

		  getActionBar().setDisplayShowHomeEnabled(true);

		  getActionBar().setHomeButtonEnabled(true);

		 getActionBar().setDisplayHomeAsUpEnabled(true);

		  getActionBar().setTitle("Moneygizer Chat Suppor..");*/


		/*actionBar = getActionBar();

		actionBar.setCustomView(R.layout.live_chat_header);*/


        errors = (TextView) findViewById(R.id.errors);

        status = (TextView) findViewById(R.id.status);

        extended = (TextView) findViewById(R.id.extended);


/*		View actionBarView = actionBar.getCustomView();*/


		/*actionBarTitle = (TextView) actionBarView.findViewById(R.id.activity_main_content_title);

        actionBarTitle.setText("Live Chat");

		title_relative_layout = (RelativeLayout) actionBarView.findViewById(R.id.title_relative_layout);

		backButtonLayout = (LinearLayout) actionBarView.findViewById(R.id.backButtonLayout);

		backButtonLayout.setOnClickListener(this);

		parentLinearLayout = (LinearLayout) actionBarView.findViewById(R.id.parentLinearLayout);

		attachmentLayout = (RelativeLayout) actionBarView.findViewById(R.id.attachmentLayout);

		attachment = (ImageView) actionBarView.findViewById(R.id.attachment);

		attachmentLayout.setOnClickListener(this);*/


        // historyArrow = (ImageView)

        // actionBarView.findViewById(R.id.historyArrow);


		/*actionBar.setDisplayShowHomeEnabled(false);

		actionBar.setDisplayShowCustomEnabled(true);

		actionBar.setDisplayShowTitleEnabled(false);*/


        chatView = (CustomHipmobChatView) findViewById(R.id.chat);


        // set up the chat

        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());


        chatView.setAppId(HIPMOB_APP_ID);

        UserBean userBean = referenceWrapper.getUserBean();

        chatView.setUser(userBean.getMobile());
// Set Person User Id

        chatView.setName(userBean.getName());
        // Set Person Name

        chatView.setEmail(userBean.getEmail());
        // Set Person Email

        chatView.setContext("Mobile Carrier");


        (chatView.getInputField()).setBackgroundColor(Color.parseColor("#ffffff"));

        (chatView.getInputField()).setHint("Type a message");

        (chatView.getInputToolbar()).setBackgroundColor(ParameterConstants.APP_COLOR);

        Button sendButton = chatView.getSendButton();


        sendButton.setTextColor(Color.parseColor("#ffffff"));


        if (ParameterConstants.HIPMOB_USER_LOCATION != null && !ParameterConstants.HIPMOB_USER_LOCATION.equals("")) {
            chatView.setLocation(ParameterConstants.HIPMOB_USER_LOCATION);

        }
        chatView.setURLToastsEnabled(true);

        chatView.setExtendedStatusListener(this);

        chatView.setStatusListener(this);

        chatView.setErrorListener(this);


        Object obj = getLastNonConfigurationInstance();

        if (obj == null)

            chatView.start();

        else

            chatView.resume(obj);

        setColor();

    }


    private void setColor() {

		/*actionBar.setBackgroundDrawable(new ColorDrawable(ParameterConstants.APP_COLOR));


		attachmentLayout.setBackgroundColor(ParameterConstants.APP_COLOR);

		backButtonLayout.setBackgroundColor(ParameterConstants.APP_COLOR);

		title_relative_layout.setBackgroundColor(ParameterConstants.APP_COLOR);


		parentLinearLayout.setBackgroundColor(ParameterConstants.APP_COLOR);*/

    }


    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);


        // create a menu item

        MenuItem item = menu.add(Menu.NONE, MENU_ATTACHMENT, Menu.NONE, "Send Picture");

        item.setIcon(R.drawable.attachment);

        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return true;

    }


    private static final int REQUEST_IMAGE_CONTENT = 1;


    public boolean onOptionsItemSelected1(MenuItem item) {

        if (item.getItemId() == MENU_ATTACHMENT) {

            // start the gallery picker

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

            intent.setType("image/*");

            startActivityForResult(intent, REQUEST_IMAGE_CONTENT);

            return true;

        }

        return super.onOptionsItemSelected(item);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            case REQUEST_IMAGE_CONTENT:

                // Result was cancelled by the user or there was an error

                if (resultCode != RESULT_OK)

                    return;


                chatView.sendMessage(data.getData());

                break;

        }

    }


    private String getUserId() {

        String userid = prefs.getString("userid", UUID.randomUUID().toString());

        if (!prefs.contains("userid")) {

            SharedPreferences.Editor editor = prefs.edit();

            editor.putString("userid", userid);

            editor.commit();

        }

        return userid;

    }


    @Override

    public Object onRetainNonConfigurationInstance() {

        return chatView.pause();

    }


    @Override

    public void onDestroy() {

        if (chatView != null) {

            chatView.destroy();

        }


        super.onDestroy();

    }


    @Override

    public void onClick(View v)

    {

        switch (v.getId()) {

            case R.id.backButtonLayout:

                backPress();

                break;

            case R.id.attachmentLayout:

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

                intent.setType("image/*");

                startActivityForResult(intent, REQUEST_IMAGE_CONTENT);

                break;

            default:
                break;
        }

    }

    @Override
    public void onAuthenticationFailed() {
        status.setVisibility(View.VISIBLE);
        status.setText("Authentication from the server failed");
    }

    @Override
    public void onDisabled() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onReconnectFailed() {
        status.setVisibility(View.VISIBLE);
        status.setText("Error input Internet Connection");

    }

    @Override
    public void onUserConnectionError() {
        status.setVisibility(View.VISIBLE);
        status.setText("Error input Internet Connection");

    }

    @Override
    public void onConnected() {
        chatSessionFlag = true;
    }

    @Override
    public void onDisconnected() {
        status.setVisibility(View.VISIBLE);
        status.setText(ParameterConstants.HIPMOB_KEY_AWAY_NOTICE);
    }

    @Override
    public void onMessageReceived(ChatMessage arg0) {

    }

    @Override
    public void onMessageSent(ChatMessage arg0) {

    }

    @Override
    public void onOperatorAccepted(String arg0) {
        backPress = false;
        status.setVisibility(View.GONE);
    }

    @Override
    public void onOperatorOffline() {
        status.setVisibility(View.VISIBLE);
        status.setText(ParameterConstants.HIPMOB_KEY_AWAY_NOTICE);
    }

    @Override
    public void onOperatorOnline() {
        status.setVisibility(View.VISIBLE);
        // status.setText("Waiting for operator to connect..");
        status.setText(ParameterConstants.HIPMOB_KEY_PRESENT_NOTICE);
    }

    @Override
    public void onUserOffline(String arg0) {
        status.setVisibility(View.VISIBLE);
        status.setText("Error input Internet Connection");
    }

    @Override
    public void onUserOnline(String arg0) {
        status.setVisibility(View.VISIBLE);
        // status.setText(ParameterConstants.HIPMOB_KEY_PRESENT_NOTICE);
    }

    ;

    @Override
    public void onBackPressed() {
        backPress();
    }

    private void backPress() {
        if (chatView != null && chatView.getInputField() != null)
            referenceWrapper.hideKeyboard(chatView.getInputField(), this);
        if (!backPress) {
            Toast.makeText(HipmobCustomMessageLayoutLiveChatActivity.this, " Leaving this screen will disconnect the chat.. Press again to exit", Toast.LENGTH_LONG).show();
            backPress = true;
        } else if (backPress) {
            finish();
        }
    }

    @Override
    public void onReconnecting() {
        if (!connectionFlag) {
            Toast.makeText(HipmobCustomMessageLayoutLiveChatActivity.this, " Attempting to connect...", Toast.LENGTH_LONG).show();
            connectionFlag = true;
        } else {
            Toast.makeText(HipmobCustomMessageLayoutLiveChatActivity.this, " Attempting to re-connect...", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onTypingIndicator(boolean isTyping, String operator) {
        if (isTyping) {

            status.setVisibility(View.VISIBLE);
            status.setText(operator + " is typing.. ");
        } else if (!isTyping) {

            status.setText("");
            status.setVisibility(View.GONE);
        }
    }
}
