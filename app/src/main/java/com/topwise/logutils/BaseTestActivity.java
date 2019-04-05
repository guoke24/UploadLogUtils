package com.topwise.logutils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;

public abstract class BaseTestActivity extends Activity {
	public static final int SHOW_MSG = 0;

	public static final String LKL_SERVICE_ACTION = "com.android.topwise.mposusdk.MposUsdkService";
	
	private int showLineNum = 0;

	private LinearLayout linearLayout;
	private ScrollView scrollView;
	private TextView textView1;
	private TextView textView2;
	private long oldTime = -1;
	public static final long DELAY_TIME = 200;
	public LinearLayout rightButArea = null;

	public EditText et_money;
	public LinearLayout ll_input_edits;
	public EditText et_order;
	public EditText et_psw;
	public EditText et_name;
	

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			Bundle bundle = msg.getData();
			String msg1 = bundle.getString("msg1");
			String msg2 = bundle.getString("msg2");
			int color = bundle.getInt("color");
			updateView(msg1, msg2, color);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// super.setContentView(R.layout.base_activity);
		linearLayout = (LinearLayout) this.findViewById(R.id.tipLinearLayout);
		scrollView = (ScrollView) this.findViewById(R.id.tipScrollView);
		//rightButArea = (LinearLayout) this.findViewById(R.id.main_linearlayout);
	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}


	/**
	 * 显示信息
	 * 
	 * @param msg1
	 * @param msg2
	 * @param color
	 * @createtor：Administrator
	 * @date:2014-9-15 下午9:45:18
	 */
	public void updateView(final String msg1, final String msg2, final int color) {
		if ((showLineNum % 300 == 0) && (showLineNum > 0)) { // 显示够20行的时候重新开始
			if(linearLayout != null) {
				linearLayout.removeAllViews();
			} else {
				linearLayout = (LinearLayout) findViewById(R.id.tipLinearLayout);
			}
			showLineNum = 0;
		}
		showLineNum++;
		LayoutInflater inflater = getLayoutInflater();
		View v = inflater.inflate(R.layout.show_item, null);
		textView1 = (TextView) v.findViewById(R.id.tip1);
		textView2 = (TextView) v.findViewById(R.id.tip2);
		textView1.setText(msg1);
		textView2.setText(msg2);
		textView1.setTextColor(Color.BLACK);
		textView2.setTextColor(color);
		textView1.setTextSize(20);
		textView2.setTextSize(20);
		linearLayout.addView(v);
		scrollView.post(new Runnable() {
			public void run() {
				scrollView.fullScroll(ScrollView.FOCUS_DOWN);
			}
		});

	}

	/**
	 * 更新UI
	 * 
	 * @param msg1
	 * @param msg2
	 * @param color
	 * @createtor：Administrator
	 * @date:2014-11-29 下午7:01:16
	 */
	public void showMessage(final String msg1, final String msg2,
                            final int color) {
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putString("msg1", msg1);
		bundle.putString("msg2", msg2);
		bundle.putInt("color", color);
		msg.setData(bundle);
		handler.sendMessage(msg);
	}

	// 显示单条信息
	public void showMessage(final String msg1, final int color) {
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putString("msg1", msg1);
		bundle.putString("msg2", "");
		bundle.putInt("color", color);
		msg.setData(bundle);
		handler.sendMessage(msg);
	}

	public void showMessage(String str) {
		this.showMessage(str, Color.BLACK);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		oldTime = -1;
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	

}
