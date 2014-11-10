package controllers;

import java.sql.*;

public class EntourageUser{
	private int uid;
	private String lastName, firstName, birthDate, title, address1, address2,
			city, state, postal, phone, email, password, userName;
	private String creationDate;
	private Blob profilePictureMedium, profilePictureSmall;

	public EntourageUser() {
	}

	public EntourageUser(String lastName, String firstName, String birthDate,
			String title, String address1, String address2, String city,
			String state, String postal, String phone, String email,
			String password, String creationDate, //Blob picMedium, Blob picSmall,
			String userName) {
		this.setLastName(lastName);
		this.setFirstName(firstName);
		this.setBirthDate(birthDate);
		this.setTitle(title);
		this.setAddress1(address1);
		this.setAddress2(address2);
		this.setCity(city);
		this.setState(state);
		this.setPostal(postal);
		this.setPhone(phone);
		this.setEmail(email);
		this.setPassword(password);
		this.setUserName(userName);
		this.setCreationDate(creationDate);
		//this.setProfilePictureMedium(picMedium);
		//this.setProfilePictureSmall(picSmall);
	}

	/**
	 * @return the UID
	 */
	public int getUid() {
		return uid;
	}

	/**
	 * @param UID
	 *            the UID to set
	 */
	public void setUid(int uid) {
		this.uid = uid;
	}

	/**
	 * @return the LastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param LastName
	 *            the LastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the FirstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param FirstName
	 *            the FirstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the BirthDate
	 */
	public String getBirthDate() {
		return birthDate;
	}

	/**
	 * @param BirthDate
	 *            the BirthDate to set
	 */
	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}

	/**
	 * @return the Title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param Title
	 *            the Title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the address1
	 */
	public String getAddress1() {
		return address1;
	}

	/**
	 * @param address1
	 *            the address1 to set
	 */
	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	/**
	 * @return the address2
	 */
	public String getAddress2() {
		return address2;
	}

	/**
	 * @param address2
	 *            the address2 to set
	 */
	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city
	 *            the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @return the postal
	 */
	public String getPostal() {
		return postal;
	}

	/**
	 * @param postal
	 *            the postal to set
	 */
	public void setPostal(String postal) {
		this.postal = postal;
	}

	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone
	 *            the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the displayName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName
	 *            the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the creationDate
	 */
	public String getCreationDate() {
		return creationDate;
	}

	/**
	 * @param creationDate
	 *            the creationDate to set
	 */
	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @return the profilePictureMedium
	 */
	public Blob getProfilePictureMedium() {
		return profilePictureMedium;
	}

	/**
	 * @param profilePictureMedium
	 *            the profilePictureMedium to set
	 */
	public void setProfilePictureMedium(Blob profilePictureMedium) {
		this.profilePictureMedium = profilePictureMedium;
	}

	/**
	 * @return the profilePictureSmall
	 */
	public Blob getProfilePictureSmall() {
		return profilePictureSmall;
	}

	/**
	 * @param profilePictureSmall
	 *            the profilePictureSmall to set
	 */
	public void setProfilePictureSmall(Blob profilePictureSmall) {
		this.profilePictureSmall = profilePictureSmall;
	}

	public void clear() {
		uid = 0;
		lastName = "";
		firstName = "";
		birthDate = "";
		title = "";
		address1 = "";
		address2 = "";
		city = "";
		state = "";
		postal = "";
		phone = "";
		email = "";
		password = "";
		userName = "";
		creationDate = null;
		profilePictureMedium = null;
		profilePictureSmall = null;
	}
}
