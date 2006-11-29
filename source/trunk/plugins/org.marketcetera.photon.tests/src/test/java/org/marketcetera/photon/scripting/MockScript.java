package org.marketcetera.photon.scripting;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;


/**
 * Mock script that keeps track of whether it has been <code>exec()</code>'ed.
 *
 * @author andrei@lissovski.org
 */
public class MockScript extends Script {

	private boolean execed = false;
	private final String scriptWorkspacePath;
	private final String scriptContent;
	
	
	public MockScript(String scriptContent,String scriptWorkspacePath) {
		super(scriptContent, scriptWorkspacePath, 1,1);
		this.scriptWorkspacePath = scriptWorkspacePath;
		this.scriptContent = scriptContent;
	}

	public boolean isExeced() {
		return execed;
	}
	
	public void reset() {
		execed = false;
	}
	
	/* (non-Javadoc)
	 * @see org.marketcetera.photon.scripting.IScript#eval(org.apache.bsf.BSFManager)
	 */
	public Object eval(BSFManager manager) throws BSFException {
		throw new NotImplementedException();
	}

	/* (non-Javadoc)
	 * @see org.marketcetera.photon.scripting.IScript#exec(org.apache.bsf.BSFManager)
	 */
	public void exec(BSFManager manager) throws BSFException {
		execed = true;
	}

	/* (non-Javadoc)
	 * @see org.marketcetera.photon.scripting.IScript#getScript()
	 */
	public String getScript() {
		return scriptContent;
	}

	public String getID() {
		return scriptWorkspacePath;
	}

}
