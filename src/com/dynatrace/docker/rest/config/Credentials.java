package com.dynatrace.docker.rest.config;

import static com.dynatrace.docker.util.StrUtil.COLON;
import static com.dynatrace.docker.util.StrUtil.isNullOrEmpty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;


/**
 * A configuration object holding user credentials
 * 
 * @author Reinhard Pilz
 *
 */
@XmlRootElement(name = Credentials.TAG)
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(
		propOrder = {
				Credentials.ATTRIBUTE_USER,
				Credentials.ATTRIBUTE_PASS
		}
)
public final class Credentials {
	
	private static final String ERR_MSG_NO_USERNAME =
			"credentials do not contain a username".intern();
	private static final String ERR_MSG_NO_PASSWORD =
			"credentials do not contain a password".intern();
	
	static final String TAG = "credentials";
	static final String ATTRIBUTE_USER = "user";
	static final String ATTRIBUTE_PASS = "pass";

	private String user = null;
	private String pass = null;
	
	public Credentials() {
	}
	
	public Credentials(final String user, final String pass) {
		this.user = user;
		this.pass = pass;
	}
	
	/**
	 * @return the user name for authentication
	 */
	@XmlAttribute(name = Credentials.ATTRIBUTE_USER)
	public final String getUser() {
		return user;
	}
	
	/**
	 * Sets the user name for authentication
	 * 
	 * @param user the user name for authentication
	 */
	public final void setUser(final String user) {
		this.user = user;
	}
	
	/**
	 * @return the plain text password for authentication
	 */
	@XmlAttribute(name = Credentials.ATTRIBUTE_PASS)
	public final String getPass() {
		return pass;
	}
	
	/**
	 * Sets the plain text password for authentication
	 * 
	 * @param pass the plain text password for authentication
	 */
	public final void setPass(final String pass) {
		this.pass = pass;
	}
	
	/**
	 * Checks if both the configured user name and password are not {@code null}
	 * and not empty.
	 * 
	 * @return {@code true} if both, the user name and the password are not
	 * 		{@code null} and not empty, {@code false} otherwise
	 */
	@XmlTransient
	public final boolean isValid() {
		if (isNullOrEmpty(user)) {
			return false;
		}
		if (isNullOrEmpty(pass)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Encodes the configured user name and password to using a BASE64 Encoder
	 * to be used for Basic Authentication (HTTP).
	 * 
	 * @return the encoded user credentials
	 * 
	 * @throws IllegalArgumentException if either the user name or the password
	 * 		are {@code null} or empty
	 */
	@SuppressWarnings("restriction")
	public final String encode() {
		if (isNullOrEmpty(user)) {
			throw new IllegalArgumentException(ERR_MSG_NO_USERNAME);
		}
		if (isNullOrEmpty(pass)) {
			throw new IllegalArgumentException(ERR_MSG_NO_PASSWORD);
		}
		final String userPassword =	user + COLON + pass;
		return new sun.misc.BASE64Encoder().encode(userPassword.getBytes());
	}
	
}
