package controllers;

import java.util.Iterator;

import models.EntourageUser;
import models.Event;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import util.HibernateUtil;

public class EventController extends Controller {

	public static Result createEvent(){
		Logger.debug("ENTOURAGE API: createEvent() called\n");
		
		Session session = HibernateUtil.getSessionFactory().openSession();
		JsonNode json = request().body().asJson();
		Transaction tx = null;
		ObjectNode result = Json.newObject();

		if( json == null){
		Logger.debug("ENTOURAGE API: createEvent() : incoming json was null"); 
		}
		
		Logger.debug("ENTOURAGE API: createEvent() : incoming json: " + json.toString() + "\n"); 
		try {
			tx = session.beginTransaction();
			String name = json.findPath("name").textValue();
			String type = json.findPath("type").textValue();
			String host = json.findPath("host").textValue();
			String privacy = json.findPath("privacy").textValue();
			String address1 = json.findPath("address1").textValue();

			//how to we check for uniqueness? 
//			if (isExist(userName)) {
//				result.put("status", "Event Already Exists");
//				return ok("{Event Exists:" + result + "}");
//			}
			
			Event event = new Event(name, type, host,
					privacy, address1);

			Logger.debug("ENTOURAGE API: creatEvent(): New Event :", event.toString() + "\n");
			session.save(event);
			tx.commit();
			result.put("status", "User Created");

			return ok("{SignUp Response:" + result + "}");

		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return null;
	}
	
	public static Result updateEvent(){
		JsonNode json = request().body().asJson(); 
		ObjectNode result = Json.newObject(); 
		
		Session session = HibernateUtil.getSessionFactory().openSession(); 
		Transaction tx = null; 
		
		//is this bad implementation with the double call? 
		//is it using multiple sessions for the getidbyusername method call and the udateuser call?
		//is it possible to perform two separate transactions sequentially?  
		Event event = null; 
		
		String name = json.findPath("name").textValue(); 
		String type = json.findPath("type").textValue(); 
		String privacy = json.findPath("privacy").textValue(); 
		String address1 = json.findPath("address1").textValue(); 
		
		try{
			tx = session.beginTransaction(); 
//			if (!isExist(username)) {
//				return badRequest("user does not exist!"); 
//			}
			
			String sql = "SELECT * FROM events WHERE name = '" name + "'" + " and "; 
			SQLQuery query = session.createSQLQuery(sql); 
			query.addEntity(Event.class); 
			
			//should we use list instead? or just iterate the query object? 
			//TODO: how do we resolve the typesafe warning? 
			Iterator<EntourageUser> resultIterator = query.list().iterator(); 
			while (resultIterator.hasNext()){
				
				event = resultIterator.next(); 
				
				//refactor this code to use strategy pattern 
				if (city != null){ 
					user.setCity(city); 
					result.put("City changed to  ", city); 
				}
				
				if (state != null){
					user.setState(state);
					result.put("State changed to  ", state); 
				}
				
				if (newUsername != null){ 
					user.setUserName(username); 
					result.put("Username changed to ", username); 
				}
			    
				
			}
			session.save(user); 
			tx.commit(); 
		}catch (HibernateException e){
			tx.rollback(); 
			e.printStackTrace(); 
		}finally {
			session.close(); 
		}
		return ok("Updated the following fields: " + result); 
		
	}
	
	//events are uniquely identified by host+date+
	//must know when to index and what to index. this could be one of those cases 
	public static Result getEventDetails(){
		
		
		return TODO; 
	}
	

	public static Result getLiveEventData(){
		return TODO; 
	}
	
	public static Result deleteEvent(){
		return TODO; 
	}
	
	//how will we implement filter? 
	public static Result getEvents(){
		JsonNode json = request().body().asJson(); 
		ObjectNode result = Json.newObject(); 
		
		Session session = HibernateUtil.getSessionFactory().openSession(); 
		Transaction tx = null; 
		
		try{
			tx = session.beginTransaction(); 
			
			String sql = "Select * from events"; 
			SQLQuery query = session.createSQLQuery(sql);
			query.addEntity(Event.class); 
			Iterator<Event> resultList = query.list().iterator(); 
			
			while (resultList.hasNext()){
				int i = 1; 
				Event event = resultList.next(); 
				
				JsonNode eventNode = Json.toJson(event); 
				result.put("Event : " + i, eventNode); 
				i++; 
				//put the entire jsonnode in the result 
				
			}
			
			tx.commit(); 
			return ok(result); 
		}catch (HibernateException e){
			tx.rollback(); 
			e.printStackTrace(); 
		}finally {
			session.close(); 
		}
		return TODO; 
	}
	
	public static Result addEventToTimeline(){
		return TODO; 
	}
	
	public static Result removeEventFromTimeline(){
		return TODO; 
	}
	
	public static Result checkIn(){
		return TODO; 
	}
}
