package com.zp.aqi;

import com.zp.aqi.db.DBManager;
import com.zp.aqi.domain.AQIInfo;
import com.zp.aqi.domain.Constant;
import com.zp.aqi.utils.CommonUtils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DetailActivity extends Activity implements OnClickListener, OnLongClickListener{
	
	private static final String TAG = "DetailActivity";
	
	private LinearLayout ll_activity_detail;
	private TextView tv_area;
	private TextView tv_time_point;
	private TextView tv_quality;
	private TextView tv_aqi;
	private TextView tv_primary_pollutant;
	private Button bt;
	
	private DBManager mDBManager;
	
	private AQIInfo mAQIInfo;
	private Bundle mAQIBundle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_detail);
		
		// <1> 从Intent的Bundle中获取数据
		Intent intent = getIntent();
		mAQIBundle = intent.getBundleExtra(Constant.AQI_INFO);
		mAQIInfo = mAQIBundle.getParcelable(Constant.AQI_INFO);
		// <2> 初始化View
		initViews();
		// <3> 将获取的数据显示到界面
		setViews(mAQIInfo);
		// <4> 将数据写入到数据库
		mDBManager.addAQIInfo(mAQIInfo);
		// <5> 将当前获取的城市名写入到sp中作为默认城市
		SharedPreferences sp = getSharedPreferences(Constant.SP_AQI, MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString("defaultCity", mAQIInfo.getArea());
		editor.commit();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	private void initViews() {
		ll_activity_detail = (LinearLayout) findViewById(R.id.ll_activity_detail);
		tv_area = (TextView) findViewById(R.id.tv_area);
		tv_time_point = (TextView) findViewById(R.id.tv_time_point);
		tv_quality = (TextView) findViewById(R.id.tv_quality);
		tv_aqi = (TextView) findViewById(R.id.tv_aqi);
		tv_primary_pollutant = (TextView) findViewById(R.id.tv_primary_pollutant);
		bt = (Button) findViewById(R.id.bt);
		bt.setOnClickListener(this);
		bt.setOnLongClickListener(this);
		
		Log.i(TAG, "init views in detail activity ");
		// 数据库管理器
		mDBManager = new DBManager(DetailActivity.this);
		
		Log.i(TAG, "float view is added in detail activity ");
	}
	
	private void setViews(AQIInfo info){
		int aqi = info.getAqi();
		CommonUtils.setBackgroundColor(DetailActivity.this, ll_activity_detail, aqi);
		tv_area.setText(info.getArea());
		tv_time_point.setText(info.getTime_point());
		tv_quality.setText("空气质量："+info.getQuality());
		
		tv_aqi.setText(info.getAqi()+""); // info.getAqi()是int类型，需要转化为String ，否则会出现运行时错误
		
		if (info.getPrimary_pollutant().length() > 1) {
			tv_primary_pollutant.setText("主要污染物："+info.getPrimary_pollutant());
		} else {
			tv_primary_pollutant.setText("主要污染物：无");
		}
		Log.i(TAG, "set views in detail activity ");
	}

	@Override
	public void onClick(View v) {
		Intent historyActivity = new Intent(this, HistoryActivity.class);
		startActivity(historyActivity);
		Log.i(TAG, "按钮被点击了");
	}

	@Override
	public boolean onLongClick(View v) {
		Intent citylistActivity = new Intent(DetailActivity.this, CityListActivity.class);
		startActivity(citylistActivity);
		Log.i(TAG, "按钮被长按了");
		return true;
	}
	
}
