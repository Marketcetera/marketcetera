package org.rubypeople.rdt.internal.launching;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.eclipse.osgi.service.environment.Constants;
import org.rubypeople.rdt.launching.AbstractVMInstallType;
import org.rubypeople.rdt.launching.IVMInstall;

public class StandardVMType extends AbstractVMInstallType {

	private static final String USR_BIN = "/usr/bin";
	private static final String USR_BIN_RUBY = USR_BIN + "/ruby";
	private static final String USR_LOCAL_BIN_RUBY = "/usr/local/bin/ruby";
	private static final String OPT_LOCAL_BIN_RUBY = "/opt/local/bin/ruby";
	private static final String MAC_OSX_LEOPARD_RUBY_PATH = "/System/Library/Frameworks/Ruby.framework/Versions/Current/usr/bin/ruby";

	/**
	 * Map of the install path for which we were unable to generate
	 * the library info during this session.
	 */
	private static Map<String, LibraryInfo> fgFailedInstallPath= new HashMap<String, LibraryInfo>();
	
	/**
	 * Convenience handle to the system-specific file separator character
	 */															
	private static final char fgSeparator = File.separatorChar;

	/**
	 * The list of locations in which to look for the ruby executable in candidate
	 * VM install locations, relative to the VM install location.
	 */
	private static final String[] fgCandidateRubyFiles = {"rubyw", "rubyw.exe", "ruby", "ruby.exe"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	private static final String[] fgCandidateRubyLocations = {"", "bin" + fgSeparator}; //$NON-NLS-1$ //$NON-NLS-2$
	
	
	@Override
	protected IVMInstall doCreateVMInstall(String id) {
		return new StandardVM(this, id);
	}

	public IPath[] getDefaultLibraryLocations(File installLocation) {		
		File rubyExecutable = findRubyExecutable(installLocation);
		LibraryInfo info;
		if (rubyExecutable == null) {
			info = getDefaultLibraryInfo(installLocation);
		} else {
			info = getLibraryInfo(installLocation, rubyExecutable);
		}
		String[] loadpath = info.getBootpath();
		IPath[] paths = new IPath[loadpath.length];
		for (int i = 0; i < loadpath.length; i++) {
			paths[i] = new Path(loadpath[i]);
		}
		return paths;
	}

	public String getName() {
		return LaunchingMessages.StandardVMType_Standard_VM_3; 
	}

	public IStatus validateInstallLocation(File rubyHome) {
		IStatus status = null;
		File rubyExecutable = findRubyExecutable(rubyHome);
		if (rubyExecutable == null) {
			status = new Status(IStatus.ERROR, LaunchingPlugin.getUniqueIdentifier(), 0, LaunchingMessages.StandardVMType_Not_a_JDK_Root__Java_executable_was_not_found_1, null);						
		} else {
			if (canDetectDefaultSystemLibraries(rubyHome, rubyExecutable)) {
				status = new Status(IStatus.OK, LaunchingPlugin.getUniqueIdentifier(), 0, LaunchingMessages.StandardVMType_ok_2, null); 
			} else {
				status = new Status(IStatus.ERROR, LaunchingPlugin.getUniqueIdentifier(), 0, LaunchingMessages.StandardVMType_Not_a_JDK_root__System_library_was_not_found__1, null); 
			}
		}
		return status;		
	}
	
	/**
	 * Starting in the specified VM install location, attempt to find the 'ruby' executable
	 * file.  If found, return the corresponding <code>File</code> object, otherwise return
	 * <code>null</code>.
	 */
	public static File findRubyExecutable(File vmInstallLocation) {
		// Try each candidate in order.  The first one found wins.  Thus, the order
		// of fgCandidateRubyLocations and fgCandidateRubyFiles is significant.
		for (int i = 0; i < fgCandidateRubyFiles.length; i++) {
			for (int j = 0; j < fgCandidateRubyLocations.length; j++) {
				File rubyFile = new File(vmInstallLocation, fgCandidateRubyLocations[j] + fgCandidateRubyFiles[i]);
				if (rubyFile.isFile()) {
					return rubyFile;
				}				
			}
		}		
		return null;							
	}
	
	/**
	 * Return <code>true</code> if the appropriate system libraries can be found for the
	 * specified ruby executable, <code>false</code> otherwise.
	 */
	protected boolean canDetectDefaultSystemLibraries(File rubyHome, File rubyExecutable) {
		IPath[] locations = getDefaultLibraryLocations(rubyHome);
		return locations.length > 0; 
	}

	public String getVMVersion(File installLocation, File executable) {
		LibraryInfo info = getLibraryInfo(installLocation, executable);
		return info.getVersion();
	}

	/**
	 * Return library information corresponding to the specified install
	 * location. If the info does not exist, create it using the given Ruby
	 * executable.
	 */
	protected synchronized LibraryInfo getLibraryInfo(File rubyHome, File rubyExecutable) {		
		// See if we already know the info for the requested VM.  If not, generate it.
		String installPath = rubyHome.getAbsolutePath();
		LibraryInfo info = LaunchingPlugin.getLibraryInfo(this, installPath);
		if (info == null) {
			info= fgFailedInstallPath.get(installPath);
			if (info == null) {
				info = generateLibraryInfo(rubyHome, rubyExecutable);
				if (info == null) {
					info = getDefaultLibraryInfo(rubyHome);
					fgFailedInstallPath.put(installPath, info);
				} else {
				    LaunchingPlugin.setLibraryInfo(this, installPath, info);
				}
			}
		} 
		return info;
	}	
	
	private LibraryInfo generateLibraryInfo(File rubyHome, File rubyExecutable) {
		LibraryInfo info = null;		
		//locate the script to grab us our loadpaths
		File file = LaunchingPlugin.getFileInPlugin(new Path("ruby").append("standard_vm_type").append("loadpath.rb")); //$NON-NLS-1$
		if (file.exists()) {	
			String rubyExecutablePath = rubyExecutable.getAbsolutePath();
			String[] cmdLine = new String[] {rubyExecutablePath, file.getAbsolutePath()};  //$NON-NLS-1$			
			info = parseLibraryInfo(executeAndRead(cmdLine));			
		}
		if (info == null) {
		    // log error that we were unable to generate library info - see bug 70011
		    LaunchingPlugin.log(MessageFormat.format("Failed to retrieve default libraries for {0}", rubyHome.getAbsolutePath())); //$NON-NLS-1$
		}
		return info;
	}
	
	/**
	 * Parses the output from 'LibraryDetector'.
	 */
	protected LibraryInfo parseLibraryInfo(List<String> lines) {
		if (lines == null || lines.size() == 0) return null;
		
		String version = lines.remove(0);
	    removeNotExistingLibs(lines);
		if (lines.size() > 0) {
			String[] loadpath = lines.toArray(new String[lines.size()]);
			return new LibraryInfo(version, loadpath);		
		}
		return null;
	}
	
	private List<String> readOutput(IProcess process) {
		IStreamsProxy streamsProxy = process.getStreamsProxy();
		String text = null;
		if (streamsProxy != null) {
			text = streamsProxy.getOutputStreamMonitor().getContents();
		}
		BufferedReader reader = new BufferedReader(new StringReader(text));
		List<String> lines = new ArrayList<String>();
		try {
			String line = null;
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
		} catch (IOException e) {
			LaunchingPlugin.log(e);
		}
		return lines;
	}

	/**
	 * Do not consider libraries which does not exist.
	 * @param libraries
	 */
	private void removeNotExistingLibs(List<String> libraries) {
		List<String> toRemove = new ArrayList<String>();
		for (String path : libraries) {
			File file = new File(path);
			if (!file.exists())
				toRemove.add(path);
		}
		libraries.removeAll(toRemove);
	}

	/**
	 * Returns default library info for the given install location.
	 * 
	 * @param installLocation
	 * @return LibraryInfo
	 */
	protected LibraryInfo getDefaultLibraryInfo(File installLocation) {
		IPath[] dflts = getDefaultSystemLibrary(installLocation);
		String[] strings = new String[dflts.length];
		for (int i = 0; i < dflts.length; i++) {
			strings[i] = dflts[i].toOSString();
		}
		return new LibraryInfo("1.8.4", strings);		 //$NON-NLS-1$
	}
	
	/**
	 * Return an <code>IPath</code> corresponding to the single library file containing the
	 * standard Ruby classes for VMs version 1.8.x.
	 */
	protected IPath[] getDefaultSystemLibrary(File rubyHome) {
		String stdPath = rubyHome.getAbsolutePath() + fgSeparator + "lib" + fgSeparator + "ruby" + fgSeparator + "1.8";
		String sitePath = rubyHome.getAbsolutePath() + fgSeparator + "lib" + fgSeparator + "ruby" + fgSeparator + "site_ruby" + fgSeparator + "1.8";
		IPath[] paths = new IPath[2];		
		paths[0] = new Path(sitePath);
		paths[1] = new Path(stdPath);
		return paths;
	}

	/* (non-Javadoc)
	 * @see org.rubypeople.rdt.launching.IVMInstallType#detectInstallLocation()
	 */
	public File detectInstallLocation() {
		File rubyExecutable = null;
		if (Platform.getOS().equals(Constants.OS_WIN32)) {
			String winPath = System.getenv("Path");  // iterate through system path and try to find ruby.exe
			String[] paths = winPath.split(";");
			for (int i = 0; i < paths.length; i++) {
				String possibleExecutablePath = paths[i] + File.separator + "ruby.exe";
				File possible = new File(possibleExecutablePath);
				if (possible.exists()) {
					rubyExecutable = possible; 
					break;
				}
			}
			if (rubyExecutable == null) { // if all else fails, just try "C:/ruby"				
				rubyExecutable = new File("C:" + File.separator + "ruby" + File.separator + "bin" + File.separator + "ruby.exe");
			}
		} else { 
			// Fix for ROR-179
			if (Platform.getOS().equals(Constants.OS_MACOSX)) { // Mac OSX Leopard
				File rubyHome =	tryLocation(new File(MAC_OSX_LEOPARD_RUBY_PATH));
				if (rubyHome != null) return rubyHome;
			}
			
			// Mac, Linux - so let's just run 'which ruby' and parse out the result
			String[] cmdLine = new String[] { "which", "ruby" }; //$NON-NLS-1$ //$NON-NLS-2$
			Process p = null;			
			try {
				p = Runtime.getRuntime().exec(cmdLine);
				IProcess process = DebugPlugin.newProcess(new Launch(null,
						ILaunchManager.RUN_MODE, null), p,
						"Standard Ruby VM Install Detection"); //$NON-NLS-1$
				for (int i = 0; i < 200; i++) {
					// Wait no more than 10 seconds (200 * 50 mils)
					if (process.isTerminated()) {
						break;
					}
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
					}
				}
				rubyExecutable = parseRubyExecutableLocation(process);
			} catch (IOException ioe) {
				LaunchingPlugin.log(ioe);
			} finally {
				if (p != null) {
					p.destroy();
				}
			}
			
			// If we don;t find ruby, or we find one at /usr/bin/ruby:
			// try to see if there's one at /usr/local or /opt/local. If so, then prefer one of those.
			if (rubyExecutable == null || rubyExecutable.getAbsolutePath().startsWith(USR_BIN)) {
				File rubyHome =	tryLocation(new File(USR_LOCAL_BIN_RUBY));
				if (rubyHome != null) return rubyHome;
				
				rubyHome =	tryLocation(new File(OPT_LOCAL_BIN_RUBY));
				if (rubyHome != null) return rubyHome;
			}
			if (rubyExecutable == null) { // force trying /usr/bin/ruby if we didn't detect anything, and found nothing at /usr/local or /opt/local
				rubyExecutable = new File(USR_BIN_RUBY);
			}
		}		
		return tryLocation(rubyExecutable);
	}

	private File tryLocation(File rubyExecutable) {
		if (rubyExecutable == null) {
			return null;
		}

		File bin = rubyExecutable.getParentFile();
		if (!bin.exists()) return null;
		File rubyHome = bin.getParentFile();
		if (!rubyHome.exists()) return null;
		if (!canDetectDefaultSystemLibraries(rubyHome, rubyExecutable)) {
			return null;
		}	
	
		return rubyHome;
	}

	/**
	 * Parses the output from 'Standard Ruby VM Install Detector'.
	 */
	protected File parseRubyExecutableLocation(IProcess process) {
		IStreamsProxy streamsProxy = process.getStreamsProxy();
		String text = null;
		if (streamsProxy != null) {
			text = streamsProxy.getOutputStreamMonitor().getContents();
		}
		BufferedReader reader = new BufferedReader(new StringReader(text));
		List<String> lines = new ArrayList<String>();
		try {
			String line = null;
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
		} catch (IOException e) {
			LaunchingPlugin.log(e);
		}
		if (lines.size() > 0) {
			String location = lines.remove(0);
		    File executable = new File(location);
		    if (executable.isFile() && executable.exists()) return executable;
		}
		return null;
	}

	public File findExecutable(File installLocation) {
		return findRubyExecutable(installLocation);
	}
	
	public String getVMPlatform(File rubyHome, File rubyExecutable) {
		String rubyExecutablePath = rubyExecutable.getAbsolutePath();
		String[] cmdLine = new String[] {rubyExecutablePath, "-v"};  //$NON-NLS-1$
		
		String platform = parsePlatform(executeAndRead(cmdLine));
		if (platform == null) {
		    // log error that we were unable to generate library info - see bug 70011
		    LaunchingPlugin.log(MessageFormat.format("Failed to retrieve platform for {0}", rubyHome.getAbsolutePath())); //$NON-NLS-1$
		}
		return platform;
	}

	private List<String> executeAndRead(String[] cmdLine) {
		Process p = null;
		try {
			p = Runtime.getRuntime().exec(cmdLine);
			IProcess process = DebugPlugin.newProcess(new Launch(null, ILaunchManager.RUN_MODE, null), p, "Library Detection"); //$NON-NLS-1$
			for (int i= 0; i < 200; i++) {
				// Wait no more than 10 seconds (200 * 50 mils)
				if (process.isTerminated()) {
					break;
				}
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
				}
			}
			return readOutput(process);
		} catch (IOException ioe) {
			LaunchingPlugin.log(ioe);
		} finally {
			if (p != null) {
				p.destroy();
			}
		}
		return null;
	}
	
	private String parsePlatform(List<String> lines) {
		if (lines == null || lines.size() == 0) return null;
		String firstLine = lines.remove(0);
		Pattern pat = Pattern.compile("ruby\\s\\d\\.\\d\\.\\d\\s\\(\\d\\d\\d\\d\\-\\d\\d\\-\\d\\d\\spatchlevel\\s\\d+\\)\\s\\[.+\\-(.+)\\]");
		Matcher m = pat.matcher(firstLine);
		if (m.find()) {
			return m.group(1);
		}
		return null;
	}	
}
