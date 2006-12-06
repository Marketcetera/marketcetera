package org.marketcetera.photon.scripting;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;

public interface IScript {
	
	public static final String RUBY_LANG_STRING = "ruby";

	public void exec(BSFManager manager) throws BSFException;
	public Object eval(BSFManager manager) throws BSFException;

	public String getScript();
	
	public String getID();

}
