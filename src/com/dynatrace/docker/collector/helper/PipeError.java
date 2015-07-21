package com.dynatrace.docker.collector.helper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;

import com.dynatrace.docker.DockerMonitor;

public class PipeError extends Pipe implements Runnable {

	private InputStream in=null;
    private OutputStream out=null;

	public PipeError() {}
	private PipeError(InputStream in, OutputStream out) {
		this.in = in;
		this.out = out;
	}
    
	public void pipe(InputStream in, OutputStream out) {
        final Thread thread = new Thread(new PipeError(in, out));
        thread.setDaemon(true);
        thread.start();
    }

    public void run() {
        try {
            int i = -1;
            byte[] buf = new byte[1024];

            while ((i = in.read(buf)) != -1) {
            		out.write(buf, 0, i);
            }
        } 
        catch (Exception e) {
        	try {
        		DockerMonitor.log.log(Level.SEVERE, "Error in PipeError:" + e.getMessage());
        		out.write(("Error:" + e.getMessage()).getBytes());
        	}
        	catch (IOException ioe) {}
        }
        
        return;
    }

}
