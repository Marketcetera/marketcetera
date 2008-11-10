package org.marketcetera.photon.actions;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;

public class StartScriptRegistryJob
    extends Job
    implements Messages
{

	public StartScriptRegistryJob(String name) {
		super(name);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		monitor.beginTask(START_SCRIPT_REGISTRY.getText(),
		                  2);
		try {
			monitor.worked(1);
			PhotonPlugin.getDefault().startScriptRegistry();
			monitor.worked(1);
		} finally {
			monitor.done();
		}
		return Status.OK_STATUS;
	}

}
