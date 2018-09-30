package com.github.neone35.chargent.model;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class Model{

	@SerializedName("photoUrl")
	private String photoUrl;

	@SerializedName("id")
	private int id;

	@SerializedName("title")
	private String title;

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