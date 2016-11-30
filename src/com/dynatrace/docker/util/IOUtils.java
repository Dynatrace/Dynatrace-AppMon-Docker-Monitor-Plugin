package com.dynatrace.docker.util;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;


public final class IOUtils {
	
	public static final void close(final Closeable closeable) throws IOException {
		if (closeable == null) {
			return;
		}
		closeable.close();
	}

	public static final void closeQuietly(final Closeable closeable) {
		if (closeable == null) {
			return;
		}
		try {
			closeable.close();
		} catch (final RuntimeException | Error e) {
			throw e;
		} catch (final IOException e) {
			// ignore
		}
	}
	
	public static final void copy(final InputStream in, final OutputStream out) throws IOException {
		final byte buffer[] = new byte[1024];
		int read = in.read(buffer, 0, buffer.length);
		while (read > 0) {
			out.write(buffer,0, read);
			read = in.read(buffer, 0, buffer.length);
		}
	}
	
	public static final void copy(final InputStream in, final OutputStream out, final int len) throws IOException {
		final byte buffer[] = new byte[1024];
		int readSum = 0;
		int read = 0;
		int bytesToRead = Math.min(len - readSum, buffer.length);
		read = in.read(buffer, 0, bytesToRead);
		while (read > 0) {
			readSum += read;
			out.write(buffer, 0, read);
			bytesToRead = Math.min(len - readSum, buffer.length);
			read = in.read(buffer, 0, bytesToRead);
		}
	}
	
	public static final void copyLines(final InputStream in, final OutputStream out) throws IOException {
		PrintStream ps = null;
		try {
			ps = new PrintStream(out);
			copyLines(in, ps);
		} finally {
			ps.close();
		}
	}
	
	public static final void copyLines(final InputStream in, final PrintStream out) throws IOException {
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			isr = new InputStreamReader(in);
			br = new BufferedReader(isr);
			String line = br.readLine();
			while (line != null) {
				out.println(line);
				line = br.readLine();
			}
		} finally {
			br.close();
			isr.close();
		}
	}
}
