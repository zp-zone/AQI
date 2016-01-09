package com.zp.aqi.domain;

public class Constant {
	
	// 全局消息定义
	public static final int GET_LOCATION_FINISH = 201; // 定位
	//public static final int GET_LOCATION_FAILED = 401;
	
	public static final int GET_AQIINFO_SUCCEED = 202; // 获取对应城市AQI数据
	public static final int GET_AQIINFO_FAILED = 402;

	public static final int GET_CITYLIST_SUCCEED = 203; // 获取城市列表数据
	public static final int GET_CITYLIST_FAILED = 403;
	
	public static final int GET_TIME_OUT = 404; // 网络连接超时
	
	public static final int SPLASH_SLEEP_FINISH = 405; // splash休眠时间到

	public static final int ACTIVITY_FINISH = 406; // splash休眠时间到
	
	// 全局的key
	public static final String AQI_INFO = "aqiInfo"; // 网络查询的AQI信息成功或失败时通过handler传递信息时的key
	public static final String CITY_LIST = "cityList";
	
	// sp 名
	public static final String SP_AQI = "aqiSharedPreference";
	public static final String LOCATION_INFO = "locationInfo";
}
