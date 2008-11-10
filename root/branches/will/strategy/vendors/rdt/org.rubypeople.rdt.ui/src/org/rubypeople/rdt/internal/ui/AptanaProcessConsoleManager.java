package org.rubypeople.rdt.internal.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.views.console.ProcessConsole;
import org.eclipse.debug.internal.ui.views.console.ProcessConsoleManager;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.console.IConsoleColorProvider;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.rubypeople.rdt.launching.IRubyLaunchConfigurationConstants;
import org.rubypeople.rdt.launching.ITerminal;

public class AptanaProcessConsoleManager extends ProcessConsoleManager {
	
	private static final String RESET_AUTO_REMOVE_TERMINATED_LAUNCHES_PREF = "RESET_AUTO_REMOVE_TERMINATED_LAUNCHES_PREF";
	
	private static final String TERMINALS_EXTENSION_POINT = "org.rubypeople.rdt.launching.terminals";
	
	public AptanaProcessConsoleManager() {
		// Hack to set the pref value back to true once. If user overrides it later, we don't care.
		if (RubyPlugin.getDefault().getPreferenceStore().getBoolean(RESET_AUTO_REMOVE_TERMINATED_LAUNCHES_PREF)) return;
		if (!DebugUIPlugin.getDefault().getPreferenceStore().getBoolean(IDebugUIConstants.PREF_AUTO_REMOVE_OLD_LAUNCHES)) {
			DebugUIPlugin.getDefault().getPreferenceStore().setValue(IDebugUIConstants.PREF_AUTO_REMOVE_OLD_LAUNCHES, true);
			RubyPlugin.getDefault().getPreferenceStore().setValue(RESET_AUTO_REMOVE_TERMINATED_LAUNCHES_PREF, true);
		}
	}

	public void launchChanged(final ILaunch launch) {
		String terminalType = launch.getAttribute(IRubyLaunchConfigurationConstants.ATTR_USE_TERMINAL);
		IConsole console = findConsole(terminalType);
		if (console != null) {			
			if (console instanceof ITerminal) {
				ITerminal terminal = (ITerminal) console;
				
				String projectName = launch.getAttribute(IRubyLaunchConfigurationConstants.ATTR_PROJECT_NAME);
				if (projectName != null) {
					IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
					if (project != null)
						terminal.setProject(project);
				}
				
				// If not entered from terminal we need to print out a command line to terminal
				String command = launch.getAttribute(IRubyLaunchConfigurationConstants.ATTR_TERMINAL_COMMAND);
				if (command != null) {
					terminal.write(IDebugUIConstants.ID_STANDARD_INPUT_STREAM, command + "\n");					
				}
				terminal.attach(launch.getProcesses()[0]);
				return;
			}
			
			String toDisplay = getDisplayString(launch);
			if (toDisplay == null) toDisplay = terminalType;
			Job job = new Job(toDisplay) { // Job that just terminates when launch does, so we get a progress bar for user
			
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					while (!launch.isTerminated()) {
						if (monitor.isCanceled()) {
							try {
								launch.terminate();
							} catch (DebugException e) {
								RubyPlugin.log(e);
							}
							return Status.CANCEL_STATUS;
						}
						Thread.yield();
					}
					monitor.done();
					return Status.OK_STATUS;
				}
			
			};
			job.setPriority(Job.DECORATE);
			job.schedule();
		}
		
		String force = launch.getAttribute(IRubyLaunchConfigurationConstants.ATTR_FORCE_NO_CONSOLE);		
		if (force != null && Boolean.parseBoolean(force)) {
			// Create a process console, but never add it to list
			handleButDontAddConsole(launch);
		} else {
			super.launchChanged(launch);
		}
	}
	
	private String getDisplayString(ILaunch launch) {
		StringBuffer buffer = new StringBuffer();
		String fileName = launch.getAttribute(IRubyLaunchConfigurationConstants.ATTR_FILE_NAME);
		if (fileName != null) buffer.append(fileName);
		
		String args = launch.getAttribute(IRubyLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS);
		if (args != null) buffer.append(" " + args);
		if (buffer.toString().length() == 0) return null;
		return buffer.toString();
	}

	private IConsole findConsole(String type) {
		IConsole[] consoles = ConsolePlugin.getDefault().getConsoleManager().getConsoles();
		for (int i = 0; i < consoles.length; i++) {
			if (consoles[i].getType().equals(type)) 
				return consoles[i];
		}
		// If there are no instances, open a brand new one!
		ITerminal terminal = getTerminal(type);
		if (terminal != null) {
			ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] {terminal});
			terminal.activate();	
		}
			
		return terminal;
	}
	
	public static ITerminal getTerminal(String id) {
		try {
			IExtensionRegistry registry= Platform.getExtensionRegistry();
			IConfigurationElement[] elements= registry.getConfigurationElementsFor(TERMINALS_EXTENSION_POINT);
			for (int i = 0; i < elements.length; i++) {
				String terminalId = elements[i].getAttribute("id");
				if (terminalId.equals(id))
					return (ITerminal) elements[i].createExecutableExtension("class");			
			}
		} catch (InvalidRegistryObjectException e) {
			RubyPlugin.log(e);
		} catch (CoreException e) {
			RubyPlugin.log(e);
		}
		return null;
	}

	public void launchAdded(ILaunch launch) {
		DebugPlugin.getDefault().getLaunchManager().removeLaunchListener(DebugUIPlugin.getDefault().getProcessConsoleManager());	
		super.launchAdded(launch);
	}

	private void handleButDontAddConsole(ILaunch launch) {
		IProcess[] processes= launch.getProcesses();
		for (int i= 0; i < processes.length; i++) {
		    if (getConsoleDocument(processes[i]) == null) {
		        IProcess process = processes[i];
		        if (process.getStreamsProxy() == null) {
		            continue;
		        }
		        ILaunchConfiguration launchConfiguration = launch.getLaunchConfiguration();

		        //create a new console.
		        IConsoleColorProvider colorProvider = getColorProvider(process.getAttribute(IProcess.ATTR_PROCESS_TYPE));
		        String encoding = null;
		        try {
		            if (launchConfiguration != null) {
		                encoding = launchConfiguration.getAttribute(IDebugUIConstants.ATTR_CONSOLE_ENCODING, (String)null);
		            }
		        } catch (CoreException e) {
		        }
		        ProcessConsole pc = new ProcessConsole(process, colorProvider, encoding);
//	                pc.setAttribute(IDebugUIConstants.ATTR_CONSOLE_PROCESS, process);

		        //add new console to console manager.
//	                ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[]{pc});
		    }
		}
	}
}
