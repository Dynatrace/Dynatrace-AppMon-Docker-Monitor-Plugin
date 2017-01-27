package com.dynatrace.docker.rest;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Objects;

import com.dynatrace.docker.rest.HTTPClient.Method;
import com.dynatrace.docker.rest.config.ConnectionConfig;
import com.dynatrace.docker.rest.config.ServerConfig;
import com.dynatrace.docker.util.Protocol;

/**
 * Base class for all REST Requests to be executed to the dynaTrace Server
 * 
 * @author Asad Ali

 *
 */
public class Request {
	static final String ERR_MSG_INVALID_SERVER_CONFIG =
			"Server Config is not valid".intern();
	
	private String path = null;
	/**
	 * Executes the REST Request to the dynaTrace Server
	 * 
	 * @param config the configuration holding the details about how to connect
	 * 		to the dynaTrace Server
	 * @param out the {@link OutputStream} to send the response to
	 * 
	 * @throws IOException if sending the request fails
	 */
	public String execute(final ServerConfig config, boolean unlimitedRead) throws IOException {
		Objects.requireNonNull(config);
		if (!config.isValid()) {
			throw new IllegalArgumentException(ERR_MSG_INVALID_SERVER_CONFIG);
		}
		final ConnectionConfig connectionConfig = config.getConnectionConfig();
		final URL url = connectionConfig.createURL(getPath());
		String responseStr=null;
		if (connectionConfig.getProtocol() == Protocol.HTTP) {
			HTTPClient httpClient = new HTTPClient();
			responseStr = httpClient.request(
					url,
					Method.GET,
					config.getCredentials(), unlimitedRead);
		}
		else {
			HTTPSClient httpsClient = new HTTPSClient();
			try {
			responseStr = httpsClient.request(
					url,
					Method.GET,
					connectionConfig.getCertificateFile(),
					connectionConfig.getIsSelfSigned(), unlimitedRead);
			}
			catch (IOException | KeyStoreException | NoSuchAlgorithmException |
		               CertificateException | KeyManagementException exception) {
				throw new IOException(exception);
			}
			
		}
		if (responseStr == null) {
			throw new IOException("Response code is not OK");
		}
		return responseStr;
	}
	

	protected final String getPath() {
		return path;
	}

	public final void setPath(String path) {
		this.path = path;
	}
}
