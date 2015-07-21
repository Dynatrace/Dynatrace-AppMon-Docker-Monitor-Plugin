package com.dynatrace.docker.util;

import java.io.Closeable;
import java.io.IOException;


public final class CloseUtil {

	public final static void closeQuietly(final Closeable closeable) {
		try {
			close(closeable);
		} catch (final IOException e) {
			// ignore
		}
	}
	
	public final static void close(final Closeable closeable) throws IOException {
		if (closeable == null) {
			return;
		}
		closeable.close();
	}
}
