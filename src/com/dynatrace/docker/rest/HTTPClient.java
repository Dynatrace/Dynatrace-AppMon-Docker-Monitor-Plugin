package com.dynatrace.docker.rest;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Objects;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import com.dynatrace.docker.rest.config.Credentials;
import com.dynatrace.docker.util.IOUtils;

/**
 * A minimalistic HTTP Client
 * 
 * @author Asad Ali
 *
 */
public final class HTTPClient implements HostnameVerifier, X509TrustManager {
	
	public static final String AUTHORIZATION = "Authorization".intern();
	public static final String BASIC = "Basic ".intern();
	
	/**
	 * Supported HTTP Methods
	 * 
	 * @author Reinhard Pilz
	 *
	 */
	public static enum Method {
		GET, POST
	}
		
	/**
	 * Known HTTP Response Codes
	 * 
	 * @author Reinhard Pilz
	 *
	 */
	public static enum ResponseCode {
		OK(200),
		METHOD_NOT_ALLOWED(405);
		
		ResponseCode(final int code) {
			this.code = code;
		};
		
		private final int code;
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public final String toString() {
			return new StringBuilder(code).
					append(" (").
					append(name())
					.append(")").
					toString();
		}
		
		/**
		 * Checks if the given integer based response code matches up with
		 * the response code of this enum value.
		 * 
		 * @param code the integer response code to check against
		 * 
		 * @return {@code true} if the given integer response code matches up
		 * 		with the response code of this enum value
		 */
		public final boolean matches(final int code) {
			return (this.code == code);
		}
		
		/**
		 * Queries for the right enum value for the given integer response code
		 * 
		 * @param code the integer response code to query with
		 * 
		 * @return the enum value matching up with the given integer response
		 * 		code or {@code null} if none is matching
		 */
		public static final ResponseCode fromCode(final int code) {
			final ResponseCode[] values = values();
			for (ResponseCode value : values) {
				if (value.matches(code)) {
					return value;
				}
			}
			return null;
		}
	}
	
	/**
	 * Executes a request to the given {@link URL} using the given
	 * {@link Method} an optionally performs Basic Authentication with the
	 * given {@link Credentials}.
	 * 
	 * @param url the {@link URL} to send the request to
	 * @param method the {@link Method} to use for the request
	 * @param credentials the user credentials to be used for
	 * 		Basic Authentication or {@code null} if no authentication is
	 * 		expected to be required
	 * @param out the {@link OutputStream} where the response delivered by the
	 * 		HTTP Server should be streamed into.
	 * 
	 * @return the HTTP Response code delivered by the Server
	 * 
	 * @throws IOException if opening the connection to the HTTP Server fails
	 * @throws NullPointerException if the given {@link URL} or the given
	 * 		{@link Method} are {@code null}.
	 */
	public final String request(
			final URL url,
			final Method method,
			final Credentials credentials, 
			boolean unlimitedRead)
			throws IOException
	{
		Objects.requireNonNull(url);
		Objects.requireNonNull(method);
		if ( unlimitedRead) {
			return readUnlimited(url, method, credentials);
		}
		else {
			return readlimited(url, method, credentials);
		}
	}

	private String readUnlimited(URL url, Method method, Credentials credentials) throws IOException {
		int responseCode = 0;
		InputStream in = null;
		HttpURLConnection connection = null;
		ByteArrayOutputStream baos= null;
		try {
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod(method.name());
			setCredentials(connection, credentials);
			connection.connect();
			//final int contentLength = connection.getContentLength();
			in = connection.getInputStream();
			responseCode = connection.getResponseCode();
			if (responseCode != ResponseCode.OK.code) {
				return null;
			}
			baos = new ByteArrayOutputStream();
			IOUtils.copy(in, baos);
			String result = new String(baos.toByteArray());
			return result;
		} catch (final IOException e) {
			throw e;
		} finally {
			IOUtils.close(in);
			IOUtils.close(baos);
			if ( connection != null ) {
				connection.disconnect();
			}
		}
	}
	
	private String readlimited(URL url, Method method, Credentials credentials) throws IOException {
		int responseCode = 0;
		InputStream in = null;
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod(method.name());
			setCredentials(connection, credentials);
			connection.connect();
			//final int contentLength = connection.getContentLength();
			in = connection.getInputStream();
			responseCode = connection.getResponseCode();
			if (responseCode != ResponseCode.OK.code) {
				return null;
			}
			InputStreamReader reader = new InputStreamReader(in);
			BufferedReader buffReader = new BufferedReader(reader);
			String line = buffReader.readLine();
			return line;
		} catch (final IOException e) {
			throw e;
		} finally {
			IOUtils.close(in);
			if ( connection != null ) {
				connection.disconnect();
			}
		}
	}
	
	/**
	 * Prepares the given {@link HttpURLConnection} to authenticate via
	 * Basic Authentication using the given {@link Credentials}.
	 * 
	 * @param connection the {@link HttpURLConnection} to prepare for
	 * 		Basic Authentication
	 * @param credentials the {@link Credentials} providing user name and
	 * 		password for Basic Authentication
	 * 
	 * @throws NullPointerException if the given {@link HttpURLConnection} is
	 * 		{@code null}.
	 * @throws IllegalArgumentException if the given {@link Credentials} either
	 * 		don't contain a user name or password
	 */
	private void setCredentials(
			final HttpURLConnection connection,
			final Credentials credentials
	) {
		Objects.requireNonNull(connection);
		if (credentials == null) {
			return;
		}
		connection.setRequestProperty(
				AUTHORIZATION,
				BASIC + credentials.encode()
		);
	}
	
	@Override
	public final boolean verify(final String hostName, final SSLSession sslSession) {
		return true;
	}


	@Override
	public final void checkClientTrusted(final X509Certificate[] certs, final String authType) throws CertificateException {
	}


	@Override
	public final void checkServerTrusted(final X509Certificate[] certs, final String authType) throws CertificateException {
	}


	@Override
	public final X509Certificate[] getAcceptedIssuers() {
		return null;
	}
}
