package com.swtworkbench.community.xswt;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class DefaultURIHandler implements URIHandler {

	public InputStream getInputStream(String uri) throws IOException {
		InputStream input = null;
		try {
			input = new FileInputStream(uri);
		} catch (FileNotFoundException fnfe) {
		}
		if (input == null) {
			input = new URL(uri).openStream();
		}
		return input;
	}

	public String resolve(String uri, String base) {
		if (base == null) {
			return uri;
		}
		try {
			return new URI(base).resolve(uri).toString();
		} catch (URISyntaxException e) {
		}
		return null;
	}
}
