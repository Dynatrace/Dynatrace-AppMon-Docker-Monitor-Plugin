package com.dynatrace.docker.rest.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.dynatrace.docker.util.Protocol;

/**
 * A configuration object holding the necessary information to connect to and
 * authenticate against a HTTP server
 * 
 * @author Reinhard Pilz
 *
 */
@XmlRootElement(name = ServerConfig.TAG)
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(
		propOrder = {
				"connectionConfig",
				Credentials.TAG
		}
)
public final class ServerConfig {
	
	static final String TAG = "server";

	private Credentials credentials = null;
	private ConnectionConfig connectionConfig = null;
	
	/**
	 * @return the {@link Credentials} to use for Basic Authentication when
	 * 		opening a connection to the HTTP server or {@code null} if no
	 * 		authentication is required
	 */
	@XmlElement(name = Credentials.TAG)
	public final Credentials getCredentials() {
		return credentials;
	}
	
	/**
	 * Sets the {@link Credentials} to use for Basic Authentication when
	 * 		opening a connection to the HTTP server
	 * 
	 * @param credentials the {@link Credentials} to use for
	 * 		Basic Authentication when opening a connection to the HTTP server
	 * 		or {@code null} if no authentication is expected to be required
	 */
	public final void setCredentials(final Credentials credentials) {
		this.credentials = credentials;
	}
	
	public final void setCredentials(final String user, final String pass) {
		this.credentials = new Credentials(user, pass);
	}
	
	/**
	 * @return the {@link ConnectionConfig} holding address, protocol and port
	 * 		to connect to when opening a connection to the HTTP server
	 */
	@XmlElement(name = ConnectionConfig.TAG)
	public final ConnectionConfig getConnectionConfig() {
		return connectionConfig;
	}
	
	/**
	 * Sets the {@link ConnectionConfig} holding address, protocol and port
	 * 		to connect to when opening a connection to the HTTP server
	 * 
	 * @param connectionConfig the {@link ConnectionConfig} holding address,
	 * 		protocol and port to connect to when opening a connection to the
	 * 		HTTP server
	 */
	public final void setConnectionConfig(
			final ConnectionConfig connectionConfig
	) {
		this.connectionConfig = connectionConfig;
	}
	
	public final void setConnectionConfig(final Protocol protocol, final String host, final int port) {
		this.connectionConfig = new ConnectionConfig(protocol, host, port);
	}
	
	/**
	 * Checks if the configured {@link ConnectionConfig} is defined and valid
	 * and if the configured {@link Credentials} if configured are valid.
	 * 
	 * @return {@code true} if there is a {@link ConnectionConfig} set and valid
	 * 		and the {@link Credentials} are valid if defined, {@code false}
	 * 		otherwise.
	 */
	public final boolean isValid() {
		if (connectionConfig == null) {
			return false;
		}
		if (!connectionConfig.isValid()) {
			return false;
		}
		if (credentials != null) {
			return credentials.isValid();
		}
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		return new StringBuilder().append(connectionConfig.getHost()).append(":").append(connectionConfig.getPort()).toString();
	}
	
}
