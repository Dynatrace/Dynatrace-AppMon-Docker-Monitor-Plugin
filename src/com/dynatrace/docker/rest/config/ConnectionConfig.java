package com.dynatrace.docker.rest.config;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.dynatrace.docker.util.Protocol;
import com.dynatrace.docker.util.StrUtil;

/**
 * A configuration object holding the necessary information to open a HTTP
 * connection to a server
 * 
 * @author Reinhard Pilz
 *
 */
@XmlRootElement(name = ConnectionConfig.TAG)
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(
		propOrder = {
				ConnectionConfig.ATTRIBUTE_PROTOCOL,
				ConnectionConfig.ATTRIBUTE_HOST,
				ConnectionConfig.ATTRIBUTE_PORT
		}
)
public final class ConnectionConfig {
	
	private static final int UNDEFINED_PORT = 0;

	static final String TAG					= "connection";
	static final String ATTRIBUTE_PROTOCOL	= "protocol";
	static final String ATTRIBUTE_HOST		= "host";
	static final String ATTRIBUTE_PORT		= "port";
	static final String CERTIFICATE_FILE = "certificateFile";
	static final String IS_SELF_SIGNED = "isSelfSigned";
	
	private Protocol protocol = null;
	private String host = null;
	private int port = UNDEFINED_PORT;
	private String certificateFile;
	private boolean isSelfSigned;
	
	public ConnectionConfig() {
		
	}
	
	public ConnectionConfig(final Protocol protocol, final String host, final int port) {
		this.protocol = protocol;
		this.host = host;
		this.port = port;
	}
	
	/**
	 * @return the {@link Protocol} to use for HTTP connections
	 */
	@XmlAttribute(name = ConnectionConfig.ATTRIBUTE_PROTOCOL)
	public final Protocol getProtocol() {
		return protocol;
	}
	
	/**
	 * Sets the {@link Protocol} to use for HTTP connections
	 * 
	 * @param protocol the {@link Protocol} to use for HTTP connections
	 */
	public final void setProtocol(final Protocol protocol) {
		this.protocol = protocol;
	}
	
	public final void setCertificateFile (String certificateFile) {
		this.certificateFile = certificateFile;
	}
	
	public final String getCertificateFile() {
		return this.certificateFile;
	}
	
	public final void setIsSelfSigned(boolean signed) {
		this.isSelfSigned = signed;
	}
	
	public final boolean getIsSelfSigned() {
		return this.isSelfSigned;
	}
	
	/**
	 * @return the host name of IP address of the HTTP server
	 */
	@XmlAttribute(name = ConnectionConfig.ATTRIBUTE_HOST)
	public final String getHost() {
		return host;
	}
	
	/**
	 * Sets the host name or IP address of the HTTP server
	 * 
	 * @param host the host name or IP address of the HTTP server
	 */
	public final void setHost(final String host) {
		this.host = host;
	}
	
	/**
	 * @return the port the HTTP server is expected to listen at
	 */
	@XmlAttribute(name = ConnectionConfig.ATTRIBUTE_PORT)
	public final int getPort() {
		return port;
	}
	
	/**
	 * Sets the port the HTTP server is expected to listen at
	 * 
	 * @param port the port the HTTP server is expected to listen at
	 */
	public final void setPort(final int port) {
		this.port = port;
	}
	
	/**
	 * Generates a {@link URL} based on the configured {@link Protocol}, host
	 * and port and the given server absolute path.
	 * 
	 * @param path the server absolute path of the {@link URL} to create
	 * 
	 * @return a {@link URL} referring to a request path on the server
	 * 		configured
	 * 
	 * @throws MalformedURLException if the {@link URL} to create would be
	 * 		invalid
	 * @throws NullPointerException if the given path is {@code null}
	 */
	public final URL createURL(final String path) throws MalformedURLException {
		Objects.requireNonNull(path);
		return new URL(protocol.name(), host, port, path);
	}
	
	/**
	 * Checks if the configured {@link Protocol}, host and port are valid.
	 * 
	 * @return {@code true} if the configured {@link Protocol}, host and port
	 * 		are valid, {@code false} otherwise
	 */
	@XmlTransient
	public final boolean isValid() {
		if (protocol == null) {
			return false;
		}
		if (StrUtil.isNullOrEmpty(host)) {
			return false;
		}
		if (port == UNDEFINED_PORT) {
			return false;
		}
		return true;
	}
	
	
	@Override
	public final String toString() {
		return new StringBuilder(protocol.name()).append(StrUtil.COLON).append(StrUtil.SLASH).append(StrUtil.SLASH).append(host).append(StrUtil.COLON).append(port).toString();
	}
	
}
