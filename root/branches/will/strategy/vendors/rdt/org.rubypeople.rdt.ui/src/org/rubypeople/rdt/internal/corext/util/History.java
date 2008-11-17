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
package org.rubypeople.rdt.internal.corext.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.rubypeople.rdt.internal.corext.CorextMessages;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.RubyUIException;
import org.rubypeople.rdt.internal.ui.RubyUIStatus;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * History stores a list of key, object pairs. The list is bounded at size
 * MAX_HISTORY_SIZE. If the list exceeds this size the eldest element is removed
 * from the list. An element can be added/renewed with a call to <code>accessed(Object)</code>. 
 * 
 * The history can be stored to/loaded from an xml file.
 */
public abstract class History {

	private static final String DEFAULT_ROOT_NODE_NAME= "histroyRootNode"; //$NON-NLS-1$
	private static final String DEFAULT_INFO_NODE_NAME= "infoNode"; //$NON-NLS-1$
	private static final int MAX_HISTORY_SIZE= 60;

	private static RubyUIException createException(Throwable t, String message) {
		return new RubyUIException(RubyUIStatus.createError(IStatus.ERROR, message, t));
	}

	private final Map fHistory;
	private final Hashtable fPositions;
	private final String fFileName;
	private final String fRootNodeName;
	private final String fInfoNodeName;
		
	public History(String fileName, String rootNodeName, String infoNodeName) {
		fHistory= new LinkedHashMap(80, 0.75f, true) {
			private static final long serialVersionUID= 1L;
			protected boolean removeEldestEntry(Map.Entry eldest) {
				return size() > MAX_HISTORY_SIZE;
			}
		};
		fFileName= fileName;
		fRootNodeName= rootNodeName;
		fInfoNodeName= infoNodeName;
		fPositions= new Hashtable(MAX_HISTORY_SIZE);
	}
	
	public History(String fileName) {
		this(fileName, DEFAULT_ROOT_NODE_NAME, DEFAULT_INFO_NODE_NAME);
	}
	
	public synchronized void accessed(Object object) {
		fHistory.put(getKey(object), object);
		rebuildPositions();
	}
	
	public synchronized boolean contains(Object object) {
		return fHistory.containsKey(getKey(object));
	}
	
	public synchronized boolean containsKey(Object key) {
		return fHistory.containsKey(key);
	}
	
	public synchronized boolean isEmpty() {
		return fHistory.isEmpty();
	}
	
	public synchronized Object remove(Object object) {
		Object removed= fHistory.remove(getKey(object));
		rebuildPositions();
		return removed;
	}
	
	public synchronized Object removeKey(Object key) {
		Object removed= fHistory.remove(key);
		rebuildPositions();
		return removed;
	}
	
	/**
	 * Normalized position in history of object denoted by key.
	 * The position is a value between zero and one where zero
	 * means not contained in history and one means newest element
	 * in history. The lower the value the older the element.
	 * 
	 * @param key The key of the object to inspect
	 * @return value in [0.0, 1.0] the lower the older the element
	 */
	public synchronized float getNormalizedPosition(Object key) {
		if (!containsKey(key)) 
			return 0.0f;

		int pos= ((Integer)fPositions.get(key)).intValue() + 1;
		
		//containsKey(key) implies fHistory.size()>0	
		return (float)pos / (float)fHistory.size();
	}
	
	/**
	 * Absolute position of object denoted by key in the
	 * history or -1 if !containsKey(key). The higher the
	 * newer.
	 * 
	 * @param key The key of the object to inspect
	 * @return value between 0 and MAX_HISTORY_SIZE - 1, or -1
	 */
	public synchronized int getPosition(Object key) {
		if (!containsKey(key))
			return -1;
		
		return ((Integer)fPositions.get(key)).intValue();
	}

	public synchronized void load() {
		IPath stateLocation= RubyPlugin.getDefault().getStateLocation().append(fFileName);
		File file= new File(stateLocation.toOSString());
		if (file.exists()) {
			InputStreamReader reader= null;
	        try {
				reader = new InputStreamReader(new FileInputStream(file), "utf-8");//$NON-NLS-1$
				load(new InputSource(reader));
			} catch (IOException e) {
				RubyPlugin.log(e);
			} catch (CoreException e) {
				RubyPlugin.log(e);
			} finally {
				try {
					if (reader != null)
						reader.close();
				} catch (IOException e) {
					RubyPlugin.log(e);
				}
			}
		}
	}
	
	public synchronized void save() {
		IPath stateLocation= RubyPlugin.getDefault().getStateLocation().append(fFileName);
		File file= new File(stateLocation.toOSString());
		OutputStream out= null;
		try {
			out= new FileOutputStream(file); 
			save(out);
		} catch (IOException e) {
			RubyPlugin.log(e);
		} catch (CoreException e) {
			RubyPlugin.log(e);
		} catch (TransformerFactoryConfigurationError e) {
			// The XML library can be misconficgured (e.g. via 
			// -Djava.endorsed.dirs=C:\notExisting\xerces-2_7_1)
			RubyPlugin.log(e);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				RubyPlugin.log(e);
			}
		}
	}
	
	protected Set getKeys() {
		return fHistory.keySet();
	}
	
	protected Collection getValues() {
		return fHistory.values();
	}
	
	/**
	 * Store <code>Object</code> in <code>Element</code>
	 * 
	 * @param object The object to store
	 * @param element The Element to store to
	 */
	protected abstract void setAttributes(Object object, Element element);
	
	/**
	 * Return a new instance of an Object given <code>element</code>
	 * 
	 * @param element The element containing required information to create the Object
	 */
	protected abstract Object createFromElement(Element element);
	
	/**
	 * Get key for object
	 * 
	 * @param object The object to calculate a key for, not null
	 * @return The key for object, not null
	 */
	protected abstract Object getKey(Object object);
	
	private void rebuildPositions() {
		fPositions.clear();
		Collection values= fHistory.values();
		int pos=0;
		for (Iterator iter= values.iterator(); iter.hasNext();) {
			Object element= iter.next();
			fPositions.put(getKey(element), new Integer(pos));
			pos++;
		}
	}

	private void load(InputSource inputSource) throws CoreException {
		Element root;
		try {
			DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			root = parser.parse(inputSource).getDocumentElement();
		} catch (SAXException e) {
			throw createException(e, Messages.format(CorextMessages.History_error_read, fFileName));  
		} catch (ParserConfigurationException e) {
			throw createException(e, Messages.format(CorextMessages.History_error_read, fFileName)); 
		} catch (IOException e) {
			throw createException(e, Messages.format(CorextMessages.History_error_read, fFileName)); 
		}
		
		if (root == null) return;
		if (!root.getNodeName().equalsIgnoreCase(fRootNodeName)) {
			return;
		}
		NodeList list= root.getChildNodes();
		int length= list.getLength();
		for (int i= 0; i < length; ++i) {
			Node node= list.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element type= (Element) node;
				if (type.getNodeName().equalsIgnoreCase(fInfoNodeName)) {
					Object object= createFromElement(type);
					fHistory.put(getKey(object), object);
				}
			}
		}
		rebuildPositions();
	}
	
	private void save(OutputStream stream) throws CoreException {
		try {
			DocumentBuilderFactory factory= DocumentBuilderFactory.newInstance();
			DocumentBuilder builder= factory.newDocumentBuilder();		
			Document document= builder.newDocument();
			
			Element rootElement = document.createElement(fRootNodeName);
			document.appendChild(rootElement);
	
			Iterator values= getValues().iterator();
			while (values.hasNext()) {
				Object object= values.next();
				Element element= document.createElement(fInfoNodeName);
				setAttributes(object, element);
				rootElement.appendChild(element);
			}
			
			Transformer transformer=TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.METHOD, "xml"); //$NON-NLS-1$
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8"); //$NON-NLS-1$
			transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(stream);

			transformer.transform(source, result);
		} catch (TransformerException e) {
			throw createException(e, Messages.format(CorextMessages.History_error_serialize, fFileName));
		} catch (ParserConfigurationException e) {
			throw createException(e, Messages.format(CorextMessages.History_error_serialize, fFileName));
		}
	}

}
