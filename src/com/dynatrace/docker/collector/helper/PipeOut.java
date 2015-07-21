package com.dynatrace.docker.collector.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.logging.Level;

import com.dynatrace.docker.DockerMonitor;

//import org.apache.commons.lang3.ArrayUtils;

public class PipeOut extends Pipe implements Runnable {
	private InputStream in=null;
	private OutputStream out=null;

	private boolean unlimited;

	public PipeOut() {}
	private PipeOut(InputStream in, OutputStream out, boolean unlimited) {
		this.in = in;
		this.out = out;
		this.unlimited = unlimited;
	}
	public void pipe(InputStream in, OutputStream out, boolean unlimited) {
		final Thread thread = new Thread(new PipeOut(in, out, unlimited));
		thread.setDaemon(true);
		thread.start();
	}

	public void run() {
		InputStreamReader ibuf = null;
		BufferedReader bufR = null;
		try {
			ibuf = new InputStreamReader(in);
			bufR = new BufferedReader(ibuf);
			String status = bufR.readLine();
			DockerMonitor.log.log(Level.SEVERE, "Status=" + status);
			if (status.contains("200 OK"))
			{
				int hdrResp = parseHeaders(bufR);
				if ( hdrResp != 0) {
					out.write("Error: Failed to parse headers".getBytes());
				}

				if (unlimited)
				{
					String line;
					while ((line = bufR.readLine())!= null)
					{
						out.write(line.getBytes());
					}
				}
				else {
					String line = bufR.readLine();
					out.write(line.getBytes());
				}
			}
			else
			{
				out.write(("Error:" + status).getBytes());
			}
		} 
		catch (IOException e) {
			DockerMonitor.log.log(Level.SEVERE, e.getMessage());
			try {
				out.write(("Error:" + e.getMessage()).getBytes());
			}
			catch (IOException ioe) {}
		}
		finally {
			try {
				if (bufR != null) {
					bufR.close();
				}

				if (ibuf != null) {
					ibuf.close();
				}
				
				if (in != null ) {
					in.close();
				}
			}
			catch (IOException ioe) {}
		}
	}

	private int parseHeaders(BufferedReader buf) {
		String line;
		try {
			while ((line = buf.readLine()) != null ) {
				if (line.equals("") || line.equals("\n")) {
					break;
				}
			}
			return 0;
		}
		catch (IOException ioe) {
			DockerMonitor.log.log(Level.SEVERE, "IOException in parseHeaders:" + ioe.getMessage());
			return -1;
		}
	}
}
