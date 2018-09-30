package com.github.neone35.chargent.model;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class Location{

	@SerializedName("address")
	private String address;

	@SerializedName("latitude")
	private double latitude;

	@SerializedName("id")
	private int id;

	@SerializedName("longitude")
	private double longitude;

	public void setAddress(String address){
		this.address = address;
	}

	public String getAddress(){
		return address;
	}

	public void setLatitude(double latitude){
		this.latitude = latitude;
	}

	public double getLatitude(){
		return latitude;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setLongitude(double longitude){
		this.longitude = longitude;
	}

	public double getLongitude(){
		return longitude;
	}

	@Override
 	public String toString(){
		return 
			"Location{" + 
			"address = '" + address + '\'' + 
			",latitude = '" + latitude + '\'' + 
			",id = '" + id + '\'' + 
			",longitude = '" + longitude + '\'' + 
			"}";
		}
}