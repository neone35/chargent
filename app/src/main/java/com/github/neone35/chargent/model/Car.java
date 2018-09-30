package com.github.neone35.chargent.model;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class Car {

	@SerializedName("batteryEstimatedDistance")
	private int batteryEstimatedDistance;

	@SerializedName("location")
	private Location location;

	@SerializedName("model")
	private Model model;

	@SerializedName("batteryPercentage")
	private int batteryPercentage;

	@SerializedName("isCharging")
	private boolean isCharging;

	@SerializedName("id")
	private int id;

	@SerializedName("plateNumber")
	private String plateNumber;

	public void setBatteryEstimatedDistance(int batteryEstimatedDistance){
		this.batteryEstimatedDistance = batteryEstimatedDistance;
	}

	public int getBatteryEstimatedDistance(){
		return batteryEstimatedDistance;
	}

	public void setLocation(Location location){
		this.location = location;
	}

	public Location getLocation(){
		return location;
	}

	public void setModel(Model model){
		this.model = model;
	}

	public Model getModel(){
		return model;
	}

	public void setBatteryPercentage(int batteryPercentage){
		this.batteryPercentage = batteryPercentage;
	}

	public int getBatteryPercentage(){
		return batteryPercentage;
	}

	public void setIsCharging(boolean isCharging){
		this.isCharging = isCharging;
	}

	public boolean isIsCharging(){
		return isCharging;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setPlateNumber(String plateNumber){
		this.plateNumber = plateNumber;
	}

	public String getPlateNumber(){
		return plateNumber;
	}

	@Override
 	public String toString(){
		return 
			"Car{" +
			"batteryEstimatedDistance = '" + batteryEstimatedDistance + '\'' + 
			",location = '" + location + '\'' + 
			",model = '" + model + '\'' + 
			",batteryPercentage = '" + batteryPercentage + '\'' + 
			",isCharging = '" + isCharging + '\'' + 
			",id = '" + id + '\'' + 
			",plateNumber = '" + plateNumber + '\'' + 
			"}";
		}
}