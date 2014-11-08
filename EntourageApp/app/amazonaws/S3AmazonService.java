package amazonaws;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import play.Logger;
import plugins.S3Plugin;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

@Entity
public class S3AmazonService {

	@Id
	public UUID uid;

	private String bucket;

	public String name;

	@Transient
	public File file;

	public URL getUrl() throws MalformedURLException {
		return new URL("https://s3.amazonaws.com/" + bucket + "/"
				+ getActualFileName());
	}

	private String getActualFileName() {
		return uid + "/" + name;
	}

	public void save() {
		if (S3Plugin.amazonS3 == null) {
			Logger.error("Could not save because amazonS3 was null");
			throw new RuntimeException("Could not save");
		} else {
			this.bucket = S3Plugin.s3Bucket;

			PutObjectRequest putObjectRequest = new PutObjectRequest(bucket,
					getActualFileName(), file);
			putObjectRequest.withCannedAcl(CannedAccessControlList.Private);

			S3Plugin.amazonS3.putObject(putObjectRequest); // upload file
		}
	}

	public void delete() {
		if (S3Plugin.amazonS3 == null) {
			Logger.error("Could not delete because amazonS3 was null");
			throw new RuntimeException("Could not delete");
		} else {
			S3Plugin.amazonS3.deleteObject(bucket, getActualFileName());
		}
	}

	public UUID getUid() {
		return uid;
	}

	public void setUid(UUID uid) {
		this.uid = uid;
	}

	public String getBucket() {
		return bucket;
	}

	public void setBucket(String bucket) {
		this.bucket = bucket;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
}