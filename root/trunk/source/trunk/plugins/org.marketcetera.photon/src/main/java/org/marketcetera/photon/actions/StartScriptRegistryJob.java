package org.marketcetera.photon.actions;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.marketcetera.photon.PhotonPlugin;

public class StartScriptRegistryJob extends Job {

	public StartScriptRegistryJob(String name) {
		super(name);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		PhotonPlugin.getDefault().startScriptRegistry();
		return Status.OK_STATUS;
	}

}
