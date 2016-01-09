package com.zp.aqi.utils;

import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.LinearLayout;

import com.zp.aqi.R;
import com.zp.aqi.domain.AQIInfo;
import com.zp.aqi.domain.Constant;

/**
 * 
 * @file CommonUtils.java
 * @package com.zp.aqi.utils 
 * @comment 全局功能类。提供各种不同的工具方法，比较杂乱
 * 
 * @author zp
 * @date 2016-1-5 下午4:51:07
 */
public class CommonUtils {

	private static final String TAG = "CommonUtils";

	/**
	 * 
	 * @comment 将给定的字符串中的城市名称解析出来
	 * 			例如：中国四川省成都市武侯区…… 解析结果为：成都
	 * @param @param str 源字符串
	 * @param @return   
	 * @return String  成功解析则返回城市名否则返回一个空串
	 * @throws
	 * @date 2016-1-5 下午4:51:55
	 */
	public static String getCityName(String str){
		int startIndex = -1;
		int endIndex = 0;
		int strLength = str.length();
		startIndex = str.indexOf("省");
		endIndex = str.indexOf("市");
		if(startIndex == -1){
			startIndex = 0;
		}
		if(endIndex == -1){
			endIndex = str.indexOf("县");
		}
		if(startIndex < endIndex && endIndex < strLength){
			return str.substring(startIndex+1, endIndex);
		}else{
			return "";
		}
	}
	
	/**
	 * 
	 * @comment 设置HTTP连接的连接参数，主要是超时设置
	 * 
	 * @param @return   
	 * @return HttpParams  
	 * @throws
	 * @date 2016-1-7 下午2:02:10
	 */
	public static HttpParams getMyHttpParams(){
		// 必须设置超时连接，否则线程会一直执行而得不到返回
		HttpParams httpParam = new BasicHttpParams(); 
		HttpConnectionParams.setConnectionTimeout(httpParam, 4000); //设置连接超时
		HttpConnectionParams.setSoTimeout(httpParam, 2000); //设置请求超时
		return httpParam;
	}
	
	/**
	 * 
	 * @comment 将AQI数据写入到SP中
	 * 
	 * @param @param context
	 * @param @param info   
	 * @return void  
	 * @throws
	 * @date 2016-1-7 下午2:01:52
	 */
	public static void writeToSharedPreference(Context context, AQIInfo info){
		SharedPreferences sp = context.getSharedPreferences(Constant.SP_AQI, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		
		editor.putInt("aqi", info.getAqi());
		
		editor.putInt("co", info.getCo());
		editor.putInt("co_24h", info.getCo_24h());
		
		editor.putInt("no2", info.getNo2());
		editor.putInt("no2_24h", info.getNo2_24h());
		
		editor.putInt("o3", info.getO3());
		editor.putInt("o3_24h", info.getO3_24h());
		editor.putInt("o3_8h", info.getO3_8h());
		editor.putInt("o3_8h_24h", info.getO3_8h_24h());
		
		editor.putInt("pm10", info.getPm10());
		editor.putInt("pm10_24h", info.getPm10_24h());
		
		editor.putInt("pm2_5", info.getPm2_5());
		editor.putInt("pm2_5_24h", info.getPm2_5_24h());
		
		editor.putInt("so2", info.getSo2());
		editor.putInt("so2_24h", info.getSo2_24h());
		
		editor.putString("area", info.getArea());
		editor.putString("quality", info.getQuality());
		editor.putString("primary_pollutant", info.getPrimary_pollutant());
		editor.putString("time_point", info.getTime_point());
		
		editor.commit();
	}
	
	/**
	 * 
	 * @comment 从Sp中读取AQI数据
	 * 
	 * @param @param context
	 * @param @return   
	 * @return AQIInfo  
	 * @throws
	 * @date 2016-1-7 下午2:01:33
	 */
	public static AQIInfo getAQIInfoFromSharedPreference(Context context){
		AQIInfo info = new AQIInfo();
		SharedPreferences sp = context.getSharedPreferences(Constant.SP_AQI, Context.MODE_PRIVATE);
		info.setAqi(sp.getInt("aqi", 0));
		info.setCo(sp.getInt("co", 0));
		info.setCo_24h(sp.getInt("co_24h", 0));
		info.setNo2(sp.getInt("no2", 0));
		info.setNo2(sp.getInt("no2_24h", 0));
		info.setO3(sp.getInt("o3", 0));
		info.setO3_24h(sp.getInt("o3_24h", 0));
		info.setO3_8h(sp.getInt("o3_8h", 0));
		info.setO3_8h_24h(sp.getInt("o3_8h_24h", 0));
		info.setPm10(sp.getInt("pm10", 0));
		info.setPm10_24h(sp.getInt("pm10_24h", 0));
		info.setPm2_5(sp.getInt("pm2_5", 0));
		info.setPm2_5_24h(sp.getInt("pm2_5_24h", 0));
		info.setSo2(sp.getInt("so2", 0));
		info.setSo2_24h(sp.getInt("so2_24h", 0));
		
		info.setArea(sp.getString("area", "--"));
		info.setQuality(sp.getString("quality", "--"));
		info.setPrimary_pollutant(sp.getString("primary_pollutant", "--"));
		info.setTime_point(sp.getString("time_point", "--"));
		
		return info;
	}
	
	/**
	 * 
	 * @comment 根据给定值设置控件的背景颜色
	 * 
	 * @param @param context
	 * @param @param ll 需要设置背景色的控件
	 * @param @param aqi  判断数据，颜色的设定取决于该参数落在那个取色空间
	 * @return void  
	 * @throws
	 * @date 2016-1-7 下午2:00:19
	 */
	public static void setBackgroundColor(Context context, LinearLayout ll, int aqi){
		if(aqi <= 50){ // 优 0-50
			ll.setBackgroundColor(context.getResources().getColor(R.color.color_aqi_level_1));
		}else if(aqi <= 100){ // 良 50-100
			ll.setBackgroundColor(context.getResources().getColor(R.color.color_aqi_level_2));
		}else if(aqi <= 150){ // 轻度污染 100-150
			ll.setBackgroundColor(context.getResources().getColor(R.color.color_aqi_level_3));
		}else if(aqi <= 200){ // 中度污染150-200 
			ll.setBackgroundColor(context.getResources().getColor(R.color.color_aqi_level_4));
		}else if(aqi <= 300){ // 重度污染 200-300
			ll.setBackgroundColor(context.getResources().getColor(R.color.color_aqi_level_5));
		}else if(aqi > 300){ // 严重污染 300-500
			ll.setBackgroundColor(context.getResources().getColor(R.color.color_aqi_level_6));
		}
	}
	
	/**
	 * 
	 * @comment 判断当前网络是否可用
	 * @param @param context
	 * @param @return   
	 * @return boolean  
	 * @throws
	 * @date 2016-1-7 下午1:59:57
	 */
	public static boolean isNetworkAvailable(Context context) {
		// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager == null) {
			return false;
		} else {
			// 获取NetworkInfo对象
			NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
			if (networkInfo != null && networkInfo.length > 0) {
				for (int i = 0; i < networkInfo.length; i++) {
					Log.i(TAG, "网络类型：" + networkInfo[i].getTypeName() + " -- " + networkInfo[i].getState());
					// 判断当前网络状态是否为连接状态
					if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
