package org.rubypeople.rdt.internal.launching;

import org.rubypeople.rdt.launching.AbstractVMInstall;
import org.rubypeople.rdt.launching.IVMInstallType;
import org.rubypeople.rdt.launching.IVMRunner;

public class RubiniusVM extends AbstractVMInstall {

	public RubiniusVM(IVMInstallType type, String id) {
		super(type, id);
	}

	@Override
	public IVMRunner getVMRunner(String mode) {
		IVMRunner runner = new RubiniusVMRunner();
		runner.setVMInstall(this);
		return runner;
	}
	
	public String getPlatform() {
		return "rubinius";
	}
}
