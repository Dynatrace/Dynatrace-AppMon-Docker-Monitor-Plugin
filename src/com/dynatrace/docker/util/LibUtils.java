package com.dynatrace.docker.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.logging.Level;

import org.eclipse.core.runtime.Platform;

import com.dynatrace.docker.DockerMonitor;

public class LibUtils {
	public static void installNativeLibraryFromResources(String pLibraryName) throws IOException, URISyntaxException {
		// Get resource file location (Source)
		URL sourceFileURL = LibUtils.class.getResource("/res/" + pLibraryName);
		File sourceFile = null;

		if (sourceFileURL == null) {
			// Debug: For running from IDE
			File aIDEFilePath = new File(LibUtils.class.getResource("/").toURI());
			sourceFile = new File(aIDEFilePath.getAbsolutePath().replace("bin", "res") + "/" + pLibraryName);
			sourceFileURL = sourceFile.toURI().toURL();
		}
		if (sourceFileURL != null) {
			// Install resource into user directory
			// The reason for doing this is to have the native library installed
			// into a location which is included in the default JVM library
			// path.
			// So the JVM can load the native library without the need to
			// reconfigure the library path itself.

			// WARNING: Would prefer to use FileLocator to avoid the usage of a
			// @deprecated method, but then dynaTrace can't compile.
			try {
				sourceFileURL = Platform.resolve(sourceFileURL);
				sourceFile = new File(sourceFileURL.getPath());
			} catch (NullPointerException pException) {
			}

			File destinationFile = new File(System.getProperty("user.dir") + "/" + pLibraryName);
			if (!destinationFile.exists() && sourceFile != null) {
				copyFile(sourceFile, destinationFile);
			}
		} else {
			DockerMonitor.log.log(Level.SEVERE,"Failed to install native library '" + pLibraryName + "'");
		}
	}

	private static void copyFile(File pSourceFile, File pDestinationFile) throws IOException {
		// Channels
		FileChannel sourceFileChannel = null;
		FileChannel destinationFileChannel = null;
		try {
			// Open channels
			sourceFileChannel = new FileInputStream(pSourceFile).getChannel();
			destinationFileChannel = new FileOutputStream(pDestinationFile).getChannel();

			// File size (Source)
			long fileSize = sourceFileChannel.size();

			// Buffer (Source)
			MappedByteBuffer fileBuffer = sourceFileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileSize);

			// Buffer (Source) -> Channel (Destination)
			destinationFileChannel.write(fileBuffer);

		} finally {
			// Close channels
			if (sourceFileChannel != null) {
				sourceFileChannel.close();
			}
			if (destinationFileChannel != null) {
				destinationFileChannel.close();
			}
		}
	}



}
