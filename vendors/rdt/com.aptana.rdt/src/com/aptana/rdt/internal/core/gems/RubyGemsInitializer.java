package com.aptana.rdt.internal.core.gems;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.osgi.framework.Bundle;
import org.rubypeople.rdt.launching.IVMInstall;
import org.rubypeople.rdt.launching.IVMInstallChangedListener;
import org.rubypeople.rdt.launching.PropertyChangeEvent;
import org.rubypeople.rdt.launching.RubyRuntime;

import com.aptana.rdt.AptanaRDTPlugin;

// FIXME This should run after we prompt user about installing C Ruby!
public class RubyGemsInitializer extends Job implements IVMInstallChangedListener {
	
	private boolean initialized;

	public RubyGemsInitializer() {
		super("Forcing GemManager to initialize");
	}

	public void defaultVMInstallChanged(IVMInstall previous, IVMInstall current) {
		if (current == null) return;
		if (!initialized) {
			initialize();
		}
	}

	public void vmAdded(IVMInstall newVm) {}

	public void vmChanged(PropertyChangeEvent event) {}

	public void vmRemoved(IVMInstall removedVm) {}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		monitor.beginTask("Initializing GemManager", 100);
		
		if (rubyInstalled()) {
			Bundle bundle = Platform.getBundle("org.eclipse.debug.ui");
			while( bundle.getState() != Bundle.ACTIVE) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// ignore
				}
			}
			initialize();
		}
		
		RubyRuntime.addVMInstallChangedListener(this);		
		monitor.done();
		return Status.OK_STATUS;
	}
	
	private void initialize() {
		AptanaRDTPlugin.getDefault().getGemManager().initialize();
		initialized = true;
		
		// Force the UI plugin to load so the auto-installer will run
		try {
			Platform.getBundle("com.aptana.rdt.ui").loadClass("com.aptana.rdt.ui.AptanaRDTUIPlugin"); // force UI plugin to load
		} catch (Exception e) {
			// ignore
		}
	}

	/**
	 * Returns whether we have a ruby interpreter set up
	 * 
	 * @return
	 */
	private boolean rubyInstalled() {
		return RubyRuntime.getDefaultVMInstall() != null;
	}
}
