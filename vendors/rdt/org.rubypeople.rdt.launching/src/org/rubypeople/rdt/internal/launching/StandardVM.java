package org.rubypeople.rdt.internal.launching;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.core.model.IProcess;
import org.rubypeople.rdt.core.util.Util;
import org.rubypeople.rdt.launching.AbstractVMInstall;
import org.rubypeople.rdt.launching.IVMInstallType;
import org.rubypeople.rdt.launching.IVMRunner;

public class StandardVM extends AbstractVMInstall {

	private static final String FINISHED_MARKER = "finished.txt";
	private static final int FIVE_MINUTES = 5 * 60 * 1000;
	private static final String VERSION_TXT = "version.txt";
	private static final int CORE_STUBS_VERSION = 3;
	private static boolean isRunning = false;
	
	public StandardVM(IVMInstallType type, String id) {
		super(type, id);
	}
	
	@Override
	public IVMRunner getVMRunner(String mode) {
		if (ILaunchManager.RUN_MODE.equals(mode)) {
			IVMRunner runner = new StandardVMRunner();
			runner.setVMInstall(this);
			return runner;
		} else if (ILaunchManager.DEBUG_MODE.equals(mode)) {
			IVMRunner runner = null;
			if (useRDebug()) {
				runner = new RDebugVMDebugger();
			} else {
				runner = new StandardVMDebugger();
			}
			runner.setVMInstall(this);
			return runner;
		} else if (ILaunchManager.PROFILE_MODE.equals(mode)) {
			return getVMRunner(this, mode);
		}
		return null;
	}

	protected boolean useRDebug() {
		return LaunchingPlugin.getDefault().getPluginPreferences().getBoolean(PreferenceConstants.USE_RUBY_DEBUG);
	}

	public String getRubyVersion() {
		 IVMInstallType installType = getVMInstallType();
	        File installLocation = getInstallLocation();
	        if (installLocation != null) {
	            File executable = installType.findExecutable(installLocation);
	            if (executable != null) {
	                String vmVersion = installType.getVMVersion(installLocation, executable);
	                // strip off extra info
	                StringBuffer version = new StringBuffer();
	                for (int i = 0; i < vmVersion.length(); i++) {
	                    char ch = vmVersion.charAt(i);
	                    if (Character.isDigit(ch) || ch == '.') {
	                        version.append(ch);
	                    } else {
	                        break;
	                    }
	                }
	                if (version.length() > 0) {
	                    return version.toString();
	                }
	            }
	        }
	        return null;
	}
	
	public String getPlatform() {
		 IVMInstallType installType = getVMInstallType();
	     File installLocation = getInstallLocation();
	     if (installLocation != null) {
	    	 File executable = installType.findExecutable(installLocation);
	         if (executable != null) {
	        	 String platform = installType.getVMPlatform(installLocation, executable);
	        	 if (platform != null)
	        		 return platform;
	         }
	     }
	     return "ruby";
	}
	
	@Override
	public IPath[] getLibraryLocations() {
		IPath[] paths =  super.getLibraryLocations();
		if (paths != null) return paths;
		return getDefaultLibraryLocations();
	}

	private IPath[] getDefaultLibraryLocations() {
		 IPath[] dflts = getVMInstallType().getDefaultLibraryLocations(getInstallLocation());
		 IPath coreStubsPath = generateCoreStubs(StandardVMType.findRubyExecutable(getInstallLocation()));
		 if (coreStubsPath == null) {
			 return dflts;
		 }
		 IPath[] paths = new IPath[dflts.length + 1];
		 for (int i = 0; i < dflts.length; i++) {
			 paths[i] = dflts[i];
		 }
		 paths[dflts.length] = coreStubsPath;		 
		 return paths;
	}
	
	/**
	 * Launch a ruby script to generate core class stubs for use in RDT internally (since Ruby core 
	 * stuff is not in any scripts, they're built into the VM in C code).
	 * @param rubyExecutable
	 * @return an IPath pointing to the directory containing the core library stubs
	 */
	private IPath generateCoreStubs(final File rubyExecutable) {
		if (rubyExecutable == null) return null;
		//locate the script to generate our core stubs
		final File coreStubber = LaunchingPlugin.getFileInPlugin(new Path("ruby").append("standard_vm").append("core_stubber.rb")); //$NON-NLS-1$ //$NON-NLS-2$
		if (coreStubber.exists()) {	
			final IPath stubFolder = LaunchingPlugin.getDefault().getStateLocation().append(getId()).append("lib"); //$NON-NLS-1$
			if (stubFolder.toFile().exists() && stubFolder.append(FINISHED_MARKER).toFile().exists()) { // if last run failed to finish, relaunch generation
				// If version doesn't match current, then wipe the stubs and re-generate
				int version = getCoreStubsVersion(stubFolder);
				if (version == CORE_STUBS_VERSION) {
					return stubFolder; // we've already created the stubs for this VM
				} else {
					// Delete the old stubs
					delete(stubFolder.toFile());
					// Insert new version file
					writeNewVersion(stubFolder);
				}				
			}
			stubFolder.toFile().mkdirs(); // Make the directory structure to throw the files into			

			if (isRunning) return stubFolder;
			isRunning = true;
			
			Job job = new Job("Generating core library stubs..."){
			
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					String rubyExecutablePath = rubyExecutable.getAbsolutePath();
					rubyExecutablePath = rubyExecutablePath.replace("rubyw", "ruby"); // Use ruby.exe, not rubyw!
					String[] cmdLine = new String[] {rubyExecutablePath, coreStubber.getAbsolutePath(), stubFolder.toOSString()};
					Process p = null;
					try {
						p = Runtime.getRuntime().exec(cmdLine);
						IProcess process = DebugPlugin.newProcess(new Launch(null, ILaunchManager.RUN_MODE, null), p, "Core Classes Stub Generation"); //$NON-NLS-1$
						long start = System.currentTimeMillis();
						while (!process.isTerminated()) {
							Thread.yield();
							if (monitor.isCanceled() || (System.currentTimeMillis() > (start + (FIVE_MINUTES)))) {
								p.destroy();
								return Status.CANCEL_STATUS;
							}
						}
						stubFolder.append(FINISHED_MARKER).toFile().createNewFile(); // Add file which just marks we were able to finish
					} catch (IOException ioe) {
						LaunchingPlugin.log(ioe);
					} finally {
						if (p != null) {
							p.destroy();
						}
						isRunning = false;
					}
					return Status.OK_STATUS;
				}
			
			};		
			job.schedule();
			return stubFolder;
		}
		return null;
	}

	private void writeNewVersion(IPath stubFolder) {
		stubFolder.toFile().mkdirs();
		File versionFile = stubFolder.append(VERSION_TXT).toFile();
		FileWriter writer = null;
		try {
			versionFile.createNewFile();
			writer = new FileWriter(versionFile);
			writer.write(Integer.toString(CORE_STUBS_VERSION));
		} catch (IOException e) {
			LaunchingPlugin.log(e);
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (IOException e) {
				// ignore
			}
		}		
	}

	private void delete(File file) {
		if (file.isDirectory()) {
			File[] children = file.listFiles();
			for (int i = 0; i < children.length; i++) {
				delete(children[i]);
			}
		} 
		file.delete();
		file.deleteOnExit();	
	}

	private int getCoreStubsVersion(IPath stubFolder) {
		try {
			File versionFile = stubFolder.append(VERSION_TXT).toFile();
			if (!versionFile.exists()) {
				return 1;
			}
			String raw = new String(Util.getFileCharContent(versionFile, null));
			return Integer.parseInt(raw);
		} catch (NumberFormatException e) {
			LaunchingPlugin.log(e);
		} catch (IOException e) {
			LaunchingPlugin.log(e);
		}
		return CORE_STUBS_VERSION;
	}
}
