package amazonaws;

import java.util.*;

import org.hibernate.*;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import util.HibernateUtil;

public class AWSController extends Controller {

	public static Result upload() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			Http.MultipartFormData body = request().body()
					.asMultipartFormData();
			Http.MultipartFormData.FilePart uploadFilePart = body
					.getFile("uploaded");
			if (uploadFilePart != null) {
				S3AmazonService s3 = new S3AmazonService();
				s3.uid = UUID.randomUUID();
				s3.name = uploadFilePart.getFilename();
				s3.file = uploadFilePart.getFile();
				s3.save(); // saves image to AWS
				session.save(s3); // saves image meta-data to DB
				tx.commit();
			} else {
				return badRequest("File upload error");
			}
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
