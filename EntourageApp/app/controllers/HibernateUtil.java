package controllers;

import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.*;
import org.hibernate.boot.registry.*;

public class HibernateUtil {

	private static final SessionFactory sessionFactory;
	static {
		try {
			Configuration cfg = new Configuration()
					.configure("hibernate.cfg.xml");
			StandardServiceRegistryBuilder sb = new StandardServiceRegistryBuilder()
					.applySettings(cfg.getProperties());
			ServiceRegistry standardServiceRegistry = sb.build();
			sessionFactory = cfg.buildSessionFactory(standardServiceRegistry);
		} catch (Throwable ex) {
			// Make sure you log the exception, as it might be swallowed
			System.err.println("Initial SessionFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public static void closeSession() {
		getSessionFactory().close();
	}

}