package org.rubypeople.rdt.internal.launching;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IStatusHandler;
import org.eclipse.debug.internal.core.DebugCoreMessages;
import org.osgi.framework.Bundle;
import org.rubypeople.rdt.launching.IRubyLaunchConfigurationConstants;
import org.rubypeople.rdt.launching.IVMInstall;
import org.rubypeople.rdt.launching.IVMRunner;
import org.rubypeople.rdt.launching.VMRunnerConfiguration;

public class JRubyVMRunner extends StandardVMRunner implements IVMRunner {

	private static final String GEM_MEMORY_HACK = "-J-Xmx512M";
	
	@Override
	protected String[] combineVmArgs(VMRunnerConfiguration configuration, IVMInstall vmInstall) {
		String[] result = tryGemMemoryHack(configuration, super.combineVmArgs(configuration, vmInstall));
		result = tryRailsRubyPlatformHack(configuration, result);
		return result;
	}

	/**
	 * Hack to set RUBY_PLATFORM to "jruby mswin" on win32 under jruby for rails scripts.
	 * @param configuration
	 * @param old
	 * @return
	 */
	private String[] tryRailsRubyPlatformHack(VMRunnerConfiguration configuration, String[] old) {
		if (!isWindows()) return old;
		
		String file = configuration.getFileToLaunch();
		if (file.endsWith("script/server")) { // when we run rails server, we already have the -e load(ARGV.shift) arg
			if (old.length == 0) {
				return new String[] {"-e", "RUBY_PLATFORM='java mswin'", "-e", "load(ARGV.shift)"};
			}
			String newArray[] = new String[old.length + 2];
			System.arraycopy(old, 0, newArray, 0, old.length);
		
			newArray[old.length - 2] = "-e";
			newArray[old.length - 1] = "RUBY_PLATFORM='java mswin'";
			newArray[old.length] = "-e";
			newArray[old.length + 1] = "load(ARGV.shift)";
			return newArray;
		}
		
		if (!(file.endsWith("rake") || file.endsWith("script/generate") || 
				file.endsWith("script/plugin") || file.endsWith("script/console") || 
				file.endsWith("script/destroy"))) return old;
		
		String newArray[] = new String[old.length + 4];
		System.arraycopy(old, 0, newArray, 0, old.length);
	
		newArray[old.length] = "-e";
		newArray[old.length + 1] = "RUBY_PLATFORM='java mswin'";
		newArray[old.length + 2] = "-e";
		newArray[old.length + 3] = "load(ARGV.shift)";		
		return newArray;
	}

	/**
	 * This is a hack to bump up max memory to 512M when running gem commands on JRuby. 
	 * Otherwise it will usually fail with out of memory errors.
	 * @param configuration
	 * @param vmInstall
	 * @return
	 */
	private String[] tryGemMemoryHack(VMRunnerConfiguration configuration, String[] old) {
		if (isWindows()) return old; // return since we set this down in constructProgramString on Windows
		
		String file = configuration.getFileToLaunch();
		if (!file.endsWith("gem")) return old;
		
		String newArray[] = new String[old.length + 1];
		System.arraycopy(old, 0, newArray, 1, old.length);
		newArray[0] = GEM_MEMORY_HACK;
		return newArray;
	}

	@Override
	protected String getCommand(VMRunnerConfiguration config) {
		String command = super.getCommand(config);
		if (command == null) return null;
		// in jruby they preface these commands with a j...
		if (command.equals("ruby") || command.equals("rubyw") || command.equals("irb"))
			return "j" + command;
		return command;
	}
	
	/**
	 * Construct and return a String containing the full path of a ruby executable
	 * command such as 'jruby' or 'jrubyw'.  If the configuration specifies an
	 * explicit executable, that is used.
	 * 
	 * @return full path to ruby executable
	 * @exception CoreException if unable to locate an executable
	 */
	protected List<String> constructProgramString(VMRunnerConfiguration config) throws CoreException {		
		if (!isWindows()) {
			String installLocation = fVMInstance.getInstallLocation().getAbsolutePath();
			File exe = new File(installLocation + File.separatorChar + "bin" + File.separatorChar + "jruby"); //$NON-NLS-1$ 		
			if (fileExists(exe)) {	
				// force it to be executable on non windows platforms?
				try {
					Process p = setExecutableBit(exe.getAbsolutePath());
					p.waitFor();
				} catch (InterruptedException e) {
					LaunchingPlugin.log(e);
				}
			}	
			// FIXME Hack to add derby.jar symbolic link in lib folder for non-windows platforms
			try {
				String link = installLocation + File.separator + "lib" + File.separator + "derbyclient.jar";
				File linkFile = new File(link);
				if (!linkFile.exists()) {
					Bundle bundle = Platform.getBundle("com.aptana.ide.libraries");
					if (bundle != null) {
						URL url = FileLocator.find(bundle, new Path("derbyclient.jar"), null);
						url = FileLocator.toFileURL(url);
						String path = url.getFile();
						File file = new File(path);
						Process p = createSymbolicLink(file.getAbsolutePath(), link);
						p.waitFor();				
					}		
				}
			} catch (Exception e) {
				LaunchingPlugin.log(e);
			}
			return super.constructProgramString(config);
		}
		
		List<String> string = new ArrayList<String>();
		// On windows we need to launch via "java"
		string.add("java");
				
		// Build the path to the jruby jar
		String installLocation = fVMInstance.getInstallLocation().getAbsolutePath();
		File exe = new File(installLocation + File.separatorChar + "bin" + File.separatorChar + "jruby.bat"); //$NON-NLS-1$ 		
		if (fileExists(exe)) {						
			File lib = new File(fVMInstance.getInstallLocation(), "lib");
			String[] jars = lib.list(new FilenameFilter() {
			
				public boolean accept(File dir, String name) {
					return name.endsWith(".jar");
				}
			
			});
			String jarString = "";
			for (int i = 0; i < jars.length; i++) {
				if ( i != 0) jarString += File.pathSeparator;
				jarString += lib.getAbsolutePath() + File.separator + jars[i];
			}		
			// FIXME This is a hack for using derby with JRuby. adds derby from aptana plugin to classpath on Windows
			try {
				Bundle bundle = Platform.getBundle("com.aptana.ide.libraries");
				if (bundle != null) {
					URL url = FileLocator.find(bundle, new Path("derbyclient.jar"), null);
					url = FileLocator.toFileURL(url);
					String path = url.getFile();
					File file = new File(path.substring(1));
					jarString += File.pathSeparator + file.getAbsolutePath();
				}
			} catch (IOException e) {
				LaunchingPlugin.log(e);
			}
			
			string.add("-Xverify:none");
			if (config.getFileToLaunch().endsWith("gem"))  {
				string.add("-Xmx512m");
			} else {
				string.add("-Xmx256m");
			}
			string.add("-Xss1024k");
			string.add("-cp");
			string.add("\"" + jarString + "\"");
			string.add("-Djruby.base=\"" + installLocation + "\"");
			string.add("-Djruby.home=\"" + installLocation + "\"");
			string.add("-Djruby.lib=\"" + lib.getAbsolutePath() + "\"");
			if (isWindows())
				string.add("-Djruby.shell=\"cmd.exe\"");
			else
				string.add("-Djruby.shell=/bin/sh");
			string.add("-Djruby.script=\"" + exe.getName() + "\"");
			string.add("org.jruby.Main");			
			return string;
		}
		
		abort(MessageFormat.format(LaunchingMessages.StandardVMRunner_Specified_executable__0__does_not_exist_for__1__4, "jruby.jar", fVMInstance.getName()), null, IRubyLaunchConfigurationConstants.ERR_INTERNAL_ERROR); 
		// NOTE: an exception will be thrown - null cannot be returned
		return null;		
	}

	private boolean isWindows() {
		return Platform.getOS().equals(Platform.OS_WIN32);
	}	
	
	@Override
	protected String[] getEnvironment(VMRunnerConfiguration config) {
		String[] env = super.getEnvironment(config);
		if (env == null) env = new String[0];
		int itemsToAdd = 4;
		if (isWindows())
			itemsToAdd++;		
		String[] special = new String[env.length + itemsToAdd];
		System.arraycopy(env, 0, special, 0, env.length);
		special[env.length + 0] = "CLASSPATH=.";
		special[env.length + 1] = "JRUBY_BASE=" + fVMInstance.getInstallLocation().getAbsolutePath();
		special[env.length + 2] = "JRUBY_HOME=" + fVMInstance.getInstallLocation().getAbsolutePath();
		special[env.length + 3] = "JAVA_HOME=" + System.getProperty("java.home");
		String root = System.getenv("SYSTEMROOT");
		if (root == null || root.trim().length() == 0) {
			root = "C:\\Windows";
		}
		if (isWindows())
			special[env.length + 4] = "SystemRoot=" + root; // It is absolutely essential that this is set for JRuby to work properly! Otherwise you will get problems running rails server "initialize: name or service unknown"!
		return special;
	}
	
	@Override
	protected void addStreamSync(List<String> arguments) {
		// do nothing
	}
	
	/**
	 * @since 0.9.0
	 * @see DebugPlugin#exec(String[], File, String[])
	 */
	protected Process exec(String[] cmdLine, File workingDirectory, String[] envp) throws CoreException {	
		String cmd = getCmdLineAsString(cmdLine);
		LaunchingPlugin.info("Starting: " + cmd);
		Process p= null;
		try {
			if (isWindows()) { // have to use quoted strings on windows when paths contain spaces
				if (workingDirectory == null) {
					p= Runtime.getRuntime().exec(cmd, envp);
				} else {
					p= Runtime.getRuntime().exec(cmd, envp, workingDirectory);
				}
			} else { // on mac (and hopefully otehr OSes), have to use array of command line strings when parts contain spaces
				if (workingDirectory == null) {
					p= Runtime.getRuntime().exec(cmdLine, envp);
				} else {
					p= Runtime.getRuntime().exec(cmdLine, envp, workingDirectory);
				}
			}
		} catch (IOException e) {
		    Status status = new Status(IStatus.ERROR, DebugPlugin.getUniqueIdentifier(), DebugPlugin.INTERNAL_ERROR, DebugCoreMessages.DebugPlugin_Exception_occurred_executing_command_line__1, e); 
		    throw new CoreException(status);
		} catch (NoSuchMethodError e) {
			//attempting launches on 1.2.* - no ability to set working directory			
			IStatus status = new Status(IStatus.ERROR, DebugPlugin.getUniqueIdentifier(), DebugPlugin.ERR_WORKING_DIRECTORY_NOT_SUPPORTED, DebugCoreMessages.DebugPlugin_Eclipse_runtime_does_not_support_working_directory_2, e); 
			IStatusHandler handler = DebugPlugin.getDefault().getStatusHandler(status);
			
			if (handler != null) {
				Object result = handler.handleStatus(status, null);
				if (result instanceof Boolean && ((Boolean)result).booleanValue()) {
					p= exec(cmdLine, null);
				}
			}
		}
		return p;
	}
	
	private Process setExecutableBit(String filePath) {
		LaunchingPlugin.log("Setting executable bit for: " + filePath);
		
		if (filePath == null) return null;
		try {
			Process pr = Runtime.getRuntime().exec(new String[] { "chmod", "a+x", filePath }); //$NON-NLS-1$ //$NON-NLS-2$
			Thread chmodOutput = new StreamConsumer(pr.getInputStream());
			chmodOutput.setName("chmod output reader"); //$NON-NLS-1$
			chmodOutput.start();
			Thread chmodError = new StreamConsumer(pr.getErrorStream());
			chmodError.setName("chmod error reader"); //$NON-NLS-1$
			chmodError.start();
			return pr;
		} catch (IOException ioe) {
			LaunchingPlugin.log(ioe);
		}		
		return null;
	}
	
	private Process createSymbolicLink(String target, String linkLocation) {
		LaunchingPlugin.log("Creating symbolic link from: " + linkLocation + " to: " + target);
		
		if (target == null || linkLocation == null) return null;
		try {
			Process pr = Runtime.getRuntime().exec(new String[] { "ln", "-s", target, linkLocation }); //$NON-NLS-1$ //$NON-NLS-2$
			Thread chmodOutput = new StreamConsumer(pr.getInputStream());
			chmodOutput.setName("ln -s output reader"); //$NON-NLS-1$
			chmodOutput.start();
			Thread chmodError = new StreamConsumer(pr.getErrorStream());
			chmodError.setName("ln - error reader"); //$NON-NLS-1$
			chmodError.start();
			return pr;
		} catch (IOException ioe) {
			LaunchingPlugin.log(ioe);
		}		
		return null;
	}
	
	public static class StreamConsumer extends Thread {
		InputStream is;
		byte[] buf;
		public StreamConsumer(InputStream inputStream) {
			super();
			this.setDaemon(true);
			this.is = inputStream;
			buf = new byte[512];
		}
		public void run() {
			try {
				int n = 0;
				while (n >= 0)
					n = is.read(buf);
			} catch (IOException ioe) {
			}
		}
	}
	
}
