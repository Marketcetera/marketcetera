package org.rubypeople.rdt.internal.launching;

import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.rubypeople.rdt.launching.IVMInstallType;
import org.rubypeople.rdt.launching.IVMRunner;

public class JRubyVM extends StandardVM {
	
	public JRubyVM(IVMInstallType type, String id) {
		super(type, id);
	}

	@Override
	public IVMRunner getVMRunner(String mode) {
		IVMRunner runner = null;
		if (ILaunchManager.RUN_MODE.equals(mode)) {
			runner = new JRubyVMRunner();
		} else if (ILaunchManager.DEBUG_MODE.equals(mode)) {
			runner = new JRubyDebugVMDebugger();
		} else if (ILaunchManager.PROFILE_MODE.equals(mode)) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Profiling not yet supported on JRuby", "Profiling is not yet available for the JRuby interpreter. We rely on the ruby-prof gem, which requires native code, and there is not yet a Java based version of the gem.");
			return null;
		}
		if (runner != null) runner.setVMInstall(this);
		return runner;
		
	}

}
