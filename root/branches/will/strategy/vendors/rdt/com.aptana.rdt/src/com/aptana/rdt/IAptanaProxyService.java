package com.aptana.rdt;

public interface IAptanaProxyService {

	public boolean isAuthenticating();
	
	public boolean enabled();
	
	public String getHostName();
	
	public String getUsername();
	
	public String getPassword();
	
	public int getPort();
	
	public boolean useSOCKS();
	
	public String[] getNonProxyHosts();
}
