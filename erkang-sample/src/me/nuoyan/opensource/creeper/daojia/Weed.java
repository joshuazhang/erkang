package me.nuoyan.opensource.creeper.daojia;

import me.nuoyan.opensource.creeper.persistence.Transparent;

public class Weed {

	private String url;
	
	private String name;
	
	private Double price;
	
	private String createTime;
	
	private String unit;
	
	private String remark;
	
	private String catagory;
	
	private String shopName;
	
	private String shopAddress;
	
	private String shopCatagory;
	
	private String shopCity;
	
	private String city;
	
	@Transparent
	private Integer sid;//??????shop id
	
	public Integer getSid() {
		return sid;
	}

	public void setSid(Integer sid) {
		this.sid = sid;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getShopCity() {
		return shopCity;
	}

	public void setShopCity(String shopCity) {
		this.shopCity = shopCity;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getCatagory() {
		return catagory;
	}

	public void setCatagory(String catagory) {
		this.catagory = catagory;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public String getShopAddress() {
		return shopAddress;
	}

	public void setShopAddress(String shopAddress) {
		this.shopAddress = shopAddress;
	}

	public String getShopCatagory() {
		return shopCatagory;
	}

	public void setShopCatagory(String shopCatagory) {
		this.shopCatagory = shopCatagory;
	}
	
}
