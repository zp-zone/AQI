package com.zp.aqi.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 
 * @Description	空气质量信息，根据PM25.in返回的结果信息填充
 * 
 * @Date		2016-1-2 下午10:10:38
 * @Author		SugarZ
 */
/*
[
    {
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
    }
] 
 */
public class AQIInfo implements Parcelable,Comparable<AQIInfo>{
	private int aqi; // 空气质量
	
	private int co; // co 及 24小时均值
	private int co_24h;
	
	private int no2;
	private int no2_24h;
	
	private int o3;
	private int o3_24h;
	private int o3_8h;
	private int o3_8h_24h;
	
	private int pm10;
	private int pm10_24h;
	
	private int pm2_5;
	private int pm2_5_24h;
	
	private int so2;
	private int so2_24h;
	
	private String area; // "北京"
	private String quality;  // 重度污染
	private String primary_pollutant;  // 颗粒物(PM2.5)
	private String time_point; // "2016-01-02T12:00:00Z"
	
	
	
	public AQIInfo() {
		super();
	}
	public int getAqi() {
		return aqi;
	}
	public void setAqi(int aqi) {
		this.aqi = aqi;
	}
	public int getCo() {
		return co;
	}
	public void setCo(int co) {
		this.co = co;
	}
	public int getCo_24h() {
		return co_24h;
	}
	public void setCo_24h(int co_24h) {
		this.co_24h = co_24h;
	}
	public int getNo2() {
		return no2;
	}
	public void setNo2(int no2) {
		this.no2 = no2;
	}
	public int getNo2_24h() {
		return no2_24h;
	}
	public void setNo2_24h(int no2_24h) {
		this.no2_24h = no2_24h;
	}
	public int getO3() {
		return o3;
	}
	public void setO3(int o3) {
		this.o3 = o3;
	}
	public int getO3_24h() {
		return o3_24h;
	}
	public void setO3_24h(int o3_24h) {
		this.o3_24h = o3_24h;
	}
	public int getO3_8h() {
		return o3_8h;
	}
	public void setO3_8h(int o3_8h) {
		this.o3_8h = o3_8h;
	}
	public int getO3_8h_24h() {
		return o3_8h_24h;
	}
	public void setO3_8h_24h(int o3_8h_24h) {
		this.o3_8h_24h = o3_8h_24h;
	}
	public int getPm10() {
		return pm10;
	}
	public void setPm10(int pm10) {
		this.pm10 = pm10;
	}
	public int getPm10_24h() {
		return pm10_24h;
	}
	public void setPm10_24h(int pm10_24h) {
		this.pm10_24h = pm10_24h;
	}
	public int getPm2_5() {
		return pm2_5;
	}
	public void setPm2_5(int pm2_5) {
		this.pm2_5 = pm2_5;
	}
	public int getPm2_5_24h() {
		return pm2_5_24h;
	}
	public void setPm2_5_24h(int pm2_5_24h) {
		this.pm2_5_24h = pm2_5_24h;
	}
	public int getSo2() {
		return so2;
	}
	public void setSo2(int so2) {
		this.so2 = so2;
	}
	public int getSo2_24h() {
		return so2_24h;
	}
	public void setSo2_24h(int so2_24h) {
		this.so2_24h = so2_24h;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getQuality() {
		return quality;
	}
	public void setQuality(String quality) {
		this.quality = quality;
	}
	public String getPrimary_pollutant() {
		return primary_pollutant;
	}
	public void setPrimary_pollutant(String primary_pollutant) {
		this.primary_pollutant = primary_pollutant;
	}
	public String getTime_point() {
		return time_point;
	}
	public void setTime_point(String time_point) {
		this.time_point = time_point;
	}
	
	/*
	 * 实现序列化，以便于在进程间传递
	 * http://www.cnblogs.com/renqingping/archive/2012/10/25/Parcelable.html
	 * 
	 * 此处的构造顺序需要和writeToParcel的写入顺序一致
	 *
	 */
	public AQIInfo(Parcel in) {
		aqi = in.readInt();
		
		co = in.readInt();		co_24h = in.readInt();
		no2 = in.readInt();		no2_24h = in.readInt();
		o3 = in.readInt();		o3_24h = in.readInt();
		o3_8h = in.readInt();	o3_8h_24h = in.readInt();
		pm10 = in.readInt();	pm10_24h = in.readInt();
		pm2_5 = in.readInt();	pm2_5_24h = in.readInt();
		so2 = in.readInt();		so2_24h = in.readInt();
		
		area = in.readString();
		quality = in.readString();
		primary_pollutant = in.readString();
		time_point = in.readString();
	}
	
	@Override
	public int describeContents() {
		return 0; // 返回零即可
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(aqi);
		dest.writeInt(co); 		dest.writeInt(co_24h);
		dest.writeInt(no2); 	dest.writeInt(no2_24h);
		dest.writeInt(o3);		dest.writeInt(o3_24h);
		dest.writeInt(o3_8h);	dest.writeInt(o3_8h_24h);
		dest.writeInt(pm10);	dest.writeInt(pm10_24h);
		dest.writeInt(pm2_5);	dest.writeInt(pm2_5_24h);
		dest.writeInt(so2);		dest.writeInt(so2_24h);
		
		dest.writeString(area);
		dest.writeString(quality);
		dest.writeString(primary_pollutant);
		dest.writeString(time_point);
	}
	
	public static final Parcelable.Creator<AQIInfo> CREATOR = new Parcelable.Creator<AQIInfo>() {
		public AQIInfo createFromParcel(Parcel in) {
			return new AQIInfo(in);
		}

		public AQIInfo[] newArray(int size) {
			return new AQIInfo[size];
		}
	};



	/**
	 * 用于排序
	 */
	@Override
	public int compareTo(AQIInfo another) {
		if(another != null){
			// 一般不会出现空字段，但是由于在测试的时候插入了很多各种各样不规范数据
			// 导致空指针异常。所以这里多于的判断就暂且先放在这里了
			if(another.time_point != null && this.time_point != null)
				return another.time_point.compareTo(this.time_point);
		}
		return 0;
	}
	
}
