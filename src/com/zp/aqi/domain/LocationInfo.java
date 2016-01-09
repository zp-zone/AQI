package com.zp.aqi.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 
 * @Description	地点的相关信息
 * 
 * @Date		2016-1-2 下午10:09:42
 * @Author		SugarZ
 */
public class LocationInfo implements Parcelable{

	private int code;  // 返回码
	
	private double longitude; // 经度
	private double latitude; // 纬度

	private String time; // 日期和时间，字符串形式
	private String address; // 中国XX省XX市XX区XX路……
	private String locationDescribe; // 地点描述
	
	
	public LocationInfo() {
		super();
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getLocationDescribe() {
		return locationDescribe;
	}
	public void setLocationDescribe(String locationDescribe) {
		this.locationDescribe = locationDescribe;
	}
	
	public LocationInfo(Parcel in) {
		code = in.readInt();
		
		longitude = in.readDouble();
		latitude = in.readDouble();
		
		time = in.readString();
		address = in.readString();
		locationDescribe = in.readString();
	}
	
	@Override
	public int describeContents() {
		return 0; 
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(code);
		
		dest.writeDouble(longitude);
		dest.writeDouble(latitude);
		
		dest.writeString(time);
		dest.writeString(address);
		dest.writeString(locationDescribe);
	}
	
	public static final Parcelable.Creator<LocationInfo> CREATOR = new Parcelable.Creator<LocationInfo>() {
		public LocationInfo createFromParcel(Parcel in) {
			return new LocationInfo(in);
		}

		public LocationInfo[] newArray(int size) {
			return new LocationInfo[size];
		}
	};
}
