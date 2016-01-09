package com.zp.aqi.engine;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.zp.aqi.domain.Constant;
import com.zp.aqi.domain.LocationInfo;

/**
 * 
 * @Description	提供位置功能
 * 				由于使用的是第三方SDK，因此并不能很好的控制访问流程
 * 				目前只能对onReceiveLocation被回调的时候才能得知目前的定位状态
 * 				而对于超时或者其他，是在是没有办法判断
 * 
 * @Date		2016-1-2 下午7:51:43
 * @Author		SugarZ
 */
public class LocationService extends AsyncTask<Void, Void, Void>{
	private static final String TAG = "LocationService";
	private Context context;
	private Handler mHandler;
	private LocationClient mLocationClient = null;
	private BDLocationListener mLocationListener;
	private boolean isReceivedData = false; // 判断listener的onReceive方法是否被执行
    
    public LocationService(Context context, Handler mHandler, BDLocationListener mLocationListener){
    	this.context = context;
    	this.mHandler = mHandler;
    	this.mLocationListener = new MyLocationListener();
    	this.mLocationClient = new LocationClient(this.context);     //声明LocationClient类
    	Log.i(TAG, "LocationService创建");
    }
    
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		LocationClientOption option = new LocationClientOption();
		// 设置低功耗
		option.setLocationMode(LocationMode.Battery_Saving);
		// 网络定位优先
		option.setPriority(LocationClientOption.NetWorkFirst);
		option.setCoorType("bd09ll");
		// 只需要定位一次，由逻辑判断如果没有得到定位数据，那么再定位
		option.setScanSpan(0); 
		// 需要显示地址信息
		option.setIsNeedAddress(true);
		// 不使用GPS，本身精度要求不是很高主要需要响应速度。而且GPS太慢
		option.setOpenGps(false);
		// 
		option.setLocationNotify(false);
		// 设置获取语义结果
		option.setIsNeedLocationDescribe(true);
		// stop则杀死服务进程
		option.setIgnoreKillProcess(false);
		// 不搜集crash信息
		option.SetIgnoreCacheException(true);
		// 不需要GPS仿真
		option.setEnableSimulateGps(true);
		
		mLocationClient.setLocOption(option);
		Log.i(TAG, "LocationService初始化并设置定位option");
		
    	mLocationClient.registerLocationListener( mLocationListener);    //注册监听函数
    	Log.i(TAG, "LocationService注册监听函数");
	}

	@Override
	protected Void doInBackground(Void... params) {
		mLocationClient.start();
    	Log.i(TAG, "LocationService开启定位");
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		
		if (isReceivedData) {
			if (mLocationClient != null) {
				if (mLocationClient.isStarted()) {
					mLocationClient.stop();
					Log.i(TAG, "LocationService结束定位");
				}
			}
		}
	}
    
    private class MyLocationListener implements BDLocationListener {
    	
		@Override
	    public void onReceiveLocation(BDLocation location) {
			LocationInfo tempLocationInfo = new LocationInfo();
			// Receive Location
			StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());			tempLocationInfo.setTime(location.getTime());
			sb.append("\nerror code : ");
			sb.append(location.getLocType());		tempLocationInfo.setCode(location.getLocType());
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());		tempLocationInfo.setLatitude(location.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());		tempLocationInfo.setLongitude(location.getLongitude());
			
			if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果 161
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());	tempLocationInfo.setAddress(location.getAddrStr());
				// 运营商信息
				sb.append("\noperationers : ");
				sb.append(location.getOperators());
				sb.append("\ndescribe : ");
				sb.append("网络定位成功");
				
			} else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果 66
				sb.append("\ndescribe : ");
				sb.append("离线定位成功，离线定位结果也是有效的");
			} else if (location.getLocType() == BDLocation.TypeServerError) { // 167
				sb.append("\ndescribe : ");
				sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
			} else if (location.getLocType() == BDLocation.TypeNetWorkException) { // 63
				sb.append("\ndescribe : ");
				sb.append("网络不同导致定位失败，请检查网络是否通畅");
			} else if (location.getLocType() == BDLocation.TypeCriteriaException) { // 62
				sb.append("\ndescribe : ");
				sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
			}
			
			sb.append("\nlocationdescribe : ");		tempLocationInfo.setLocationDescribe(location.getLocationDescribe());
			sb.append(location.getLocationDescribe());// 位置语义化信息
			
			isReceivedData = true;
			
			Message msg = new Message();
			Bundle data = new Bundle();
			data.putParcelable(Constant.LOCATION_INFO, tempLocationInfo);
			msg.setData(data);
			msg.what = Constant.GET_LOCATION_FINISH;
			mHandler.sendMessage(msg);
			
			Log.i(TAG, sb.toString());
		}
	}
	
}
