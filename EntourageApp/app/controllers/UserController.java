package controllers;

import java.util.*;
import models.EntourageUser;

import org.hibernate.*;
import org.mindrot.jbcrypt.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import util.HibernateUtil;

public class UserController extends Controller {
	static Session session = null;
	// private static boolean _validate = false;
	// private static String _uname, _password;
	// private static boolean _validate = false;
	private static String _uname, _password;

	public static Result signUp() {
		
		Logger.debug("ENTOURAGE API: signUp() called\n");
		
		Session session = HibernateUtil.getSessionFactory().openSession();
		JsonNode json = request().body().asJson();
		Transaction tx = null;
		ObjectNode result = Json.newObject();

		if (json == null) {
			Logger.debug("ENTOURAGE API: signUp() : incoming json was null");
			return badRequest("Expecting Json data");
		}
		
		Logger.debug("ENTOURAGE API: signUp() : incoming json: " + json.toString() + "\n"); 
		try {
			tx = session.beginTransaction();
			String userName = json.findPath("username").textValue();
			String email = json.findPath("email").textValue();
			String city = json.findPath("city").textValue();
			String state = json.findPath("state").textValue();
			String password = json.findPath("password").textValue();

			if (isExist(userName)) {
				result.put("status", "Username Already Exists");
				return ok("{UserExist:" + result + "}");
			}
			EntourageUser user = new EntourageUser(userName, email, city,
					state, password);

			Logger.debug("ENTOURAGE API: signUp(): New EntourageUser :", user.toString() + "\n");
			session.save(user);
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

	public static Result login() {
		Logger.debug("ENTOURAGE API: login() called\n");
		
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = null;
		JsonNode json = request().body().asJson();
		ObjectNode result = Json.newObject();
		
		
		if (json == null) {
			Logger.debug("ENTOURAGE API: incoming json was null");
			return badRequest("Expecting Json data");
		} else {
			try {
				Logger.debug("ENTOURAGE API: incoming json: " + json.toString() + "\n"); 
				String username = json.findPath("username").textValue();
				String password = json.findPath("password").textValue();
				tx = session.beginTransaction();
				String sql = "Select * from ent_user where username = '"
						+ username + "' and password = '" + password + "';";
				SQLQuery q = session.createSQLQuery(sql);
				q.addEntity(EntourageUser.class);
				@SuppressWarnings("unchecked")
				Iterator<EntourageUser> iterator = q.list().iterator();
				if (!iterator.hasNext()) {

					result.put("status", "User Not Found");
					return ok("{User:" + result + "}");
				} else
					do {
						EntourageUser entUser = iterator.next();
						_uname = entUser.getUserName();
						_password = entUser.getPassword();
						// _passwordHash = entUser.getPassword();
						// _validate = authenticate(username, password,
						// _passwordHash);
						if (username.equalsIgnoreCase(_uname)
								&& password.equals(_password)) {
							result.put("status", "User Found");
							return ok("{User:" + result + "}");
						}
					} while (iterator.hasNext());
				tx.commit();
			} catch (HibernateException e) {
				if (tx != null)
					tx.rollback();
				e.printStackTrace();
			} finally {
				session.close();
			}
			return null;
		}
	}

	public static Result getAllUsers() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			String sql = "Select * from ent_user";
			SQLQuery q = session.createSQLQuery(sql);
			q.addEntity(EntourageUser.class);
			@SuppressWarnings("unchecked")
			Iterator<EntourageUser> iterator = q.list().iterator();
			while (iterator.hasNext()) {
				EntourageUser entUser = iterator.next();
				System.out.print("First Name: " + entUser.getFirstName());
				System.out.print("  Last Name: " + entUser.getLastName());
				System.out.println("  Display Name: " + entUser.getUserName());
			}
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return ok("All users listed!");
	}

	public static Result getUserDetail() {
		Logger.debug("ENTOURAGE API: getUserDetail() called"); 
		
		JsonNode json = request().body().asJson(); 
		Logger.debug("ENTOURAGE API: getUserDetail() : incoming JSON : " + json.toString()); 
		
		ObjectNode result = Json.newObject(); 
		
		//Get the static instance of the Hibernate session from the HibernateUtil class 
		//based on new info from the tutorial, we may want to create/destroy them as needed 
		// i.e Session session = factory.openSession(); 
		Session session = HibernateUtil.getSessionFactory().openSession(); 
		
		//Create a transaction object for the hibernate session 
		Transaction tx = null; 
		
		String jsonUsername = json.findPath("username").textValue(); 
			
		try{
			tx = session.beginTransaction(); 
			String sql = "SELECT * from ent_user WHERE username = '" + jsonUsername + "'"; 
			SQLQuery query = session.createSQLQuery(sql); 
			query.addEntity(EntourageUser.class); 
			List<EntourageUser> aueryResult = query.list(); 
			
			Iterator<EntourageUser> iterator = aueryResult.iterator(); 
			while(iterator.hasNext()){
				EntourageUser user = (EntourageUser) iterator.next(); 
				String username = user.getUserName(); 
				String city = user.getCity(); 
				String state = user.getState(); 
				
				Logger.debug("ENTOURAGE API: getUserDetail(): EntouageUser : " + user.toString()); 
				result.put("username", username); 
				result.put("city", city); 
				result.put("state", state); 
				
			}
			tx.commit(); 
		} catch (HibernateException e){
			if (tx != null){
				tx.rollback(); 
				e.printStackTrace(); 
			}
		} finally {
			session.close(); 
		}
		return ok("{ UserDetails : " + result + " }");
	}

	public static Result updateUserSecuredInfo(String userName) {

		return ok("Update Secured Information");
	}

	public static Result updateUser() {
		JsonNode json = request().body().asJson(); 
		ObjectNode result = Json.newObject(); 
		
		Session session = HibernateUtil.getSessionFactory().openSession(); 
		Transaction tx = null; 
		
		//is this bad implementation with the double call? 
		//is it using multiple sessions for the getidbyusername method call and the udateuser call?
		//is it possible to perform two separate transactions sequentially?  
		EntourageUser user = null; 
		
		String username = json.findPath("username").textValue(); 
		String newUsername = json.findPath("newUsername").textValue(); 
		String city = json.findPath("city").textValue(); 
		String state = json.findPath("state").textValue(); 
		
		try{
			tx = session.beginTransaction(); 
			if (!isExist(username)) {
				
				return badRequest("user does not exist!"); 
			}
			
			//call session.save instead of the line below. take advantage of the orm
			//refactor buildUpdateQuery method to call selective object setters instead(i believe this is the strategy pattern) 
			//String sql = "UPATE ent_user SET " + buildUpdateQuery(json); 
			String sql = "SELECT * FROM ent_user WHERE username = '" + username + "'"; 
			SQLQuery query = session.createSQLQuery(sql); 
			query.addEntity(EntourageUser.class); 
			
			//should we use list instead? or just iterate the query object? 
			//TODO: how do we resolve the typesafe warning? 
			Iterator<EntourageUser> resultIterator = query.list().iterator(); 
			while (resultIterator.hasNext()){
				
				user = resultIterator.next(); 
				
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
		} catch (HibernateException e) {
			tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return ok("Updated the following fields: " + result);
	}
	
	static String buildUpdateQuery(JsonNode json){
		//Check the json paths for the specific groups/arrays. build query containing subset for each applicable group
		String sql = ""; 
		
		String username = json.findPath("username").textValue(); 
		String city = json.findPath("city").textValue(); 
		String state= json.findParent("state").textValue(); 
		String email = json.findPath("email").textValue(); 
		
		if (username != null) {
			if(sql != "") sql += ", "; 
			sql += "username = '" + username + "'"; 
		}

		if (city != null) {
			if(sql != "") sql += ", "; 
			sql += "city = '" + city + "'"; 
		}

		if (state != null) {
			if(sql != "") sql += ", "; 
			sql += "state = '" + state + "'"; 
		}

		if (email != null) {
			if(sql != "") sql += ", "; 
			sql += "email = '" + email + "'"; 
		}
		
		Logger.debug("Building Query for update : " + sql); 
		
		return sql; 
	}

	public static Result deleteUser() {
		Session session = HibernateUtil.getSessionFactory().openSession();

		Logger.debug("ENTOURAGE API: deleteUser() called");

		JsonNode json = request().body().asJson();

		Logger.debug("ENTOURAGE API: getUserDetail() : incoming JSON : " + json.toString());
		
		String username = json.findPath("username").textValue();
		int deleted = 0;
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			String sql = "DELETE FROM ent_user WHERE username = '" + username
					+ "'";
			SQLQuery q = session.createSQLQuery(sql);
			q.addEntity(EntourageUser.class);
			deleted = q.executeUpdate();
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		if (deleted > 0) {
			return ok("User " + username + " deleted!");
		}
		return null;
	}

	public static Result countUsers() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			String sql = "Select count(1) from ent_user";
			SQLQuery q = session.createSQLQuery(sql);
			q.addEntity(EntourageUser.class);
			int count = ((Long) q.uniqueResult()).intValue();
			return ok("There are " + count + "user(s)!");
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return null;
	}

	public static boolean isExist(String userName) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = null;
		boolean exist = false;
		try {
			tx = session.beginTransaction();
			String sql = "Select 1 FROM ent_user WHERE username = '" + userName
					+ "'";
			SQLQuery q = session.createSQLQuery(sql);
			@SuppressWarnings("unchecked")
			Iterator<EntourageUser> iterator = q.list().iterator();
			if (!iterator.hasNext())
				exist = false;
			else
				exist = true;
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return exist;
	}
	
	public static Integer getUserIdByUsername(String username){
		Integer id = null; 
		
		Session session = HibernateUtil.getSessionFactory().openSession(); 
		Transaction tx = null; 
		
		try {
			tx = session.beginTransaction(); 
			String sql = "SELECT * FROM ent_user WHERE username ='" + username + "'"; 
			SQLQuery query = session.createSQLQuery(sql); 
			query.addEntity(EntourageUser.class); 
			Iterator<EntourageUser> resultIterator = query.list().iterator(); 
			
			while( resultIterator.hasNext()){
				EntourageUser user = resultIterator.next(); 
				id = user.getUid(); 
			}
			
		} catch (HibernateException e){
			tx.rollback(); 
			e.printStackTrace(); 
		} finally {
			session.close();
		}
		return id;
	}
	
	public static EntourageUser getUserByUsername(String username){
		Integer id = null; 
		
		Session session = HibernateUtil.getSessionFactory().openSession(); 
		Transaction tx = null; 
		EntourageUser user = null; 
		
		try {
			tx = session.beginTransaction(); 
			String sql = "SELECT * FROM ent_user WHERE username ='" + username + "'"; 
			SQLQuery query = session.createSQLQuery(sql); 
			query.addEntity(EntourageUser.class); 
			Iterator<EntourageUser> resultIterator = query.list().iterator(); 
			
			while( resultIterator.hasNext()){
				user = resultIterator.next(); 
			
			}
			
		} catch (HibernateException e){
			tx.rollback(); 
			e.printStackTrace(); 
		} finally {
			session.close(); 
		}
		
		return user; 
	}

	public static boolean authenticate(String userName, String password,
			String passwordHash) {
		if (userName != null && BCrypt.checkpw(password, passwordHash)) {
			return true;
		} else {
			return false;
		}
	}
}