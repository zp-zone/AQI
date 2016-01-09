package com.zp.aqi.db;

import java.util.ArrayList;
import java.util.List;

import com.zp.aqi.domain.AQIInfo;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBManager {

	private static final String TAG = "DBManager";
	private DBHelper helper;
	private SQLiteDatabase db;

	public DBManager(Context context) {
		helper = DBHelper.getInstance(context);
		db = helper.getWritableDatabase();
	}
/*

+ AQI_INFO_TABLE +
			"(area VARCHAR, time_point VARCHAR, primary_pollutant VARCHAR, quality VARCHAR," +
			"aqi INTEGER, co INTEGER, co_24h INTEGER, no2 INTEGER, no2_24h INTEGER," +
			"o3 INTEGER, o3_24h INTEGER, o3_8h INTEGER, o3_8h_24h INTEGER, pm10 INTEGER, pm10_24h INTEGER," +
			"pm2_5 INTEGER, pm2_5_24h INTEGER, so2 INTEGER, so2_24h INTEGER)";	
 */
	public void addAQIInfo(AQIInfo info) {
		db.beginTransaction();
		try {
			db.execSQL("INSERT INTO " + DBHelper.AQI_INFO_TABLE + " VALUES(?, ?, ?, ?, ?, ?,?,?,?,?,?, ?, ?, ?, ?, ?,?,?,?)",
					new Object[] {info.getArea(),info.getTime_point(),info.getPrimary_pollutant(),info.getQuality(),
					info.getAqi(),info.getCo(),info.getCo_24h(),info.getNo2(),info.getNo2_24h(),
					info.getO3(),info.getO3_24h(),info.getO3_8h(),info.getO3_8h_24h(),info.getPm10(),info.getPm10_24h(),
					info.getPm2_5(),info.getPm2_5_24h(),info.getSo2(),info.getSo2_24h()});

			db.setTransactionSuccessful();
			Log.i(TAG, "addAQIInfo" + info.getArea() + " succeed");
		} catch (SQLException e) {
			Log.e(TAG, "SQLException: " + e.toString());
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}
	}
	
	/**
	 * 
	 * @comment 获取所有的历史AQI信息（实际上有用的只有几条而已）
	 * 
	 * @param @return   
	 * @return ArrayList<AQIInfo>  AQI信息列表
	 * @throws
	 * @date 2016-1-7 下午3:10:30
	 */
	public ArrayList<AQIInfo> findAllAQIInfo(){
		ArrayList<AQIInfo> infos = new ArrayList<AQIInfo>();
		String sql = "SELECT * FROM " + DBHelper.AQI_INFO_TABLE;

		Cursor c = db.rawQuery(sql, null);
		while (c.moveToNext()) { 
			AQIInfo info = new AQIInfo();
			info.setAqi(c.getInt(c.getColumnIndex("aqi")));
			info.setCo(c.getInt(c.getColumnIndex("co")));
			info.setCo_24h(c.getInt(c.getColumnIndex("co_24h")));
			info.setNo2(c.getInt(c.getColumnIndex("no2")));
			info.setNo2(c.getInt(c.getColumnIndex("no2_24h")));
			info.setO3(c.getInt(c.getColumnIndex("o3")));
			info.setO3_24h(c.getInt(c.getColumnIndex("o3_24h")));
			info.setO3_8h(c.getInt(c.getColumnIndex("o3_8h")));
			info.setO3_8h_24h(c.getInt(c.getColumnIndex("o3_8h_24h")));
			info.setPm10(c.getInt(c.getColumnIndex("pm10")));
			info.setPm10_24h(c.getInt(c.getColumnIndex("pm10_24h")));
			info.setPm2_5(c.getInt(c.getColumnIndex("pm2_5")));
			info.setPm2_5_24h(c.getInt(c.getColumnIndex("pm2_5_24h")));
			info.setSo2(c.getInt(c.getColumnIndex("so2")));
			info.setSo2_24h(c.getInt(c.getColumnIndex("so2_24h")));
			
			info.setArea(c.getString(c.getColumnIndex("area")));
			info.setQuality(c.getString(c.getColumnIndex("quality")));
			info.setPrimary_pollutant(c.getString(c.getColumnIndex("primary_pollutant")));
			info.setTime_point(c.getString(c.getColumnIndex("time_point")));
			
			infos.add(info);
		}
		c.close();
		return infos;
	}
	
	/**
	 * 
	 * @comment 插入一条数据到城市列表数据库中
	 * 
	 * @param @param cityName   
	 * @return void  
	 * @throws 异常本来应该抛出去的，为了简化，此处在单元测试的时候结合日志进行测试。
	 * 			如果不结合日志，象此类被捕获的异常导致的失败是无法被发现的
	 * 
	 * @date 2016-1-7 下午3:21:17
	 */
	public void addCity(String cityName){
		db.beginTransaction();
		try {
			db.execSQL("INSERT INTO " + DBHelper.CITY_NAME_TABLE + " VALUES(?)", new Object[] {cityName});
			db.setTransactionSuccessful();
			Log.i(TAG, "add city " + cityName + " succeed");
		} catch (SQLException e) {
			Log.e(TAG, "SQLException : " + e.toString());
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}
	}
	
	/**
	 * 
	 * @comment 插入城市数组到数据库中。一般在初次使用或者是更新数据库的时候使用
	 * 
	 * @param @param cities   
	 * @return void  
	 * @throws
	 * @date 2016-1-7 下午3:20:01
	 */
	public void addAllCity(List<String> cities){
		for(String cityName : cities){
			addCity(cityName);
		}
	}
	
	/**
	 * 
	 * @comment 清空城市列表数据库
	 * 
	 * @param @return   
	 * @return int  被删除的行数
	 * @throws
	 * @date 2016-1-7 下午3:20:59
	 */
	public int deleteAllCity(){
		return db.delete(DBHelper.CITY_NAME_TABLE, null, null);
	}
	
	public List<String> findAllCity(){
		List<String> cityList = new ArrayList<String>();
		
		String sql = "SELECT * FROM " + DBHelper.CITY_NAME_TABLE;

		Cursor c = db.rawQuery(sql, null);
		while (c.moveToNext()) { 
			String city = new String();
			city = c.getString(c.getColumnIndex("cityName"));
			cityList.add(city);
		}
		c.close();
		
		return cityList;
	}
}
