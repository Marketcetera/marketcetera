package com.aptana.rdt.internal.core;

import java.util.StringTokenizer;

import com.aptana.rdt.IAptanaProxyService;

/**
 * Reads the system properties that are set by Aptana's Proxy plugin.
 * 
 * @author Chris Williams
 *
 */
public class SystemPropertyProxyService implements IAptanaProxyService {

	private static final String SOCKS_PROXY_HOST = "socksProxyHost"; //$NON-NLS-1$
	private static final String SOCKS_PROXY_PORT = "socksProxyPort"; //$NON-NLS-1$

	private static final String HTTP_PROXY_SET = "http.proxySet"; //$NON-NLS-1$
	private static final String HTTP_PROXY_HOST = "http.proxyHost"; //$NON-NLS-1$
	private static final String HTTP_PROXY_PORT = "http.proxyPort"; //$NON-NLS-1$
	private static final String HTTP_NON_PROXY_HOSTS = "http.nonProxyHosts"; //$NON-NLS-1$	
	private static final String HTTP_PROXY_USERNAME = "http.proxyUserName"; //$NON-NLS-1$
	private static final String HTTP_PROXY_PASSWORD = "http.proxyPassword"; //$NON-NLS-1$
	
	public boolean enabled() {
		return getHostName() != null && getHostName().trim().length() > 0;
	}

	public String getHostName() {
		if (useSOCKS()) return System.getProperty(SOCKS_PROXY_HOST);	
		return System.getProperty(HTTP_PROXY_HOST);		
	}

	public String[] getNonProxyHosts() {
		String nonProxy = System.getProperty(HTTP_NON_PROXY_HOSTS);
		if (nonProxy == null || nonProxy.trim().length() == 0) return new String[0];
		StringTokenizer tokenizer = new StringTokenizer(nonProxy, "|");
		String[] nonProxies = new String[tokenizer.countTokens()];
		int i = 0;
		while (tokenizer.hasMoreTokens()) {
			String hostname = tokenizer.nextToken();
			nonProxies[i++] = hostname;
		}
		return nonProxies;
	}

	public String getPassword() {
		return System.getProperty(HTTP_PROXY_PASSWORD);
	}

	public int getPort() {
		String port = "80";
		if (useSOCKS()) {
			port = System.getProperty(SOCKS_PROXY_PORT);
		}
		else {
			port = System.getProperty(HTTP_PROXY_PORT);
		}
		if (port != null && port.trim().length() > 0)
			return Integer.parseInt(port);
		return 80;
	}

	public String getUsername() {
		return System.getProperty(HTTP_PROXY_USERNAME);
	}

	public boolean isAuthenticating() {
		return getUsername() != null && getUsername().trim().length() > 0;
	}

	public boolean useSOCKS() {
		String socksHost = System.getProperty(SOCKS_PROXY_HOST);
		return socksHost != null && socksHost.trim().length() > 0;
	}

}
