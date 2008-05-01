package com.swtworkbench.community.xswt;

import java.io.IOException;
import java.io.InputStream;

public interface URIHandler {
	public InputStream getInputStream(String uri) throws IOException;
	public String resolve(String uri, String base);
}
