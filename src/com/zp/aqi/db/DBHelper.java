package com.zp.aqi.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 
 * @file DBHelper.java
 * @package com.zp.aqi.db 
 * @comment 创建数据库和城市表以及信息表
 * @author zp
 * @date 2016-1-7 下午2:39:31
 */
public class DBHelper extends SQLiteOpenHelper {
	
	public static DBHelper mDBHelper;
	private static final String DATABASE_NAME = "aqi.db";		// 数据库名字
	private static final int DATABASE_VERSION = 1;					// 数据库版本号
	
	public static String CITY_NAME_TABLE = "city_name_table";		//城市列表数据库
	public static String AQI_INFO_TABLE = "aqi_info_table";				//aqi数据库
	
	// 设置ID字段，主键且自增长;city_id INTEGER PRIMARY KEY AUTOINCREMENT, 
	// 而如果将cityName设置为主键之后，在单元测试的时候如果插入重复数据就会出现异常（在以事务的形式批量插入的情况下）
	// 如果一条一条的插入，就不会出现异常
	public static String CREATE_CITY_TABLE = "CREATE TABLE IF NOT EXISTS " 
			+ CITY_NAME_TABLE +
			"(cityName VARCHAR PRIMARY KEY)";
	
	// 以时间为主键，如果获取的数据采集时间是一样的，那么重复插入数据库只能保证数据库中只有一条数据而不是重复的多条
	public static String CREATE_AQIINFO_TABLE = "CREATE TABLE IF NOT EXISTS "
			+ AQI_INFO_TABLE +
			"(area VARCHAR, time_point VARCHAR PRIMARY KEY, primary_pollutant VARCHAR, quality VARCHAR," +
			"aqi INTEGER, co INTEGER, co_24h INTEGER, no2 INTEGER, no2_24h INTEGER," +
			"o3 INTEGER, o3_24h INTEGER, o3_8h INTEGER, o3_8h_24h INTEGER, pm10 INTEGER, pm10_24h INTEGER," +
			"pm2_5 INTEGER, pm2_5_24h INTEGER, so2 INTEGER, so2_24h INTEGER)";
	
	/**
	 * 
	 * <p>Description: 使用时只需要指定Context即可其他字段默认</p>
	 * @param context 
	 * @param name
	 * @param factory
	 * @param version
	 */
	private DBHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public static synchronized DBHelper getInstance(Context context) {  
		if (mDBHelper == null) {  
			mDBHelper = new DBHelper(context, null, null, 0);  
		}  
		return mDBHelper;  
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_CITY_TABLE);
		db.execSQL(CREATE_AQIINFO_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
