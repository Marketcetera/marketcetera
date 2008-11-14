package com.aptana.rdt.internal.core.gems;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.rubypeople.rdt.launching.IRubyLaunchConfigurationConstants;
import org.rubypeople.rdt.launching.IVMInstall;
import org.rubypeople.rdt.launching.IVMInstallChangedListener;
import org.rubypeople.rdt.launching.PropertyChangeEvent;
import org.rubypeople.rdt.launching.RubyRuntime;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.aptana.rdt.AptanaRDTPlugin;
import com.aptana.rdt.IAptanaProxyService;
import com.aptana.rdt.core.gems.Gem;
import com.aptana.rdt.core.gems.GemListener;
import com.aptana.rdt.core.gems.GemRequirement;
import com.aptana.rdt.core.gems.IGemManager;
import com.aptana.rdt.core.gems.LogicalGem;
import com.aptana.rdt.core.gems.Version;
import com.aptana.rdt.internal.core.SystemPropertyProxyService;
// XXX If user tries to install a gem that someone has contributed a local copy of, try using the local copy! (Need to worry about dependencies then!)
public class GemManager implements IGemManager, IVMInstallChangedListener {

	private static final String DETAIL_SWITCH = "-d";
	private static final String SOURCE_SWITCH = "--source";	
	private static final String INCLUDE_DEPENDENCIES_SWITCH = "-y";
	private static final String LOCAL_SWITCH = "-l";
	private static final String VERSION_SWITCH = "-v";
	private static final String REMOTE_SWITCH = "-r";	
	
	private static final String LIST_COMMAND = "list";
	private static final String INSTALL_COMMAND = "install";
	private static final String UNINSTALL_COMMAND = "uninstall";
	private static final String UPDATE_COMMAND = "update";
	private static final String CLEANUP_COMMAND = "cleanup";
	private static final String EXECUTABLE = "ruby";

	private static final String LOCAL_GEMS_CACHE_FILE = "local_gems.xml";
//	private static final String RAILS_GEM_HOST = "http://gems.rubyonrails.org";
	
	private static GemManager fgInstance;

	private Set<Gem> gems;
	private Set<GemListener> listeners;
	private Set<String> urls;
	private List<IPath> fGemInstallPaths;
	private Map<String, Set<Gem>> fRemoteGems = new HashMap<String, Set<Gem>>();
	
	protected boolean isInitialized;
	private Version fVersion;	
	
	private static int seed = 0; // A number we append to the launch configs' name to ensure uniqueness (because the method which is supposed to generate unique names in the LaunchManager doesn't actually do it).

	protected GemManager() {
		urls = new HashSet<String>();
		gems = new HashSet<Gem>();
		listeners = new HashSet<GemListener>();
	}
	
	public boolean isInitialized() {
		return isInitialized;
	}

	protected Set<Gem> loadLocalCache(File file) {
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(file);
			XMLReader reader = SAXParserFactory.newInstance().newSAXParser()
					.getXMLReader();
			GemManagerContentHandler handler = new GemManagerContentHandler();
			reader.setContentHandler(handler);
			reader.parse(new InputSource(fileReader));

			return handler.getGems();
		} catch (FileNotFoundException e) {
			// This is okay, will get thrown if no config exists yet
		} catch (SAXException e) {
			AptanaRDTPlugin.log(e);
		} catch (ParserConfigurationException e) {
			AptanaRDTPlugin.log(e);
		} catch (FactoryConfigurationError e) {
			AptanaRDTPlugin.log(e);
		} catch (IOException e) {
			AptanaRDTPlugin.log(e);
		} finally {
			try {
				if (fileReader != null)
					fileReader.close();
			} catch (IOException e) {
				// ignore
			}
		}
		return new HashSet<Gem>();
	}

	protected void storeGemCache(Set<Gem> gems, File file) {
		XMLWriter out = null;
		try {
			out = new XMLWriter(new FileOutputStream(file));
			writeXML(gems, out);
		} catch (FileNotFoundException e) {
			AptanaRDTPlugin.log(e);
		} catch (IOException e) {
			AptanaRDTPlugin.log(e);
		} finally {
			if (out != null)
				out.close();
		}
	}

	protected File getConfigFile(String fileName) {
		return AptanaRDTPlugin.getDefault().getStateLocation().append(fileName)
				.toFile();
	}

	/**
	 * Writes each server configuration to file in XML format.
	 * 
	 * @param gems
	 * 
	 * @param out
	 *            the writer to use
	 */
	private void writeXML(Set<Gem> gems, XMLWriter out) {
		out.startTag("gems", null);
		for (Gem gem : gems) {
			out.startTag("gem", null);
			out.printSimpleTag("name", gem.getName());
			out.printSimpleTag("version", gem.getVersion());
			out.printSimpleTag("description", gem.getDescription());
			out.printSimpleTag("platform", gem.getPlatform());
			out.endTag("gem");
		}
		out.endTag("gems");
		out.flush();
	}

	protected Set<Gem> loadRemoteGems(String gemIndexUrl, IProgressMonitor monitor) {
		if (!isRubyGemsInstalled()) return new HashSet<Gem>();
		
		GemParser parser = getGemParser();		
		String output = getRemoteGemsListing(gemIndexUrl);
		return parser.parse(output);
	}

	private Set<Gem> loadLocalGems() {
		if (!isRubyGemsInstalled()) return new HashSet<Gem>();
		
		GemParser parser = getGemParser();		
		String output = getLocalGemsListing();
		return parser.parse(output);
	}

	protected GemParser getGemParser() {
		if (getVersion() != null && getVersion().isGreaterThanOrEqualTo("1.2.0"))
			return new GemOnePointTwoParser();
		return new GemParser();
	}

	private String launchInBackgroundAndRead(final ILaunchConfiguration config, final File file) {
		return RubyRuntime.launchInBackgroundAndRead(config, file);
	}
	
	private String launchInBackgroundAndRead(String command, File file) {
		return launchInBackgroundAndRead(createGemLaunchConfiguration(command, false), file);
	}
	
	private Version getVersion() {
		if (fVersion != null) return fVersion;
			int tries = 0;
			while (fVersion == null && tries < 3) {
				String version = launchInBackgroundAndRead("-v", getStateFile("version.txt"));
				try {
					if (version != null && version.trim().length() > 0)
						fVersion = new Version(version.trim());
				} catch (RuntimeException e) {
					AptanaRDTPlugin.log(e);
					fVersion = null;
				}
				tries++;
			}
		return fVersion;
	}
	
	private String getLocalGemsListing() {	
		String command = "query -d";
		// If we're using RubyGems older than 0.9.3, we need to do a "gem list -l" to get the equivalent of query -d
		if (getVersion() != null && getVersion().isLessThanOrEqualTo("0.9.3")) {
			command = LIST_COMMAND + " " + LOCAL_SWITCH;
		}
		return launchInBackgroundAndRead(command, getGemListingFile());
	}
	
	private String getRemoteGemsListing(String sourceURL) {	
		String command = LIST_COMMAND + " " + DETAIL_SWITCH + " " + REMOTE_SWITCH + " " + SOURCE_SWITCH + " " + sourceURL;
		return launchInBackgroundAndRead(command, getStateFile("remote_listing.txt"));
	}

	private File getGemListingFile() {
		return getStateFile("local_listing.txt");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aptana.rdt.internal.gems.IGemManager#update(com.aptana.rdt.internal.gems.Gem)
	 */
	public boolean update(final Gem gem) {
		if (!isRubyGemsInstalled()) return false;
		try {
			String command = UPDATE_COMMAND + " " + gem.getName();
			command = addProxy(command);
			ILaunchConfiguration config = createGemLaunchConfiguration(command, true);
			final ILaunch launch = config.launch(ILaunchManager.RUN_MODE, null);
			Job job = new Job("Updating gem " + gem.getName()) {
			
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					while (!launch.isTerminated()) {
						Thread.yield();
					}
					refresh();
					return Status.OK_STATUS;
				}
			
			};
			job.schedule();			
		} catch (CoreException e) {
			AptanaRDTPlugin.log(e);
			return false;
		}
		return true;
	}

	private ILaunchConfigurationType getRubyApplicationConfigType() {
		return getLaunchManager().getLaunchConfigurationType(
				IRubyLaunchConfigurationConstants.ID_RUBY_APPLICATION);
	}

	private ILaunchManager getLaunchManager() {
		return DebugPlugin.getDefault().getLaunchManager();
	}

	private ILaunchConfiguration createGemLaunchConfiguration(String arguments, boolean isSudo) {
		String gemPath = getGemScriptPath();
		ILaunchConfiguration config = null;
		try {
			ILaunchConfigurationType configType = getRubyApplicationConfigType();
			ILaunchConfigurationWorkingCopy wc = configType
					.newInstance(null, getUniqueName(gemPath));
			wc.setAttribute(IRubyLaunchConfigurationConstants.ATTR_FILE_NAME,
					gemPath);
			wc.setAttribute(
					IRubyLaunchConfigurationConstants.ATTR_VM_INSTALL_NAME,
					RubyRuntime.getDefaultVMInstall().getName());
			wc.setAttribute(
					IRubyLaunchConfigurationConstants.ATTR_VM_INSTALL_TYPE,
					RubyRuntime.getDefaultVMInstall().getVMInstallType()
							.getId());
//			 FIXME Use IProxyService in 3.3 and go through proxy (by passing the necessary values as args) if we need to!
			wc.setAttribute(
					IRubyLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS,
					arguments);
			wc.setAttribute(
					IRubyLaunchConfigurationConstants.ATTR_VM_ARGUMENTS,
					"");
			wc.setAttribute(IRubyLaunchConfigurationConstants.ATTR_IS_SUDO, isSudo);
			if (isSudo) {
				wc.setAttribute(IRubyLaunchConfigurationConstants.ATTR_TERMINAL_COMMAND, "gem " + arguments);
				wc.setAttribute(IRubyLaunchConfigurationConstants.ATTR_USE_TERMINAL, "org.radrails.rails.shell"); // use rails shell if it's available
			}
			Map<String, String> map = new HashMap<String, String>();
			map.put(IRubyLaunchConfigurationConstants.ATTR_RUBY_COMMAND,
					EXECUTABLE);
			wc
					.setAttribute(
							IRubyLaunchConfigurationConstants.ATTR_VM_INSTALL_TYPE_SPECIFIC_ATTRS_MAP,
							map);
			wc.setAttribute(IDebugUIConstants.ATTR_PRIVATE, true);
			wc.setAttribute(IDebugUIConstants.ATTR_LAUNCH_IN_BACKGROUND, false);
			config = wc.doSave();
		} catch (CoreException ce) {
			// ignore for now
		}
		return config;
	}
	
	private synchronized String getUniqueName(String name) {
		String unique = getLaunchManager().generateUniqueLaunchConfigurationNameFrom(name) + seed++;
		return unique;
	}

	public ILaunchConfiguration run(String args) throws CoreException {
		boolean useSudo = false;
		if (args.contains("install ") || args.contains("update") || args.contains("uninstall ")
				|| args.contains("cleanup")) {
			useSudo = true;
		}
		return createGemLaunchConfiguration(args, useSudo);
	}

	private static String getGemScriptPath() {
		IVMInstall vm = RubyRuntime.getDefaultVMInstall();
		if (vm == null) return null;
		File installLocation = vm.getInstallLocation();
		String path = installLocation.getAbsolutePath();
		return path + File.separator + "bin" + File.separator + "gem";
	}
	
	public boolean isRubyGemsInstalled() {
		String path = getGemScriptPath();
		if (path == null) return false;
		File file = new File(path);
		return file.exists();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.rdt.core.gems.IGemManager#installGem(com.aptana.rdt.core.gems.Gem)
	 */
	public boolean installGem(final Gem gem) {
		return installGem(gem, true);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.aptana.rdt.core.gems.IGemManager#installGem(com.aptana.rdt.core.gems.Gem, boolean)
	 */
	public boolean installGem(final Gem gem, boolean includeDependencies) {
		if (gem.isLocal()) {			
			return doLocalInstallGem(gem);			
		}
		return installGem(gem, DEFAULT_GEM_HOST, includeDependencies);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aptana.rdt.internal.gems.IGemManager#removeGem(com.aptana.rdt.internal.gems.Gem)
	 */
	public boolean removeGem(final Gem gem) {
		if (!isRubyGemsInstalled()) return false;
		try {
			String command = UNINSTALL_COMMAND + " " + gem.getName();			
			if (gem.getVersion() != null
					&& gem.getVersion().trim().length() > 0) {
				command += " " + VERSION_SWITCH + " " + gem.getVersion();
			}
			ILaunchConfiguration config = createGemLaunchConfiguration(command, true);
			final ILaunch launch = config.launch(ILaunchManager.RUN_MODE, null);
			Job job = new Job("Notifying gem listeners of uninstalled gem") {
			
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					while (!launch.isTerminated()) {
						Thread.yield();
					}
					refresh();
					// Need to wait until uninstall is finished
					for (GemListener listener : new ArrayList<GemListener>(listeners)) {
						listener.gemRemoved(gem);
					} 
					return Status.OK_STATUS;
				}
			
			};
			job.setSystem(true);
			job.schedule();			
		} catch (CoreException e) {
			AptanaRDTPlugin.log(e);
			return false;
		}
		
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aptana.rdt.internal.gems.IGemManager#getGems()
	 */
	public Set<Gem> getGems() {
		return Collections.unmodifiableSortedSet(new TreeSet<Gem>(gems));
	}

	public static GemManager getInstance() {
		if (fgInstance == null)
			fgInstance = new GemManager();
		return fgInstance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aptana.rdt.internal.gems.IGemManager#refresh()
	 */
	public boolean refresh() {
		Set<Gem> newGems = loadLocalGems();
		gems = newGems;
		storeGemCache(gems, getConfigFile(LOCAL_GEMS_CACHE_FILE));
		Job job  = new Job("notifying Gem Listeners of refresh") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				for (GemListener listener : new ArrayList<GemListener>(listeners)) {
					listener.gemsRefreshed();
				}
				return Status.OK_STATUS;
			}		
		
		};
		job.setSystem(true);
		job.schedule();
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aptana.rdt.internal.gems.IGemManager#addGemListener(com.aptana.rdt.internal.gems.GemManager.GemListener)
	 */
	public synchronized void addGemListener(GemListener listener) {
		listeners.add(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aptana.rdt.internal.gems.IGemManager#getRemoteGems()
	 */
	public Set<Gem> getRemoteGems() {
		return getRemoteGems(DEFAULT_GEM_HOST, new NullProgressMonitor());
	}
	
	public Set<Gem> getRemoteGems(String sourceURL, IProgressMonitor monitor) {
		Set<Gem> remoteGems = new HashSet<Gem>();
		if (fRemoteGems.containsKey(sourceURL)) {
			// FIXME How long should we be caching this?
			remoteGems = fRemoteGems.get(sourceURL);
		} else{
			remoteGems = makeLogical(loadRemoteGems(sourceURL, monitor));
			if (!remoteGems.isEmpty()) {
				addSourceURL(sourceURL);
				fRemoteGems.put(sourceURL, remoteGems);
			}
		}
		return Collections.unmodifiableSortedSet(new TreeSet<Gem>(remoteGems));
	}
	
	protected void addSourceURL(String sourceURL) {
		if (urls.contains(sourceURL)) return;
		launchInBackgroundAndRead("sources -a " + sourceURL, getConfigFile("add_source.txt"));
		urls.add(sourceURL);
	}
	
	public Set<String> getSourceURLs() {
		return Collections.unmodifiableSet(new TreeSet<String>(urls));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aptana.rdt.internal.gems.IGemManager#gemInstalled(java.lang.String)
	 */
	public boolean gemInstalled(String gemName) {
		Set<Gem> gems = getGems();
		for (Gem gem : gems) {
			if (gem.getName().equalsIgnoreCase(gemName))
				return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aptana.rdt.internal.gems.IGemManager#removeGemListener(com.aptana.rdt.internal.gems.GemManager.GemListener)
	 */
	public synchronized void removeGemListener(GemListener listener) {
		listeners.remove(listener);
	}
	
	public List<IPath> getGemInstallPaths() {
		if (fGemInstallPaths == null) {
			if (!isRubyGemsInstalled()) return null;
			ILaunchConfiguration config = createGemLaunchConfiguration("", false);
			try {
				ILaunchConfigurationWorkingCopy wc = config.getWorkingCopy();
				wc.setAttribute(IRubyLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, "-r rubygems -e p(Gem.path)");
				config = wc.doSave();
			} catch (CoreException e) {
				AptanaRDTPlugin.log(e);
			}
			int tries = 0;
			while(tries < 3) {
				try {
					String output = launchInBackgroundAndRead(config, getGemInstallPathFile());			
					fGemInstallPaths = parseInstallPaths(output);
					break;
				} catch (IllegalArgumentException e) {
					// ignore
					tries++;
				}
			}
		}
		return fGemInstallPaths;
	}

	private List<IPath> parseInstallPaths(String output) {
		try {	
			if (output == null || output.trim().length() == 0) throw new IllegalArgumentException("Got empty output for gem install paths");
			output = output.trim();
			if (!output.startsWith("[") || !output.endsWith("]")) throw new IllegalArgumentException("Expected an array for gem install paths, but was: " + output);
			// toss the array brackets
			output = output.substring(1, output.length() - 1);
			
			String[] paths = output.split(",");
			if (paths == null || paths.length < 1) return null;
			List<IPath> installPaths = new ArrayList<IPath>();
			for (int i = 0; i < paths.length; i++) {
				String path = paths[i].trim();
				// toss out the quotes
				path = path.substring(1, path.length() - 1);
				installPaths.add(new Path(path.trim()));
			}			
			return installPaths;
		} catch (Exception e) {
			AptanaRDTPlugin.log(e);
		}
		return null;
	}

	private File getGemInstallPathFile() {
		return getStateFile("install_path.txt");
	}
	
	private File getStateFile(String name) {
		String currentVMId = RubyRuntime.getDefaultVMInstall().getId();
		File file = AptanaRDTPlugin.getDefault().getStateLocation().append("gems").append(currentVMId).append(name).toFile();
		try {
			file.getParentFile().mkdirs();
			file.createNewFile();
		} catch (IOException e) {
			// ignore
		}
		return file;
	}

	public IPath getGemPath(String gemName) {
		List<IPath> paths = getGemInstallPaths();
		if (paths == null) return null;
		List<IPath> matches = new ArrayList<IPath>();
		for (IPath path : paths) {
			path = path.append("gems");
			File gemFolder = path.toFile();
			File[] gems = gemFolder.listFiles();
			if (gems == null) continue;
			for (int i = 0; i < gems.length; i++) {
				File gem = gems[i];
				String name = gem.getName();
				if (name.startsWith(gemName)) 
					matches.add(new Path(gem.getAbsolutePath()));
			}
		}		
		
		if (matches.isEmpty()) return null;
		if (matches.size() == 1) return matches.get(0).append("lib");
		// otherwise, find latest version
		List<Version> versions = new ArrayList<Version>();
		for (IPath match : matches) {
			String name = match.lastSegment();
			String[] parts = name.split("-");
			for (int i = parts.length - 1; i >= 0; i--) {
				String version = parts[i];
				if (!Version.correctFormat(version)) continue;
				try {					
					Version duh = new Version(version);
					versions.add(duh);
					break;
				} catch (IllegalArgumentException e) {
					// ignore, that part may not be version for gem
				}
			}			
		}
		Collections.sort(versions);
		Version latest = versions.get(versions.size() - 1);
		for (IPath match : matches) {
			String name = match.lastSegment();
			String[] parts = name.split("-");
			String version = null;
			for (int i = parts.length - 1; i >= 0; i--) {				
				if (!Version.correctFormat(parts[i])) continue;
				version = parts[i];
				try {					
					Version duh = new Version(version);
					versions.add(duh);
					break;
				} catch (IllegalArgumentException e) {
					// ignore, that part may not be version for gem
				}
			}			
			if (version.equals(latest.toString())) return match.append("lib");
		}
		return null;
	}
	
	public IPath getGemPath(String gemName, String version) {
		return getGemPath(gemName + "-" + version);
	}

	public boolean updateAll() {
		if (!isRubyGemsInstalled()) return false;
		updateSystem();
		try {			
			ILaunchConfiguration config = createGemLaunchConfiguration(addProxy(UPDATE_COMMAND + " " + INCLUDE_DEPENDENCIES_SWITCH), true);
			final ILaunch launch = config.launch(ILaunchManager.RUN_MODE, null);
			Job job = new Job("Updating gem listing") {
			
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					while (!launch.isTerminated()) {
						Thread.yield();
					}
					refresh();
					return Status.OK_STATUS;
				}
			
			};
			job.schedule();			
		} catch (CoreException e) {
			AptanaRDTPlugin.log(e);
			return false;
		}
		return true;
	}
	
	private boolean updateSystem() {
		if (!isRubyGemsInstalled()) return false;
		if (RubyRuntime.currentVMIsJRuby()) return false;  // ROR-307. For now don't let user upgrade rubygems on JRuby until we find a fix.
		try {
			ILaunchConfiguration config = createGemLaunchConfiguration(addProxy(UPDATE_COMMAND + " rubygems-update"), true);
			final ILaunch launch = config.launch(ILaunchManager.RUN_MODE, null);
			while (!launch.isTerminated()) {
				Thread.yield();
			}
		} catch (CoreException e) {
			AptanaRDTPlugin.log(e);
			return false;
		}
		return true;
	}

	public void initialize() {
		RubyRuntime.addVMInstallChangedListener(this);
		scheduleLoadingSources();
		scheduleLoadingLocalGems();
	}

	private void scheduleLoadingSources() {
		Job job = new Job("Loading Remote Gem Sources") {
		
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				urls = loadSourceURLs(); 
//				if (urls.size() < 2) {
//					addSourceURL(RAILS_GEM_HOST);
//				}
				return Status.OK_STATUS;
			}
		
		};
		job.setPriority(Job.LONG);
		job.setSystem(true);
		job.schedule();
	}

	protected Set<String> loadSourceURLs() {
		Set<String> sources = new HashSet<String>();
		String output = launchInBackgroundAndRead("sources -l", getConfigFile("sources_list.txt"));
		if (output == null) return sources;
		String[] lines = output.split("\n");
		if (lines == null) return sources;
		for (int i = 2; i < lines.length; i++) {
			sources.add(lines[i].trim());
		}
		return sources;
	}

	private void scheduleLoadingLocalGems() {
		Job job = new Job(GemsMessages.GemManager_loading_local_gems) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					gems = loadLocalCache(getConfigFile(LOCAL_GEMS_CACHE_FILE));
					for (GemListener listener : new ArrayList<GemListener>(listeners)) {
						listener.gemsRefreshed();
					}
					gems = loadLocalGems();
					int tries = 0;
					while (gems.isEmpty() && tries < 3) { // if we get back an empty list retry up to 3 times
						tries++;
						gems = loadLocalGems();
					}
					storeGemCache(gems, getConfigFile(LOCAL_GEMS_CACHE_FILE));
					isInitialized = true;
					for (GemListener listener : new ArrayList<GemListener>(listeners)) {
						listener.managerInitialized();
					}
					for (GemListener listener : new ArrayList<GemListener>(listeners)) {
						listener.gemsRefreshed();
					}
				} catch (Exception e) {
					AptanaRDTPlugin.log(e);
					return Status.CANCEL_STATUS;
				}
				return Status.OK_STATUS;
			}

		};
		job.setPriority(Job.LONG);
		job.setSystem(true);
		job.schedule();
	}

	protected Set<Gem> makeLogical(Set<Gem> remoteGems) {
		SortedSet<Gem> sorted = new TreeSet<Gem>(remoteGems);
		SortedSet<Gem> logical = new TreeSet<Gem>();
		String name = null;
		Collection<Gem> temp = new HashSet<Gem>();
		for (Gem gem : sorted) {
			if (name != null && !gem.getName().equals(name)) {
				logical.add(LogicalGem.create(temp));
				temp.clear();
			}
			name = gem.getName();
			temp.add(gem);
		}
		if (name != null && !temp.isEmpty()) {
			logical.add(LogicalGem.create(temp));
			temp.clear();
		}
		return Collections.unmodifiableSortedSet(logical);
	}

	public boolean cleanup() {
		if (!isRubyGemsInstalled()) return false;
		try {
			String command = CLEANUP_COMMAND;
			ILaunchConfiguration config = createGemLaunchConfiguration(command, true);
			final ILaunch launch = config.launch(ILaunchManager.RUN_MODE, null);
			Job job = new Job("Cleaning up old versions of gems") {
			
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					while (!launch.isTerminated()) {
						Thread.yield();
					}
					refresh();
					return Status.OK_STATUS;
				}
			
			};
			job.schedule();			
		} catch (CoreException e) {
			AptanaRDTPlugin.log(e);
			return false;
		}
		return true;		
	}

	public boolean installGem(Gem gem, String sourceURL) {
		return installGem(gem, sourceURL, true);
	}
	
	private boolean doInstallGem(final Gem gem, String command) {
		if (!isRubyGemsInstalled()) return false;
		try {			
			ILaunchConfiguration config = createGemLaunchConfiguration(command, true);
			final ILaunch launch = config.launch(ILaunchManager.RUN_MODE, null);	
			Job job = new Job("Installing gem " + gem.getName()) {
			
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					while (!launch.isTerminated()) {
						Thread.yield();
					}
					refresh();
					// Need to wait until install is finished
					for (GemListener listener : new ArrayList<GemListener>(listeners)) {
						listener.gemAdded(gem);
					} 
					return Status.OK_STATUS;
				}
			
			};
			job.setSystem(true);
			job.schedule();		
		} catch (CoreException e) {
			AptanaRDTPlugin.log(e);
			return false;
		}
		return true;
	}
	
	private boolean doLocalInstallGem(final Gem gem) {
		if (!isRubyGemsInstalled()) return false;
		try {			
			// force working directory to that containing the gem
			String command = INSTALL_COMMAND + " -l " + new File(gem.getAbsolutePath()).getName() + "";
			ILaunchConfiguration config = createGemLaunchConfiguration(command, true);
			ILaunchConfigurationWorkingCopy wc = config.getWorkingCopy();
			wc.setAttribute(IRubyLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY, new File(gem.getAbsolutePath()).getParent());
			config = wc.doSave();
			final ILaunch launch = config.launch(ILaunchManager.RUN_MODE, null);	
			Job job = new Job("Installing gem " + gem.getName()) {
			
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					while (!launch.isTerminated()) {
						Thread.yield();
					}
					refresh();
					// Need to wait until uninstall is finished
					for (GemListener listener : new ArrayList<GemListener>(listeners)) {
						listener.gemAdded(gem);
					} 
					return Status.OK_STATUS;
				}
			
			};
			job.schedule();		
		} catch (CoreException e) {
			AptanaRDTPlugin.log(e);
			return false;
		}
		return true;
	}
	
	private boolean installGem(final Gem gem, String sourceURL, boolean includeDependencies) {
		String command = INSTALL_COMMAND + " " + gem.getName();
		if (gem.getVersion() != null && gem.getVersion().trim().length() > 0) {
			command += " " + VERSION_SWITCH + " " + gem.getVersion();
		}
		if (getVersion().isGreaterThanOrEqualTo("0.9.5")) {
			if (!includeDependencies) {
				// dependencies included by default in 0.9.5+
				command += " --ignore-dependencies";
			}
		} else {
			if (includeDependencies) { // need to to add switch if on older than 0.9.5
				command += " " + INCLUDE_DEPENDENCIES_SWITCH;
			}
		}		
		if (sourceURL != null && !sourceURL.equals(DEFAULT_GEM_HOST)) {
			command += " " + SOURCE_SWITCH + " " + sourceURL;
		}			
		command = addProxy(command);
		return doInstallGem(gem, command);		
	}

	private String addProxy(String command) {
		IAptanaProxyService service = getProxyService();
		if (service == null || !service.enabled()) return command;
		return command + " -p http://" + service.getUsername() + ":" + service.getPassword() + "@" + service.getHostName() + ":" + service.getPort();
	}

	private IAptanaProxyService getProxyService() {
		// FIXME Use IProxyService in 3.3 and adapt it!
		return new SystemPropertyProxyService();
	}

	public Set<GemRequirement> getDependencies(Gem gem) {
		if (!isRubyGemsInstalled()) return Collections.emptySet();
		String command = "dependency " + gem.getName() + " -v " + gem.getVersion();
		File file = getStateFile("dependencies_" + gem.getName() + "_" + gem.getVersion() + ".txt");
		String output = launchInBackgroundAndRead(command, file);
		Set<GemRequirement> requirements = parseDependencies(output);
		if (requirements.isEmpty() && gem.getName().equals("rails")) {
			AptanaRDTPlugin.log("Got into a bad state!");
		}
		return requirements;
	}

	private Set<GemRequirement> parseDependencies(String output) {
		if (output == null) return Collections.emptySet();
		Set<GemRequirement> dependencies = new HashSet<GemRequirement>();
		Pattern pat = Pattern.compile("\\s+(\\w+)\\s+\\((.+?)\\)");
		String[] lines = output.split("[\\r|\\n]");
		for (int i = 1; i < lines.length; i++) { // skip first line
			String line = lines[i];
			Matcher matcher = pat.matcher(line);
			if (!matcher.find()) continue;
			String name = matcher.group(1);
			String version = matcher.group(2);
			dependencies.add(new GemRequirement(name, version));
		}
		return dependencies;
	}
	
	public Gem findGem(GemRequirement dependency) {
		// There's probably a more efficient way to do this, but oh well.
		// FIXME Should grab latest version of gem that meets the requirements
		// FIXME Break logical gems up!
		for (Gem gem : gems) {
			if (gem instanceof LogicalGem) {
				LogicalGem logical = (LogicalGem) gem;
				Collection<Gem> logicalsGems = logical.getGems();
				for (Gem gem2 : logicalsGems) {
					if (gem2.meetsRequirements(dependency)) return gem2;
				}
			}
			if (gem.meetsRequirements(dependency)) return gem;
		}
		return null;
	}

	public void defaultVMInstallChanged(IVMInstall previous, IVMInstall current) {
		fVersion = null; // invalidate the cached version
		fGemInstallPaths = null; // invalidate cached install path(s)
		Job job = new Job("Refreshing local gem listing"){
		
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				refresh();
				return Status.OK_STATUS;
			}
		
		};
		job.schedule();
	}

	public void vmAdded(IVMInstall newVm) {}

	public void vmChanged(PropertyChangeEvent event) {}

	public void vmRemoved(IVMInstall removedVm) {}
	
	public List<Version> getVersions(String gemName) {
		List<Version> versions = new ArrayList<Version>();
		for (Gem gem : gems) {
			if (gem.getName().equals(gemName)) {
				versions.add(gem.getVersionObject());
			}
		}
		return versions;
	}
}
