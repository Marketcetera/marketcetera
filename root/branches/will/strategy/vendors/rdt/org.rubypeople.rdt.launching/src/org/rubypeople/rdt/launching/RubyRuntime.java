package org.rubypeople.rdt.launching;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.rubypeople.rdt.core.ILoadpathContainer;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.IRubyModel;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.util.Util;
import org.rubypeople.rdt.internal.launching.CompositeId;
import org.rubypeople.rdt.internal.launching.DefaultEntryResolver;
import org.rubypeople.rdt.internal.launching.DefaultProjectLoadpathEntry;
import org.rubypeople.rdt.internal.launching.LaunchingMessages;
import org.rubypeople.rdt.internal.launching.LaunchingPlugin;
import org.rubypeople.rdt.internal.launching.ListenerList;
import org.rubypeople.rdt.internal.launching.RuntimeLoadpathEntry;
import org.rubypeople.rdt.internal.launching.RuntimeLoadpathEntryResolver;
import org.rubypeople.rdt.internal.launching.RuntimeLoadpathProvider;
import org.rubypeople.rdt.internal.launching.VMDefinitionsContainer;
import org.rubypeople.rdt.internal.launching.VMListener;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class RubyRuntime {
	
	private static final String STD_RUBY_VMTYPE = "org.rubypeople.rdt.launching.StandardVMType";
	
	private static final String JRUBY_VMTYPE = "org.rubypeople.rdt.launching.JRubyVMType";

	/**
	 * Loadpath container used for a project's Ruby
	 * (value <code>"org.rubypeople.rdt.launching.RUBY_CONTAINER"</code>). A
	 * container is resolved in the context of a specific Ruby project, to one
	 * or more system libraries contained in the Ruby std library. The container can have zero
	 * or two path segments following the container name. When no segments
	 * follow the container name, the workspace default Ruby is used to build a
	 * project. Otherwise the segments identify a specific Ruby used to build a
	 * project:
	 * <ol>
	 * <li>VM Install Type Identifier - identifies the type of Ruby VM used to build the
	 * 	project. For example, the standard VM.</li>
	 * <li>VM Install Name - a user defined name that identifies that a specific VM
	 * 	of the above kind. For example, <code>JRuby 1.8.4</code>. This information is
	 *  shared in a projects loadpath file, so teams must agree on Ruby VM naming
	 * 	conventions.</li>
	 * </ol>
	 * @since 0.9.0
	 */
	public static final String RUBY_CONTAINER = LaunchingPlugin.getUniqueIdentifier() + ".RUBY_CONTAINER"; //$NON-NLS-1$
	
	/**
	 * Preference key for the String of XML that defines all installed VMs.
	 * 
	 * @since 0.9.0
	 */
	public static final String PREF_VM_XML = LaunchingPlugin.getUniqueIdentifier() + ".PREF_VM_XML"; //$NON-NLS-1$

	/**
	 * Simple identifier constant (value <code>"vmInstalls"</code>) for the
	 * VM installs extension point.
	 * 
	 * @since 0.9.0
	 */
	public static final String EXTENSION_POINT_VM_INSTALLS = "vmInstalls";	 //$NON-NLS-1$			

	/**
	 * Classpath variable name used for the default RubyVM's library
	 * (value <code>"RUBY_LIB"</code>).
	 */
	public static final String RUBYLIB_VARIABLE= "RUBY_LIB"; //$NON-NLS-1$

	/**
	 * Simple identifier constant (value <code>"runtimeLoadpathEntryResolvers"</code>) for the
	 * runtime loadpath entry resolvers extension point.
	 * 
	 * @since 0.9.0
	 */
	public static final String EXTENSION_POINT_RUNTIME_CLASSPATH_ENTRY_RESOLVERS= "runtimeLoadpathEntryResolvers";	 //$NON-NLS-1$	

	/**
	 * Simple identifier constant (value <code>"loadpathProviders"</code>) for the
	 * runtime loadpath providers extension point.
	 * 
	 * @since 0.9.0
	 */
	public static final String EXTENSION_POINT_RUNTIME_CLASSPATH_PROVIDERS= "loadpathProviders";	 //$NON-NLS-1$		
	
	private static IVMInstallType[] fgVMTypes= null;

	protected static RubyRuntime runtime;
	private static Object fgVMLock = new Object();
	private static boolean fgInitializingVMs;
	private static String fgDefaultVMId;
    private static ListenerList fgVMListeners = new ListenerList(5);
		
	/**
	 * Cache of already resolved projects in container entries. Used to avoid
	 * cycles in project dependencies when resolving loadpath container entries.
	 * Counters used to know when entering/exiting to clear cache
	 */
	private static ThreadLocal<List<IRubyProject>> fgProjects = new ThreadLocal<List<IRubyProject>>(); // Lists
	private static ThreadLocal<Integer> fgEntryCount = new ThreadLocal<Integer>(); // Integers
	
	/**
	 * Default loadpath provider.
	 */
	private static IRuntimeLoadpathProvider fgDefaultLoadpathProvider = new StandardLoadpathProvider();
	
	/**
	 * Path providers keyed by id
	 */
	private static Map<String, RuntimeLoadpathProvider> fgPathProviders = null;
	
    /**
     *  Set of IDs of VMs contributed via vmInstalls extension point.
     */
    private static Set<String> fgContributedVMs = new HashSet<String>();
		
	/**
	 * Resolvers keyed by variable name, container id,
	 * and runtime loadpath entry id.
	 */
	private static Map<String, RuntimeLoadpathEntryResolver> fgVariableResolvers = null;
	private static Map<String, RuntimeLoadpathEntryResolver> fgContainerResolvers = null;
	private static Map<String, RuntimeLoadpathEntryResolver> fgRuntimeLoadpathEntryResolvers = null;
    
	protected RubyRuntime() {
		super();
	}

	public static RubyRuntime getDefault() {
		if (runtime == null) {
			runtime = new RubyRuntime();
		}
		return runtime;
	}
        
    public static void removeVMInstallChangedListener(IVMInstallChangedListener listener) {
        fgVMListeners.remove(listener);
    }
	
	/**
	 * Return the default VM set with <code>setDefaultVM()</code>.
	 * @return	Returns the default VM. May return <code>null</code> when no default
	 * 			VM was set or when the default VM has been disposed.
	 */
	public static IVMInstall getDefaultVMInstall() {
		IVMInstall install= getVMFromCompositeId(getDefaultVMId());
		if (install != null && install.getInstallLocation().exists()) {
			return install;
		}
		// if the default Ruby VM goes missing, re-detect
		if (install != null) {
			install.getVMInstallType().disposeVMInstall(install.getId());
		}
		synchronized (fgVMLock) {
			fgDefaultVMId = null;
			fgVMTypes = null;
			initializeVMs();
		}
		return getVMFromCompositeId(getDefaultVMId());
	}
	
	/**
	 * Return the VM corresponding to the specified composite Id.  The id uniquely
	 * identifies a VM across all vm types.  
	 * 
	 * @param idString the composite id that specifies an instance of IVMInstall
	 * 
	 * @since 0.9.0
	 */
	public static IVMInstall getVMFromCompositeId(String idString) {
		if (idString == null || idString.length() == 0) {
			return null;
		}
		CompositeId id= CompositeId.fromString(idString);
		if (id.getPartCount() == 2) {
			IVMInstallType vmType= getVMInstallType(id.get(0));
			if (vmType != null) {
				return vmType.findVMInstall(id.get(1));
			}
		}
		return null;
	}
	
	private static String getDefaultVMId() {
		initializeVMs();
		return fgDefaultVMId;
	}
		
	/**
	 * Saves the VM configuration information to the preferences. This includes
	 * the following information:
	 * <ul>
	 * <li>The list of all defined IVMInstall instances.</li>
	 * <li>The default VM</li>
	 * <ul>
	 * This state will be read again upon first access to VM
	 * configuration information.
	 */
	public static void saveVMConfiguration() throws CoreException {
		if (fgVMTypes == null) {
			// if the VM types have not been instantiated, there can be no changes.
			return;
		}
		try {
			String xml = getVMsAsXML();
			getPreferences().setValue(PREF_VM_XML, xml);
			savePreferences();
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, LaunchingPlugin.getUniqueIdentifier(), IStatus.ERROR, LaunchingMessages.RubyRuntime_exceptionsOccurred, e)); 
		} catch (ParserConfigurationException e) {
			throw new CoreException(new Status(IStatus.ERROR, LaunchingPlugin.getUniqueIdentifier(), IStatus.ERROR, LaunchingMessages.RubyRuntime_exceptionsOccurred, e)); 
		} catch (TransformerException e) {
			throw new CoreException(new Status(IStatus.ERROR, LaunchingPlugin.getUniqueIdentifier(), IStatus.ERROR, LaunchingMessages.RubyRuntime_exceptionsOccurred, e)); 
		}
	}
	
	private static String getVMsAsXML() throws IOException, ParserConfigurationException, TransformerException {
		VMDefinitionsContainer container = new VMDefinitionsContainer();	
		container.setDefaultVMInstallCompositeID(getDefaultVMId());	
		IVMInstallType[] vmTypes= getVMInstallTypes();
		for (int i = 0; i < vmTypes.length; ++i) {
			IVMInstall[] vms = vmTypes[i].getVMInstalls();
			for (int j = 0; j < vms.length; j++) {
				IVMInstall install = vms[j];
				container.addVM(install);
			}
		}
		return container.getAsXML();
	}
	
	/**
	 * Saves the preferences for the launching plug-in.
	 * 
	 * @since 0.9.0
	 */
	public static void savePreferences() {
		LaunchingPlugin.getDefault().savePluginPreferences();
	}

	public static void addVMInstallChangedListener(IVMInstallChangedListener listener) {
		fgVMListeners.add(listener);		
	}
	
	private static void notifyDefaultVMChanged(IVMInstall previous, IVMInstall current) {
		Object[] listeners = fgVMListeners.getListeners();
		for (int i = 0; i < listeners.length; i++) {
			IVMInstallChangedListener listener = (IVMInstallChangedListener)listeners[i];
			listener.defaultVMInstallChanged(previous, current);
		}
	}
	
	/**
	 * Returns the VM install type with the given unique id. 
	 * @param id the VM install type unique id
	 * @return	The VM install type for the given id, or <code>null</code> if no
	 * 			VM install type with the given id is registered.
	 */
	public static IVMInstallType getVMInstallType(String id) {
		IVMInstallType[] vmTypes= getVMInstallTypes();
			for (int i= 0; i < vmTypes.length; i++) {
				if (vmTypes[i].getId().equals(id)) {
					return vmTypes[i];
				}
			}
			return null;	
	}
	
	/**
	 * Returns the list of registered VM types. VM types are registered via
	 * <code>"org.rubypeople.rdt.launching.vmTypes"</code> extension point.
	 * Returns an empty list if there are no registered VM types.
	 * 
	 * @return the list of registered VM types
	 */
	public static IVMInstallType[] getVMInstallTypes() {
		initializeVMs();
		return fgVMTypes; 
	}

	/**
	 * Perform VM type and VM install initialization. Does not hold locks
	 * while performing change notification.
	 * 
	 * @since 0.9.0
	 */
	private static void initializeVMs() {
		VMDefinitionsContainer vmDefs = null;
		boolean setPref = false;
		synchronized (fgVMLock) {
			if (fgVMTypes == null) {
				try {
					fgInitializingVMs = true;
					// 1. load VM type extensions
					initializeVMTypeExtensions();
					try {
						vmDefs = new VMDefinitionsContainer();
						// 2. add persisted VMs
						setPref = addPersistedVMs(vmDefs);
						// 3. if there are none, detect defaults VMs for each VM Type
						if (vmDefs.getValidVMList().isEmpty()) {
							// calling out to detectDefaultVMs() could allow clients to change
							// VM settings (i.e. call back into change VM settings).
							VMListener listener = new VMListener();
							addVMInstallChangedListener(listener);
							setPref = true;
							VMStandin[] runtime = detectDefaultVMs();
							removeVMInstallChangedListener(listener);
							if (!listener.isChanged()) {
								if (runtime != null && runtime.length > 0) {
									for (int i = 0; i < runtime.length; i++) {
										vmDefs.addVM(runtime[i]);
									}
									VMStandin defaultVM = chooseDefault(runtime);
									vmDefs.setDefaultVMInstallCompositeID(getCompositeIdFromVM(defaultVM));
								}
							} else {
								// VMs were changed - reflect current settings
								addPersistedVMs(vmDefs);
								vmDefs.setDefaultVMInstallCompositeID(fgDefaultVMId);

							}
						}
						// 4. If JRuby VM isn't there, forcibly add JRuby VM
						else {
							if (noJRubyVM(vmDefs)) {
								IVMInstallType type = getVMInstallType(JRUBY_VMTYPE);
								VMStandin vm = detectDefaultVM(type);	
								if (vm != null) vmDefs.addVM(vm);
							}	
							if (noStdRubyVM(vmDefs)) {								
								IVMInstallType type = getVMInstallType(STD_RUBY_VMTYPE);
								VMStandin vm = detectDefaultVM(type);		
								if (vm != null) {
									vmDefs.addVM(vm);
//									if (!LaunchingPlugin.getDefault().getPluginPreferences().getBoolean(LaunchingPlugin.USING_INCLUDED_JRUBY)) {
//										vmDefs.setDefaultVMInstallCompositeID(getCompositeIdFromVM(vm));
//									}
								}
							}	
						}					
						// 5. load contributed VM installs
						addVMExtensions(vmDefs);
						// 6. verify default VM is valid
						String defId = vmDefs.getDefaultVMInstallCompositeID();
						boolean validDef = false;
						if (defId != null) {
							Iterator iterator = vmDefs.getValidVMList().iterator();
							while (iterator.hasNext()) {
								IVMInstall vm = (IVMInstall) iterator.next();
								if (getCompositeIdFromVM(vm).equals(defId)) {
									validDef = true;
									break;
								}
							}
						}
						if (!validDef) {
							// use the first as the default
							setPref = true;
							List list = vmDefs.getValidVMList();
							if (!list.isEmpty()) {
								IVMInstall vm = (IVMInstall) list.get(0);
								vmDefs.setDefaultVMInstallCompositeID(getCompositeIdFromVM(vm));
							}
						}
						fgDefaultVMId = vmDefs.getDefaultVMInstallCompositeID();
						
						// Create the underlying VMs for each valid VM
						List vmList = vmDefs.getValidVMList();
						Iterator vmListIterator = vmList.iterator();
						while (vmListIterator.hasNext()) {
							VMStandin vmStandin = (VMStandin) vmListIterator.next();
							vmStandin.convertToRealVM();
						}						
						

					} catch (IOException e) {
						LaunchingPlugin.log(e);
					}
				} finally {
					fgInitializingVMs = false;
				}
			}
		}
		if (vmDefs != null) {
			// notify of initial VMs for backwards compatibility
			IVMInstallType[] installTypes = getVMInstallTypes();
			for (int i = 0; i < installTypes.length; i++) {
				IVMInstallType type = installTypes[i];
				IVMInstall[] installs = type.getVMInstalls();
				for (int j = 0; j < installs.length; j++) {
					fireVMAdded(installs[j]);
				}
			}
			
			// save settings if required
			if (setPref) {
				try {
					String xml = vmDefs.getAsXML();
					LaunchingPlugin.getDefault().getPluginPreferences().setValue(PREF_VM_XML, xml);
				} catch (ParserConfigurationException e) {
					LaunchingPlugin.log(e);
				} catch (IOException e) {
					LaunchingPlugin.log(e);
				} catch (TransformerException e) {
					LaunchingPlugin.log(e);
				}
				
			}
		}
	}
	
	private static boolean noJRubyVM(VMDefinitionsContainer vmDefs) {
		return !hasVMOfType(vmDefs, JRUBY_VMTYPE);
	}
	
	private static boolean noStdRubyVM(VMDefinitionsContainer vmDefs) {		
		return !hasVMOfType(vmDefs, STD_RUBY_VMTYPE);
	}
	
	private static boolean hasVMOfType(VMDefinitionsContainer vmDefs, String vmTypeId) {
		List<IVMInstall> vms = vmDefs.getValidVMList();
		for (IVMInstall install : vms) {
			if (install.getVMInstallType() == null || install.getVMInstallType().getId() == null) continue;
			if (install.getVMInstallType().getId().equals(vmTypeId)) return true;
		}
		return false;
	}

	/**
	 * Prefer the first standard VM install (over JRuby). Otherwise, just pick first found VM.
	 * @param runtime
	 * @return
	 */
	private static VMStandin chooseDefault(VMStandin[] runtime) {
		for (int i = 0; i < runtime.length; i++) {
			if (runtime[i].getVMInstallType().getId().equals(STD_RUBY_VMTYPE)) {
				return runtime[i];
			}
		}
		return runtime[0];
	}

	/**
	 * Detect the VM that is used by the system by default.
	 * 
	 * @return a VM standin representing the VM that Eclipse is running on, or
	 * <code>null</code> if unable to detect the runtime VM
	 */
	private static VMStandin[] detectDefaultVMs() {
		List<VMStandin> detected = new ArrayList<VMStandin>();
		// Try to detect a VM for each declared VM type
		IVMInstallType[] vmTypes= getVMInstallTypes();
		for (int i = 0; i < vmTypes.length; i++) {			
			VMStandin standin = detectDefaultVM(vmTypes[i]);	
			if (standin != null) detected.add(standin);
		}
		return detected.toArray(new VMStandin[detected.size()]);
	}

	private static VMStandin detectDefaultVM(IVMInstallType vmType) {
		File detectedLocation= vmType.detectInstallLocation();
		if (detectedLocation != null) {
			
			// Make sure the VM id is unique
			long unique = System.currentTimeMillis();	
			while (vmType.findVMInstall(String.valueOf(unique)) != null) {
				unique++;
			}

			// Create a standin for the detected VM and add it to the result collector
			String vmID = String.valueOf(unique);
			VMStandin detectedVMStandin = new VMStandin(vmType, vmID);
			detectedVMStandin.setInstallLocation(detectedLocation);
			detectedVMStandin.setName(generateDetectedVMName(detectedVMStandin));
			return detectedVMStandin;
		}
		return null;
	}
	
	/**
	 * Make the name of a detected VM stand out.
	 */
	private static String generateDetectedVMName(IVMInstall vm) {
		return vm.getInstallLocation().getName();
	}
	
	/**
	 * Initializes vm type extensions.
	 */
	private static void initializeVMTypeExtensions() {
		IExtensionPoint extensionPoint= Platform.getExtensionRegistry().getExtensionPoint(LaunchingPlugin.PLUGIN_ID, "vmInstallTypes"); //$NON-NLS-1$
		IConfigurationElement[] configs= extensionPoint.getConfigurationElements(); 
		MultiStatus status= new MultiStatus(LaunchingPlugin.getUniqueIdentifier(), IStatus.OK, LaunchingMessages.RubyRuntime_exceptionOccurred, null); 
		fgVMTypes= new IVMInstallType[configs.length];

		for (int i= 0; i < configs.length; i++) {
			try {
				IVMInstallType vmType= (IVMInstallType)configs[i].createExecutableExtension("class"); //$NON-NLS-1$
				fgVMTypes[i]= vmType;
			} catch (CoreException e) {
				status.add(e.getStatus());
			}
		}
		if (!status.isOK()) {
			//only happens on a CoreException
			LaunchingPlugin.log(status);
			//cleanup null entries in fgVMTypes
			List<IVMInstallType> temp= new ArrayList<IVMInstallType>(fgVMTypes.length);
			for (int i = 0; i < fgVMTypes.length; i++) {
				if(fgVMTypes[i] != null) {
					temp.add(fgVMTypes[i]);
				}
				fgVMTypes= new IVMInstallType[temp.size()];
				fgVMTypes= temp.toArray(fgVMTypes);
			}
		}
	}

	/**
	 * This method loads installed JREs based an existing user preference
	 * or old vm configurations file. The VMs found in the preference
	 * or vm configurations file are added to the given VM definitions container.
	 * 
	 * Returns whether the user preferences should be set - i.e. if it was
	 * not already set when initialized.
	 */
	private static boolean addPersistedVMs(VMDefinitionsContainer vmDefs) throws IOException {
		// Try retrieving the VM preferences from the preference store
		String vmXMLString = getPreferences().getString(PREF_VM_XML);
		
		// If the preference was found, load VMs from it into memory
		if (vmXMLString.length() > 0) {
			try {
				ByteArrayInputStream inputStream = new ByteArrayInputStream(vmXMLString.getBytes());
				VMDefinitionsContainer.parseXMLIntoContainer(inputStream, vmDefs);
				return false;
			} catch (IOException ioe) {
				LaunchingPlugin.log(ioe);
			}			
		} else {			
			// Otherwise, look for the old file that previously held the VM definitions
			IPath stateLocation= LaunchingPlugin.getDefault().getStateLocation();
			IPath stateFile= stateLocation.append("runtimeConfiguration.xml"); //$NON-NLS-1$
			File file = new File(stateFile.toOSString());
			
			if (file.exists()) {        
				// If file exists, load VM definitions from it into memory and write the definitions to
				// the preference store WITHOUT triggering any processing of the new value
				FileInputStream fileInputStream = new FileInputStream(file);
				VMDefinitionsContainer.parseXMLIntoContainer(fileInputStream, vmDefs);
			}		
		}
		return true;
	}
	
	/**
	 * Returns the preference store for the launching plug-in.
	 * 
	 * @return the preference store for the launching plug-in
	 * @since 0.9.0
	 */
	public static Preferences getPreferences() {
		return LaunchingPlugin.getDefault().getPluginPreferences();
	}
	
	/** 
	 * Returns a String that uniquely identifies the specified VM across all VM types.
	 * 
	 * @param vm the instance of IVMInstallType to be identified
	 * 
	 * @since 0.9.0
	 */
	public static String getCompositeIdFromVM(IVMInstall vm) {
		if (vm == null) {
			return null;
		}
		IVMInstallType vmType= vm.getVMInstallType();
		String typeID= vmType.getId();
		CompositeId id= new CompositeId(new String[] { typeID, vm.getId() });
		return id.toString();
	}

	/**
	 * Loads contributed VM installs
	 * @since 0.9.0
	 */
	private static void addVMExtensions(VMDefinitionsContainer vmDefs) {
		IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(LaunchingPlugin.PLUGIN_ID, RubyRuntime.EXTENSION_POINT_VM_INSTALLS);
		IConfigurationElement[] configs= extensionPoint.getConfigurationElements();
		for (int i = 0; i < configs.length; i++) {
			IConfigurationElement element = configs[i];
			try {
				if ("vmInstall".equals(element.getName())) { //$NON-NLS-1$
					String vmType = element.getAttribute("vmInstallType"); //$NON-NLS-1$
					if (vmType == null) {
						abort(MessageFormat.format("Missing required vmInstallType attribute for vmInstall contributed by {0}", //$NON-NLS-1$
								(Object[]) new String[]{element.getContributor().getName()}), null);
					}
					String id = element.getAttribute("id"); //$NON-NLS-1$
					if (id == null) {
						abort(MessageFormat.format("Missing required id attribute for vmInstall contributed by {0}", //$NON-NLS-1$
								(Object[]) new String[]{element.getContributor().getName()}), null);
					}
					IVMInstallType installType = getVMInstallType(vmType);
					if (installType == null) {
						abort(MessageFormat.format("vmInstall {0} contributed by {1} references undefined VM install type {2}", //$NON-NLS-1$
								(Object[]) new String[]{id, element.getContributor().getName(), vmType}), null);
					}
					IVMInstall install = installType.findVMInstall(id);
					if (install == null) {
						// only load/create if first time we've seen this VM install
						String name = element.getAttribute("name"); //$NON-NLS-1$
						if (name == null) {
							abort(MessageFormat.format("vmInstall {0} contributed by {1} missing required attribute name", //$NON-NLS-1$
									(Object[]) new String[]{id, element.getContributor().getName()}), null);
						}
						String home = element.getAttribute("home"); //$NON-NLS-1$
						if (home == null) {
							abort(MessageFormat.format("vmInstall {0} contributed by {1} missing required attribute home", //$NON-NLS-1$
									(Object[]) new String[]{id, element.getContributor().getName()}), null);
						}		
						String vmArgs = element.getAttribute("vmArgs"); //$NON-NLS-1$
						VMStandin standin = new VMStandin(installType, id);
						standin.setName(name);
						home = substitute(home);
						File homeDir = new File(home);
                        if (homeDir.exists()) {
                            try {
                            	// adjust for relative path names
                                home = homeDir.getCanonicalPath();
                                homeDir = new File(home);
                            } catch (IOException e) {
                            }
                        }
                        IStatus status = installType.validateInstallLocation(homeDir);
                        if (!status.isOK()) {
                        	abort(MessageFormat.format("Illegal install location {0} for vmInstall {1} contributed by {2}: {3}", //$NON-NLS-1$
                        			(Object[]) new String[]{home, id, element.getContributor().getName(), status.getMessage()}), null);
                        }
                        standin.setInstallLocation(homeDir);
						if (vmArgs != null) {
							standin.setVMArgs(vmArgs);
						}
                        IConfigurationElement[] libraries = element.getChildren("library"); //$NON-NLS-1$
                        IPath[] locations = null;
                        if (libraries.length > 0) {
                            locations = new IPath[libraries.length];
                            for (int j = 0; j < libraries.length; j++) {
                                IConfigurationElement library = libraries[j];
                                String libPathStr = library.getAttribute("path"); //$NON-NLS-1$
                                if (libPathStr == null) {
                                    abort(MessageFormat.format("library for vmInstall {0} contributed by {1} missing required attribute libPath", //$NON-NLS-1$
                                    		(Object[]) new String[]{id, element.getContributor().getName()}), null);
                                }

                                IPath homePath = new Path(home);
                                IPath libPath = homePath.append(substitute(libPathStr));
                                locations[j] = libPath;
                            }
                        }
                        standin.setLibraryLocations(locations);
                        vmDefs.addVM(standin);
					}
                    fgContributedVMs.add(id);
				} else {
					abort(MessageFormat.format("Illegal element {0} in vmInstalls extension contributed by {1}", //$NON-NLS-1$
							(Object[]) new String[]{element.getName(), element.getContributor().getName()}), null);
				}
			} catch (CoreException e) {
				LaunchingPlugin.log(e);
			}
		}
	}
	
    /**
     * Performs string substitution on the given expression.
     * 
     * @param expression
     * @return expression after string substitution 
     * @throws CoreException
     * @since 0.9.0
     */
    private static String substitute(String expression) throws CoreException {
        return VariablesPlugin.getDefault().getStringVariableManager().performStringSubstitution(expression);
    }
	
	/**
	 * Throws a core exception with an internal error status.
	 * 
	 * @param message the status message
	 * @param exception lower level exception associated with the
	 *  error, or <code>null</code> if none
	 */
	private static void abort(String message, Throwable exception) throws CoreException {
		abort(message, IRubyLaunchConfigurationConstants.ERR_INTERNAL_ERROR, exception);
	}	
	
	/**
	 * Throws a core exception with an internal error status.
	 * 
	 * @param message the status message
	 * @param code status code
	 * @param exception lower level exception associated with the
	 * 
	 *  error, or <code>null</code> if none
	 */
	private static void abort(String message, int code, Throwable exception) throws CoreException {
		throw new CoreException(new Status(IStatus.ERROR, LaunchingPlugin.getUniqueIdentifier(), code, message, exception));
	}	
	
	static void fireVMAdded(IVMInstall vm) {
		if (!fgInitializingVMs) {
			Object[] listeners = fgVMListeners.getListeners();
			for (int i = 0; i < listeners.length; i++) {
				IVMInstallChangedListener listener = (IVMInstallChangedListener)listeners[i];
				listener.vmAdded(vm);
			}
		}
	}

	public static void fireVMChanged(PropertyChangeEvent event) {
		Object[] listeners = fgVMListeners.getListeners();
		for (int i = 0; i < listeners.length; i++) {
			IVMInstallChangedListener listener = (IVMInstallChangedListener)listeners[i];
			listener.vmChanged(event);
		}		
	}
	
	/**
	 * Notifies all VM install changed listeners of the VM removal
	 * 
	 * @param vm the VM that has been removed
	 * @since 0.9.0
	 */
	public static void fireVMRemoved(IVMInstall vm) {
		Object[] listeners = fgVMListeners.getListeners();
		for (int i = 0; i < listeners.length; i++) {
			IVMInstallChangedListener listener = (IVMInstallChangedListener)listeners[i];
			listener.vmRemoved(vm);
		}		
	}		

	/**
	 * Evaluates library locations for a IVMInstall. If no library locations are set on the install, a default
	 * location is evaluated and checked if it exists.
	 * @return library locations with paths that exist or are empty
	 * @since 0.9.0
	 */
	public static IPath[] getLibraryLocations(IVMInstall vm)  {
		IPath[] locations= vm.getLibraryLocations();
		if (locations != null) return locations;

		IPath[] dflts= vm.getVMInstallType().getDefaultLibraryLocations(vm.getInstallLocation());
		IPath[] libraryPaths = new IPath[dflts.length];			
		for (int i = 0; i < dflts.length; i++) {
			libraryPaths[i]= dflts[i];               
			if (!libraryPaths[i].toFile().isDirectory()) {
				libraryPaths[i]= Path.EMPTY;
			}
		}
		return libraryPaths;
	}

	/**
	 * Returns the VM install for the given launch configuration.
	 * The VM install is determined in the following prioritized way:
	 * <ol>
	 * <li>The VM install is explicitly specified on the launch configuration
	 *  via the <code>ATTR_JRE_CONTAINER_PATH</code> attribute (since 3.2).</li>
	 * <li>The VM install is explicitly specified on the launch configuration
	 * 	via the <code>ATTR_VM_INSTALL_TYPE</code> and <code>ATTR_VM_INSTALL_ID</code>
	 *  attributes.</li>
	 * <li>If no explicit VM install is specified, the VM install associated with
	 * 	the launch configuration's project is returned.</li>
	 * <li>If no project is specified, or the project does not specify a custom
	 * 	VM install, the workspace default VM install is returned.</li>
	 * </ol>
	 * 
	 * @param configuration launch configuration
	 * @return vm install
	 * @exception CoreException if unable to compute a vm install
	 * @since 0.9.0
	 */
	public static IVMInstall computeVMInstall(ILaunchConfiguration configuration) throws CoreException {
		String rubyVmAttr = configuration.getAttribute(IRubyLaunchConfigurationConstants.ATTR_RUBY_CONTAINER_PATH, (String)null);
		if (rubyVmAttr == null) {
			String type = configuration.getAttribute(IRubyLaunchConfigurationConstants.ATTR_VM_INSTALL_TYPE, (String)null);
			if (type == null) {
				IRubyProject proj = getRubyProject(configuration);
				if (proj != null) {
					IVMInstall vm = getVMInstall(proj);
					if (vm != null) {
						return vm;
					}
				}
			} else {
				String name = configuration.getAttribute(IRubyLaunchConfigurationConstants.ATTR_VM_INSTALL_NAME, (String)null);
				return resolveVM(type, name, configuration);
			}
		} else {
			IPath rubyVmPath = Path.fromPortableString(rubyVmAttr);
			ILoadpathEntry entry = RubyCore.newContainerEntry(rubyVmPath);
			IRuntimeLoadpathEntryResolver2 resolver = getVariableResolver(rubyVmPath.segment(0));
			if (resolver != null) {
				return resolver.resolveVMInstall(entry);
			}
			resolver = getContainerResolver(rubyVmPath.segment(0));
			if (resolver != null) {
				return resolver.resolveVMInstall(entry);
			}
		}
		
		return getDefaultVMInstall();
	}
	
	/**
	 * Returns the VM of the given type with the specified name.
	 *  
	 * @param type vm type identifier
	 * @param name vm name
	 * @return vm install
	 * @exception CoreException if unable to resolve
	 * @since 0.9.0
	 */
	private static IVMInstall resolveVM(String type, String name, ILaunchConfiguration configuration) throws CoreException {
		IVMInstallType vt = getVMInstallType(type);
		if (vt == null) {
			// error type does not exist
			abort(MessageFormat.format(LaunchingMessages.JavaRuntime_Specified_VM_install_type_does_not_exist___0__2, type), null); 
		}
		IVMInstall vm = null;
		// look for a name
		if (name == null) {
			// error - type specified without a specific install (could be an old config that specified a VM ID)
			// log the error, but choose the default VM.
			IStatus status = new Status(IStatus.WARNING, LaunchingPlugin.getUniqueIdentifier(), 
					IRubyLaunchConfigurationConstants.ERR_UNSPECIFIED_VM_INSTALL, 
					MessageFormat.format(LaunchingMessages.JavaRuntime_VM_not_fully_specified_in_launch_configuration__0____missing_VM_name__Reverting_to_default_VM__1, configuration.getName()), 
					null); 
			LaunchingPlugin.log(status);
			return getDefaultVMInstall();
		} 
		vm = vt.findVMInstallByName(name);
		if (vm == null) {
			// error - install not found
			abort(MessageFormat.format(LaunchingMessages.JavaRuntime_Specified_VM_install_not_found__type__0___name__1__2, vt.getName(), name), null);					 
		} else {
			return vm;
		}
		// won't reach here
		return null;
	}
	
	/**
	 * Returns the resolver registered for the given variable, or
	 * <code>null</code> if none.
	 * 
	 * @param variableName the variable to determine the resolver for
	 * @return the resolver registered for the given variable, or
	 * <code>null</code> if none
	 */
	private static IRuntimeLoadpathEntryResolver2 getVariableResolver(String variableName) {
		return (IRuntimeLoadpathEntryResolver2)getVariableResolvers().get(variableName);
	}
	
	/**
	 * Returns the resolver registered for the given container id, or
	 * <code>null</code> if none.
	 * 
	 * @param containerId the container to determine the resolver for
	 * @return the resolver registered for the given container id, or
	 * <code>null</code> if none
	 */	
	private static IRuntimeLoadpathEntryResolver2 getContainerResolver(String containerId) {
		return (IRuntimeLoadpathEntryResolver2)getContainerResolvers().get(containerId);
	}
	
	private static Map getVariableResolvers() {
		if (fgVariableResolvers == null) {
			initializeResolvers();
		}
		return fgVariableResolvers;
	}
	
	/**
	 * Returns all registered container resolvers.
	 */
	private static Map getContainerResolvers() {
		if (fgContainerResolvers == null) {
			initializeResolvers();
		}
		return fgContainerResolvers;
	}
	
	private static void initializeResolvers() {
		IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint(LaunchingPlugin.PLUGIN_ID, EXTENSION_POINT_RUNTIME_CLASSPATH_ENTRY_RESOLVERS);
		IConfigurationElement[] extensions = point.getConfigurationElements();
		fgVariableResolvers = new HashMap<String, RuntimeLoadpathEntryResolver>(extensions.length);
		fgContainerResolvers = new HashMap<String, RuntimeLoadpathEntryResolver>(extensions.length);
		fgRuntimeLoadpathEntryResolvers = new HashMap<String, RuntimeLoadpathEntryResolver>(extensions.length);
		for (int i = 0; i < extensions.length; i++) {
			RuntimeLoadpathEntryResolver res = new RuntimeLoadpathEntryResolver(extensions[i]);
			String variable = res.getVariableName();
			String container = res.getContainerId();
			String entryId = res.getRuntimeLoadpathEntryId();
			if (variable != null) {
				fgVariableResolvers.put(variable, res);
			}
			if (container != null) {
				fgContainerResolvers.put(container, res);
			}
			if (entryId != null) {
				fgRuntimeLoadpathEntryResolvers.put(entryId, res);
			}
		}		
	}
	
	/**
	 * Returns the VM assigned to build the given Java project.
	 * The project must exist. The VM assigned to a project is
	 * determined from its build path.
	 * 
	 * @param project the project to retrieve the VM from
	 * @return the VM instance that is assigned to build the given Java project
	 * 		   Returns <code>null</code> if no VM is referenced on the project's build path.
	 * @throws CoreException if unable to determine the project's VM install
	 */
	public static IVMInstall getVMInstall(IRubyProject project) throws CoreException {
		// check the loadpath
		IVMInstall vm = null;
		ILoadpathEntry[] loadpath = project.getRawLoadpath();
		IRuntimeLoadpathEntryResolver resolver = null;
		for (int i = 0; i < loadpath.length; i++) {
			ILoadpathEntry entry = loadpath[i];
			switch (entry.getEntryKind()) {
				case ILoadpathEntry.CPE_VARIABLE:
					resolver = getVariableResolver(entry.getPath().segment(0));
					if (resolver != null) {
						vm = resolver.resolveVMInstall(entry);
					}
					break;
				case ILoadpathEntry.CPE_CONTAINER:
					resolver = getContainerResolver(entry.getPath().segment(0));
					if (resolver != null) {
						vm = resolver.resolveVMInstall(entry);
					}
					break;
			}
			if (vm != null) {
				return vm;
			}
		}
		return null;
	}
	
	/**
	 * Return the <code>IRubyProject</code> referenced in the specified configuration or
	 * <code>null</code> if none.
	 *
	 * @exception CoreException if the referenced Ruby project does not exist
	 * @since 0.9.0
	 */
	public static IRubyProject getRubyProject(ILaunchConfiguration configuration) throws CoreException {
		String projectName = configuration.getAttribute(IRubyLaunchConfigurationConstants.ATTR_PROJECT_NAME, (String)null);
		if ((projectName == null) || (projectName.trim().length() < 1)) {
			return null;
		}			
		IRubyProject javaProject = getRubyModel().getRubyProject(projectName);
		if (javaProject != null && javaProject.getProject().exists() && !javaProject.getProject().isOpen()) {
			abort(MessageFormat.format(LaunchingMessages.JavaRuntime_28, configuration.getName(), projectName), IRubyLaunchConfigurationConstants.ERR_PROJECT_CLOSED, null); 
		}
		if ((javaProject == null) || !javaProject.exists()) {
			abort(MessageFormat.format(LaunchingMessages.JavaRuntime_Launch_configuration__0__references_non_existing_project__1___1,configuration.getName(), projectName), IRubyLaunchConfigurationConstants.ERR_NOT_A_RUBY_PROJECT, null); 
		}
		return javaProject;
	}
	
	/**
	 * Convenience method to get the ruby model.
	 */
	private static IRubyModel getRubyModel() {
		return RubyCore.create(ResourcesPlugin.getWorkspace().getRoot());
	}

	/**
	 * Returns a new runtime loadpath entry for the given archive (possibly
	 * external).
	 * 
	 * @param path absolute path to an archive
	 * @return runtime loadpath entry
	 * @since 0.9.0
	 */
	public static IRuntimeLoadpathEntry newArchiveRuntimeLoadpathEntry(IPath path) {
		ILoadpathEntry cpe = RubyCore.newLibraryEntry(path);
		return newRuntimeLoadpathEntry(cpe);
	}
	
	/**
	 * Returns a runtime loadpath entry that corresponds to the given
	 * loadpath entry. The loadpath entry may not be of type <code>CPE_SOURCE</code>
	 * or <code>CPE_CONTAINER</code>.
	 * 
	 * @param entry a loadpath entry
	 * @return runtime loadpath entry
	 * @since 0.9.0
	 */
	private static IRuntimeLoadpathEntry newRuntimeLoadpathEntry(ILoadpathEntry entry) {
		return new RuntimeLoadpathEntry(entry);
	}

	/**
	 * Returns a runtime loadpath entry for the given container path with the given
	 * loadpath property to be resolved in the context of the given Java project.
	 * 
	 * @param path container path
	 * @param loadpathProperty the type of entry - one of <code>USER_CLASSES</code>,
	 * 	<code>BOOTSTRAP_CLASSES</code>, or <code>STANDARD_CLASSES</code>
	 * @param project Java project context used for resolution, or <code>null</code>
	 *  if to be resolved in the context of the launch configuration this entry
	 *  is referenced in
	 * @return runtime loadpath entry
	 * @exception CoreException if unable to construct a runtime loadpath entry
	 * @since 0.9.0
	 */
	public static IRuntimeLoadpathEntry newRuntimeContainerLoadpathEntry(IPath path, int loadpathProperty, IRubyProject project) throws CoreException {
		ILoadpathEntry cpe = RubyCore.newContainerEntry(path);
		RuntimeLoadpathEntry entry = new RuntimeLoadpathEntry(cpe, loadpathProperty);
		entry.setRubyProject(project);
		return entry;
	}

	/**
	 * Returns a new runtime loadpath entry for the loadpath
	 * variable with the given path.
	 * 
	 * @param path variable path; first segment is the name of the variable; 
	 * 	trailing segments are appended to the resolved variable value
	 * @return runtime loadpath entry
	 * @since 0.9.0
	 */
	public static IRuntimeLoadpathEntry newVariableRuntimeLoadpathEntry(
			IPath path) {
		ILoadpathEntry cpe = RubyCore.newVariableEntry(path);
		return newRuntimeLoadpathEntry(cpe);
	}
	
	/**
	 * Computes and returns the unresolved class path for the given launch configuration.
	 * Variable and container entries are unresolved.
	 * 
	 * @param configuration launch configuration
	 * @return unresolved runtime loadpath entries
	 * @throws CoreException 
	 * @exception CoreException if unable to compute the loadpath
	 * @since 0.9.0
	 */
	public static IRuntimeLoadpathEntry[] computeUnresolvedRuntimeLoadpath(
			ILaunchConfiguration configuration) throws CoreException {
		return getLoadpathProvider(configuration).computeUnresolvedLoadpath(configuration);
	}	
	
	/**
	 * Returns the loadpath provider for the given launch configuration.
	 * 
	 * @param configuration launch configuration
	 * @return loadpath provider
	 * @exception CoreException if unable to resolve the path provider
	 * @since 0.9.0
	 */
	public static IRuntimeLoadpathProvider getLoadpathProvider(ILaunchConfiguration configuration) throws CoreException {
		String providerId = configuration.getAttribute(IRubyLaunchConfigurationConstants.ATTR_LOADPATH_PROVIDER, (String)null);
		IRuntimeLoadpathProvider provider = null;
		if (providerId == null) {
			provider = fgDefaultLoadpathProvider;
		} else {
			provider = (IRuntimeLoadpathProvider)getLoadpathProviders().get(providerId);
			if (provider == null) {
				abort(MessageFormat.format(LaunchingMessages.JavaRuntime_26, providerId), null); 
			}
		}
		return provider;
	}	
	
	/**
	 * Returns all registered loadpath providers.
	 */
	private static Map getLoadpathProviders() {
		if (fgPathProviders == null) {
			initializeProviders();
		}
		return fgPathProviders;
	}
	
	private static void initializeProviders() {
		IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint(LaunchingPlugin.PLUGIN_ID, EXTENSION_POINT_RUNTIME_CLASSPATH_PROVIDERS);
		IConfigurationElement[] extensions = point.getConfigurationElements();
		fgPathProviders = new HashMap<String, RuntimeLoadpathProvider>(extensions.length);
		for (int i = 0; i < extensions.length; i++) {
			RuntimeLoadpathProvider res = new RuntimeLoadpathProvider(extensions[i]);
			fgPathProviders.put(res.getIdentifier(), res);
		}		
	}

	/**
	 * Returns a runtime loadpath entry constructed from the given memento.
	 * 
	 * @param memento a memento for a runtime loadpath entry
	 * @return runtime loadpath entry
	 * @exception CoreException if unable to construct a runtime loadpath entry
	 * @since 0.9.0
	 */
	public static IRuntimeLoadpathEntry newRuntimeLoadpathEntry(String memento) throws CoreException {
		try {
			Element root = null;
			DocumentBuilder parser = LaunchingPlugin.getParser();
			StringReader reader = new StringReader(memento);
			InputSource source = new InputSource(reader);
			root = parser.parse(source).getDocumentElement();
												
			String id = root.getAttribute("id"); //$NON-NLS-1$
			if (id == null || id.length() == 0) {
				// assume an old format
				return new RuntimeLoadpathEntry(root);
			}
			// get the extension & create a new one
			IRuntimeLoadpathEntry2 entry = LaunchingPlugin.getDefault().newRuntimeLoadpathEntry(id);
			NodeList list = root.getChildNodes();
			for (int i = 0; i < list.getLength(); i++) {
				Node node = list.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element)node;
					if ("memento".equals(element.getNodeName())) { //$NON-NLS-1$
						entry.initializeFrom(element);
					}
				}
			}
			return entry;
		} catch (SAXException e) {
			abort(LaunchingMessages.JavaRuntime_31, e); 
		} catch (IOException e) {
			abort(LaunchingMessages.JavaRuntime_32, e); 
		}
		return null;
	}
	
	/**
	 * Returns a runtime loadpath entry identifying the JRE to use when launching the specified
	 * configuration or <code>null</code> if none is specified. The entry returned represents a
	 * either a loadpath variable or loadpath container that resolves to a JRE.
	 * <p>
	 * The entry is resolved as follows:
	 * <ol>
	 * <li>If the <code>ATTR_JRE_CONTAINER_PATH</code> is present, it is used to create
	 *  a loadpath container referring to a JRE.</li>
	 * <li>Next, if the <code>ATTR_VM_INSTALL_TYPE</code> and <code>ATTR_VM_INSTALL_NAME</code>
	 * attributes are present, they are used to create a loadpath container.</li>
	 * <li>When none of the above attributes are specified, a default entry is
	 * created which refers to the JRE referenced by the build path of the configuration's
	 * associated Java project. This could be a loadpath variable or loadpath container.</li>
	 * <li>When there is no Java project associated with a configuration, the workspace
	 * default JRE is used to create a container path.</li>
	 * </ol>
	 * </p>
	 * @param configuration
	 * @return loadpath container path identifying a RubyVM or <code>null</code>
	 * @exception org.eclipse.core.runtime.CoreException if an exception occurs retrieving
	 *  attributes from the specified launch configuration
	 * @since 0.9.0
	 */
	public static IRuntimeLoadpathEntry computeRubyVMEntry(ILaunchConfiguration configuration) throws CoreException {
		String rubyVmAttr = configuration.getAttribute(IRubyLaunchConfigurationConstants.ATTR_RUBY_CONTAINER_PATH, (String)null);
		IPath containerPath = null;
		if (rubyVmAttr == null) {
			String type = configuration.getAttribute(IRubyLaunchConfigurationConstants.ATTR_VM_INSTALL_TYPE, (String)null);
			if (type == null) {
				// default RubyVM for the launch configuration
				IRubyProject proj = getRubyProject(configuration);
				if (proj == null) {
					containerPath = newDefaultRubyVMContainerPath();
				} else {
					return computeRubyVMEntry(proj);
				}
			} else {
				String name = configuration.getAttribute(IRubyLaunchConfigurationConstants.ATTR_VM_INSTALL_NAME, (String)null);
				if (name != null) {
					containerPath = newDefaultRubyVMContainerPath().append(type).append(name);
				}
			}
		} else {
			containerPath = Path.fromPortableString(rubyVmAttr);
		}
		if (containerPath != null) {
			return newRuntimeContainerLoadpathEntry(containerPath, IRuntimeLoadpathEntry.STANDARD_CLASSES);
		}
		return null;
	}
	
	/**
	 * Returns a runtime loadpath entry identifying the JRE referenced by the specified
	 * project, or <code>null</code> if none. The entry returned represents a either a
	 * loadpath variable or loadpath container that resolves to a JRE.
	 * 
	 * @param project Java project
	 * @return Ruby VM runtime loadpath entry or <code>null</code>
	 * @exception org.eclipse.core.runtime.CoreException if an exception occurs
	 * 	accessing the project's loadpath
	 * @since 0.9.0
	 */
	public static IRuntimeLoadpathEntry computeRubyVMEntry(IRubyProject project) throws CoreException {
		ILoadpathEntry[] rawClasspath = project.getRawLoadpath();
		IRuntimeLoadpathEntryResolver2 resolver = null;
		for (int i = 0; i < rawClasspath.length; i++) {
			ILoadpathEntry entry = rawClasspath[i];
			switch (entry.getEntryKind()) {
				case ILoadpathEntry.CPE_VARIABLE:
					resolver = getVariableResolver(entry.getPath().segment(0));
					if (resolver != null) {
						if (resolver.isVMInstallReference(entry)) {
							return newRuntimeLoadpathEntry(entry);
						}
					}					
					break;
				case ILoadpathEntry.CPE_CONTAINER:
					resolver = getContainerResolver(entry.getPath().segment(0));
					if (resolver != null) {
						if (resolver.isVMInstallReference(entry)) {
							ILoadpathContainer container = RubyCore.getLoadpathContainer(entry.getPath(), project);
							if (container != null) {
								switch (container.getKind()) {
									case ILoadpathContainer.K_APPLICATION:
										break;
									case ILoadpathContainer.K_DEFAULT_SYSTEM:
										return newRuntimeContainerLoadpathEntry(entry.getPath(), IRuntimeLoadpathEntry.STANDARD_CLASSES);
									case ILoadpathContainer.K_SYSTEM:
										return newRuntimeContainerLoadpathEntry(entry.getPath(), IRuntimeLoadpathEntry.BOOTSTRAP_CLASSES);
								}
							}
						}
					}
					break;
			}
			
		}
		return null;
	}	
	
	/**
	 * Returns a path for the JRE loadpath container identifying the 
	 * default VM install.
	 * 
	 * @return loadpath container path
	 * @since 0.9.0
	 */	
	public static IPath newDefaultRubyVMContainerPath() {
		return new Path(RUBY_CONTAINER);
	}
	
	/**
	 * Returns a runtime loadpath entry for the given container path with the given
	 * loadpath property.
	 * 
	 * @param path container path
	 * @param loadpathProperty the type of entry - one of <code>USER_CLASSES</code>,
	 * 	<code>BOOTSTRAP_CLASSES</code>, or <code>STANDARD_CLASSES</code>
	 * @return runtime loadpath entry
	 * @exception CoreException if unable to construct a runtime loadpath entry
	 * @since 0.9.0
	 */
	public static IRuntimeLoadpathEntry newRuntimeContainerLoadpathEntry(IPath path, int loadpathProperty) throws CoreException {
		return newRuntimeContainerLoadpathEntry(path, loadpathProperty, null);
	}

	/**
	 * Computes and returns the default unresolved runtime loadpath for the
	 * given project.
	 * 
	 * @return runtime loadpath entries
	 * @exception CoreException if unable to compute the runtime loadpath
	 * @see IRuntimeClasspathEntry
	 * @since 0.9.0
	 */
	public static IRuntimeLoadpathEntry[] computeUnresolvedRuntimeLoadpath(IRubyProject project) throws CoreException {
		ILoadpathEntry[] entries = project.getRawLoadpath();
		List<IRuntimeLoadpathEntry> loadpathEntries = new ArrayList<IRuntimeLoadpathEntry>(3);
		for (int i = 0; i < entries.length; i++) {
			ILoadpathEntry entry = entries[i];
			switch (entry.getEntryKind()) {
				case ILoadpathEntry.CPE_CONTAINER:
					ILoadpathContainer container = RubyCore.getLoadpathContainer(entry.getPath(), project);
					if (container != null) {
						switch (container.getKind()) {
							case ILoadpathContainer.K_APPLICATION:
								// don't look at application entries
								break;
							case ILoadpathContainer.K_DEFAULT_SYSTEM:
								loadpathEntries.add(newRuntimeContainerLoadpathEntry(container.getPath(), IRuntimeLoadpathEntry.STANDARD_CLASSES, project));
								break;	
							case ILoadpathContainer.K_SYSTEM:
								loadpathEntries.add(newRuntimeContainerLoadpathEntry(container.getPath(), IRuntimeLoadpathEntry.BOOTSTRAP_CLASSES, project));
								break;
						}						
					}
					break;
				case ILoadpathEntry.CPE_VARIABLE:
					if (RUBYLIB_VARIABLE.equals(entry.getPath().segment(0))) {
						IRuntimeLoadpathEntry jre = newVariableRuntimeLoadpathEntry(entry.getPath());
						jre.setLoadpathProperty(IRuntimeLoadpathEntry.STANDARD_CLASSES);
						loadpathEntries.add(jre);
					}
					break;
				default:
					break;
			}
		}
		loadpathEntries.add(newDefaultProjectLoadpathEntry(project));
		return loadpathEntries.toArray(new IRuntimeLoadpathEntry[loadpathEntries.size()]);
	}
	
	/**
	 * Returns a new runtime loadpath entry containing the default loadpath
	 * for the specified Ruby project. 
	 * 
	 * @param project Ruby project
	 * @return runtime loadpath entry
	 * @since 0.9.0
	 */
	public static IRuntimeLoadpathEntry newDefaultProjectLoadpathEntry(IRubyProject project) {
		return new DefaultProjectLoadpathEntry(project);
	}
	
	/**
	 * Returns resolved entries for the given entry in the context of the given
	 * launch configuration. If the entry is of kind
	 * <code>VARIABLE</code> or <code>CONTAINER</code>, variable and container
	 * resolvers are consulted. If the entry is of kind <code>PROJECT</code>,
	 * and the associated Ruby project specifies non-default output locations,
	 * the corresponding output locations are returned. Otherwise, the given
	 * entry is returned.
	 * <p>
	 * If the given entry is a variable entry, and a resolver is not registered,
	 * the entry itself is returned. If the given entry is a container, and a
	 * resolver is not registered, resolved runtime loadpath entries are calculated
	 * from the associated container loadpath entries, in the context of the project
	 * associated with the given launch configuration.
	 * </p>
	 * @param entry runtime loadpath entry
	 * @param configuration launch configuration
	 * @return resolved runtime loadpath entry
	 * @exception CoreException if unable to resolve
	 * @see IRuntimeClasspathEntryResolver
	 * @since 2.0
	 */
	public static IRuntimeLoadpathEntry[] resolveRuntimeLoadpathEntry(IRuntimeLoadpathEntry entry, ILaunchConfiguration configuration) throws CoreException {
		switch (entry.getType()) {
			case IRuntimeLoadpathEntry.PROJECT:
				// if the project has multiple output locations, they must be returned
				IResource resource = entry.getResource();
				if (resource instanceof IProject) {
					IProject p = (IProject)resource;
					IRubyProject project = RubyCore.create(p);
					if (project == null || !p.isOpen() || !project.exists()) { 
						return new IRuntimeLoadpathEntry[0];
					}
				} else {
					// could not resolve project
					abort(MessageFormat.format(LaunchingMessages.JavaRuntime_Classpath_references_non_existant_project___0__3, entry.getPath().lastSegment()), null); 
				}
				break;
			case IRuntimeLoadpathEntry.VARIABLE:
				IRuntimeLoadpathEntryResolver resolver = getVariableResolver(entry.getVariableName());
				if (resolver == null) {
					IRuntimeLoadpathEntry[] resolved = resolveVariableEntry(entry, null, configuration);
					if (resolved != null) { 
						return resolved;
					}
					break;
				} 
				return resolver.resolveRuntimeLoadpathEntry(entry, configuration);				
			case IRuntimeLoadpathEntry.CONTAINER:
				resolver = getContainerResolver(entry.getVariableName());
				if (resolver == null) {
					return computeDefaultContainerEntries(entry, configuration);
				} 
				return resolver.resolveRuntimeLoadpathEntry(entry, configuration);
			case IRuntimeLoadpathEntry.ARCHIVE:
				// verify the archive exists
				String location = entry.getLocation();
				if (location == null) {
					abort(MessageFormat.format(LaunchingMessages.JavaRuntime_Classpath_references_non_existant_archive___0__4, entry.getPath().toString()), null); 
				}
				File file = new File(location);
				if (!file.exists()) {
					abort(MessageFormat.format(LaunchingMessages.JavaRuntime_Classpath_references_non_existant_archive___0__4, entry.getPath().toString()), null); 
				}
				break;
			case IRuntimeLoadpathEntry.OTHER:
				resolver = getContributedResolver(((IRuntimeLoadpathEntry2)entry).getTypeId());
				return resolver.resolveRuntimeLoadpathEntry(entry, configuration);
			default:
				break;
		}
		return new IRuntimeLoadpathEntry[] {entry};
	}
	
	/**
	 * Performs default resolution for a container entry.
	 * Delegates to the Ruby model.
	 */
	private static IRuntimeLoadpathEntry[] computeDefaultContainerEntries(IRuntimeLoadpathEntry entry, ILaunchConfiguration config) throws CoreException {
		IRubyProject project = entry.getRubyProject();
		if (project == null) {
			project = getRubyProject(config);
		}
		return computeDefaultContainerEntries(entry, project);
	}
	
	/**
	 * Performs default resolution for a container entry.
	 * Delegates to the Ruby model.
	 */
	private static IRuntimeLoadpathEntry[] computeDefaultContainerEntries(IRuntimeLoadpathEntry entry, IRubyProject project) throws CoreException {
		if (project == null || entry == null) {
			// cannot resolve without entry or project context
			return new IRuntimeLoadpathEntry[0];
		} 
		ILoadpathContainer container = RubyCore.getLoadpathContainer(entry.getPath(), project);
		if (container == null) {
			abort(MessageFormat.format(LaunchingMessages.JavaRuntime_Could_not_resolve_classpath_container___0__1,entry.getPath().toString()), null); 
			// execution will not reach here - exception will be thrown
			return null;
		} 
		ILoadpathEntry[] cpes = container.getLoadpathEntries();
		int property = -1;
		switch (container.getKind()) {
			case ILoadpathContainer.K_APPLICATION:
				property = IRuntimeLoadpathEntry.USER_CLASSES;
				break;
			case ILoadpathContainer.K_DEFAULT_SYSTEM:
				property = IRuntimeLoadpathEntry.STANDARD_CLASSES;
				break;	
			case ILoadpathContainer.K_SYSTEM:
				property = IRuntimeLoadpathEntry.BOOTSTRAP_CLASSES;
				break;
		}			
		List<IRuntimeLoadpathEntry> resolved = new ArrayList<IRuntimeLoadpathEntry>(cpes.length);
		List<IRubyProject> projects = fgProjects.get();
		Integer count = fgEntryCount.get();
		if (projects == null) {
			projects = new ArrayList<IRubyProject>();
			fgProjects.set(projects);
			count = new Integer(0);
		}
		int intCount = count.intValue();
		intCount++;
		fgEntryCount.set(new Integer(intCount));
		try {
			for (int i = 0; i < cpes.length; i++) {
				ILoadpathEntry cpe = cpes[i];
				if (cpe.getEntryKind() == ILoadpathEntry.CPE_PROJECT) {
					IProject p = ResourcesPlugin.getWorkspace().getRoot().getProject(cpe.getPath().segment(0));
					IRubyProject rp = RubyCore.create(p);
					if (!projects.contains(rp)) {
						projects.add(rp);
						IRuntimeLoadpathEntry loadpath = newDefaultProjectLoadpathEntry(rp);
						IRuntimeLoadpathEntry[] entries = resolveRuntimeLoadpathEntry(loadpath, rp);
						for (int j = 0; j < entries.length; j++) {
							IRuntimeLoadpathEntry e = entries[j];
							if (!resolved.contains(e)) {
								resolved.add(entries[j]);
							}
						}
					}
				} else {
					IRuntimeLoadpathEntry e = newRuntimeLoadpathEntry(cpe);
					if (!resolved.contains(e)) {
						resolved.add(e);
					}
				}
			}
		} finally {
			intCount--;
			if (intCount == 0) {
				fgProjects.set(null);
				fgEntryCount.set(null);
			} else {
				fgEntryCount.set(new Integer(intCount));
			}
		}
		// set loadpath property
		IRuntimeLoadpathEntry[] result = new IRuntimeLoadpathEntry[resolved.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = resolved.get(i);
			result[i].setLoadpathProperty(property);
		}
		return result;
	}
	
	/**
	 * Default resolution for a loadpath variable - resolve to an archive. Only
	 * one of project/configuration can be non-null.
	 * 
	 * @param entry
	 * @param project the project context or <code>null</code>
	 * @param configuration configuration context or <code>null</code>
	 * @return IRuntimeLoadpathEntry[]
	 * @throws CoreException
	 */
	private static IRuntimeLoadpathEntry[] resolveVariableEntry(IRuntimeLoadpathEntry entry, IRubyProject project, ILaunchConfiguration configuration) throws CoreException {
		// default resolution - an archive
		IPath archPath = RubyCore.getResolvedVariablePath(entry.getPath());
		if (archPath != null) {
			if (entry.getPath().segmentCount() > 1) {
				archPath = archPath.append(entry.getPath().removeFirstSegments(1));
			}
			if (archPath != null && !archPath.isEmpty()) {				
				// now resolve the archive (recursively)
				ILoadpathEntry archEntry = RubyCore.newLibraryEntry(archPath, entry.getLoadpathEntry().isExported());
				IRuntimeLoadpathEntry runtimeArchEntry = newRuntimeLoadpathEntry(archEntry);
				runtimeArchEntry.setLoadpathProperty(entry.getLoadpathProperty());
				if (configuration == null) {
					return resolveRuntimeLoadpathEntry(runtimeArchEntry, project);
				} 
				return resolveRuntimeLoadpathEntry(runtimeArchEntry, configuration);
			}		
		}
		return null;
	}
	
	/**
	 * Returns resolved entries for the given entry in the context of the given
	 * Ruby project. If the entry is of kind
	 * <code>VARIABLE</code> or <code>CONTAINER</code>, variable and container
	 * resolvers are consulted. If the entry is of kind <code>PROJECT</code>,
	 * and the associated Ruby project specifies non-default output locations,
	 * the corresponding output locations are returned. Otherwise, the given
	 * entry is returned.
	 * <p>
	 * If the given entry is a variable entry, and a resolver is not registered,
	 * the entry itself is returned. If the given entry is a container, and a
	 * resolver is not registered, resolved runtime loadpath entries are calculated
	 * from the associated container loadpath entries, in the context of the 
	 * given project.
	 * </p>
	 * @param entry runtime loadpath entry
	 * @param project Ruby project context
	 * @return resolved runtime loadpath entry
	 * @exception CoreException if unable to resolve
	 * @see IRuntimeClasspathEntryResolver
	 * @since 0.9.0
	 */
	public static IRuntimeLoadpathEntry[] resolveRuntimeLoadpathEntry(IRuntimeLoadpathEntry entry, IRubyProject project) throws CoreException {
		switch (entry.getType()) {
			case IRuntimeLoadpathEntry.PROJECT:
				// if the project has multiple output locations, they must be returned
				IResource resource = entry.getResource();
				if (resource instanceof IProject) {
					IProject p = (IProject)resource;
					IRubyProject jp = RubyCore.create(p);
					if (!(jp != null && p.isOpen() && jp.exists())) {
						return new IRuntimeLoadpathEntry[0];
					}
				}
				break;			
			case IRuntimeLoadpathEntry.VARIABLE:
				IRuntimeLoadpathEntryResolver resolver = getVariableResolver(entry.getVariableName());
				if (resolver == null) {
					IRuntimeLoadpathEntry[] resolved = resolveVariableEntry(entry, project, null);
					if (resolved != null) { 
						return resolved;
					}
					break;
				} 
				return resolver.resolveRuntimeLoadpathEntry(entry, project);				
			case IRuntimeLoadpathEntry.CONTAINER:
				resolver = getContainerResolver(entry.getVariableName());
				if (resolver == null) {
					return computeDefaultContainerEntries(entry, project);
				} 
				return resolver.resolveRuntimeLoadpathEntry(entry, project);
			case IRuntimeLoadpathEntry.OTHER:
				resolver = getContributedResolver(((IRuntimeLoadpathEntry2)entry).getTypeId());
				return resolver.resolveRuntimeLoadpathEntry(entry, project);				
			default:
				break;
		}
		return new IRuntimeLoadpathEntry[] {entry};
	}	
	
	/**
	 * Returns the resolver registered for the given contributed loadpath
	 * entry type.
	 * 
	 * @param typeId the id of the contributed loadpath entry
	 * @return the resolver registered for the given loadpath entry
	 */	
	private static IRuntimeLoadpathEntryResolver getContributedResolver(String typeId) {
		IRuntimeLoadpathEntryResolver resolver = (IRuntimeLoadpathEntryResolver)getEntryResolvers().get(typeId);
		if (resolver == null) {
			return new DefaultEntryResolver();
		}
		return resolver;
	}	
	
	/**
	 * Returns all registered runtime loadpath entry resolvers.
	 */
	private static Map getEntryResolvers() {
		if (fgRuntimeLoadpathEntryResolvers == null) {
			initializeResolvers();
		}
		return fgRuntimeLoadpathEntryResolvers;
	}
	
	/**
	 * Resolves the given loadpath, returning the resolved loadpath
	 * in the context of the given launch configuration.
	 *
	 * @param entries unresolved loadpath
	 * @param configuration launch configuration
	 * @return resolved runtime loadpath entries
	 * @throws CoreException 
	 * @exception CoreException if unable to compute the loadpath
	 * @since 0.9.0
	 */
	public static IRuntimeLoadpathEntry[] resolveRuntimeLoadpath(
			IRuntimeLoadpathEntry[] entries, ILaunchConfiguration configuration) throws CoreException {
		return getLoadpathProvider(configuration).resolveLoadpath(entries, configuration);
	}

	/**
	 * Sets a VM as the system-wide default VM, and notifies registered VM install
	 * change listeners of the change.
	 * 
	 * @param vm	The vm to make the default. May be <code>null</code> to clear 
	 * 				the default.
	 * @param monitor progress monitor or <code>null</code>
	 * @param savePreference If <code>true</code>, update workbench preferences to reflect
	 * 		   				  the new default VM.
	 * @throws CoreException 
	 * @since 0.9.0
	 */
	public static void setDefaultVMInstall(IVMInstall vm, IProgressMonitor monitor, boolean savePreference) throws CoreException {
		IVMInstall previous = null;
		if (fgDefaultVMId != null) {
			previous = getVMFromCompositeId(fgDefaultVMId);
		}
		fgDefaultVMId = getCompositeIdFromVM(vm);
		if (savePreference) {
			saveVMConfiguration();
		}
		IVMInstall current = null;
		if (fgDefaultVMId != null) {
			current = getVMFromCompositeId(fgDefaultVMId);
		}
		if (previous != current) {
			notifyDefaultVMChanged(previous, current);
		}
	}	
	
	public static File getRI() {
		return getBinExecutable("qri");
	}
	
	public static File getRDoc() {
		return getBinExecutable("rdoc");
	}
	
	private static File getBinExecutable(String command) {
		IVMInstall vm = RubyRuntime.getDefaultVMInstall();
		if (vm == null) return null;
		File installLocation = vm.getInstallLocation();
		String path = installLocation.getAbsolutePath();
		if (!installLocation.getName().equals("bin")) {
			path += File.separator + "bin";
		}
		path += File.separator + command;
		if (Platform.getOS().equals(Platform.OS_WIN32)) {
			path += ".bat";
			File file = new File(path);
			if (file.exists()) return file;
			path = path.substring(0, path.length() - 3) + "cmd";			
		}
		File file = new File(path);
		if (file.exists()) return file;
		// try adding major version number to end of command for OSes like Debian that do ri1.8
		String version = vm.getRubyVersion();
		if (version == null || version.length() < 3) return file;
		version = version.substring(0, 3);
		path = installLocation.getAbsolutePath();
		if (!installLocation.getName().equals("bin")) {
			path += File.separator + "bin";
		}
		path = File.separator + command + version;
		if (Platform.getOS().equals(Platform.OS_WIN32)) {
			path += ".bat";
		}
		return new File(path);
	}

	public static File getIRB() {
		return getBinExecutable("irb");
	}
	
	private static ILaunchManager getLaunchManager() {
		return DebugPlugin.getDefault().getLaunchManager();
	}

	public static ILaunchConfigurationWorkingCopy createBasicLaunch(String file, String args, IProject project, String workingDirectory) throws CoreException {
		ILaunchConfigurationType configType = getLaunchManager().getLaunchConfigurationType(IRubyLaunchConfigurationConstants.ID_RUBY_APPLICATION);
		ILaunchConfigurationWorkingCopy wc = configType.newInstance(null, getLaunchManager().generateUniqueLaunchConfigurationNameFrom(file));
		wc.setAttribute(IRubyLaunchConfigurationConstants.ATTR_FILE_NAME,
				file);
		wc.setAttribute(
				IRubyLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS,
				args);
		wc.setAttribute(
				IRubyLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY,
				workingDirectory);
		wc.setAttribute(
				IRubyLaunchConfigurationConstants.ATTR_REQUIRES_REFRESH, true);
		wc.setAttribute(IRubyLaunchConfigurationConstants.ATTR_PROJECT_NAME,
				project.getName());
		return wc;
	}

	public static boolean currentVMIsJRuby() {
		if (RubyRuntime.getDefaultVMInstall() == null) return false;
		if (RubyRuntime.getDefaultVMInstall().getVMInstallType() == null) return false;
		if (RubyRuntime.getDefaultVMInstall().getVMInstallType().getId() == null) return false;
		return RubyRuntime.getDefaultVMInstall().getVMInstallType().getId().equals(RubyRuntime.JRUBY_VMTYPE);
	}
	
	public static IPath checkInterpreterBin(String exe) {
		IVMInstall vm = getDefaultVMInstall();
		if (vm == null) return null;
		File installLocation = vm.getInstallLocation();
		if (installLocation == null) return null;
		IPath path = new Path(installLocation.getAbsolutePath());
		if (!installLocation.getName().equals("bin")) {
			path = path.append("bin");
		}
		path = path.append(exe);		
		if (path.toFile().exists()) 
			return path;
		return null;
	}

	public static boolean currentVMIsCygwin() {
		if (RubyRuntime.getDefaultVMInstall() == null) return false;
		if (RubyRuntime.getDefaultVMInstall().getPlatform() == null) return false;
		return RubyRuntime.getDefaultVMInstall().getPlatform().equals(IVMInstall.CYWGIN_PLATFORM);
	}
	
	public static String launchInBackgroundAndRead(final ILaunchConfiguration config, final File file) {
		try {
				ILaunchConfigurationWorkingCopy wc = config.getWorkingCopy();
				wc.setAttribute(IDebugUIConstants.ATTR_LAUNCH_IN_BACKGROUND, true);
				wc.setAttribute(IDebugUIConstants.ATTR_CAPTURE_IN_CONSOLE, false);
				wc.setAttribute(IDebugUIConstants.ATTR_CAPTURE_IN_FILE, file.getAbsolutePath());
				wc.setAttribute(IDebugUIConstants.ATTR_PRIVATE, true);
				wc.setAttribute(IRubyLaunchConfigurationConstants.ATTR_FORCE_NO_CONSOLE, true);
				ILaunchConfiguration config2 = wc.doSave();
				
				ILaunch launch = config2.launch(ILaunchManager.RUN_MODE, new NullProgressMonitor());
				IProcess iproc = launch.getProcesses()[0];

				IStreamMonitor stdOut = iproc.getStreamsProxy().getOutputStreamMonitor();
				StreamListener listener = new StreamListener();
				stdOut.addListener(listener);				
				while (!launch.isTerminated()) {
					Thread.yield();
				}
				//if (listener.getContents().trim().length() == 0) { // if we didn't get anything out of the listener, try reading the output file
					return readFile(file);
				//}			
				//return listener.getContents();
		} catch (Exception e) {
			LaunchingPlugin.log(e);
		}
		return null;
	}
	
	private static String readFile(File file) {
		try {
			return new String(Util.getFileCharContent(file, null));
		} catch (FileNotFoundException e) {
			LaunchingPlugin.log(e);
		} catch (IOException e) {
			LaunchingPlugin.log(e);
		}
		return null;
	}
	
	private static class StreamListener implements IStreamListener {
		
		private StringBuffer buf;

		public StreamListener() {
			buf = new StringBuffer();
		}
		
		public void streamAppended(final String text, IStreamMonitor monitor) {
			buf.append(text);
		}
		
		public String getContents() {
			return buf.toString();
		}
	}
}
