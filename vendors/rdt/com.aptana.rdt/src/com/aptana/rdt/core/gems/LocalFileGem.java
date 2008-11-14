package com.aptana.rdt.core.gems;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.rubypeople.rdt.core.util.Util;

import com.aptana.rdt.AptanaRDTPlugin;

public class LocalFileGem extends Gem {

	private URL url;
	private Set<String> dependencies;
	private File file;
	private boolean compiles;

	public LocalFileGem(URL url, String name, String version, String description, String platform) {
		super(name, version, description, platform);
		this.url = url;
		this.dependencies = new HashSet<String>();
	}

	public static LocalFileGem create(URL file) {
		String[] fileNameParts = file.getPath().split("[\\|/]");
		String name = fileNameParts[fileNameParts.length - 1];
		String[] parts = name.split("-");
		String version = "";
		String platform = RUBY_PLATFORM;
		if (parts != null && parts.length > 1) {
			name = parts[0];
			version = parts[1];
			if (parts.length > 2) platform = parts[2];
		}
		if (version.endsWith(".gem")) version = version.substring(0, version.length() - 4);
		if (platform.endsWith(".gem")) platform = platform.substring(0, platform.length() - 4);
		return new LocalFileGem(file, name, version, "", platform);
	}
	
	public boolean isLocal() {
		return true;
	}
	
	public String getAbsolutePath() {
		if (file == null)
			file = copyFile(url);
		return file.getAbsolutePath();
	}
	
	public void addDependency(String name) {
		dependencies.add(name);
	}
	
	public void delete() {
		if (file == null) return;
		
		if (!file.delete())
			file.deleteOnExit();
	}

	public Set<String> getDependencies() {
		return Collections.unmodifiableSet(dependencies);
	}
	
	private File copyFile(URL url) {
		byte[] contents = getFileContents(url);		
		String[] fileNameParts = url.getPath().split("[\\|/]");
		String filename = fileNameParts[fileNameParts.length - 1];
		return writeContents(contents, filename);
	}
	
	private File writeContents(byte[] contents, String filename) {
		File tempGemLocation = AptanaRDTPlugin.getDefault().getStateLocation().toFile();
		File file = new File(tempGemLocation, filename);
		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(file);
			outputStream.write(contents);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (outputStream != null) outputStream.close();
			} catch (IOException e) {
				// ignore
			}
		}		
		return file;
	}

	private byte[] getFileContents(URL url) {
		InputStream stream = null;
		try {
			stream = url.openStream();
			return Util.getInputStreamAsByteArray(stream, -1);			
		} catch (IOException e) {
			AptanaRDTPlugin.log(e);
		} finally {
			try {
				if (stream != null) stream.close();
			} catch (IOException e) {
				// ignore
			}
		}
		return new byte[0];
	}

	public void setCompiles(boolean compiles) {
		this.compiles = compiles;		
	}
	
	public boolean compiles() {
		return compiles;
	}
	
}
