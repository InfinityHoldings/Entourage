package models;

import java.util.Date;

public class Event {
	//Change eventtype to enum 
	//Change privacy to enum 
	private int evnt_id, ent_usr_uid; 
	
	private String name, type, host, privacy, address1; 
	private Date eventdate, starttime, endtime; 
	
	EntourageUser user; 
	Venue venue; 

	public Event(){
		
	}
	
	public Event(String name, String type, String host, String privacy, String address1, int ent_usr_uid){
		this.name = name; 
		this.type = type; 
		this.host = host; 
		this.privacy = privacy; 
		this.address1 = address1; 
		this.ent_usr_uid = ent_usr_uid; 
	}
	
	//Setters 
	
	public void setEvnt_id(int id){
		this.evnt_id = id; 
	}
	
	public void setEnt_usr_uid(int id){
		this.ent_usr_uid = id; 
	}
	
	public void setName(String name){
		this.name = name; 
	}
	
	public void setHost(String host){
		this.host = host; 
	}

	public void setType(String type){
		this.type = type; 
	}
	
	public void setPrivacy(String privacy){
		this.privacy = privacy; 
	}
	
	public void setAddress1(String address1){
		this.address1 = address1; 
	}
	
	
	//Getters
	
	public int getevnt_id(){
		return this.evnt_id; 
	}
	
	public int getEnt_usr_uid(){
		return this.ent_usr_uid; 
	}
	
	public String getName(){
		return this.name; 
	}
	
	public String getType(){
		return this.type; 
	}
	
	public String getHost(){
		// CHANGE THIS TO AN ACTUAL USER OBJECT 
		//may have to add annotations (how to we represent this relationship?
		return this.host; 
	}

	public String getPrivacy(){
		return this.privacy; 
	}
	
	public String getAddress1(){
		return this.address1; 
	}
}

