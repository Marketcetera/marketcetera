package org.rubypeople.rdt.internal.launching;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.rubypeople.rdt.core.LoadpathVariableInitializer;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.launching.IVMInstall;
import org.rubypeople.rdt.launching.IVMInstallChangedListener;
import org.rubypeople.rdt.launching.PropertyChangeEvent;
import org.rubypeople.rdt.launching.RubyRuntime;

public class RubyLoadpathVariablesInitializer extends LoadpathVariableInitializer implements IVMInstallChangedListener {

	private IProgressMonitor fMonitor;
	private String fVariable;

	public RubyLoadpathVariablesInitializer() {
		RubyRuntime.addVMInstallChangedListener(this);
	}
	

	public void initialize(String variable) {
		this.fVariable = variable;
		IVMInstall vmInstall= RubyRuntime.getDefaultVMInstall();
		if (vmInstall != null) {
			IPath[] systemLib= RubyRuntime.getLibraryLocations(vmInstall);			
			if (systemLib != null) {				
				IWorkspace workspace= ResourcesPlugin.getWorkspace();
				IWorkspaceDescription wsDescription= workspace.getDescription();				
				boolean wasAutobuild= wsDescription.isAutoBuilding();
				try {
					setAutobuild(workspace, false);
					setRubyVMVariable(systemLib, variable);	
				} catch (CoreException ce) {
					LaunchingPlugin.log(ce);
					return;
				} finally {
					try {
						setAutobuild(workspace, wasAutobuild);
					} catch (CoreException ce) {
						LaunchingPlugin.log(ce);
					}
				}
			}
		}		
	}
	
	private void setRubyVMVariable(IPath[] newPath, String var) throws CoreException {
		RubyCore.setLoadpathVariable(var, newPath, getMonitor());
	}
	
	private boolean setAutobuild(IWorkspace ws, boolean newState) throws CoreException {
		IWorkspaceDescription wsDescription= ws.getDescription();
		boolean oldState= wsDescription.isAutoBuilding();
		if (oldState != newState) {
			wsDescription.setAutoBuilding(newState);
			ws.setDescription(wsDescription);
		}
		return oldState;
	}
	
	protected IProgressMonitor getMonitor() {
		if (fMonitor == null) {
			return new NullProgressMonitor();
		}
		return fMonitor;
	}

	public void defaultVMInstallChanged(IVMInstall previous, IVMInstall current) {
		initialize(fVariable);		
	}

	public void vmAdded(IVMInstall newVm) {
	}

	public void vmChanged(PropertyChangeEvent event) {
	}

	public void vmRemoved(IVMInstall removedVm) {
	}

}
