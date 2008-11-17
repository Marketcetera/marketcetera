package org.rubypeople.rdt.internal.launching;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.SocketUtil;
import org.rubypeople.rdt.internal.debug.core.RubyDebuggerProxy;
import org.rubypeople.rdt.internal.debug.core.model.RubyDebugTarget;
import org.rubypeople.rdt.internal.debug.core.model.RubyProcessingException;
import org.rubypeople.rdt.launching.IRubyLaunchConfigurationConstants;
import org.rubypeople.rdt.launching.IVMRunner;
import org.rubypeople.rdt.launching.RubyRuntime;
import org.rubypeople.rdt.launching.VMRunnerConfiguration;

public class JRubyDebugVMDebugger extends JRubyVMRunner implements IVMRunner {
	
	private static final String PORT_SWITCH = "--port";
	private static final String VERBOSE_FLAG = "-d";
	private static final String RDEBUG_EXECUTABLE = "rdebug-ide";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jdt.launching.IVMRunner#run(org.eclipse.jdt.launching.VMRunnerConfiguration,
	 *      org.eclipse.debug.core.ILaunch,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void run(VMRunnerConfiguration config, ILaunch launch, IProgressMonitor monitor) throws CoreException {

		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}

		IProgressMonitor subMonitor = new SubProgressMonitor(monitor, 1);
		subMonitor.beginTask(LaunchingMessages.StandardVMDebugger_Launching_VM____1, 4);
		subMonitor.subTask(LaunchingMessages.StandardVMDebugger_Finding_free_socket____2);

		int port = SocketUtil.findFreePort();
		if (port == -1) {
			abort(LaunchingMessages.StandardVMDebugger_Could_not_find_a_free_socket_for_the_debugger_1, null, IRubyLaunchConfigurationConstants.ERR_NO_SOCKET_AVAILABLE);
		}

		subMonitor.worked(1);

		// check for cancellation
		if (monitor.isCanceled()) {
			return;
		}

		subMonitor.subTask(LaunchingMessages.StandardVMDebugger_Constructing_command_line____3);

		RubyDebugTarget debugTarget = new RubyDebugTarget(launch, port);
		List<String> arguments = constructProgramString(config);
		
		// VM args are the first thing after the ruby program so that users can
		// specify
		// options like '-client' & '-server' which are required to be the first
		// options
		String[] allVMArgs = combineVmArgs(config, fVMInstance);
		addArguments(allVMArgs, arguments);

		String[] cp = config.getLoadPath();
		if (cp.length > 0) {
			arguments.addAll(convertLoadPath(config, cp));
		}
		addStreamSync(arguments);
		arguments.addAll(debugSpecificVMArgs(debugTarget));		
		
		arguments.add(StandardVMRunner.END_OF_OPTIONS_DELIMITER);
		
		arguments.addAll(debugArgs(debugTarget));		

		arguments.add(StandardVMRunner.END_OF_OPTIONS_DELIMITER);
		
		arguments.add(getFileToLaunch(config));		
		addArguments(config.getProgramArguments(), arguments);
		String[] cmdLine = new String[arguments.size()];
		arguments.toArray(cmdLine);

		String[] envp = getEnvironment(config);

		// check for cancellation
		if (monitor.isCanceled()) {
			return;
		}

		subMonitor.worked(1);
		subMonitor.subTask(LaunchingMessages.StandardVMDebugger_Starting_virtual_machine____4);

		Process p = null;

		// check for cancellation
		if (monitor.isCanceled()) {
			return;
		}

		File workingDir = getWorkingDir(config);
		p = exec(cmdLine, workingDir, envp);
		if (p == null) {
			return;
		}

		// check for cancellation
		if (monitor.isCanceled()) {
			p.destroy();
			return;
		}

		IProcess process = newProcess(launch, p, renderProcessLabel(cmdLine), getDefaultProcessMap());
		sudoPrompt(config, process);
		
		String commandLine = renderCommandLine(cmdLine);
		LaunchingPlugin.debug("Starting: " + commandLine) ;
		process.setAttribute(IProcess.ATTR_CMDLINE, commandLine);
		subMonitor.worked(1);
		subMonitor.subTask(LaunchingMessages.StandardVMDebugger_Establishing_debug_connection____5);

		debugTarget.setProcess(process);
		RubyDebuggerProxy proxy = getDebugProxy(debugTarget);
		try {
			proxy.start();
			launch.addDebugTarget(debugTarget);
		} catch (IOException iox) {
			LaunchingPlugin.log(new Status(IStatus.ERROR, LaunchingPlugin.PLUGIN_ID, IStatus.ERROR, LaunchingMessages.RdtLaunchingPlugin_processTerminatedBecauseNoDebuggerConnection, null));
			debugTarget.terminate();
		} catch (RubyProcessingException e) {
			abort(LaunchingMessages.StandardVMDebugger_Couldn__t_connect_to_VM_5, e, IRubyLaunchConfigurationConstants.ERR_CONNECTION_FAILED);
			debugTarget.terminate();
		}
		// if (p != null) {
		// p.destroy();
		// }
	}

	protected List<String> debugSpecificVMArgs(RubyDebugTarget debugTarget) {
		return new ArrayList<String>();
	}
	
	protected List<String> debugArgs(RubyDebugTarget debugTarget) {
		List<String> arguments = new ArrayList<String>();
		
		String rdebug = findRDebugExecutable(fVMInstance.getInstallLocation());	
		arguments.add(rdebug);
		arguments.add(PORT_SWITCH);
		arguments.add(Integer.toString(debugTarget.getPort()));
		if (isDebuggerVerbose()) {
			arguments.add(VERBOSE_FLAG);
		}
		return arguments;
	}
	
	protected static boolean isDebuggerVerbose() {
		return LaunchingPlugin.getDefault().getPluginPreferences().getBoolean(PreferenceConstants.VERBOSE_DEBUGGER);
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
