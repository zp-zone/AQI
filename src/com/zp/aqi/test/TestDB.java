package com.zp.aqi.test;

import java.util.ArrayList;
import java.util.List;

import android.test.AndroidTestCase;
import android.util.Log;

import com.zp.aqi.db.DBManager;
import com.zp.aqi.domain.AQIInfo;

public class TestDB extends AndroidTestCase {

	private static final String TAG = "TEST";

	public void TestAdd() {
		AQIInfo aqiInfo = new AQIInfo();
		AQIInfo aqiInfo2 = new AQIInfo();

		aqiInfo.setAqi(283);
		aqiInfo.setArea("北京");
		aqiInfo.setCo(4);
		aqiInfo.setCo_24h(4);
		aqiInfo.setNo2(121);
		aqiInfo.setNo2_24h(121);
		aqiInfo.setO3(11);
		aqiInfo.setO3_24h(18);
		aqiInfo.setO3_8h(7);
		aqiInfo.setO3_8h_24h(12);
		aqiInfo.setPm10(258);
		aqiInfo.setPm10_24h(232);
		aqiInfo.setPm2_5(232);
		aqiInfo.setPm2_5_24h(217);
		aqiInfo.setSo2(49);
		aqiInfo.setSo2_24h(35);
		aqiInfo.setQuality("重度污染");
		aqiInfo.setPrimary_pollutant("颗粒物(PM2.5)");
		aqiInfo.setTime_point("2016-01-05T12:00:00Z");
		
		aqiInfo2.setAqi(283);
		aqiInfo2.setArea("上海");
		aqiInfo2.setCo(4);
		aqiInfo2.setCo_24h(5);
		aqiInfo2.setNo2(121);
		aqiInfo2.setNo2_24h(151);
		aqiInfo2.setO3(11);
		aqiInfo2.setO3_24h(15);
		aqiInfo2.setO3_8h(7);
		aqiInfo2.setO3_8h_24h(15);
		aqiInfo2.setPm10(255);
		aqiInfo2.setPm10_24h(252);
		aqiInfo2.setPm2_5(252);
		aqiInfo2.setPm2_5_24h(215);
		aqiInfo2.setSo2(55);
		aqiInfo2.setSo2_24h(35);
		aqiInfo2.setQuality("重度污染");
		aqiInfo2.setPrimary_pollutant("颗粒物(PM2.5)");
		aqiInfo2.setTime_point("2016-01-05T12:00:00Z");
		
		DBManager manager = new DBManager(getContext());
		manager.addAQIInfo(aqiInfo);
		manager.addAQIInfo(aqiInfo2);
	}
	
	public void TestFindAllAQIInfo(){
		
		DBManager manager = new DBManager(getContext());
		List<AQIInfo> infos = manager.findAllAQIInfo();
		Log.i(TAG, "infos.size() = " + infos.size());
		assertEquals(2, infos.size());
	}
	
	public void TestAddCity(){
		DBManager manager = new DBManager(getContext());
		manager.addCity("襄阳");
	}
	
	public void TestAddAllCity(){
		DBManager manager = new DBManager(getContext());
		List<String> cities = new ArrayList<String>();
		
		cities.add("北京");
		cities.add("襄阳");
		cities.add("上海");
		cities.add("深圳");
		
		manager.addAllCity(cities);
	}
	
	public void TestGetAllCity(){
		DBManager manager = new DBManager(getContext());
		List<String> cities = new ArrayList<String>();
		cities = manager.findAllCity();
		Log.i(TAG, cities.toString());
	}
	
	public void TestDeleteAllCity(){
		DBManager manager = new DBManager(getContext());
		assertEquals(4, manager.deleteAllCity());
	}
}
