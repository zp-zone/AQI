package com.zp.aqi;

import java.util.List;

import com.zp.aqi.db.DBManager;
import com.zp.aqi.domain.AQIInfo;
import com.zp.aqi.domain.Constant;
import com.zp.aqi.engine.AirQualityService;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @file CityListActivity.java
 * @package com.zp.aqi 
 * @comment 当定位失败，且没有默认的城市可以使用的时候，开启城市选择界面
 * 			当用户选择了指定的城市之后，根据用户选择的城市去查询数据，同时将这个城市设置为默认城市
 * 
 * @author zp
 * @date 2016-1-8 下午1:50:53
 */
public class CityListActivity extends Activity {

	protected static final String TAG = "CityListActivity";
	
	private ListView lv_citylist;
	private List<String> mCityList;
	private ProgressDialog pd;
	private MyAdapter adapter;
	private String selectedCity;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Constant.GET_AQIINFO_SUCCEED:
				pd.dismiss();
				
				Bundle aqiData = msg.getData();
				AQIInfo aqiInfo = aqiData.getParcelable(Constant.AQI_INFO);
				Intent detailActivity = new Intent(CityListActivity.this, DetailActivity.class);
				detailActivity.putExtra(Constant.AQI_INFO, aqiData);
				startActivity(detailActivity);
				
				Log.i(TAG, "AQI数据获取成功： area = " + aqiInfo.getArea());
				finish();
				break;
			case Constant.GET_AQIINFO_FAILED:
				pd.dismiss();
				Toast.makeText(CityListActivity.this, selectedCity+"AQI获取失败，服务器或网络暂时不可用", 0).show();
				finish();
				break;
			}
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_citylist);
		
		DBManager mDBManager = new DBManager(CityListActivity.this);
		
		selectedCity = "";
		mCityList = mDBManager.findAllCity();
		lv_citylist = (ListView) findViewById(R.id.lv_citylist);
		adapter = new MyAdapter();
		
		if(mCityList.size() > 0){
			lv_citylist.setAdapter(adapter);
		}
		
		lv_citylist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				selectedCity = (String) adapter.getItem(position);
				
				AlertDialog.Builder builder = new Builder(CityListActivity.this);
				builder.setTitle("选择城市");
				builder.setMessage("查看"+selectedCity+"的空气质量(AQI)信息");
				builder.setPositiveButton("确定",
						new android.content.DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,	int which) {
								// 如果确定，那么根据此城市名去查询AQI数据，同时显示ProgressDialog
								new AirQualityService(CityListActivity.this, mHandler, selectedCity).execute();
								pd = ProgressDialog.show(CityListActivity.this, "", "正在查询数据请稍候……", true, false);
							}
						});
				builder.setNegativeButton("取消",
						new android.content.DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,	int which) {
								//finish(); // 如果取消，什么也不做
							}
						});
				builder.setCancelable(false);
				builder.create().show();
			}
		});
	}
	
	class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return mCityList.size();
		}

		@Override
		public Object getItem(int position) {
			return mCityList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			String cityName = mCityList.get(position);
			View view = View.inflate(CityListActivity.this, R.layout.activity_citylist_item, null);
			TextView tv_cityname = (TextView) view.findViewById(R.id.tv_cityname);
			tv_cityname.setText(cityName);
			
			return view;
		}
		
	}
}
