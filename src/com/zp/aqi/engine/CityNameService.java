package com.zp.aqi.engine;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.zp.aqi.R;
import com.zp.aqi.domain.Constant;
import com.zp.aqi.utils.CommonUtils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 
 * @file CitiesService.java
 * @package com.zp.aqi.engine 
 * @comment 从服务器获取所有城市信息，并定期执行以更新本地城市信息数据库
 * 
 * @author zp
 * @date 2016-1-4 下午10:37:35
 */
public class CityNameService extends AsyncTask<Void, Void, Void>{
	
	private static final String TAG = "CityNameService";
	private Context context;
	private Handler mHandler;
	private String urlPath;
	private String tokenStr;
	private ArrayList<String> cityNames = new ArrayList<String>();

	public CityNameService(Context context, Handler handler) {
		super();
		this.context = context;
		this.mHandler = handler;
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		HttpClient client = new DefaultHttpClient(CommonUtils.getMyHttpParams());
		urlPath = urlPath + tokenStr;
		Log.i(TAG, "url path : " + urlPath);
		
		HttpGet httpGet = new HttpGet(urlPath);
		HttpResponse response;
		try {
			response = client.execute(httpGet);
			int code = response.getStatusLine().getStatusCode();
			
			if(code == 200){
				String jsonStr = EntityUtils.toString(response.getEntity(),"UTF-8");
				Log.i(TAG, "jsonStr : " + jsonStr);
				if(jsonStr == null){
					String errorMessage = "返回的JSON字符串为空";
					sendFailedMessage(errorMessage);
					Log.e(TAG, "echo from pm25.in : " + errorMessage);
				}else{
					if(jsonStr.contains("error")){
						// 获取的是错误信息JSON对象。可以直接以字符串的形式处理，也可转化为JSON对象处理
						// 获取服务器返回的出错信息
						String errorMessage = (new JSONObject(jsonStr)).getString("error");
						sendFailedMessage(errorMessage);
						Log.i(TAG, "error message from pm25.in :" + errorMessage);
					}else{
						// 获取的是城市列表JSON对象
						cityNames = (ArrayList<String>) splitToNames(jsonStr);
						if(cityNames.size() > 0){ // 成功获取城市名称列表，那么将其通过消息发给调用者
							sendSuccessMessage(cityNames);
							Log.i(TAG, "city list from pm25.in :" + cityNames.toString());
						}else{ // 否则获取城市列表失败，发消息通知调用者
							String errorMessage = "服务器返回城市列表为空";
							sendFailedMessage(errorMessage);
							Log.i(TAG, "error message from pm25.in :" + errorMessage);
						}
					}
				}
			}else{
				String errorMessage = "服务器响应失败，返回码： "+code;
				sendFailedMessage(errorMessage);
				Log.i(TAG, "echo from pm25.in :" + errorMessage);
			}
			
		}  catch (Exception e) {
			sendFailedMessage(e.toString());
			Log.e(TAG, "Exception : " + e.toString());
			e.printStackTrace();
		}
		return null;
	}

	private void sendFailedMessage(String errorMessage) {
		Message msg = new Message();
		Bundle data = new Bundle();
		data.putString("error", errorMessage);
		msg.setData(data);
		msg.what = Constant.GET_CITYLIST_FAILED;
		mHandler.sendMessage(msg);
	}
	
	private void sendSuccessMessage(ArrayList<String> cities) {
		Message msg = new Message();
		Bundle data = new Bundle();
		data.putStringArrayList(Constant.CITY_LIST, cities);
		msg.setData(data);
		msg.what = Constant.GET_CITYLIST_SUCCEED;
		mHandler.sendMessage(msg);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		urlPath = this.context.getResources().getString(R.string.query_cities_url);
		tokenStr = "?token=" + this.context.getResources().getString(R.string.query_pm25in_token);
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		
	}

	public List<String> splitToNames(String jsonStr){
		List<String> result = new ArrayList<String>();
		
		int startIndex = jsonStr.lastIndexOf('[');
		int endIndex = jsonStr.lastIndexOf(']');
		String subStr = jsonStr.substring(startIndex+1, endIndex);
		
		Log.i(TAG, subStr);
		
		String[] temp = subStr.split(","); // 分隔出每个城市名称
		for(int i=0;i<temp.length;i++){
			String name = temp[i].substring(1, temp[i].length()-1); // 去除双引号
			result.add(name);
			
			Log.i(TAG, name);
		}
		return result;
	}
}
