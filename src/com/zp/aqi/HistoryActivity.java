package com.zp.aqi;

import java.util.ArrayList;
import java.util.Collections;

import com.zp.aqi.db.DBManager;
import com.zp.aqi.domain.AQIInfo;
import com.zp.aqi.utils.CommonUtils;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 
 * @file HistoryActivity.java
 * @package com.zp.aqi 
 * @comment 显示历史数据，只显示最近2条数据（当前数据和最近一次数据，如果当前获取数据失败就只显示一条）
 * 			包含数据的详细信息，例如so2 no2 等四种主要污染物的含量
 * 			由于本身历史数据参考意义不大，所以显示过多没有意义
 * 
 * @author zp
 * @date 2016-1-6 下午9:54:36
 */
public class HistoryActivity extends Activity {
	private ViewHolder mViewHolder_1;
	private ViewHolder mViewHolder_2;
	private ViewHolder mViewHolder_3;
	private LinearLayout ll_aqi_data_1;
	private LinearLayout ll_aqi_data_2;
	private LinearLayout ll_aqi_data_3;
	
//	private Bundle mAQIBundle;
//	private AQIInfo mAQIInfo;
	private DBManager mDBManager;
	private ArrayList<AQIInfo> aqiInfos;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_history);
		/*
		Intent intent = getIntent();
		mAQIBundle = intent.getBundleExtra(Constant.AQI_INFO);
		mAQIInfo = mAQIBundle.getParcelable(Constant.AQI_INFO);
		*/
		ll_aqi_data_1 = (LinearLayout) findViewById(R.id.ll_aqi_data_1);
		ll_aqi_data_2 = (LinearLayout) findViewById(R.id.ll_aqi_data_2);
		ll_aqi_data_3 = (LinearLayout) findViewById(R.id.ll_aqi_data_3);
		mViewHolder_1 = new ViewHolder(ll_aqi_data_1);
		mViewHolder_2 = new ViewHolder(ll_aqi_data_2);
		mViewHolder_3 = new ViewHolder(ll_aqi_data_3);
		
		mDBManager = new DBManager(HistoryActivity.this);
		aqiInfos = mDBManager.findAllAQIInfo();
		if(aqiInfos.size() > 1){ // 多于1个元素的时候才进行排序
			Collections.sort(aqiInfos);
		}
		
		if(aqiInfos.size() > 2){
			setDataToViews(aqiInfos.get(0), mViewHolder_1);
			setDataToViews(aqiInfos.get(1), mViewHolder_2);
			setDataToViews(aqiInfos.get(2), mViewHolder_3);
		}else if(aqiInfos.size() > 1){
			setDataToViews(aqiInfos.get(0), mViewHolder_1);
			setDataToViews(aqiInfos.get(1), mViewHolder_2);
			setDataToViews(CommonUtils.getAQIInfoFromSharedPreference(HistoryActivity.this), mViewHolder_3);
		}else if(aqiInfos.size() > 0){
			setDataToViews(aqiInfos.get(0), mViewHolder_1);
			setDataToViews(CommonUtils.getAQIInfoFromSharedPreference(HistoryActivity.this), mViewHolder_2);
			setDataToViews(CommonUtils.getAQIInfoFromSharedPreference(HistoryActivity.this), mViewHolder_3);
		}else{
			setDataToViews(CommonUtils.getAQIInfoFromSharedPreference(HistoryActivity.this), mViewHolder_1);
			setDataToViews(CommonUtils.getAQIInfoFromSharedPreference(HistoryActivity.this), mViewHolder_2);
			setDataToViews(CommonUtils.getAQIInfoFromSharedPreference(HistoryActivity.this), mViewHolder_3);
		}
		
		// CommonUtils.writeToSharedPreference(HistoryActivity.this, mAQIInfo);
	}
	
	private void setDataToViews(AQIInfo info, ViewHolder holder){
		CommonUtils.setBackgroundColor(HistoryActivity.this, holder.ll_item_background, info.getAqi());
		holder.tv_history_area.setText(info.getArea());
		holder.tv_history_aqi.setText("AQI : "+info.getAqi()+"");
		holder.tv_history_quality.setText(info.getQuality());
		holder.tv_history_time_point.setText(info.getTime_point());
		holder.tv_history_pm2_5.setText("PM2.5 : "+info.getPm2_5()+" ug/m3");
		holder.tv_history_pm10.setText("PM10 : "+info.getPm10()+" ug/m3");
		holder.tv_history_so2.setText("So2 : "+info.getSo2()+" ug/m3");
		holder.tv_history_no2.setText("No2 : "+info.getNo2()+" ug/m3");
	}
	
	private class ViewHolder{
		private LinearLayout ll_item_background;
		private TextView tv_history_area;
		private TextView tv_history_aqi;
		private TextView tv_history_quality;
		private TextView tv_history_time_point;
		private TextView tv_history_pm2_5;
		private TextView tv_history_pm10;
		private TextView tv_history_so2;
		private TextView tv_history_no2;
		
		public ViewHolder(View view){
			ll_item_background = (LinearLayout) view.findViewById(R.id.ll_item_background);
			tv_history_area = (TextView) view.findViewById(R.id.tv_history_area);
			tv_history_aqi = (TextView) view.findViewById(R.id.tv_history_aqi);
			tv_history_quality = (TextView) view.findViewById(R.id.tv_history_quality);
			tv_history_time_point = (TextView) view.findViewById(R.id.tv_history_time_point);
			tv_history_pm2_5 = (TextView) view.findViewById(R.id.tv_history_pm2_5);
			tv_history_pm10 = (TextView) view.findViewById(R.id.tv_history_pm10);
			tv_history_so2 = (TextView) view.findViewById(R.id.tv_history_so2);
			tv_history_no2 = (TextView) view.findViewById(R.id.tv_history_no2);
		}
	}
	
}
