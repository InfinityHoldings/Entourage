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
		Session session = HibernateUtil.getSessionFactory().openSession();
		JsonNode json = request().body().asJson();
		Transaction tx = null;
		ObjectNode result = Json.newObject();

		try {
			tx = session.beginTransaction();
			String userName = json.findPath("username").textValue();
			String email = json.findPath("email").textValue();
			String city = json.findPath("city").textValue();
			String state = json.findPath("state").textValue();
			String password = json.findPath("password").textValue();

			if (isExist(userName)) {
				result.put("status", "0");
				return ok("{UserExist:" + result + "}");
			}
			EntourageUser user = new EntourageUser(userName, email, city,
					state, password);

			Logger.debug("SignUp EntourageUser", user.toString());
			session.save(user);
			tx.commit();
			result.put("status", "1");
			return ok("{UserExist:" + result + "}");
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
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = null;
		JsonNode json = request().body().asJson();
		ObjectNode result = Json.newObject();
		if (json == null) {
			return badRequest("Expecting Json data");
		} else {
			try {
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

	public static boolean authenticate(String userName, String password,
			String passwordHash) {
		if (userName != null && BCrypt.checkpw(password, passwordHash)) {
			return true;
		} else {
			return false;
		}
	}
}