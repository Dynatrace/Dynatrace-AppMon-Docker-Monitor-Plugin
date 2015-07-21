package com.dynatrace.docker.collector;

import java.io.IOException;

public interface DataCollector {
	public <T> T collectContainerList(Class<T> retunClass) throws IOException;
	
	public <T> T collectContainerData(String containerId, Class<T> theClass) throws IOException;
	
	public <T> T collectContainerAndImageCount(Class<T> returnClass) throws IOException;
}
