package com.github.neone35.chargent.model;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
@Generated("com.robohorse.robopojogenerator")
public class Model{

	@SerializedName("photoUrl")
	String photoUrl;

	@SerializedName("id")
	int id;

	@SerializedName("title")
	String title;

	// empty constructor needed by the Parceler library
	public Model() { }

	public Model(String photoUrl, int id, String title) {
		this.photoUrl = photoUrl;
		this.id = id;
		this.title = title;
	}

	public void setPhotoUrl(String photoUrl){
		this.photoUrl = photoUrl;
	}

	public String getPhotoUrl(){
		return photoUrl;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setTitle(String title){
		this.title = title;
	}

	public String getTitle(){
		return title;
	}

	@Override
 	public String toString(){
		return 
			"Model{" + 
			"photoUrl = '" + photoUrl + '\'' + 
			",id = '" + id + '\'' + 
			",title = '" + title + '\'' + 
			"}";
		}
}