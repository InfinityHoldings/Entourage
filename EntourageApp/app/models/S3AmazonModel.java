package models;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class S3AmazonModel {

	@Id
	public UUID uid;

	private String bucket;

	public String name;

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
}