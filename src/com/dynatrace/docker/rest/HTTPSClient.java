package com.dynatrace.docker.rest;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import com.dynatrace.docker.rest.HTTPClient.Method;
import com.dynatrace.docker.util.IOUtils;

public class HTTPSClient {
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
	

	public String request(final URL url,
			final Method method,
			final String certificateFile,
			final boolean isSelfSigned,
			boolean unlimitedRead)
		throws IOException, KeyStoreException, NoSuchAlgorithmException,
	               CertificateException, KeyManagementException {
		InputStream in = null;
		ByteArrayOutputStream baos= null;

	    X509Certificate cert = null;
	    FileInputStream pemFileStream = new FileInputStream(new File(certificateFile));
	        CertificateFactory certFactory = CertificateFactory.getInstance("X509");
	        cert = (X509Certificate) certFactory.generateCertificate(pemFileStream);
	    SSLContext sslContext = SSLContext.getInstance("TLS");
	    // If the certificate is self signed, using a dummy trust manager.
	    if (isSelfSigned) {
	    	ServerTrustManager serverTrustManager = new ServerTrustManager();
	    	sslContext.init(null, new TrustManager[]{serverTrustManager}, null);
	    }
	    else {
		    //create truststore
		    KeyStore trustStore = KeyStore.getInstance("JKS");
		    trustStore.load(null); //create an empty trustore
		    //add certificate to truststore - you can use a simpler alias
		    String alias = cert.getSubjectX500Principal().getName() + "["
		            + cert.getSubjectX500Principal().getName().hashCode() + "]";
		    trustStore.setCertificateEntry(alias, cert);
		    TrustManagerFactory trustManagerFactory =
		 	       TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		    trustManagerFactory.init(trustStore);
		    sslContext.init(null, trustManagerFactory.getTrustManagers(), null);
	    }

	    HttpsURLConnection conn=null;
	    try {
	    	conn = (HttpsURLConnection) url.openConnection();
	    	conn.setSSLSocketFactory(sslContext.getSocketFactory());
	    	conn.setRequestMethod("GET");
	    	conn.connect();
	    	if (unlimitedRead) {
	    		return readUnlimited(conn);
	    	}
	    	else {
	    		return readLimited(conn);
	    	}
	    } catch (final IOException e) {
	    	throw e;
	    } finally {
	    	IOUtils.close(in);
	    	IOUtils.close(baos);
	    	if ( conn != null ) {
	    		conn.disconnect();
	    	}
	    }

	}

	private String readUnlimited(HttpsURLConnection connection) throws IOException {
		int responseCode = 0;
		InputStream in = null;
		ByteArrayOutputStream baos= null;
		try {
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
	
	private String readLimited(HttpsURLConnection connection) throws IOException {
		int responseCode = 0;
		InputStream in = null;
		try {
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


}
