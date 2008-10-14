package org.marketcetera.photon.registry;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IPersistableSourceLocator;
import org.eclipse.debug.core.model.IStackFrame;

public class RubySourceLocator implements IPersistableSourceLocator {

	@Override
	public String getMemento() throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initializeDefaults(ILaunchConfiguration configuration)
			throws CoreException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initializeFromMemento(String memento) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getSourceElement(IStackFrame stackFrame) {
		// TODO Auto-generated method stub
		return null;
	}

}
