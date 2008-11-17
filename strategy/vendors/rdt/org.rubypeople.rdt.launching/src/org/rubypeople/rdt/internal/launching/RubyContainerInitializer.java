package org.rubypeople.rdt.internal.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.rubypeople.rdt.core.ILoadpathContainer;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.LoadpathContainerInitializer;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.launching.IVMInstall;
import org.rubypeople.rdt.launching.IVMInstallType;
import org.rubypeople.rdt.launching.RubyRuntime;

public class RubyContainerInitializer extends LoadpathContainerInitializer {

	@Override
	public void initialize(IPath containerPath, IRubyProject project)
			throws CoreException {
		int size = containerPath.segmentCount();
		if (size > 0) {
			if (containerPath.segment(0).equals(RubyRuntime.RUBY_CONTAINER)) {
				IVMInstall vm = resolveInterpreter(containerPath);
				RubyVMContainer container = null;
				if (vm != null) {
					container = new RubyVMContainer(vm, containerPath);
				}
				RubyCore.setLoadpathContainer(containerPath,
						new IRubyProject[] { project },
						new ILoadpathContainer[] { container }, null);
			}
		}

	}

	/**
	 * Returns the VM install associated with the container path, or
	 * <code>null</code> if it does not exist.
	 */
	public static IVMInstall resolveInterpreter(IPath containerPath) {
		IVMInstall vm = null;
		if (containerPath.segmentCount() > 1) {
			// specific Ruby VM
			String vmTypeId = getInterpreterTypeId(containerPath);
			String vmName = getInterpreterName(containerPath);
			IVMInstallType vmType = RubyRuntime
					.getVMInstallType(vmTypeId);
			if (vmType != null) {
				vm = vmType.findVMInstallByName(vmName);
			}
		} else {
			// workspace default Ruby VM
			vm = RubyRuntime.getDefaultVMInstall();
		}
		return vm;
	}

	/**
	 * Returns the VM type identifier from the given container ID path.
	 * 
	 * @return the VM type identifier from the given container ID path
	 */
	public static String getInterpreterTypeId(IPath path) {
		return path.segment(1);
	}

	/**
	 * Returns the VM name from the given container ID path.
	 * 
	 * @return the VM name from the given container ID path
	 */
	public static String getInterpreterName(IPath path) {
		return path.segment(2);
	}

}
