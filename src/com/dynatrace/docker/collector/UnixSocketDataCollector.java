package com.dynatrace.docker.collector;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.SocketAddress;
import java.util.logging.Level;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpParser;
import org.newsclub.net.unix.AFUNIXSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;
import org.newsclub.net.unix.AFUNIXSocketException;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;

import com.dynatrace.docker.DockerMonitor;

public class UnixSocketDataCollector extends AbstractDataCollector implements DataCollector {
	private String connectionMode;
	private String host;
	private String password;
	private String user;
	private static String UNIX_SOCKET_CMD = "echo -e \"GET %s HTTP/1.0\r\n\" | sudo /bin/nc -q -1 -U /var/run/docker.sock";
//	private static String UNIX_SOCKET_CMD_LOCAL = "echo -e \"GET %s HTTP/1.0\r\n\" | sudo /bin/nc -U /var/run/docker.//sock";
	private static String UNIX_SOCKET_CMD_LOCAL = "GET %s HTTP/1.0\r\n";
	private static String SOCKET_FILE = "/var/run/docker.sock";

	public <T> T collectContainerList(Class<T> theClass) throws IOException {
		String reply=null;
		if (connectionMode.equals(DockerMonitor.SSH)) {
			DockerMonitor.log.log(Level.FINER, "In SSH");
			reply = getDataRemotely("/containers/json", true);
		}
		else {
			reply = getDataLocally("/containers/json", true);			
		}
		
		if ( reply == null ) {
			throw new IOException("Recevied null value from the docker query for container list in UnixSocketDataCollector. Please check log files to find the reason.");
		}
		return convert(reply, theClass);
	}

	public <T> T collectContainerData(String containerId, Class<T> theClass) throws IOException {
		String reply=null;
		if (connectionMode.equals(DockerMonitor.SSH)) {
			reply = getDataRemotely("/containers/" + containerId + "/stats", false);
		}
		else {
			reply = getDataLocally("/containers/" + containerId + "/stats", false);			
		}
		if ( reply == null ) {
			throw new IOException("Recevied null value from the docker query for container data in UnixSocketDataCollector. Please check log files to find the reason.");
		}
		return convert(reply, theClass);
	}
	
	public <T> T collectContainerAndImageCount(Class<T> theClass) throws IOException {
		String reply=null;
		if (connectionMode.equals(DockerMonitor.SSH)) {
			reply = getDataRemotely("/info", true);
		}
		else {
			reply = getDataLocally("/info", true);			
		}
		if ( reply == null ) {
			throw new IOException("Recevied null value from the docker query for container and image count in UnixSocketDataCollector. Please check log files to find the reason.");
		}
		return convert(reply, theClass);
	}

	public void setConnectionMode(String mode) {
		this.connectionMode = mode;
	}
	
	public void setHost(String host) {
		this.host = host;
	}
	
	public void setPassword(String pass) {
		this.password = pass;
	}
	
	public void setUser(String u) {
		this.user = u;
	}
	
	private String getDataRemotely(String command, boolean readUnlimited) throws IOException {
		Session sess=null;
		Connection conn=null;
		conn = new Connection(host);
		conn.connect();
		boolean isAuthenticated = conn.authenticateWithPassword(user, password);

		if (!isAuthenticated) {
			DockerMonitor.log.log(Level.SEVERE, "SSH: Unable to authenticate to the server");
		}
		sess = conn.openSession();
		String updatedCmd = String.format(UNIX_SOCKET_CMD, command);
		DockerMonitor.log.log(Level.FINER, "Command=" + updatedCmd);

		String replyStr=null;
		if (readUnlimited) {
			replyStr= readUnlimitedRemotely(sess, updatedCmd);
		}
		else {
			replyStr= readLimitedRemotely(sess, updatedCmd);
		}
		if ( sess != null) {
			sess.close();
		}
		return replyStr;
	}
	
	private String readUnlimitedRemotely(Session sess, String updatedCmd) throws IOException {
		sess.execCommand(updatedCmd);

		InputStream in = sess.getStdout();
		InputStream err = sess.getStderr();
		StringBuilder sb = new StringBuilder();
		String temp, errorStr;
		while ((errorStr = HttpParser.readLine(err, "UTF-8")) != null)
		{
			sb.append(errorStr);
		}
		
		errorStr = sb.toString();
		if (!errorStr.equals("")) {
			DockerMonitor.log.log(Level.SEVERE, "Received error from SSH command." + errorStr);
			throw new IOException("SSH command failed:" + errorStr);
		}

		String statusLine = HttpParser.readLine(in, "UTF-8");
		DockerMonitor.log.log(Level.FINER, "statusLIne=" + statusLine);
		if (statusLine.contains("200 OK"))
		{
			Header[] headers = HttpParser.parseHeaders(in, "UTF-8");
			sb = new StringBuilder();
			while ((temp = HttpParser.readLine(in, "UTF-8")) != null)
			{
				sb.append(temp);
			}
			try
			{
				in.close();
			}
			catch (IOException e) {}
			return sb.toString();
		}
		else
		{
			DockerMonitor.log.log(Level.SEVERE, "Response Code=" + statusLine);
			throw new IOException("Response Code not OK:" + statusLine );
		}
	}
	
	private String readLimitedRemotely(Session sess, String updatedCmd) throws IOException {
		sess.execCommand(updatedCmd);

		InputStream in = sess.getStdout();
		// TODO: Add ability to read errors from the command. Right now, cannot read errors because the stats command
		// keeps pushing the data out continuously.
		//		InputStream err = sess.getStderr();
		String temp;
		String statusLine = HttpParser.readLine(in, "UTF-8");
		DockerMonitor.log.log(Level.FINER, "statusLIne=" + statusLine);
		if (statusLine.contains("200 OK"))
		{
			DockerMonitor.log.log(Level.FINER, "getting header");
			
			Header[] headers = HttpParser.parseHeaders(in, "UTF-8");
			DockerMonitor.log.log(Level.FINER, "got header");
			while ((temp = HttpParser.readLine(in, "UTF-8")) != null)
			{
				DockerMonitor.log.log(Level.FINER, temp);
				return temp;
			}
		}
		else
		{
			DockerMonitor.log.log(Level.FINER, "Response Code=" + statusLine);
			return null;
		}
		return null;
	}
	
	private String getDataLocally(String command, boolean readUnlimited) throws IOException {
		String updatedCmd = String.format(UNIX_SOCKET_CMD_LOCAL, command);
		AFUNIXSocket sock = AFUNIXSocket.newInstance();
        InputStream is=null;
        OutputStream os = null;

        try {
        		final File socketFile = new File(SOCKET_FILE);
        		SocketAddress sAddress =null;
        		try {
        			sAddress = new AFUNIXSocketAddress(socketFile);
        		}
        		catch (Throwable t) {
        			if (t.getCause() != null) {
        				DockerMonitor.log.log(Level.SEVERE, "getDataLocally.AFUNIXSocketAddress-Cause:" + t.getCause().getMessage(), t.getCause());
        			}
        			DockerMonitor.log.log(Level.SEVERE, "getDataLocally-NoCause.AFUNIXSocketAddress:" +stackTraceToString(t));
        		}

        		try {
        			sock.connect(sAddress);
        		}
        		catch (Throwable t) {
        			if (t.getCause() != null) {
        				DockerMonitor.log.log(Level.SEVERE, "getDataLocally.connect-Cause:" + t.getCause().getMessage(), t.getCause());
        			}
        			DockerMonitor.log.log(Level.SEVERE, "getDataLocally.connect-NoCause:" + stackTraceToString(t));
        		}
                os = sock.getOutputStream();

                OutputStreamWriter osw = new OutputStreamWriter(os);
                PrintWriter bw = new PrintWriter(osw);

                bw.println(updatedCmd);
                bw.println();
                bw.flush();

                is = sock.getInputStream();
        		String temp;

                //InputStreamReader isr = new InputStreamReader(is);
                //BufferedReader br = new BufferedReader(isr);
        		String statusLine = HttpParser.readLine(is, "UTF-8");
        		DockerMonitor.log.log(Level.FINER, "statusLIne=" + statusLine);
        		if (statusLine.contains("200 OK"))
        		{
        			Header[] headers = HttpParser.parseHeaders(is, "UTF-8");
        			StringBuilder sb = new StringBuilder();
        			while ((temp = HttpParser.readLine(is, "UTF-8")) != null)
        			{
        				if (readUnlimited) {
        					sb.append(temp);
        				}
        				else {
        					return temp;
        				}
        			}
        			return sb.toString();
        		}
        		else
        		{
        			DockerMonitor.log.log(Level.SEVERE, "Response Code=" + statusLine);
        			throw new IOException("Response Code not OK:" + statusLine );
        		}
        } 
        catch (AFUNIXSocketException e) {
        	System.out.println("Cannot connect to server. Have you started it?");
        	System.out.flush();
        	throw e;
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
        finally {
        	try {
        		if (is != null) {
        			is.close();
        		}
        		os.close();
        		sock.close();
        		is.close();
        	}
        	catch (Exception e){}
        }
        return null;
	}
	
	private String stackTraceToString(Throwable throwable) {
		if (throwable != null) {
			PrintWriter printWriter = null;
			try {
				// Exception -> StringWriter > toString()
				StringWriter stringWriter = new StringWriter();
				try {
					printWriter = new PrintWriter(stringWriter);
					throwable.printStackTrace(printWriter);
					printWriter.flush();
				} finally {
					stringWriter.close();
				}
				return stringWriter.toString();
			} catch (Exception pException) {
			}
		}
		return "";

	}

	private static class StdOutReader extends Thread {
		Process process;
		boolean unlimited;
		String stdOut;
		String errorString=null;
		public StdOutReader(Process process, boolean unlimited) {
			this.process = process;
			this.unlimited = unlimited;
		}
		
		public void run() {
			try {
//				Process process = Runtime.getRuntime().exec(command);
				InputStream is = process.getInputStream();
				String status = HttpParser.readLine(is, "UTF-8");
				DockerMonitor.log.log(Level.FINER, "Status=" + status);
				if (status.contains("200 OK"))
				{

					Header[] headers = HttpParser.parseHeaders(is, "UTF-8");
					if (unlimited)
					{
						StringBuilder sb = new StringBuilder();
						String line;
						while ((line = HttpParser.readLine(is, "UTF-8")) != null)
						{
							sb.append(line);
						}
						this.stdOut = sb.toString();
					}
					else {
						this.stdOut = HttpParser.readLine(is, "UTF-8");
					}
				}
				else
				{
					this.errorString = status;
				}
			}
			catch (IOException ioe) {
				errorString = ioe.getMessage();
			}
		}
		
		public String getStdOut() {
			return stdOut;
		}
		
		public String getErrorString() {
			return errorString;
		}
	}
	
	private class ErrorReader extends Thread {
		private final InputStream in;
		
		public ErrorReader(InputStream in) {
			this.in = in;
		}
		
		public void run() {
			BufferedReader bReader = new BufferedReader(new InputStreamReader(in));
			try {
				String line;
				StringBuffer sb = new StringBuffer();
				while ((line = bReader.readLine()) != null ) {
					sb.append(line);
				}
				bReader.close();
			}
			catch (IOException ioe) {
				DockerMonitor.log.log(Level.SEVERE, "IOException in ErrorReader:" + ioe.getMessage());
			}
		}
	}
}
