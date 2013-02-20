package me.nuoyan.erkang.sample.lifeqq;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.httpclient.util.DateUtil;

public class LifeQQ {
	
	private String merchantName;
	
	private String phone;
	
	private String city;
	
	private String lat;
	
	private String lon;
	
	private String address;

	private String createDate;

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public String getCreateDate() {
		if (createDate == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			createDate = sdf.format(new Date());
		}
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLon() {
		return lon;
	}

	public void setLon(String lon) {
		this.lon = lon;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	
}
