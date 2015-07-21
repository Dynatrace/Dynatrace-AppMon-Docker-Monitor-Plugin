package com.dynatrace.docker.collector;

import java.io.IOException;
import java.util.logging.Level;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
public abstract class AbstractDataCollector {
	protected <T> T convert(String json, Class<T> theClass) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper objMapper = new ObjectMapper();
		com.dynatrace.docker.DockerMonitor.log.log(Level.FINER, "About to convert this JSON\n" + json);
		return (T)objMapper.readValue(json, theClass);
	}
}
