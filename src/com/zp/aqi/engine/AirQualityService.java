package com.zp.aqi.engine;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.zp.aqi.R;
import com.zp.aqi.domain.AQIInfo;
import com.zp.aqi.domain.Constant;
import com.zp.aqi.utils.CommonUtils;

/**
 * 
 * @Description	提供空气质量信息显示功能
 * 
 * @Date		2016-1-2 下午9:28:23
 * @Author		SugarZ
 */
public class AirQualityService extends AsyncTask<Void, Void, Void>{
	protected static final String TAG = "AirQualityService";
	private Handler mHandler;
	private String UrlPath;
	private String cityStr;
	private String tokenStr;
	private String stationNo;
	private Context context;
	private String mCityName;
	
	public AirQualityService(Context context, Handler mHandler, String mCityName) {
		this.context = context;
		this.mHandler = mHandler;
		this.mCityName = mCityName;
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		
		try {
			String path = UrlPath + cityStr + tokenStr	+ stationNo;
			Log.i(TAG, "path : " + path);
			HttpGet httpGet = new HttpGet(path);
			HttpClient client = new DefaultHttpClient(CommonUtils.getMyHttpParams());
			HttpResponse response = client.execute(httpGet);
			
			int code = response.getStatusLine().getStatusCode();
			if(code == 200){
				String jsonStr = EntityUtils.toString(response.getEntity(),"UTF-8");
				/*
				 * 获取到JSON字符串之后，需要对其进行判断
				 * 判断返回的数据是所需要的JSON数组还是返回的error提示
				 * 如果是JSON数组，那么就解析其中的AQI数据
				 * 如果是JSON对象，那么就解析其中的出错信息
				 * 
				 */
				if(jsonStr == null){
					// 返回数据为空，发送获取失败的消息
					String errorMessage = "获取的JSON字符串为空";
					sendFailedMessage(errorMessage);
					Log.e(TAG, errorMessage);
				}else{
					// 判断返回的是否是JSONArray，通过字符串的第一个字符区分"["代表JSONArray， "{"代表JSONObject
					// JSONArray说明返回的是AQI数据，否则返回的是非AQI数据即服务器的返回的error信息
					if(jsonStr.substring(0, 1).equals("[")){
						// 返回的是JSON数组；即所需要的结果
						Gson gson = new Gson();
						JsonParser parser = new JsonParser();
					    JsonArray Jarray = parser.parse(jsonStr).getAsJsonArray();
					    
					    ArrayList<AQIInfo> infos = new ArrayList<AQIInfo>();
					    
					    for(JsonElement joe : Jarray){ // 其实该JSONArray只有一条数据，因为请求的时候只请求了一个站点的消息
					    	AQIInfo info = gson.fromJson(joe, AQIInfo.class);
					    	infos.add(info);
					    	Log.i(TAG, "AQIInfo : area = " + info.getArea() +" aqi= "+ info.getAqi());
					    }
					    if(infos.size() > 0){
					    	AQIInfo result = infos.get(0);
					    	sendSuccessMessage(result);
					    }else{
					    	// 此处应该单独定义一种获取失败的信息
					    	String errorMessage = "获取AQI数据为空";
					    	sendFailedMessage(errorMessage);
					    	Log.i(TAG, "echo from pm25 in : " + errorMessage);
					    }
					}else{
						// 返回的是JSON对象；即服务器返回的请求出错信息
						JSONObject job = new JSONObject(jsonStr);
						String errorMessage = job.getString("error");
						sendFailedMessage(errorMessage);
				    	
						Log.i(TAG, "error message from pm25 in : " + errorMessage);
					}
					
				}
			}else{
				// 服务器返回 非 200 发送请求失败消息
				String errorMessage = "服务器响应失败： code = " + code;
				sendFailedMessage(errorMessage);
				Log.i(TAG, errorMessage);
			}
		} catch (Exception e) {
			String errorMessage = "服务器请求出现异常: " + e.toString();
			sendFailedMessage(errorMessage);
			Log.e(TAG, errorMessage);
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @comment 发送AQI请求成功的信息，同时将所获取城市的AQI数据通过bundle发送给调用者
	 * 
	 * @param @param result  需要返回给调用者的目标城市的AQI数据 
	 * @return void  
	 * @throws
	 * @date 2016-1-8 下午12:35:59
	 */
	private void sendSuccessMessage(AQIInfo result) {
		Message msg = new Message();
		Bundle data = new Bundle();
		data.putParcelable(Constant.AQI_INFO, result);
		msg.setData(data);
		msg.what = Constant.GET_AQIINFO_SUCCEED;
		mHandler.sendMessage(msg);
	}

	/**
	 * 
	 * @comment 发送失败消息
	 * @param @param errorMessage   出错原因字符串
	 * 		出错原因有：
	 * 			1) 服务器返回200OK，但是JSON字符串为空 或者JSON字符串中解析不到所需要的数据
	 * 			2) 服务器返回200OK，但是内容是error提示信息，比如token的请求次数用完了
	 * 			3) 返回码非200OK 一般不会出现
	 * 			4) 服务器请求出现异常，如连接或者请求超时
	 * 
	 * @return void  
	 * @throws
	 * @date 2016-1-8 下午12:36:54
	 */
	private void sendFailedMessage(String errorMessage) {
		Message msg = new Message();
		Bundle data = new Bundle();
		data.putString("error", errorMessage);
		msg.setData(data);
		msg.what = Constant.GET_AQIINFO_FAILED;
		mHandler.sendMessage(msg);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		try {
			UrlPath = this.context.getResources().getString(R.string.query_aqi_url);
			cityStr = "?city=" + URLEncoder.encode(mCityName, "UTF-8");
			tokenStr = "&token=" + this.context.getResources().getString(R.string.query_pm25in_token);
			stationNo = "&stations=no";
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, "UnsupportedEncodingException : " + e.toString());
			e.printStackTrace();
		}
		
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		
	}

}

/*
"aqi": 283,
"area": "北京",
"co": 4,
"co_24h": 4.109,
"no2": 121,
"no2_24h": 110,
"o3": 11,
"o3_24h": 18,
"o3_8h": 7,
"o3_8h_24h": 12,
"pm10": 258,
"pm10_24h": 232,
"pm2_5": 232,
"pm2_5_24h": 217,
"quality": "重度污染",
"so2": 49,
"so2_24h": 35,
"primary_pollutant": "颗粒物(PM2.5)",
"time_point": "2016-01-02T12:00:00Z"
 */
/*
result.setAqi(283);
result.setArea("北京");
result.setCo(4);
result.setCo_24h(4);
result.setNo2(121);
result.setNo2_24h(121);
result.setO3(11);
result.setO3_24h(18);
result.setO3_8h(7);
result.setO3_8h_24h(12);
result.setPm10(258);
result.setPm10_24h(232);
result.setPm2_5(232);
result.setPm2_5_24h(217);
result.setSo2(49);
result.setSo2_24h(35);
result.setQuality("重度污染");
result.setPrimary_pollutant("颗粒物(PM2.5)");
result.setTime_point("2016-01-02T12:00:00Z");

return result;
*/