/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.launching;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.rubypeople.rdt.launching.IVMInstall;
import org.rubypeople.rdt.launching.IVMInstallType;
import org.rubypeople.rdt.launching.RubyRuntime;
import org.rubypeople.rdt.launching.VMStandin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This is a container for VM definitions such as the VM definitions that are
 * stored in the workbench preferences.  
 * <p>
 * An instance of this class may be obtained from an XML document by calling
 * <code>parseXMLIntoContainer</code>.
 * </p>
 * <p>
 * An instance of this class may be translated into an XML document by calling
 * <code>getAsXML</code>.
 * </p>
 * <p>
 * Clients may instantiate this class; it is not intended to be subclassed.
 * </p>
 * 
 * @since 2.1
 */
public class VMDefinitionsContainer {
		
	/**
	 * Map of VMInstallTypes to Lists of corresponding VMInstalls.
	 */
	private Map<IVMInstallType, List<IVMInstall>> fVMTypeToVMMap;
	
	/**
	 * Cached list of VMs in this container
	 */
	private List<IVMInstall> fVMList;
	
	/**
	 * VMs managed by this container whose install locations don't actually exist.
	 */
	private List<IVMInstall> fInvalidVMList;
			
	/**
	 * The composite identifier of the default VM.  This consists of the install type ID
	 * plus an ID for the VM.
	 */
	private String fDefaultVMInstallCompositeID;
	
	/**
	 * Constructs an empty VM container 
	 */
	public VMDefinitionsContainer() {
		fVMTypeToVMMap = new HashMap<IVMInstallType, List<IVMInstall>>(10);
		fInvalidVMList = new ArrayList<IVMInstall>(10);	
		fVMList = new ArrayList<IVMInstall>(10);		
	}
		
	/**
	 * Add the specified VM to the VM definitions managed by this container.
	 * <p>
	 * If distinguishing valid from invalid VMs is important, the specified VM must
	 * have already had its install location set.  An invalid VM is one whose install
	 * location doesn't exist.
	 * </p>
	 * 
	 * @param vm the VM to be added to this container
	 */
	public void addVM(IVMInstall vm) {
		if (!fVMList.contains(vm)) {	
			IVMInstallType vmInstallType = vm.getVMInstallType();
			List<IVMInstall> vmList = fVMTypeToVMMap.get(vmInstallType);
			if (vmList == null) {
				vmList = new ArrayList<IVMInstall>(3);
				fVMTypeToVMMap.put(vmInstallType, vmList);			
			}
			vmList.add(vm);
			File installLocation = vm.getInstallLocation();
			if (installLocation == null || !vmInstallType.validateInstallLocation(installLocation).isOK()) {
				fInvalidVMList.add(vm);
			}
			fVMList.add(vm);
		}
	}
	
	/**
	 * Add all VM's in the specified list to the VM definitions managed by this container.
	 * <p>
	 * If distinguishing valid from invalid VMs is important, the specified VMs must
	 * have already had their install locations set.  An invalid VM is one whose install
	 * location doesn't exist.
	 * </p>
	 * 
	 * @param vmList a list of VMs to be added to this container
	 */
	public void addVMList(List vmList) {
		Iterator iterator = vmList.iterator();
		while (iterator.hasNext()) {
			IVMInstall vm = (IVMInstall) iterator.next();
			addVM(vm);
		}
	}

	/**
	 * Return a mapping of VM install types to lists of VMs.  The keys of this map are instances of
	 * <code>IInterpreterInstallType</code>.  The values are instances of <code>java.util.List</code>
	 * which contain instances of <code>IInterpreter</code>.  
	 * 
	 * @return Map the mapping of VM install types to lists of VMs
	 */
	public Map getVMTypeToVMMap() {
		return fVMTypeToVMMap;
	}
	
	/**
	 * Return a list of all VMs in this container, including any invalid VMs.  An invalid
	 * VM is one whose install location does not exist on the file system.
	 * The order of the list is not specified.
	 * 
	 * @return List the data structure containing all VMs managed by this container
	 */
	public List<IVMInstall> getVMList() {
		return fVMList;
	}
	
	/**
	 * Return a list of all valid VMs in this container.  A valid VM is one whose install
	 * location exists on the file system.  The order of the list is not specified.
	 * 
	 * @return List 
	 */
	public List<IVMInstall> getValidVMList() {
		List<IVMInstall> vms = getVMList();
		List<IVMInstall> resultList = new ArrayList<IVMInstall>(vms.size());
		resultList.addAll(vms);
		resultList.removeAll(fInvalidVMList);
		return resultList;
	}
	
	/**
	 * Returns the composite ID for the default VM.  The composite ID consists
	 * of an ID for the VM install type together with an ID for VM.  This is
	 * necessary because VM ids by themselves are not necessarily unique across
	 * VM install types.
	 * 
	 * @return String returns the composite ID of the current default VM
	 */
	public String getDefaultVMInstallCompositeID(){
		return fDefaultVMInstallCompositeID;
	}
	
	/**
	 * Sets the composite ID for the default VM.  The composite ID consists
	 * of an ID for the VM install type together with an ID for VM.  This is
	 * necessary because VM ids by themselves are not necessarily unique across
	 * VM install types.
	 * 
	 * @param id identifies the new default VM using a composite ID
	 */
	public void setDefaultVMInstallCompositeID(String id){
		fDefaultVMInstallCompositeID = id;
	}
	
	/**
	 * Return the VM definitions contained in this object as a String of XML.  The String
	 * is suitable for storing in the workbench preferences.
	 * <p>
	 * The resulting XML is compatible with the static method <code>parseXMLIntoContainer</code>.
	 * </p>
	 * @return String the results of flattening this object into XML
	 * @throws IOException if this method fails. Reasons include:<ul>
	 * <li>serialization of the XML document failed</li>
	 * </ul>
	 * @throws ParserConfigurationException if creation of the XML document failed
	 * @throws TransformerException if serialization of the XML document failed
	 */
	public String getAsXML() throws ParserConfigurationException, IOException, TransformerException {
		
		// Create the Document and the top-level node
		Document doc = LaunchingPlugin.getDocument();
		Element config = doc.createElement("vmSettings");    //$NON-NLS-1$
		doc.appendChild(config);
		
		// Set the defaultVM attribute on the top-level node
		if (getDefaultVMInstallCompositeID() != null) {
			config.setAttribute("defaultVM", getDefaultVMInstallCompositeID()); //$NON-NLS-1$
		}
			
		// Create a node for each install type represented in this container
		Set vmInstallTypeSet = getVMTypeToVMMap().keySet();
		Iterator keyIterator = vmInstallTypeSet.iterator();
		while (keyIterator.hasNext()) {
			IVMInstallType vmInstallType = (IVMInstallType) keyIterator.next();
			Element vmTypeElement = vmTypeAsElement(doc, vmInstallType);
			config.appendChild(vmTypeElement);
		}
		
		// Serialize the Document and return the resulting String
		return LaunchingPlugin.serializeDocument(doc);
	}
	
	/**
	 * Create and return a node for the specified VM install type in the specified Document.
	 */
	private Element vmTypeAsElement(Document doc, IVMInstallType vmType) {
		
		// Create a node for the vm type and set its 'id' attribute
		Element element= doc.createElement("vmType");   //$NON-NLS-1$
		element.setAttribute("id", vmType.getId());     //$NON-NLS-1$
		
		// For each vm of the specified type, create a subordinate node for it
		List vmList = (List) getVMTypeToVMMap().get(vmType);
		Iterator vmIterator = vmList.iterator();
		while (vmIterator.hasNext()) {
			IVMInstall vm = (IVMInstall) vmIterator.next();
			Element vmElement = vmAsElement(doc, vm);
			element.appendChild(vmElement);
		}
		
		return element;
	}
	
	/**
	 * Create and return a node for the specified VM in the specified Document.
	 */
	private Element vmAsElement(Document doc, IVMInstall vm) {
		
		// Create the node for the VM and set its 'id' & 'name' attributes
		Element element= doc.createElement("vm");        //$NON-NLS-1$
		element.setAttribute("id", vm.getId());	         //$NON-NLS-1$
		element.setAttribute("name", vm.getName());      //$NON-NLS-1$
		
		// Determine and set the 'path' attribute for the VM
		String installPath= "";                          //$NON-NLS-1$
		File installLocation= vm.getInstallLocation();
		if (installLocation != null) {
			installPath= installLocation.getAbsolutePath();
		}
		element.setAttribute("path", installPath);       //$NON-NLS-1$
		
		// If the 'libraryLocations' attribute is specified, create a node for it 
		IPath[] libraryLocations= vm.getLibraryLocations();
		if (libraryLocations != null) {
			Element libLocationElement = libraryLocationsAsElement(doc, libraryLocations);
			element.appendChild(libLocationElement);
		}
				
		String vmArgs = vm.getVMArgs();
		if (vmArgs != null && vmArgs.length() > 0) {
			element.setAttribute("vmargs", vmArgs); //$NON-NLS-1$
		}	
		
		return element;
	}
	
	/**
	 * Create and return a 'libraryLocations' node.  This node owns subordinate nodes that
	 * list individual library locations.
	 */
	private static Element libraryLocationsAsElement(Document doc, IPath[] locations) {
		Element root = doc.createElement("libraryLocations");       //$NON-NLS-1$
		for (int i = 0; i < locations.length; i++) {
			Element element = doc.createElement("libraryLocation");  //$NON-NLS-1$
			element.setAttribute("src", locations[i].toString()); //$NON-NLS-1$
			root.appendChild(element);
		}
		return root;
	}
	
	public static VMDefinitionsContainer parseXMLIntoContainer(InputStream inputStream) throws IOException {
		VMDefinitionsContainer container = new VMDefinitionsContainer();
		parseXMLIntoContainer(inputStream, container);
		return container;
	}
			
	/**
	 * Parse the VM definitions contained in the specified InputStream into the
	 * specified container.
	 * <p>
	 * The VMs in the returned container are instances of <code>VMStandin</code>.
	 * </p>
	 * <p>
	 * This method has no side-effects.  That is, no notifications are sent for VM adds,
	 * changes, deletes, and the workbench preferences are not affected.
	 * </p>
	 * <p>
	 * If the <code>getAsXML</code> method is called on the returned container object,
	 * the resulting XML will be sematically equivalent (though not necessarily syntactically equivalent) as
	 * the XML contained in <code>inputStream</code>.
	 * </p>
	 * @param inputStream the <code>InputStream</code> containing XML that declares a set of VMs and a default VM
	 * @param container the container to add the VM defs to
	 * @return VMDefinitionsContainer a container for the VM objects declared in <code>inputStream</code>
	 * @throws IOException if this method fails. Reasons include:<ul>
	 * <li>the XML in <code>inputStream</code> was badly formatted</li>
	 * <li>the top-level node was not 'vmSettings'</li>
	 * </ul>
	 * @since 3.2
	 */
	public static void parseXMLIntoContainer(InputStream inputStream, VMDefinitionsContainer container) throws IOException {

		// Wrapper the stream for efficient parsing
		InputStream stream= new BufferedInputStream(inputStream);

		// Do the parsing and obtain the top-level node
		Element config= null;		
		try {
			DocumentBuilder parser= DocumentBuilderFactory.newInstance().newDocumentBuilder();
			parser.setErrorHandler(new DefaultHandler());
			config = parser.parse(new InputSource(stream)).getDocumentElement();
		} catch (SAXException e) {
			throw new IOException(LaunchingMessages.RubyRuntime_badFormat); 
		} catch (ParserConfigurationException e) {
			stream.close();
			throw new IOException(LaunchingMessages.RubyRuntime_badFormat); 
		} finally {
			stream.close();
		}
		
		String nodeName = config.getNodeName();
		if (nodeName.equalsIgnoreCase("runtimeconfig")) {
			// Do the legacy stuff
			importLegacyInterpreters(config, container);
			return;
		} else if (!config.getNodeName().equalsIgnoreCase("vmSettings")) { //$NON-NLS-1$
			// If the top-level node wasn't what we expected, bail out
			throw new IOException(LaunchingMessages.RubyRuntime_badFormat); 
		}
		
		// Populate the default VM-related fields
		container.setDefaultVMInstallCompositeID(config.getAttribute("defaultVM")); //$NON-NLS-1$
		
		// Traverse the parsed structure and populate the VMType to VM Map
		NodeList list = config.getChildNodes();
		int length = list.getLength();
		for (int i = 0; i < length; ++i) {
			Node node = list.item(i);
			short type = node.getNodeType();
			if (type == Node.ELEMENT_NODE) {
				Element vmTypeElement = (Element) node;
				if (vmTypeElement.getNodeName().equalsIgnoreCase("vmType")) { //$NON-NLS-1$
					populateVMTypes(vmTypeElement, container);
				}
			}
		}
	}
	
	/**
	 * For the specified vm type node, parse all subordinate VM definitions and add them
	 * to the specified container.
	 */
	private static void populateVMTypes(Element vmTypeElement, VMDefinitionsContainer container) {
		
		// Retrieve the 'id' attribute and the corresponding VM type object
		String id = vmTypeElement.getAttribute("id");         //$NON-NLS-1$
		IVMInstallType vmType= RubyRuntime.getVMInstallType(id);
		if (vmType != null) {
			
			// For each VM child node, populate the container with a subordinate node
			NodeList vmNodeList = vmTypeElement.getChildNodes();
			for (int i = 0; i < vmNodeList.getLength(); ++i) {
				Node vmNode = vmNodeList.item(i);
				short type = vmNode.getNodeType();
				if (type == Node.ELEMENT_NODE) {
					Element vmElement = (Element) vmNode;
					if (vmElement.getNodeName().equalsIgnoreCase("vm")) { //$NON-NLS-1$
						populateVMForType(vmType, vmElement, container);
					}
				}
			}
		} else {
			LaunchingPlugin.log(LaunchingMessages.RubyRuntime_VM_type_element_with_unknown_id_1); 
		}
	}
	
	/**
	 * Parse the specified VM node, create a VMStandin for it, and add this to the 
	 * specified container.
	 */
	private static void populateVMForType(IVMInstallType vmType, Element vmElement, VMDefinitionsContainer container) {
		String id= vmElement.getAttribute("id"); //$NON-NLS-1$
		if (id != null) {
			
			// Retrieve the 'path' attribute.  If none, skip this node.
			String installPath= vmElement.getAttribute("path"); //$NON-NLS-1$
			if (installPath == null) {
				return;
			}
						
			// Create a VMStandin for the node and set its 'name' & 'installLocation' attributes
			VMStandin vmStandin = new VMStandin(vmType, id);
			vmStandin.setName(vmElement.getAttribute("name")); //$NON-NLS-1$
			File installLocation= new File(installPath);
			vmStandin.setInstallLocation(installLocation);
			container.addVM(vmStandin);
			
			// Look for subordinate nodes.  These may be 'libraryLocation',
			// 'libraryLocations' or 'versionInfo'.
			NodeList list = vmElement.getChildNodes();
			int length = list.getLength();
			for (int i = 0; i < length; ++i) {
				Node node = list.item(i);
				short type = node.getNodeType();
				if (type == Node.ELEMENT_NODE) {
					Element subElement = (Element)node;
					String subElementName = subElement.getNodeName();
					if (subElementName.equals("libraryLocation")) { //$NON-NLS-1$
						IPath loc = getLibraryLocation(subElement);
						vmStandin.setLibraryLocations(new IPath[]{loc});
						break;
					} else if (subElementName.equals("libraryLocations")) { //$NON-NLS-1$
						setLibraryLocations(vmStandin, subElement);
						break;
					}
				}
			}
						
			// vm Arguments
			String vmArgs = vmElement.getAttribute("vmargs"); //$NON-NLS-1$
			if (vmArgs != null && vmArgs.length() >0) {
				vmStandin.setVMArgs(vmArgs);
			}
		} else {
			LaunchingPlugin.log(LaunchingMessages.RubyRuntime_VM_element_specified_with_no_id_attribute_2); 
		}
	}
	
	/**
	 * Create & return a LibraryLocation object populated from the attribute values
	 * in the specified node.
	 */
	private static IPath getLibraryLocation(Element libLocationElement) {
		String src= libLocationElement.getAttribute("src"); //$NON-NLS-1$
		return new Path(src);
	}
	
	/**
	 * Set the LibraryLocations on the specified VM, by extracting the subordinate
	 * nodes from the specified 'lirbaryLocations' node.
	 */
	private static void setLibraryLocations(IVMInstall vm, Element libLocationsElement) {
		NodeList list = libLocationsElement.getChildNodes();
		int length = list.getLength();
		List<IPath> locations = new ArrayList<IPath>(length);
		for (int i = 0; i < length; ++i) {
			Node node = list.item(i);
			short type = node.getNodeType();
			if (type == Node.ELEMENT_NODE) {
				Element libraryLocationElement= (Element)node;
				if (libraryLocationElement.getNodeName().equals("libraryLocation")) { //$NON-NLS-1$
					locations.add(getLibraryLocation(libraryLocationElement));
				}
			}
		}	
		vm.setLibraryLocations(locations.toArray(new IPath[locations.size()]));
	}

	private static void importLegacyInterpreters(Element config, VMDefinitionsContainer container) {
		IVMInstallType vmType = RubyRuntime.getVMInstallType("org.rubypeople.rdt.launching.StandardVMType");
		// Traverse the parsed structure and populate the VMType to VM Map
		NodeList list = config.getChildNodes();
		int length = list.getLength();
		for (int i = 0; i < length; ++i) {
			Node node = list.item(i);
			short type = node.getNodeType();
			if (type == Node.ELEMENT_NODE) {
				Element vmElement = (Element) node;
				if (vmElement.getNodeName().equalsIgnoreCase("interpreter")) { //$NON-NLS-1$
					legacyPopulateVMForType(vmType, vmElement, container);
				}
			}
		}
		
	}

	/**
	 * Parse the specified VM node, create a VMStandin for it, and add this to the 
	 * specified container.
	 */
	private static void legacyPopulateVMForType(IVMInstallType vmType, Element vmElement, VMDefinitionsContainer container) {
		String id= vmElement.getAttribute("name"); //$NON-NLS-1$
		if (id != null) {
			
			// Retrieve the 'path' attribute.  If none, skip this node.
			String installPath= vmElement.getAttribute("path"); //$NON-NLS-1$
			if (installPath == null) {
				return;
			}
						
			// Create a VMStandin for the node and set its 'name' & 'installLocation' attributes
			VMStandin vmStandin = new VMStandin(vmType, id);
			vmStandin.setName(id); //$NON-NLS-1$
			File installLocation= new File(installPath);
			//  If the path is to the executable (which it should be), chop off last part of path!
			if (installLocation.isFile()) {
				installLocation = installLocation.getParentFile(); // move up to "bin"
				if (installLocation != null && installLocation.getParentFile() != null) {
					installLocation = installLocation.getParentFile(); // this should now be ruby install location
				}
			}
			vmStandin.setInstallLocation(installLocation);
			container.addVM(vmStandin);
		} else {
			LaunchingPlugin.log(LaunchingMessages.RubyRuntime_VM_element_specified_with_no_id_attribute_2); 
		}
	}	
	
	/**
	 * Removes the VM from this container.
	 * 
	 * @param vm vm intall
	 */
	public void removeVM(IVMInstall vm) {
		fVMList.remove(vm);
		fInvalidVMList.remove(vm);
		List list = fVMTypeToVMMap.get(vm.getVMInstallType());
		if (list != null) {
			list.remove(vm);
		}
	}
		
}
