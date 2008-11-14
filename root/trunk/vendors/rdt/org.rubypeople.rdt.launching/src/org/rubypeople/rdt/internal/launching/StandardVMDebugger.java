package org.rubypeople.rdt.internal.launching;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.SocketUtil;
import org.rubypeople.rdt.internal.debug.core.RdtDebugCorePlugin;
import org.rubypeople.rdt.internal.debug.core.RubyDebuggerProxy;
import org.rubypeople.rdt.internal.debug.core.model.RubyDebugTarget;
import org.rubypeople.rdt.internal.debug.core.model.RubyProcessingException;
import org.rubypeople.rdt.launching.IRubyLaunchConfigurationConstants;
import org.rubypeople.rdt.launching.IVMRunner;
import org.rubypeople.rdt.launching.VMRunnerConfiguration;

public class StandardVMDebugger extends StandardVMRunner implements IVMRunner {

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

	protected Collection<String> debugArgs(RubyDebugTarget debugTarget) {
		return new ArrayList<String>();
	}

	protected RubyDebuggerProxy getDebugProxy(RubyDebugTarget debugTarget) {
		return new RubyDebuggerProxy(debugTarget, false /* isRubyDebug*/);
	}

	protected List<String> debugSpecificVMArgs(RubyDebugTarget debugTarget) {
		List<String> arguments = new ArrayList<String>();
//		 FIXME Somehow hook this into the loadpath stuff?		
		arguments.add("-I");
		arguments.add(new Path(getDirectoryOfRubyDebuggerFile()).toOSString());
		if (!debugTarget.isUsingDefaultPort()) {
			arguments.add("-r" + debugTarget.getDebugParameterFile().getAbsolutePath());
		}

		if (RdtDebugCorePlugin.isRubyDebuggerVerbose() || isDebuggerVerbose()) {
			arguments.add("-rclassic-debug-verbose");
		} else {
			arguments.add("-rclassic-debug");
		}
		return arguments;
	}

	protected static boolean isDebuggerVerbose() {
		return LaunchingPlugin.getDefault().getPluginPreferences().getBoolean(PreferenceConstants.VERBOSE_DEBUGGER);
	}

	public static String getDirectoryOfRubyDebuggerFile() {
		return RubyCore.getOSDirectory(LaunchingPlugin.getDefault()) + "ruby";
	}

}
