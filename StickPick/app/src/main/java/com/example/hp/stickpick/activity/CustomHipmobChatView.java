package com.example.hp.stickpick.activity;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hp.stickpick.R;
import com.example.hp.stickpick.bean.ReferenceWrapper;
import com.hipmob.android.ChatMessage;
import com.hipmob.android.HipmobChatView;

public class CustomHipmobChatView extends HipmobChatView {

	private ReferenceWrapper referenceWrapper;

	public CustomHipmobChatView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		referenceWrapper = ReferenceWrapper.getRefrenceWrapper(context);

	}

	/**
	 * Creates the view.
	 * 
	 * @exclude
	 */
	public CustomHipmobChatView(Context context, AttributeSet attrs) {
		super(context, attrs);
		referenceWrapper = ReferenceWrapper.getRefrenceWrapper(context);
	}

	/**
	 * Creates the view.
	 * 
	 * @exclude
	 */
	public CustomHipmobChatView(Context context) {
		super(context);
		referenceWrapper = ReferenceWrapper.getRefrenceWrapper(context);
	}

	protected View buildMessageLayout() {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return inflater.inflate(R.layout.chatrow, this, false);
	}

	protected boolean renderMessage(View row, ChatMessage message) {
		if (message.isLocal()) {
			ImageView iv = (ImageView) row.findViewById(R.id.profile_pic_local);
			iv.setVisibility(View.VISIBLE);
			iv.setImageResource(R.drawable.custom_profile);
			iv = (ImageView) row.findViewById(R.id.profile_pic_remote);
			iv.setVisibility(View.GONE);
			TextView textView = ((TextView) row.findViewById(R.id.hipmob_content));
			TextView hipmob_thumb_timestamp = ((TextView) row.findViewById(R.id.hipmob_thumb_timestamp));
			TextView hipmob_timestamp = ((TextView) row.findViewById(R.id.hipmob_timestamp));
			TextView hipmob_detail = ((TextView) row.findViewById(R.id.hipmob_detail));

			textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
			hipmob_thumb_timestamp.setGravity(Gravity.LEFT | Gravity.START | Gravity.CENTER_VERTICAL);
			hipmob_timestamp.setGravity(Gravity.LEFT | Gravity.START | Gravity.CENTER_VERTICAL);
			hipmob_detail.setGravity(Gravity.LEFT | Gravity.START | Gravity.CENTER_VERTICAL);

			textView.setTextColor(getResources().getColor(R.color.black));
		} else {
			ImageView iv = (ImageView) row.findViewById(R.id.profile_pic_remote);
			iv.setVisibility(View.VISIBLE);
			iv.setImageResource(R.drawable.custom_profile);
			iv = (ImageView) row.findViewById(R.id.profile_pic_local);
			iv.setVisibility(View.GONE);
			TextView textView = ((TextView) row.findViewById(R.id.hipmob_content));
			TextView hipmob_thumb_timestamp = ((TextView) row.findViewById(R.id.hipmob_thumb_timestamp));
			TextView hipmob_timestamp = ((TextView) row.findViewById(R.id.hipmob_timestamp));
			TextView hipmob_detail = ((TextView) row.findViewById(R.id.hipmob_detail));

			textView.setGravity(Gravity.RIGHT | Gravity.END | Gravity.CENTER_VERTICAL);
			hipmob_thumb_timestamp.setGravity(Gravity.RIGHT | Gravity.END | Gravity.CENTER_VERTICAL);
			hipmob_timestamp.setGravity(Gravity.RIGHT | Gravity.END | Gravity.CENTER_VERTICAL);
			hipmob_detail.setGravity(Gravity.RIGHT | Gravity.END | Gravity.CENTER_VERTICAL);

			textView.setTextColor(getResources().getColor(R.color.black));
		}
		return false;
	}

	protected boolean willRenderMessage() {
		return true;
	}
}