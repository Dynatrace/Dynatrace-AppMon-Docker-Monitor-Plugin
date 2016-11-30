package com.dynatrace.docker.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class HelperUtils {
	public static String getExceptionAsString(Exception e) {
		return new StringBuilder(
				e.getClass().getCanonicalName() + " exception occurred. Message = '")
				.append(e.getMessage()).append("'; Stacktrace is '")
				.append(getStackTraceString(e)).append("'").toString();
	}
	private static String getStackTraceString(Exception e) {
		ByteArrayOutputStream ba = new ByteArrayOutputStream();
		e.printStackTrace(new  PrintStream(ba));
		return ba.toString();
	}

}
