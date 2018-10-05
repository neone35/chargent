package com.github.neone35.chargent.model;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
@Generated("com.robohorse.robopojogenerator")
public class Car implements Comparable<Car> {

	@SerializedName("batteryEstimatedDistance")
	int batteryEstimatedDistance;

	@SerializedName("location")
	Location location;

	@SerializedName("model")
	Model model;

	@SerializedName("batteryPercentage")
	int batteryPercentage;

	@SerializedName("isCharging")
	boolean isCharging;

	@SerializedName("id")
	int id;

	@SerializedName("plateNumber")
	String plateNumber;

	float distanceFromUser;

    // empty constructor needed by the Parceler library
	public Car() { }

    public Car(int batteryEstimatedDistance, Location location, Model model, int batteryPercentage,
               boolean isCharging, int id, String plateNumber) {
        this.batteryEstimatedDistance = batteryEstimatedDistance;
        this.location = location;
        this.model = model;
        this.batteryPercentage = batteryPercentage;
        this.isCharging = isCharging;
        this.id = id;
        this.plateNumber = plateNumber;
    }

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

    public boolean isCharging() {
        return isCharging;
    }

    public void setCharging(boolean charging) {
        isCharging = charging;
    }

    public float getDistanceFromUser() {
        return distanceFromUser;
    }

    public void setDistanceFromUser(float distanceFromUser) {
        this.distanceFromUser = distanceFromUser;
    }

    @Override
    public int compareTo(Car car) {
        return 0;
    }
}