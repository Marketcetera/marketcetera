package org.rubypeople.rdt.internal.launching;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.model.IProcess;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.IRubyInformation;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.launching.IRubyLaunchConfigurationConstants;
import org.rubypeople.rdt.launching.IRuntimeLoadpathEntry2;
import org.rubypeople.rdt.launching.IVMInstall;
import org.rubypeople.rdt.launching.IVMInstallChangedListener;
import org.rubypeople.rdt.launching.IVMInstallType;
import org.rubypeople.rdt.launching.PropertyChangeEvent;
import org.rubypeople.rdt.launching.RubyRuntime;
import org.rubypeople.rdt.launching.VMStandin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class LaunchingPlugin extends Plugin implements IVMInstallChangedListener, IPropertyChangeListener,
		IDebugEventSetListener
{

	public static final String PLUGIN_ID = "org.rubypeople.rdt.launching"; //$NON-NLS-1$
	
	/**
	 * Preference key for boolean flag indicating whether or not user has chosen to use the included JRuby 
	 * (so we shouldn't prompt them to install a ruby VM).
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 * @since 1.0.0
	 */
	public static final String USING_INCLUDED_JRUBY = PLUGIN_ID + ".us.included.jruby"; //$NON-NLS-1$

	/**
	 * Runtime classpath extensions
	 */
	private HashMap<String, IConfigurationElement> fClasspathEntryExtensions = null;

	/**
	 * Identifier for 'runtimeLoadpathEntries' extension point
	 */
	public static final String ID_EXTENSION_POINT_RUNTIME_CLASSPATH_ENTRIES = "runtimeLoadpathEntries"; //$NON-NLS-1$

	/**
	 * Mapping of top-level VM installation directories to library info for that VM.
	 */
	private static Map<String, LibraryInfo> fgLibraryInfoMap = null;

	/**
	 * Whether changes in VM preferences are being batched. When being batched the plug-in can ignore processing and
	 * changes.
	 */
	private boolean fBatchingChanges = false;

	private boolean fIgnoreVMDefPropertyChangeEvents = false;
	private String fOldVMPrefString = EMPTY_STRING;
	protected static LaunchingPlugin plugin;
	private static DocumentBuilder fgXMLParser;

	private ServiceTracker fRITracker;
	private LaunchCleaner fLaunchCleaner;
	
	private static final String EMPTY_STRING = ""; //$NON-NLS-1$	

	public static String osDependentPath(String aPath)
	{
		if (Platform.getOS().equals(Platform.OS_WIN32))
		{
			if (aPath.startsWith(File.separator))
			{
				aPath = aPath.substring(1);
			}
		}

		return aPath;
	}

	public LaunchingPlugin()
	{
		super();
		plugin = this;
	}

	public static LaunchingPlugin getDefault()
	{
		return plugin;
	}

	public static IWorkspace getWorkspace()
	{
		return RubyCore.getWorkspace();
	}

	public static void log(IStatus status)
	{
		getDefault().getLog().log(status);
	}

	public static void log(Throwable e)
	{
		log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR,
				LaunchingMessages.RdtLaunchingPlugin_internalErrorOccurred, e));
	}

	public static void debug(String message)
	{
		if (getDefault().isDebugging())
		{
			System.out.println(message);
		}
	}

	@Override
	public void start(BundleContext context) throws Exception
	{
		super.start(context);

		fRITracker = new ServiceTracker(context, IRubyInformation.class.getName(), null);
		fRITracker.open();

		getPluginPreferences().addPropertyChangeListener(this);
		RubyRuntime.addVMInstallChangedListener(this);
		DebugPlugin.getDefault().addDebugEventListener(this);
		fLaunchCleaner = new LaunchCleaner();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(fLaunchCleaner);
	}

	@Override
	public void stop(BundleContext context) throws Exception
	{
		try
		{
			fRITracker.close();
			ResourcesPlugin.getWorkspace().removeResourceChangeListener(fLaunchCleaner);
			DebugPlugin.getDefault().removeDebugEventListener(this);
			getPluginPreferences().removePropertyChangeListener(this);
			RubyRuntime.removeVMInstallChangedListener(this);
			RubyRuntime.saveVMConfiguration();
			savePluginPreferences();
			fgXMLParser = null;
		}
		finally
		{
			super.stop(context);
		}
	}

	public static String getUniqueIdentifier()
	{
		return PLUGIN_ID;
	}

	/**
	 * Save preferences whenever the connect timeout changes. Process changes to the list of installed JREs.
	 * 
	 * @see org.eclipse.core.runtime.Preferences.IPropertyChangeListener#propertyChange(PropertyChangeEvent)
	 */
	public void propertyChange(org.eclipse.core.runtime.Preferences.PropertyChangeEvent event)
	{
		String property = event.getProperty();
		// if (property.equals(RubyRuntime.PREF_CONNECT_TIMEOUT)) {
		// savePluginPreferences();
		// } else
		if (property.equals(RubyRuntime.PREF_VM_XML))
		{
			if (!isIgnoreVMDefPropertyChangeEvents())
			{
				processVMPrefsChanged((String) event.getOldValue(), (String) event.getNewValue());
			}
		}
	}

	public void setIgnoreVMDefPropertyChangeEvents(boolean ignore)
	{
		fIgnoreVMDefPropertyChangeEvents = ignore;
	}

	public boolean isIgnoreVMDefPropertyChangeEvents()
	{
		return fIgnoreVMDefPropertyChangeEvents;
	}

	/**
	 * Check for differences between the old & new sets of installed JREs. Differences may include additions, deletions
	 * and changes. Take appropriate action for each type of difference. When importing preferences, TWO propertyChange
	 * events are fired. The first has an old value but an empty new value. The second has a new value, but an empty old
	 * value. Normal user changes to the preferences result in a single propertyChange event, with both old and new
	 * values populated. This method handles both types of notification.
	 */
	protected void processVMPrefsChanged(String oldValue, String newValue)
	{

		// batch changes
		fBatchingChanges = true;
		VMChanges vmChanges = null;
		try
		{

			String oldPrefString;
			String newPrefString;

			// If empty new value, save the old value and wait for 2nd propertyChange notification
			if (newValue == null || newValue.equals(EMPTY_STRING))
			{
				fOldVMPrefString = oldValue;
				return;
			}
			// An empty old value signals the second notification in the import preferences
			// sequence. Now that we have both old & new prefs, we can parse and compare them.
			else if (oldValue == null || oldValue.equals(EMPTY_STRING))
			{
				oldPrefString = fOldVMPrefString;
				newPrefString = newValue;
			}
			// If both old & new values are present, this is a normal user change
			else
			{
				oldPrefString = oldValue;
				newPrefString = newValue;
			}

			vmChanges = new VMChanges();
			RubyRuntime.addVMInstallChangedListener(vmChanges);

			// Generate the previous VMs
			VMDefinitionsContainer oldResults = getVMDefinitions(oldPrefString);

			// Generate the current
			VMDefinitionsContainer newResults = getVMDefinitions(newPrefString);

			// Determine the deleted VMs
			List<IVMInstall> deleted = oldResults.getVMList();
			List<IVMInstall> current = newResults.getValidVMList();
			deleted.removeAll(current);

			// Dispose deleted VMs. The 'disposeVMInstall' method fires notification of the
			// deletion.
			Iterator deletedIterator = deleted.iterator();
			while (deletedIterator.hasNext())
			{
				VMStandin deletedVMStandin = (VMStandin) deletedIterator.next();
				deletedVMStandin.getVMInstallType().disposeVMInstall(deletedVMStandin.getId());
			}

			// Fire change notification for added and changed VMs. The 'convertToRealVM'
			// fires the appropriate notification.
			Iterator iter = current.iterator();
			while (iter.hasNext())
			{
				VMStandin standin = (VMStandin) iter.next();
				standin.convertToRealVM();
			}

			// set the new default VM install. This will fire a 'defaultVMChanged',
			// if it in fact changed
			String newDefaultId = newResults.getDefaultVMInstallCompositeID();
			if (newDefaultId != null)
			{
				IVMInstall newDefaultVM = RubyRuntime.getVMFromCompositeId(newDefaultId);
				if (newDefaultVM != null)
				{
					try
					{
						RubyRuntime.setDefaultVMInstall(newDefaultVM, null, false);
					}
					catch (CoreException ce)
					{
						log(ce);
					}
				}
			}

		}
		finally
		{
			// stop batch changes
			fBatchingChanges = false;
			if (vmChanges != null)
			{
				RubyRuntime.removeVMInstallChangedListener(vmChanges);
				try
				{
					vmChanges.process();
				}
				catch (CoreException e)
				{
					log(e);
				}
			}
		}
	}

	/**
	 * Parse the given xml into a VM definitions container, returning an empty container if an exception occurs.
	 * 
	 * @param xml
	 * @return VMDefinitionsContainer
	 */
	private VMDefinitionsContainer getVMDefinitions(String xml)
	{
		if (xml.length() > 0)
		{
			try
			{
				ByteArrayInputStream stream = new ByteArrayInputStream(xml.getBytes("UTF8")); //$NON-NLS-1$
				return VMDefinitionsContainer.parseXMLIntoContainer(stream);
			}
			catch (IOException e)
			{
				LaunchingPlugin.log(e);
			}
		}
		return new VMDefinitionsContainer();
	}

	/**
	 * Returns a Document that can be used to build a DOM tree
	 * 
	 * @return the Document
	 * @throws ParserConfigurationException
	 *             if an exception occurs creating the document builder
	 */
	public static Document getDocument() throws ParserConfigurationException
	{
		DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dfactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		return doc;
	}

	/**
	 * Serializes a XML document into a string - encoded in UTF8 format, with platform line separators.
	 * 
	 * @param doc
	 *            document to serialize
	 * @return the document as a string
	 */
	public static String serializeDocument(Document doc) throws IOException, TransformerException
	{
		ByteArrayOutputStream s = new ByteArrayOutputStream();

		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer();
		transformer.setOutputProperty(OutputKeys.METHOD, "xml"); //$NON-NLS-1$
		transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$

		DOMSource source = new DOMSource(doc);
		StreamResult outputTarget = new StreamResult(s);
		transformer.transform(source, outputTarget);

		return s.toString("UTF8"); //$NON-NLS-1$			
	}

	public static void log(String message)
	{
		log(new Status(IStatus.ERROR, getUniqueIdentifier(), IStatus.ERROR, message, null));
	}

	/**
	 * Returns a shared XML parser.
	 * 
	 * @return an XML parser
	 * @throws CoreException
	 *             if unable to create a parser
	 * @since 3.0
	 */
	public static DocumentBuilder getParser() throws CoreException
	{
		if (fgXMLParser == null)
		{
			try
			{
				fgXMLParser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				fgXMLParser.setErrorHandler(new DefaultHandler());
			}
			catch (ParserConfigurationException e)
			{
				abort(LaunchingMessages.LaunchingPlugin_33, e);
			}
			catch (FactoryConfigurationError e)
			{
				abort(LaunchingMessages.LaunchingPlugin_34, e);
			}
		}
		return fgXMLParser;
	}

	/**
	 * Throws an exception with the given message and underlying exception.
	 * 
	 * @param message
	 *            error message
	 * @param exception
	 *            underlying exception or <code>null</code> if none
	 * @throws CoreException
	 */
	protected static void abort(String message, Throwable exception) throws CoreException
	{
		IStatus status = new Status(IStatus.ERROR, LaunchingPlugin.getUniqueIdentifier(), 0, message, exception);
		throw new CoreException(status);
	}

	/**
	 * Sets the library info that corresponds to the specified Ruby VM install path.
	 * 
	 * @param installPath
	 *            home location for a Ruby VM
	 * @param info
	 *            the library information, or <code>null</code> to remove
	 */
	public static void setLibraryInfo(IVMInstallType type, String installPath, LibraryInfo info)
	{
		if (fgLibraryInfoMap == null)
		{
			restoreLibraryInfo();
		}
		if (info == null)
		{
			fgLibraryInfoMap.remove(buildLibraryInfoKey(type, installPath));
		}
		else
		{
			fgLibraryInfoMap.put(buildLibraryInfoKey(type, installPath), info);
		}
		saveLibraryInfo();
	}

	/**
	 * Saves the library info in a local workspace state location
	 */
	private static void saveLibraryInfo()
	{
		FileOutputStream stream = null;
		try
		{
			String xml = getLibraryInfoAsXML();
			IPath libPath = getDefault().getStateLocation();
			libPath = libPath.append("libraryInfos.xml"); //$NON-NLS-1$
			File file = libPath.toFile();
			if (!file.exists())
			{
				file.createNewFile();
			}
			stream = new FileOutputStream(file);
			stream.write(xml.getBytes("UTF8")); //$NON-NLS-1$
		}
		catch (IOException e)
		{
			log(e);
		}
		catch (ParserConfigurationException e)
		{
			log(e);
		}
		catch (TransformerException e)
		{
			log(e);
		}
		finally
		{
			if (stream != null)
			{
				try
				{
					stream.close();
				}
				catch (IOException e1)
				{
				}
			}
		}
	}

	/**
	 * Return the VM definitions contained in this object as a String of XML. The String is suitable for storing in the
	 * workbench preferences.
	 * <p>
	 * The resulting XML is compatible with the static method <code>parseXMLIntoContainer</code>.
	 * </p>
	 * 
	 * @return String the results of flattening this object into XML
	 * @throws IOException
	 *             if this method fails. Reasons include:
	 *             <ul>
	 *             <li>serialization of the XML document failed</li>
	 *             </ul>
	 */
	private static String getLibraryInfoAsXML() throws ParserConfigurationException, IOException, TransformerException
	{

		Document doc = getDocument();
		Element config = doc.createElement("libraryInfos"); //$NON-NLS-1$
		doc.appendChild(config);

		// Create a node for each info in the table
		Iterator locations = fgLibraryInfoMap.keySet().iterator();
		while (locations.hasNext())
		{
			String raw = (String) locations.next();
			int index = raw.indexOf(";");
			String home = raw.substring(index + 1);
			String vmTypeId = "org.rubypeople.rdt.launching.StandardVMType"; //$NON-NLS-1$ // Default to standard VM type...
			if (index != -1)
			{
				vmTypeId = raw.substring(0, index);
			}
			LibraryInfo info = fgLibraryInfoMap.get(raw);
			Element locationElemnet = infoAsElement(doc, info);
			locationElemnet.setAttribute("home", home); //$NON-NLS-1$
			locationElemnet.setAttribute("vmTypeId", vmTypeId);//$NON-NLS-1$
			config.appendChild(locationElemnet);
		}

		// Serialize the Document and return the resulting String
		return LaunchingPlugin.serializeDocument(doc);
	}

	/**
	 * Creates an XML element for the given info.
	 * 
	 * @param doc
	 * @param info
	 * @return Element
	 */
	private static Element infoAsElement(Document doc, LibraryInfo info)
	{
		Element libraryElement = doc.createElement("libraryInfo"); //$NON-NLS-1$
		libraryElement.setAttribute("version", info.getVersion()); //$NON-NLS-1$
		appendPathElements(doc, "bootpath", libraryElement, info.getBootpath()); //$NON-NLS-1$
		return libraryElement;
	}

	/**
	 * Appends path elements to the given library element, rooted by an element of the given type.
	 * 
	 * @param doc
	 * @param elementType
	 * @param libraryElement
	 * @param paths
	 */
	private static void appendPathElements(Document doc, String elementType, Element libraryElement, String[] paths)
	{
		if (paths.length > 0)
		{
			Element child = doc.createElement(elementType);
			libraryElement.appendChild(child);
			for (int i = 0; i < paths.length; i++)
			{
				String path = paths[i];
				Element entry = doc.createElement("entry"); //$NON-NLS-1$
				child.appendChild(entry);
				entry.setAttribute("path", path); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Returns the library info that corresponds to the specified RubyVM install path, or <code>null</code> if none.
	 * 
	 * @param type
	 * @return the library info that corresponds to the specified RubyVM install path, or <code>null</code> if none
	 */
	public static LibraryInfo getLibraryInfo(IVMInstallType type, String vmInstallPath)
	{
		if (fgLibraryInfoMap == null)
		{
			restoreLibraryInfo();
		}
		return fgLibraryInfoMap.get(buildLibraryInfoKey(type, vmInstallPath));
	}

	private static String buildLibraryInfoKey(IVMInstallType type, String vmInstallPath)
	{
		return buildLibraryInfoKey(type.getId(), vmInstallPath);
	}

	private static String buildLibraryInfoKey(String vmTypeId, String vmInstallPath)
	{
		return vmTypeId + ";" + vmInstallPath;
	}

	/**
	 * Restores library information for VMs
	 */
	private static void restoreLibraryInfo()
	{
		fgLibraryInfoMap = new HashMap<String, LibraryInfo>(10);
		IPath libPath = getDefault().getStateLocation();
		libPath = libPath.append("libraryInfos.xml"); //$NON-NLS-1$
		File file = libPath.toFile();
		if (file.exists())
		{
			try
			{
				InputStream stream = new FileInputStream(file);
				DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				parser.setErrorHandler(new DefaultHandler());
				Element root = parser.parse(new InputSource(stream)).getDocumentElement();
				if (!root.getNodeName().equals("libraryInfos")) { //$NON-NLS-1$
					return;
				}

				NodeList list = root.getChildNodes();
				int length = list.getLength();
				for (int i = 0; i < length; ++i)
				{
					Node node = list.item(i);
					short type = node.getNodeType();
					if (type == Node.ELEMENT_NODE)
					{
						Element element = (Element) node;
						String nodeName = element.getNodeName();
						if (nodeName.equalsIgnoreCase("libraryInfo")) { //$NON-NLS-1$
							String version = element.getAttribute("version"); //$NON-NLS-1$
							String location = element.getAttribute("home"); //$NON-NLS-1$
							String vmTypeId = element.getAttribute("vmTypeId"); //$NON-NLS-1$
							String[] bootpath = getPathsFromXML(element, "bootpath"); //$NON-NLS-1$
							if (location != null)
							{
								LibraryInfo info = new LibraryInfo(version, bootpath);
								fgLibraryInfoMap.put(buildLibraryInfoKey(vmTypeId, location), info);
							}
						}
					}
				}
			}
			catch (IOException e)
			{
				log(e);
			}
			catch (ParserConfigurationException e)
			{
				log(e);
			}
			catch (SAXException e)
			{
				log(e);
			}
		}
	}

	private static String[] getPathsFromXML(Element lib, String pathType)
	{
		List<String> paths = new ArrayList<String>();
		NodeList list = lib.getChildNodes();
		int length = list.getLength();
		for (int i = 0; i < length; ++i)
		{
			Node node = list.item(i);
			short type = node.getNodeType();
			if (type == Node.ELEMENT_NODE)
			{
				Element element = (Element) node;
				String nodeName = element.getNodeName();
				if (nodeName.equalsIgnoreCase(pathType))
				{
					NodeList entries = element.getChildNodes();
					int numEntries = entries.getLength();
					for (int j = 0; j < numEntries; j++)
					{
						Node n = entries.item(j);
						short t = n.getNodeType();
						if (t == Node.ELEMENT_NODE)
						{
							Element entryElement = (Element) n;
							String name = entryElement.getNodeName();
							if (name.equals("entry")) { //$NON-NLS-1$
								String path = entryElement.getAttribute("path"); //$NON-NLS-1$
								if (path != null && path.length() > 0)
								{
									paths.add(path);
								}
							}
						}
					}
				}
			}
		}
		return paths.toArray(new String[paths.size()]);
	}

	/**
	 * Returns a new runtime classpath entry of the specified type.
	 * 
	 * @param id
	 *            extension type id
	 * @return new uninitialized runtime classpath entry
	 * @throws CoreException
	 *             if unable to create an entry
	 */
	public IRuntimeLoadpathEntry2 newRuntimeLoadpathEntry(String id) throws CoreException
	{
		if (fClasspathEntryExtensions == null)
		{
			initializeRuntimeLoadpathExtensions();
		}
		IConfigurationElement config = fClasspathEntryExtensions.get(id);
		if (config == null)
		{
			abort(MessageFormat.format(LaunchingMessages.LaunchingPlugin_32, id), null);
		}
		return (IRuntimeLoadpathEntry2) config.createExecutableExtension("class"); //$NON-NLS-1$
	}

	/**
	 * Loads runtime classpath extensions
	 */
	private void initializeRuntimeLoadpathExtensions()
	{
		IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(LaunchingPlugin.PLUGIN_ID,
				ID_EXTENSION_POINT_RUNTIME_CLASSPATH_ENTRIES);
		IConfigurationElement[] configs = extensionPoint.getConfigurationElements();
		fClasspathEntryExtensions = new HashMap<String, IConfigurationElement>(configs.length);
		for (int i = 0; i < configs.length; i++)
		{
			fClasspathEntryExtensions.put(configs[i].getAttribute("id"), configs[i]); //$NON-NLS-1$
		}
	}

	public void defaultVMInstallChanged(IVMInstall previous, IVMInstall current)
	{
		if (!fBatchingChanges)
		{
			try
			{
				VMChanges changes = new VMChanges();
				changes.defaultVMInstallChanged(previous, current);
				changes.process();
			}
			catch (CoreException e)
			{
				log(e);
			}
		}
	}

	public void vmAdded(IVMInstall newVm)
	{
	}

	public void vmChanged(PropertyChangeEvent event)
	{
		if (!fBatchingChanges)
		{
			try
			{
				VMChanges changes = new VMChanges();
				changes.vmChanged(event);
				changes.process();
			}
			catch (CoreException e)
			{
				log(e);
			}
		}
	}

	public void vmRemoved(IVMInstall vm)
	{
		removeCoreStubs(vm);
		if (!fBatchingChanges)
		{
			try
			{
				VMChanges changes = new VMChanges();
				changes.vmRemoved(vm);
				changes.process();
			}
			catch (CoreException e)
			{
				log(e);
			}
		}
	}

	private void removeCoreStubs(IVMInstall vm)
	{
		IPath coreStubPath = getStateLocation().append(vm.getId());
		deleteRecursively(coreStubPath.toFile());
	}

	private void deleteRecursively(File file)
	{
		if (file.isDirectory())
		{
			File[] children = file.listFiles();
			for (int i = 0; i < children.length; i++)
			{
				deleteRecursively(children[i]);
			}
		}
		if (!file.delete())
			file.deleteOnExit();
	}

	/**
	 * Stores VM changes resulting from a JRE preference change.
	 */
	class VMChanges implements IVMInstallChangedListener
	{

		// true if the default VM changes
		private boolean fDefaultChanged = false;

		// old container ids to new
		private HashMap<IPath, IPath> fRenamedContainerIds = new HashMap<IPath, IPath>();

		/**
		 * Returns the JRE container id that the given VM would map to, or <code>null</code> if none.
		 * 
		 * @param vm
		 * @return container id or <code>null</code>
		 */
		private IPath getContainerId(IVMInstall vm)
		{
			if (vm != null)
			{
				String name = vm.getName();
				if (name != null)
				{
					IPath path = new Path(RubyRuntime.RUBY_CONTAINER);
					path = path.append(new Path(vm.getVMInstallType().getId()));
					path = path.append(new Path(name));
					return path;
				}
			}
			return null;
		}

		/**
		 * @see org.rubypeople.rdt.launching.IVMInstallChangedListener#defaultVMInstallChanged(org.rubypeople.rdt.launching.IVMInstall,
		 *      org.rubypeople.rdt.launching.IVMInstall)
		 */
		public void defaultVMInstallChanged(IVMInstall previous, IVMInstall current)
		{
			fDefaultChanged = true;
		}

		/**
		 * @see org.rubypeople.rdt.launching.IVMInstallChangedListener#vmAdded(org.rubypeople.rdt.launching.IVMInstall)
		 */
		public void vmAdded(IVMInstall vm)
		{
		}

		/**
		 * @see org.rubypeople.rdt.launching.IVMInstallChangedListener#vmChanged(org.rubypeople.rdt.launching.PropertyChangeEvent)
		 */
		public void vmChanged(org.rubypeople.rdt.launching.PropertyChangeEvent event)
		{
			String property = event.getProperty();
			IVMInstall vm = (IVMInstall) event.getSource();
			if (property.equals(IVMInstallChangedListener.PROPERTY_NAME))
			{
				IPath newId = getContainerId(vm);
				IPath oldId = new Path(RubyRuntime.RUBY_CONTAINER);
				oldId = oldId.append(vm.getVMInstallType().getId());
				String oldName = (String) event.getOldValue();
				// bug 33746 - if there is no old name, then this is not a re-name.
				if (oldName != null)
				{
					oldId = oldId.append(oldName);
					fRenamedContainerIds.put(oldId, newId);
				}
			}
		}

		/**
		 * @see org.rubypeople.rdt.launching.IVMInstallChangedListener#vmRemoved(org.rubypeople.rdt.launching.IVMInstall)
		 */
		public void vmRemoved(IVMInstall vm)
		{
		}

		/**
		 * Re-bind loadpath variables and containers affected by the JRE changes.
		 */
		public void process() throws CoreException
		{
			RubyVMUpdateJob job = new RubyVMUpdateJob(this);
			job.schedule();
		}

		protected void doit(IProgressMonitor monitor) throws CoreException
		{
			IWorkspaceRunnable runnable = new IWorkspaceRunnable()
			{
				public void run(IProgressMonitor monitor1) throws CoreException
				{
					IRubyProject[] projects = RubyCore.create(ResourcesPlugin.getWorkspace().getRoot())
							.getRubyProjects();
					monitor1.beginTask(LaunchingMessages.LaunchingPlugin_0, projects.length + 1);
					rebind(monitor1, projects);
					monitor1.done();
				}
			};
			RubyCore.run(runnable, null, monitor);
		}

		/**
		 * Re-bind loadpath variables and containers affected by the Ruby VM changes.
		 * 
		 * @param monitor
		 */
		private void rebind(IProgressMonitor monitor, IRubyProject[] projects) throws CoreException
		{

			if (fDefaultChanged)
			{
				// re-bind RUBYLIB if the default VM changed
				RubyLoadpathVariablesInitializer initializer = new RubyLoadpathVariablesInitializer();
				initializer.initialize(RubyRuntime.RUBYLIB_VARIABLE);
			}
			monitor.worked(1);

			// re-bind all container entries
			for (int i = 0; i < projects.length; i++)
			{
				IRubyProject project = projects[i];
				ILoadpathEntry[] entries = project.getRawLoadpath();
				boolean replace = false;
				for (int j = 0; j < entries.length; j++)
				{
					ILoadpathEntry entry = entries[j];
					switch (entry.getEntryKind())
					{
						case ILoadpathEntry.CPE_CONTAINER:
							IPath reference = entry.getPath();
							IPath newBinding = null;
							String firstSegment = reference.segment(0);
							if (RubyRuntime.RUBY_CONTAINER.equals(firstSegment))
							{
								if (reference.segmentCount() > 1)
								{
									IPath renamed = fRenamedContainerIds.get(reference);
									if (renamed != null)
									{
										// The JRE was re-named. This changes the identifier of
										// the container entry.
										newBinding = renamed;
									}
								}
								RubyContainerInitializer initializer = new RubyContainerInitializer();
								if (newBinding == null)
								{
									// rebind old path
									initializer.initialize(reference, project);
								}
								else
								{
									// replace old cp entry with a new one
									ILoadpathEntry newEntry = RubyCore
											.newContainerEntry(newBinding, entry.isExported());
									entries[j] = newEntry;
									replace = true;
								}
							}
							break;
						default:
							break;
					}
				}
				if (replace)
				{
					project.setRawLoadpath(entries, null);
				}
				monitor.worked(1);
			}
		}
	}

	class RubyVMUpdateJob extends Job
	{
		private VMChanges fChanges;

		public RubyVMUpdateJob(VMChanges changes)
		{
			super(LaunchingMessages.LaunchingPlugin_1);
			fChanges = changes;
			setSystem(true);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
		 */
		protected IStatus run(IProgressMonitor monitor)
		{
			try
			{
				fChanges.doit(monitor);
			}
			catch (CoreException e)
			{
				return e.getStatus();
			}
			return Status.OK_STATUS;
		}

	}

	/**
	 * Return a <code>java.io.File</code> object that corresponds to the specified <code>IPath</code> in the plugin
	 * directory.
	 */
	public static File getFileInPlugin(IPath path)
	{
		try
		{
			URL installURL = new URL(getDefault().getBundle().getEntry("/"), path.toString()); //$NON-NLS-1$
			URL localURL = FileLocator.toFileURL(installURL);
			return new File(localURL.getFile());
		}
		catch (IOException ioe)
		{
			return null;
		}
	}

	public static void info(String string)
	{
		log(new Status(IStatus.INFO, PLUGIN_ID, -1, string, null));
	}

	public void handleDebugEvents(DebugEvent[] events)
	{
		for (int i = 0; i < events.length; i++)
		{

			// Watch for termination of processes that modify the filesystem
			// and refresh the modified project
			int kind = events[i].getKind();
			Object source = events[i].getSource();
			if ((kind == DebugEvent.TERMINATE) && (source instanceof IProcess))
			{
				IProcess iProc = (IProcess) source;
				String refresh = iProc.getAttribute(IRubyLaunchConfigurationConstants.ATTR_REQUIRES_REFRESH);
				if ((refresh != null) && refresh.equals("true"))
				{
					String projName = iProc.getAttribute(IRubyLaunchConfigurationConstants.ATTR_PROJECT_NAME);
					IProject proj = ResourcesPlugin.getWorkspace().getRoot().getProject(projName);
					try
					{
						proj.refreshLocal(IResource.DEPTH_INFINITE, null);
					}
					catch (CoreException e)
					{
						log(e);
					}
				}
			}
		}
	}

	public static IRubyInformation getRubyInformation()
	{
		return (IRubyInformation) getDefault().fRITracker.getService();
	}
}
