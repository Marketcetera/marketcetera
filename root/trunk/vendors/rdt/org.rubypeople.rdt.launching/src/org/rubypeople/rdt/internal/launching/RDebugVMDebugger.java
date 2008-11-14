package org.rubypeople.rdt.internal.launching;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.internal.debug.core.RubyDebuggerProxy;
import org.rubypeople.rdt.internal.debug.core.model.RubyDebugTarget;
import org.rubypeople.rdt.launching.IVMInstall;
import org.rubypeople.rdt.launching.RubyRuntime;
import org.rubypeople.rdt.launching.VMRunnerConfiguration;

public class RDebugVMDebugger extends StandardVMDebugger {

	private static final String PORT_SWITCH = "--port";
	private static final String VERBOSE_FLAG = "-d";
	private static final String RDEBUG_EXECUTABLE = "rdebug-ide";

	@Override
	protected List<String> constructProgramString(VMRunnerConfiguration config) throws CoreException {
		String[] args = config.getProgramArguments();
		List<String> argList = new ArrayList<String>();
		argList.add(StandardVMDebugger.END_OF_OPTIONS_DELIMITER);
		for (int i = 0; i < args.length; i++) {
			argList.add(args[i]);
		}
		config.setProgramArguments(argList.toArray(new String[argList.size()]));
		return super.constructProgramString(config);
	}
	
	@Override
	protected List<String> debugSpecificVMArgs(RubyDebugTarget debugTarget) {
		return new ArrayList<String>();
	}
	
	protected List<String> debugArgs(RubyDebugTarget debugTarget) {
		List<String> arguments = new ArrayList<String>();
		
		String rdebug = findRDebugExecutable(fVMInstance.getInstallLocation());
		if (fVMInstance.getPlatform().equals(IVMInstall.CYWGIN_PLATFORM)) {
			rdebug = rdebug.replace('\\', '/');
		}		
		arguments.add(rdebug);
		arguments.add(PORT_SWITCH);
		arguments.add(Integer.toString(debugTarget.getPort()));
		if (isDebuggerVerbose()) {
			arguments.add(VERBOSE_FLAG);
		}
		return arguments;
	}
	
	protected RubyDebuggerProxy getDebugProxy(RubyDebugTarget debugTarget) {
		return new RubyDebuggerProxy(debugTarget, true);
	}

	public static String findRDebugExecutable(File vmInstallLocation) {
		// check in bin directory where ruby interpreter is
		IPath path = RubyRuntime.checkInterpreterBin(RDEBUG_EXECUTABLE);
		if (path != null && path.toFile().exists())
			return path.toOSString();
		
		// Check bin dirs of gems paths
		path = RubyCore.getResolvedVariablePath(new Path("GEM_LIB").append("bin").append(RDEBUG_EXECUTABLE));
		if (path != null && path.toFile().exists())
			return path.toOSString();
		
		// ROR-363 Need to look in /usr/bin (and maybe /usr/local/bin) for Leopard!
		path = RubyCore.checkSystemPath(RDEBUG_EXECUTABLE);
		if (path != null && path.toFile().exists())
			return path.toOSString();
		
		path = RubyCore.checkCommonBinLocations(RDEBUG_EXECUTABLE);
		if (path != null && path.toFile().exists())
			return path.toOSString();
		
		return null;
	}
	
}
