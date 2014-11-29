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
			String username = json.findPath("username").textValue();
			String privacy = json.findPath("privacy").textValue();
			String address1 = json.findPath("address1").textValue();

			//how to we check for uniqueness? 
//			if (isExist(userName)) {
//				result.put("status", "Event Already Exists");
//				return ok("{Event Exists:" + result + "}");
//			}
			EntourageUser user = UserController.getUserByUsername(username); 
			
			String host = user.getUserName(); 
			int user_id = user.getUid(); 
			
			Logger.debug("ENTOURAGE API: createEvent() : Entourage user host ", host); 
			Logger.debug("ENTOURAGE API: createEvent() : Entourage user id ", user_id); 
			Event event = new Event(name, type, host,
					privacy, address1, user_id);

			//Logger.debug("ENTOURAGE API: creatEvent(): New Event :", event.toString() + "\n");
			session.save(event);
			tx.commit();
			result.put("status", "Event " + name + "created!");

			return ok("{Event Creation Response:" + result + "}");

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
		
		String username = json.findPath("username").textValue(); 
		String name = json.findPath("name").textValue(); 
		String type = json.findPath("type").textValue(); 
		String privacy = json.findPath("privacy").textValue(); 
		String address1 = json.findPath("address1").textValue(); 
		
		try{
			tx = session.beginTransaction(); 
//			if (!isExist(username)) {
//				return badRequest("user does not exist!"); 
//			}
			
			//we must expand this to support multiple users with edit permissions (if this is to be allowed) 
			String sql = "SELECT * FROM events WHERE name = '" + name + "'" + " and host = '" + username + "'"; 
			SQLQuery query = session.createSQLQuery(sql); 
			query.addEntity(Event.class); 
			
			//should we use list instead? or just iterate the query object? 
			//TODO: how do we resolve the typesafe warning? 
			Iterator<Event> resultIterator = query.list().iterator(); 
			while (resultIterator.hasNext()){
				
				event = resultIterator.next(); 
				
				//refactor this code to use strategy pattern 
				if (name != null){ 
					event.setName(name); 
					result.put("Event name changed to  ", name); 
				}
				
				if (type != null){
					event.setType(type);
					result.put("Type changed to  ", type); 
				}
				
				if (privacy != null){ 
					event.setPrivacy(privacy); 
					result.put("Privacy setting changed to ", privacy); 
				}
				
				if (address1 != null){
					event.setAddress1(address1); 
					result.put("Address changed to ", address1); 
				}
			    
				
			}
			session.save(event); 
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
		Logger.debug("ENTOURAGE API : calling getEventDetails()"); 
		
		Session session = HibernateUtil.getSessionFactory().openSession(); 
		Transaction tx = null; 
	
		
		JsonNode json = request().body().asJson(); 
		
		Logger.debug("ENTOURAGE API: getEventDetails(): incoming json " + json); 
		ObjectNode result = Json.newObject(); 
		
		String name = json.findPath("name").textValue(); 
		String host = json.findPath("host").textValue(); 
		
		
		try{
			tx = session.beginTransaction(); 
			
			String sql = "SELECT * FROM events WHERE name = '" + name + "' and host ='" + host + "'"; 
			SQLQuery query = session.createSQLQuery(sql); 
			query.addEntity(Event.class); 
			
			Iterator<Event> resultList = query.list().iterator(); 
			
			while (resultList.hasNext()){
				Event event = resultList.next(); 
				JsonNode eventNode = Json.toJson(event); 
				result.put("Detailed Event", eventNode); 
			}
			
			tx.commit(); 
			//result.put(arg0, arg1); 
			return ok(result); 
		} catch (HibernateException e){
			tx.rollback(); 
			e.printStackTrace(); 
		} finally {
			session.close(); 
		}
		
		return null; 
	}
	

	//this method will probably aggregate other method calls to pull back multiple forms of data
	//this will be data distinguished by location (live from the event) event reporter...
	//all comments/pics/checkin stats/vids/vanity notifications (bottles ordered, numbers/dances gotten?) that fall within the event times from posted while checked into the location (another incentive to check in ) 
	public static Result getLiveEventData(){
		return TODO; 
	}
	
	public static Result deleteEvent(){
		Session session = HibernateUtil.getSessionFactory().openSession(); 
		Transaction tx = null;
		
		JsonNode json = request().body().asJson(); 
		ObjectNode result = Json.newObject(); 
		
		
		// add time parameter for total uniqueness later on 
		String name = json.findPath("name").textValue(); 
		String host = json.findPath("host").textValue(); 
		int deleteCount = 0; 
		
		try {
			tx = session.beginTransaction(); 
			String sql = "DELETE FROM events WHERE name = '" + name + "' and host ='" + host + "'"; 
			SQLQuery query = session.createSQLQuery(sql); 
			query.addEntity(Event.class); 
			
			deleteCount  = query.executeUpdate(); 
			
			tx.commit();  
		} catch (HibernateException e){
			tx.rollback(); 
			e.printStackTrace(); 
		} finally {
			session.close(); 
		}
		
		if (deleteCount > 0){
		return ok(result.toString());
		}
		
		return null ;
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
			 
		}catch (HibernateException e){
			tx.rollback(); 
			e.printStackTrace(); 
		}finally {
			session.close(); 
		}
		return ok(result);
		
	}
	

	//add to table that bridges user/registered/rsvp'd events (events the user is more than likely attending) 
	public static Result addEventToTimeline(){
		return TODO; 
	}
	
	public static Result removeEventFromTimeline(){
		return TODO; 
	}
	
	//add users to live table that ties users to the event
	public static Result checkIn(){
		return TODO; 
	}
}
