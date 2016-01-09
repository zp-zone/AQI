package com.zp.aqi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.zp.aqi.db.DBManager;
import com.zp.aqi.domain.AQIInfo;
import com.zp.aqi.domain.Constant;
import com.zp.aqi.domain.LocationInfo;
import com.zp.aqi.engine.AirQualityService;
import com.zp.aqi.engine.CityNameService;
import com.zp.aqi.engine.LocationService;
import com.zp.aqi.utils.CommonUtils;

/**
 * 
 * @file SplashActivity.java
 * @package com.zp.aqi 
 * @comment 依次1)休眠3秒  2)显示历史数据或者定位  3)获取AQI数据  4)开启界面显示AQI数据或者关闭应用 
 * 
 * @author zp
 * @date 2016-1-6 下午4:53:25
 */
public class SplashActivity extends Activity {

	protected static final String TAG = "SplashActivity";
	
	private SharedPreferences mSharedPreferences;
	private DBManager mDBManager;
	
	public Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			
			/*
			 * 开启应用：在splash界面休眠2秒之后，正式开启应用的逻辑
			 */
			case Constant.SPLASH_SLEEP_FINISH:
				// 休眠结束之后开始应用的逻辑
				startApplication();
				break;
				
			/*
			 * 结束当前Activity	
			 */
			case Constant.ACTIVITY_FINISH:
				finish();
				break;
			/*
			 * 获取城市名称及位置等信息==逻辑整理完毕
			 * 
			 * 此处把onReceiveLocation()的回调统一看做是请求成功，在此处对获取的请求数据进行解析
			 * 通过解析的errorcode判断是否获取到了城市地址
			 * 
			 * 1. error_code = 161  ：成功获取地址.定位成功，查询数据库看所定位城市是否支持AQI数据.
			 * 		如果支持，就根据城市名去查询AQI数据		
			 * 		如果不支持，执行getCityNameFailed()逻辑
			 * 
			 * 2. error_code = 61 ： 成功获取了经度和纬度值，但是没有获取到城市名称
			 * 		执行getCityNameFailed()逻辑
			 * 
			 */
			case Constant.GET_LOCATION_FINISH:
				Bundle locationData = msg.getData();
				LocationInfo locationInfo = locationData.getParcelable(Constant.LOCATION_INFO);
				
				int error_code = locationInfo.getCode();
				if(error_code == 161){ // 返回码161含有城市名
					String cityName = CommonUtils.getCityName(locationInfo.getAddress());
					// 得到城市名之后，查询数据库，看是否支持
					Log.i(TAG, "获取城市位置成功 -- city name : " + cityName);

					// 查询成功且定位城市在支持城市列表中，那么开始获取AQI数据
					List<String> cities = mDBManager.findAllCity();
					if((cities.size() > 0) && (cityName != null) && cities.contains(cityName)){
						// 如果城市名支持，那么将通过这个城市名去查询AQI，同时将当前的城市名写入到SP中，作为默认城市
						new AirQualityService(SplashActivity.this, mHandler, cityName).execute();
					} else {
						// 城市名不支持
						getCityNameFailed();
					}
				} else { 
					// 定位只有经纬度，城市名获取失败
					getCityNameFailed();
				}
				
				break;

			/*
			 * 获取对应城市的空气质量信息
			 * AQI数据获取成功之后直接开启显示界面，显示最新信息
			 * AQI数据获取失败，如果有历史信息，开启显示界面显示历史信息；如果没有历史信息，就不显示，然后关闭应用
			 */
			case Constant.GET_AQIINFO_SUCCEED:
				showFreshData(msg);
				finish();
				break;
			case Constant.GET_AQIINFO_FAILED:
				showHistoryData();
				finish();
				break;

			/*
			 * 获取所有城市数据：此种情况只能是第一次安装初始化城市列表数据库的时候发生==逻辑整理完毕
			 * 
			 * 整个获取城市列表的逻辑也就是在初次安装的时候进行. 其他时候获取城市列表仅仅是需要更新城市数据库的时候执行
			 * 当然，初次安装的时候也可以不通过网络访问获取列表，而是将已经制作好的列表xml文件读取出来初始化数据库即可
			 * 
			 * 如果城市列表数据获取成功，那么将数据存入数据库之后，设置标志位，然后开启定位
			 * 如果获取失败，只能是网络请求超时或者服务器返回的不是列表数据（出错信息或者空）。此时直接关闭应用
			 */
			case Constant.GET_CITYLIST_SUCCEED:
				Bundle cityData = msg.getData();
				ArrayList<String> cityList = cityData.getStringArrayList(Constant.CITY_LIST);
				mDBManager.addAllCity(cityList);
				
				Editor editor = mSharedPreferences.edit();
				editor.putBoolean("isFirst", false);
				editor.commit();
				
				new LocationService(SplashActivity.this, mHandler, null).execute();
				Log.i(TAG, "获取城市列表成功： "+cityList.toString()+".城市列表已存入数据库.开始定位操作");
				
				break;
			case Constant.GET_CITYLIST_FAILED:
				Bundle errorData = msg.getData();
				String errorMessage = errorData.getString("error");
				
				Toast.makeText(SplashActivity.this, "获取城市列表数据失败，服务器暂时不可用", 0).show();
				Log.i(TAG, "获取城市列表失败： " + errorMessage);
				finish();
				
				break;
			}
		}

	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash_new);
		
		mDBManager = new DBManager(SplashActivity.this);
		mSharedPreferences = getSharedPreferences(Constant.SP_AQI, MODE_PRIVATE);
		
		// 保证splash不是一闪而过，至少显示2.5秒
		new Thread(){
			public void run() {
				try {
					sleep(2500);
					mHandler.sendEmptyMessage(Constant.SPLASH_SLEEP_FINISH);
				} catch (InterruptedException e) {
					Log.e(TAG, e.toString());
					e.printStackTrace();
				}
			};
		}.start();
		
	}

	/**
	 * 
	 * @comment 显示获取的新数据：在连续两次请求时间间隔大于1小时且请求数据成功的时候执行
	 * 
	 * @param @param msg   被调用者发送的含有AQI请求结果的消息
	 * @return void  
	 * @throws
	 * @date 2016-1-8 下午12:58:47
	 */
	private void showFreshData(android.os.Message msg) {
		Bundle aqiData = msg.getData();
		AQIInfo aqiInfo = aqiData.getParcelable(Constant.AQI_INFO);
		
		Intent detailActivity = new Intent(SplashActivity.this, DetailActivity.class);
		detailActivity.putExtra(Constant.AQI_INFO, aqiData);
		startActivity(detailActivity);
		
		Log.i(TAG, aqiInfo.getArea() +" 空气质量指数 : " + aqiInfo.getAqi() + " \n主要污染物： " + aqiInfo.getPrimary_pollutant());
	}

	/**
	 * 
	 * @comment 显示历史消息：在连续两次请求时间间隔小于1小时或者请求AQI数据失败的时候显示历史消息
	 * @param    
	 * @return void  
	 * @throws
	 * @date 2016-1-8 下午12:59:48
	 */
	private void showHistoryData() {
		List<AQIInfo> aqiInfos = mDBManager.findAllAQIInfo();
		if(aqiInfos.size() > 0){
			Collections.sort(aqiInfos);
			AQIInfo aqiInfo_2 = aqiInfos.get(0);
			Bundle aqiData_2 = new Bundle();
			aqiData_2.putParcelable(Constant.AQI_INFO, aqiInfo_2);
			Intent detailActivity2 = new Intent(SplashActivity.this, DetailActivity.class);
			detailActivity2.putExtra(Constant.AQI_INFO, aqiData_2);
			startActivity(detailActivity2);
			Toast.makeText(SplashActivity.this, "数据暂时未更新，显示最近监测数据", 0).show();
			Log.i(TAG, "获取空气质量信息失败或者连续请求时间间隔小于1小时，显示历史数据");
		}else{
			Toast.makeText(SplashActivity.this, "空气质量指数获取失败", Toast.LENGTH_LONG).show();
			Log.i(TAG, "获取空气质量信息失败，当前没有历史数据");
		}
	}
	
	/**
	 * 
	 * @comment  获取城市名失败时执行的逻辑。失败的两种情形
	 *  1)定位成功，但是列表中并没有用户所在地的地名 2)定位失败 
	 *  
	 *  1.首先从sp中获取用户之前设置的城市或者之前定位成功的城市，然后通过sp中的城市名去获取信息。如果两个sp中都没有数据，执行2
	 *  2.手动选择城市
	 *  
	 * @param    
	 * @return void  
	 * @throws
	 * @date 2016-1-8 下午1:01:59
	 */
	private void getCityNameFailed() {
		String defaultCity = mSharedPreferences.getString("defaultCity", "");
		if(!"".equals(defaultCity)){
			new AirQualityService(SplashActivity.this, mHandler, defaultCity).execute();
			Log.i(TAG, "获取城市位置失败，使用用户默认设置的城市");
		}else{
			startCityListActivity();
			finish();
			Log.i(TAG, "获取城市位置失败或者城市名不支持，且默认城市为空，开启城市选择界面");
		}
	}
	
	/**
	 * 
	 * @comment 应用开始执行：
	 * 1.先判断网络状况，
	 * 		1.1 网络可用，执行2
	 *		1.2网络不可用，关闭应用
	 * 2.判断是否是第一次安装
	 * 		2.1第一次安装，开启获取城市列表服务
	 * 		2.2非第一次，开启定位服务
	 * 							
	 * @param    
	 * @return void  
	 * @throws
	 * @date 2016-1-8 上午11:13:28
	 */
	private void startApplication() {
		if(CommonUtils.isNetworkAvailable(SplashActivity.this)){
			boolean isFirst = mSharedPreferences.getBoolean("isFirst", true);
			if(isFirst){
				// 获取城市列表
				new CityNameService(SplashActivity.this, mHandler).execute();
				// 这段逻辑必须在获取城市列表成功的时候执行，因为是否是初始安装的标准是城市列表数据库是否被初始化
				/*
				Editor editor = mSharedPreferences.edit();
				editor.putBoolean("isFirst", false);
				editor.commit();
				*/
				Toast.makeText(SplashActivity.this, "初次使用，正在加载城市信息……", Toast.LENGTH_LONG).show();
				Log.i(TAG, "初次使用，获取城市列表， isFirst : " + isFirst);
			}else{
				isOneHourInterval();
			}
		}else{
			Toast.makeText(SplashActivity.this, "网络不可用，请确保网络连接已经打开", Toast.LENGTH_LONG).show();
			Log.i(TAG, "当前网络不可用，关闭应用 ");
			finish();
		}
	}
	
	/**
	 * 
	 * @comment 保证连续两次请求之间的时间间隔是1小时以上。一方面是数据更新周期是1小时，另一个方面是本身token的请求是有限制的
	 * 
	 * @param    
	 * @return void  
	 * @throws
	 * @date 2016-1-8 下午10:45:57
	 */
	private void isOneHourInterval(){
		long curMillis = java.lang.System.currentTimeMillis();
		long oneHourMillis = /*60*60*1000*/0; 
		long lastMillis = mSharedPreferences.getLong("lastMillis", curMillis-oneHourMillis-1);
		// 判断连续两次请求时间间隔是否大于1小时。默认1小时只能请求一次数据
		if((curMillis - lastMillis) > oneHourMillis){
			Editor editor = mSharedPreferences.edit();
			editor.putLong("lastMillis", curMillis);
			editor.commit();
			// 执行定位操作
			new LocationService(SplashActivity.this, this.mHandler, null).execute();
			Log.i(TAG, "非初次使用，时间间隔大于1小时，开启定位");
		}else{
			// 连续请求时间间隔小于1小时，那么直接显示历史数据
			showHistoryData();
			Log.i(TAG, "非初次使用，时间间隔小于1小时，显示历史数据");
			finish();
		}
	}
	
	/**
	 * 
	 * @comment 打开城市选择页面
	 * @param    
	 * @return void  
	 * @throwss
	 * @date 2016-1-8 上午11:17:13
	 */
	private void startCityListActivity(){
		Intent citylistActivity = new Intent(SplashActivity.this, CityListActivity.class);
		startActivity(citylistActivity);
	}

}
