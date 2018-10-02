package com.github.neone35.chargent.model;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
@Generated("com.robohorse.robopojogenerator")
public class Location{

	@SerializedName("address")
	String address;

	@SerializedName("latitude")
	double latitude;

	@SerializedName("id")
	int id;

	@SerializedName("longitude")
	double longitude;

    // empty constructor needed by the Parceler library
    public Location() { }

    public Location(String address, double latitude, int id, double longitude) {
        this.address = address;
        this.latitude = latitude;
        this.id = id;
        this.longitude = longitude;
    }

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