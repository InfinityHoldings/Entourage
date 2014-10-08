package controllers;

import java.util.*;
import org.hibernate.*;
import org.mindrot.jbcrypt.*;
import java.lang.NullPointerException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

public class UserController extends Controller {

	private static boolean _validate = false;
	private static String _uname, _passwordHash;

	public static Result createUserAccount(String lname, String fname,
			String birthDate, String title, String address1, String address2,
			String city, String state, String postal, String phone,
			String email, String password, String creationDate, String userName) {// Blob
																					// picMedium,
																					// Blob
																					// picSmall,

		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			EntourageUser entUser = new EntourageUser(lname, fname, birthDate,
					title, address1, address2, city, state, postal, phone,
					email, password, creationDate, userName);
			entUser.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
			session.save(entUser);
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return ok("User created!");
	}

	public static Result getAllUsers() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			String sql = "Select * from ent_user";
			SQLQuery q = session.createSQLQuery(sql);
			q.addEntity(EntourageUser.class);
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

	public static Result UserDetail(String userName) {
		return ok("Display id: " + userName + " user info");
	}

	public static Result updateUserSecuredInfo(String userName) {

		return ok("Update Secured Information");
	}

	public static Result updateUserNormalInfo(String userName) {
		return ok("Update Normal Information");
	}

	public static Result deleteUser(String userName) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			String sql = "DELETE FROM ent_user WHERE username = '" + userName
					+ "'";
			SQLQuery q = session.createSQLQuery(sql);
			q.addEntity(EntourageUser.class);
			int deleted = q.executeUpdate();
			tx.commit();
			return ok("Deleted: " + deleted + " user(s)");
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return ok("User deleted!");
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

	public static Result login() {
		JsonNode json = request().body().asJson();
		ObjectNode result = Json.newObject();
		if (json == null) {
			return badRequest("Expecting Json data");
		} else {
			String username = json.findPath("username").textValue();
			String password = json.findPath("password").textValue();
			if (username == null || password == null) {
				result.put("status", "KO");
				result.put("message", "Missing parameter [name]");
				return badRequest(result);
			} else {
				result.put("status", "OK");
				result.put("username", "Hello " + username);
				result.put("password", password);
				return ok(result);
			}
		}
	}

	public static Result login2(String username, String password) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			String sql = "Select * from ent_user where username = '" + username
					+ "';";
			SQLQuery q = session.createSQLQuery(sql);
			q.addEntity(EntourageUser.class);
			Iterator<EntourageUser> iterator = q.list().iterator();
			try {
				while (iterator.hasNext()) {
					EntourageUser entUser = iterator.next();
					_uname = entUser.getUserName();
					// _passwordHash = entUser.getPassword();
					// _validate = authenticate(username, password,
					// _passwordHash);

					if (username.equalsIgnoreCase(_uname)) {
						return ok("ok");
					} else {
						return ok("No");
					}
				}
			} catch (NullPointerException ex) {
				ex.printStackTrace();
			}
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

	@BodyParser.Of(BodyParser.Json.class)
	public static Result loginbones(){
		String uname = request().getQueryString("username"); 
	//	String postname = request().body().asText(); 
		RequestBody body = request().body(); 
		String 	requestString = body.toString(); 
//		JsonNode node = request().body().asJson(); 
//		String nodename = node.findPath("username").textValue(); 
//		
		System.out.print("Request String = " + requestString + "\n" + "JSON = " + body.asText() + "\n"); //+ requestString + "\n"); // + postname.findValue("username")); 
		
		return ok("Hello, "+ uname); 
	}
	
	public static boolean authenticate(String userName, String password,
			String passwordHash) {
		if (username != null && BCrypt.checkpw(password, passwordHash)) {
			return true;
		} else {
			return false;
		}
	}
}
