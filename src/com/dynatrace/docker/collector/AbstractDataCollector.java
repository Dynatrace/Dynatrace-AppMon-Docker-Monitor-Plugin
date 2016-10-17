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
		//json = json.replaceAll("[\\x00-\\x09\\x11\\x12\\x14-\\x1F\\x7F]", "");
		return (T)objMapper.readValue(json, theClass);
	}
}
