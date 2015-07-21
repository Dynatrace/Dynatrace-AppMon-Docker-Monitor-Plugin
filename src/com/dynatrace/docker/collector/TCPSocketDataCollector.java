package com.dynatrace.docker.collector;

import java.io.IOException;

import com.dynatrace.docker.rest.Request;
import com.dynatrace.docker.rest.config.ServerConfig;

public class TCPSocketDataCollector extends AbstractDataCollector implements DataCollector {
	private ServerConfig serverConfig;
	public TCPSocketDataCollector(ServerConfig serverConfig) {
		this.serverConfig = serverConfig;
	}
	public <T> T collectContainerList(Class<T> theClass) throws IOException {
		Request request = new Request();
		request.setPath("/containers/json");
		String reply = request.execute(serverConfig, true);
		if ( reply == null ) {
			throw new IOException("Recevied null value from the docker query for container list in TCPSocketDataCollector. Please check log files to find the reason.");
		}

		return convert(reply, theClass);
	}

	public <T> T collectContainerData(String containerId, Class<T> theClass) throws IOException {
		Request request = new Request();
		request.setPath("/containers/" + containerId + "/stats");
		String reply = request.execute(serverConfig, false);
		if ( reply == null ) {
			throw new IOException("Recevied null value from the docker query for container data in TCPSocketDataCollector. Please check log files to find the reason.");
		}
		return convert(reply, theClass);			
	}
	public ServerConfig getServerConfig() {
		return serverConfig;
	}
	
	public <T> T collectContainerAndImageCount(Class<T> theClass) throws IOException {
		Request request = new Request();
		request.setPath("/info");
		String reply = request.execute(serverConfig, true);
		if ( reply == null ) {
			throw new IOException("Recevied null value from the docker query for container and image count in TCPSocketDataCollector. Please check log files to find the reason.");
		}
		return convert(reply, theClass);
	}

}
