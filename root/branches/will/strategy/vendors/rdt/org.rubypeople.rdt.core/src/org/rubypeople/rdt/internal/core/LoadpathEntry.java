package org.rubypeople.rdt.internal.core;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.AssertionFailedException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.rubypeople.rdt.core.ILoadpathAttribute;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.IRubyModelStatus;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.internal.core.util.CharOperation;
import org.rubypeople.rdt.internal.core.util.Messages;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class LoadpathEntry implements ILoadpathEntry {
	
	public static final String TAG_LOADPATH = "loadpath"; //$NON-NLS-1$
	public static final String TAG_LOADPATHENTRY = "pathentry"; //$NON-NLS-1$
	public static final String TAG_KIND = "type"; //$NON-NLS-1$
	public static final String TAG_PATH = "path"; //$NON-NLS-1$
	public static final String TAG_EXPORTED = "exported"; //$NON-NLS-1$
	public static final String TAG_INCLUDING = "including"; //$NON-NLS-1$
	public static final String TAG_EXCLUDING = "excluding"; //$NON-NLS-1$
	public static final String TAG_ATTRIBUTES = "attributes"; //$NON-NLS-1$
	public static final String TAG_ATTRIBUTE = "attribute"; //$NON-NLS-1$
	public static final String TAG_ATTRIBUTE_NAME = "name"; //$NON-NLS-1$
	public static final String TAG_ATTRIBUTE_VALUE = "value"; //$NON-NLS-1$

	static class UnknownXmlElements {
		String[] attributes;
		ArrayList children;
	}
	
	private static final String TYPE_PROJECT = "project";

	private String rootID;
	private int entryKind;
	private IPath path;
	/**
	 * Patterns allowing to include/exclude portions of the resource tree
	 * denoted by this entry path.
	 */
	private IPath[] inclusionPatterns;
	private char[][] fullInclusionPatternChars;
	private IPath[] exclusionPatterns;
	private char[][] fullExclusionPatternChars;
	private final static char[][] UNINIT_PATTERNS = new char[][] { "Non-initialized yet".toCharArray()}; //$NON-NLS-1$

	/*
	 * Default extra attributes
	 */
	public final static ILoadpathAttribute[] NO_EXTRA_ATTRIBUTES = {};
	/*
	 * Default inclusion pattern set
	 */
	public final static IPath[] INCLUDE_ALL = {};

	/*
	 * Default exclusion pattern set
	 */
	public final static IPath[] EXCLUDE_NONE = {};

	private IProject project;

	/**
	 * The export flag
	 */
	private boolean isExported;
	
	/*
	 * The extra attributes
	 */
	ILoadpathAttribute[] extraAttributes;

	public LoadpathEntry(IProject project) {
		this(ILoadpathEntry.CPE_PROJECT, project.getFullPath(), INCLUDE_ALL, EXCLUDE_NONE, NO_EXTRA_ATTRIBUTES, true);
		this.project = project;
	}

	public LoadpathEntry(int entryKind, IPath path, IPath[] inclusionPatterns, IPath[] exclusionPatterns, ILoadpathAttribute[] extraAttributes, boolean isExported) {
		this.path = path;
		this.entryKind = entryKind;
		this.inclusionPatterns = inclusionPatterns;
		this.exclusionPatterns = exclusionPatterns;
		this.extraAttributes = extraAttributes;
		if (inclusionPatterns != INCLUDE_ALL && inclusionPatterns.length > 0) {
			this.fullInclusionPatternChars = UNINIT_PATTERNS;
		}
		if (exclusionPatterns.length > 0) {
			this.fullExclusionPatternChars = UNINIT_PATTERNS;
		}
		this.isExported = isExported;
	}

	public IPath getPath() {
		return path;
	}

	// FIXME We shouldn't need this!
	public IProject getProject() {
		return this.project;
	}

	public int getEntryKind() {
		return this.entryKind;
	}

	/**
	 * Returns a <code>String</code> for the kind of a class path entry.
	 */
	static String kindToString(int kind) {
		switch (kind) {
		case ILoadpathEntry.CPE_PROJECT:
			return TYPE_PROJECT; //$NON-NLS-1$
		case ILoadpathEntry.CPE_SOURCE:
			return "src"; //$NON-NLS-1$
		case ILoadpathEntry.CPE_LIBRARY:
			return "lib"; //$NON-NLS-1$
		case ILoadpathEntry.CPE_VARIABLE:
			return "var"; //$NON-NLS-1$
		case ILoadpathEntry.CPE_CONTAINER:
			return "con"; //$NON-NLS-1$
		default:
			return "unknown"; //$NON-NLS-1$
		}
	}

	public String toXML() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<pathentry type=\"");
		buffer.append(LoadpathEntry.kindToString(entryKind) + "\" ");
		buffer.append("path=\"" + getPath() + "\"/>");
		return buffer.toString();
	}

	/**
	 * Answers an ID which is used to distinguish entries during package
	 * fragment root computations
	 */
	public String rootID() {
		if (this.rootID == null) {
			switch (this.entryKind) {
			case ILoadpathEntry.CPE_LIBRARY:
				this.rootID = "[LIB]" + this.path; //$NON-NLS-1$
				break;
			case ILoadpathEntry.CPE_PROJECT:
				this.rootID = "[PRJ]" + this.path; //$NON-NLS-1$
				break;
			case ILoadpathEntry.CPE_SOURCE:
				this.rootID = "[SRC]" + this.path; //$NON-NLS-1$
				break;
			case ILoadpathEntry.CPE_VARIABLE:
				this.rootID = "[VAR]" + this.path; //$NON-NLS-1$
				break;
			case ILoadpathEntry.CPE_CONTAINER:
				this.rootID = "[CON]" + this.path; //$NON-NLS-1$
				break;
			default:
				this.rootID = ""; //$NON-NLS-1$
				break;
			}
		}
		return this.rootID;
	}

	/*
	 * Returns a char based representation of the exclusions patterns full path.
	 */
	public char[][] fullExclusionPatternChars() {

		if (this.fullExclusionPatternChars == UNINIT_PATTERNS) {
			int length = this.exclusionPatterns.length;
			this.fullExclusionPatternChars = new char[length][];
			IPath prefixPath = this.path.removeTrailingSeparator();
			for (int i = 0; i < length; i++) {
				this.fullExclusionPatternChars[i] = prefixPath.append(this.exclusionPatterns[i]).toString().toCharArray();
			}
		}
		return this.fullExclusionPatternChars;
	}

	/*
	 * Returns a char based representation of the exclusions patterns full path.
	 */
	public char[][] fullInclusionPatternChars() {

		if (this.fullInclusionPatternChars == UNINIT_PATTERNS) {
			int length = this.inclusionPatterns.length;
			this.fullInclusionPatternChars = new char[length][];
			IPath prefixPath = this.path.removeTrailingSeparator();
			for (int i = 0; i < length; i++) {
				this.fullInclusionPatternChars[i] = prefixPath.append(this.inclusionPatterns[i]).toString().toCharArray();
			}
		}
		return this.fullInclusionPatternChars;
	}

	/**
	 * @see ILoadpathEntry#isExported()
	 */
	public boolean isExported() {
		return this.isExported;
	}

	/* (non-Javadoc)
	 * @see org.rubypeople.rdt.core.ILoadpathEntry#getExclusionPatterns()
	 */
	public IPath[] getExclusionPatterns() {
		return exclusionPatterns;
	}

	/* (non-Javadoc)
	 * @see org.rubypeople.rdt.core.ILoadpathEntry#getInclusionPatterns()
	 */
	public IPath[] getInclusionPatterns() {
		return inclusionPatterns;
	}

	public LoadpathEntry combineWith(LoadpathEntry referringEntry) {
		if (referringEntry == null) return this;
		if (referringEntry.isExported() ) {
			return new LoadpathEntry(
								getEntryKind(), 
								getPath(),
								this.inclusionPatterns, 
								this.exclusionPatterns, 
								this.extraAttributes,
								referringEntry.isExported() || this.isExported); // duplicate container entry for tagging it as exported
		}
		// no need to clone
		return this;
	}

	public static ILoadpathEntry elementDecode(Element element, IRubyProject project, Map unknownElements) {
	
		IPath projectPath = project.getProject().getFullPath();
		NamedNodeMap attributes = element.getAttributes();
		NodeList children = element.getChildNodes();
		boolean[] foundChildren = new boolean[children.getLength()];
		String kindAttr = removeAttribute(TAG_KIND, attributes);
		String pathAttr = removeAttribute(TAG_PATH, attributes);

		// ensure path is absolute
		IPath path = new Path(pathAttr); 		
		int kind = kindFromString(kindAttr);
		if (kind != ILoadpathEntry.CPE_VARIABLE && kind != ILoadpathEntry.CPE_CONTAINER && !path.isAbsolute()) {
			path = projectPath.append(path);
		}
		
		// exported flag (optional)
		boolean isExported = removeAttribute(TAG_EXPORTED, attributes).equals("true"); //$NON-NLS-1$

		// inclusion patterns (optional)
		IPath[] inclusionPatterns = decodePatterns(attributes, TAG_INCLUDING);
		if (inclusionPatterns == null) inclusionPatterns = INCLUDE_ALL;
		
		// exclusion patterns (optional)
		IPath[] exclusionPatterns = decodePatterns(attributes, TAG_EXCLUDING);
		if (exclusionPatterns == null) exclusionPatterns = EXCLUDE_NONE;

//		 extra attributes (optional)
		NodeList attributeList = getChildAttributes(TAG_ATTRIBUTES, children, foundChildren);
		ILoadpathAttribute[] extraAttributes = decodeExtraAttributes(attributeList);
		
		String[] unknownAttributes = null;
		ArrayList unknownChildren = null;

		if (unknownElements != null) {
			// unknown attributes
			int unknownAttributeLength = attributes.getLength();
			if (unknownAttributeLength != 0) {
				unknownAttributes = new String[unknownAttributeLength*2];
				for (int i = 0; i < unknownAttributeLength; i++) {
					Node attribute = attributes.item(i);
					unknownAttributes[i*2] = attribute.getNodeName();
					unknownAttributes[i*2 + 1] = attribute.getNodeValue();
				}
			}
			
			// unknown children
			for (int i = 0, length = foundChildren.length; i < length; i++) {
				if (!foundChildren[i]) {
					Node node = children.item(i);
					if (node.getNodeType() != Node.ELEMENT_NODE) continue;
					if (unknownChildren == null)
						unknownChildren = new ArrayList();
					StringBuffer buffer = new StringBuffer();
					decodeUnknownNode(node, buffer, project);
					unknownChildren.add(buffer.toString());
				}
			}
		}
		
		// recreate the CP entry
		ILoadpathEntry entry = null;
		switch (kind) {

			case ILoadpathEntry.CPE_PROJECT :
				entry = new LoadpathEntry(
				ILoadpathEntry.CPE_PROJECT,
				path,
				LoadpathEntry.INCLUDE_ALL, // inclusion patterns
				LoadpathEntry.EXCLUDE_NONE, // exclusion patterns
				extraAttributes,
				isExported);
				break;				
			case ILoadpathEntry.CPE_LIBRARY :
				entry = RubyCore.newLibraryEntry(
												path,
												extraAttributes,
												isExported);
				break;
			case ILoadpathEntry.CPE_SOURCE :
				// must be an entry in this project or specify another project
				String projSegment = path.segment(0);
				if (projSegment != null && projSegment.equals(project.getElementName())) { // this project
					entry = RubyCore.newSourceEntry(path, inclusionPatterns, exclusionPatterns, extraAttributes);
				} else { 
					if (path.segmentCount() == 1) {
						// another project
						entry = RubyCore.newProjectEntry(
												path, 
												extraAttributes,
												isExported);
					} else {
						// an invalid source folder
						entry = RubyCore.newSourceEntry(path, inclusionPatterns, exclusionPatterns, extraAttributes);
					}
				}
				break;
			case ILoadpathEntry.CPE_VARIABLE :
				entry = RubyCore.newVariableEntry(
						path,
						extraAttributes,
						isExported);
				break;
			case ILoadpathEntry.CPE_CONTAINER :
				entry = RubyCore.newContainerEntry(
						path,
						extraAttributes,
						isExported);
				break;
			default :
				throw new AssertionFailedException(Messages.bind(Messages.classpath_unknownKind, kindAttr)); 
		}
		
		if (unknownAttributes != null || unknownChildren != null) {
			UnknownXmlElements unknownXmlElements = new UnknownXmlElements();
			unknownXmlElements.attributes = unknownAttributes;
			unknownXmlElements.children = unknownChildren;
			unknownElements.put(path, unknownXmlElements);
		}
		
		return entry;
	}
	
	public static NodeList getChildAttributes(String childName, NodeList children, boolean[] foundChildren) {
		for (int i = 0, length = foundChildren.length; i < length; i++) {
			Node node = children.item(i);
			if (childName.equals(node.getNodeName())) {
				foundChildren[i] = true;
				return node.getChildNodes();
			}
		}
		return null;
	}
	
	static ILoadpathAttribute[] decodeExtraAttributes(NodeList attributes) {
		if (attributes == null) return NO_EXTRA_ATTRIBUTES;
		int length = attributes.getLength();
		if (length == 0) return NO_EXTRA_ATTRIBUTES;
		ILoadpathAttribute[] result = new ILoadpathAttribute[length];
		int index = 0;
		for (int i = 0; i < length; ++i) {
			Node node = attributes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element attribute = (Element)node;
				String name = attribute.getAttribute(TAG_ATTRIBUTE_NAME);
				if (name == null) continue;
				String value = attribute.getAttribute(TAG_ATTRIBUTE_VALUE);
				if (value == null) continue;
				result[index++] = new LoadpathAttribute(name, value);
			}
		}
		if (index != length)
			System.arraycopy(result, 0, result = new ILoadpathAttribute[index], 0, index);
		return result;
	}
	
	private static void decodeUnknownNode(Node node, StringBuffer buffer, IRubyProject project) {
		ByteArrayOutputStream s = new ByteArrayOutputStream();
		OutputStreamWriter writer;
		try {
			writer = new OutputStreamWriter(s, "UTF8"); //$NON-NLS-1$
			XMLWriter xmlWriter = new XMLWriter(writer, project, false/*don't print XML version*/);
			decodeUnknownNode(node, xmlWriter, true/*insert new line*/);
			xmlWriter.flush();
			xmlWriter.close();
			buffer.append(s.toString("UTF8")); //$NON-NLS-1$
		} catch (UnsupportedEncodingException e) {
			// ignore (UTF8 is always supported)
		} 
	}
	
	private static void decodeUnknownNode(Node node, XMLWriter xmlWriter, boolean insertNewLine) {
		switch (node.getNodeType()) {
		case Node.ELEMENT_NODE:
			NamedNodeMap attributes;
			HashMap parameters = null;
			if ((attributes = node.getAttributes()) != null) {
				int length = attributes.getLength();
				if (length > 0) {
					parameters = new HashMap();
					for (int i = 0; i < length; i++) {
						Node attribute = attributes.item(i);
						parameters.put(attribute.getNodeName(), attribute.getNodeValue());
					}
				}
			}
			NodeList children = node.getChildNodes();
			int childrenLength = children.getLength();
			String nodeName = node.getNodeName();
			xmlWriter.printTag(nodeName, parameters, false/*don't insert tab*/, false/*don't insert new line*/, childrenLength == 0/*close tag if no children*/);
			if (childrenLength > 0) {
				for (int i = 0; i < childrenLength; i++) {
					decodeUnknownNode(children.item(i), xmlWriter, false/*don't insert new line*/);
				}
				xmlWriter.endTag(nodeName, false/*don't insert tab*/, insertNewLine);
			}
			break;
		case Node.TEXT_NODE:
			String data = ((Text) node).getData();
			xmlWriter.printString(data, false/*don't insert tab*/, false/*don't insert new line*/);
			break;
		}
	}
	
	/**
	 * Decode some element tag containing a sequence of patterns into IPath[]
	 */
	private static IPath[] decodePatterns(NamedNodeMap nodeMap, String tag) {
		String sequence = removeAttribute(tag, nodeMap);
		if (!sequence.equals("")) { //$NON-NLS-1$ 
			char[][] patterns = CharOperation.splitOn('|', sequence.toCharArray());
			int patternCount;
			if ((patternCount = patterns.length) > 0) {
				IPath[] paths = new IPath[patternCount];
				int index = 0;
				for (int j = 0; j < patternCount; j++) {
					char[] pattern = patterns[j];
					if (pattern.length == 0) continue; // see https://bugs.eclipse.org/bugs/show_bug.cgi?id=105581
					paths[index++] = new Path(new String(pattern));
				}
				if (index < patternCount)
					System.arraycopy(paths, 0, paths = new IPath[index], 0, index);
				return paths;
			}
		}
		return null;
	}
	
	/**
	 * Returns the kind of a <code>SourceFoldertRoot</code> from its <code>String</code> form.
	 */
	static int kindFromString(String kindStr) {

		if (kindStr.equalsIgnoreCase("project")) //$NON-NLS-1$
			return ILoadpathEntry.CPE_PROJECT;
		if (kindStr.equalsIgnoreCase("var")) //$NON-NLS-1$
			return ILoadpathEntry.CPE_VARIABLE;
		if (kindStr.equalsIgnoreCase("con")) //$NON-NLS-1$
			return ILoadpathEntry.CPE_CONTAINER;
		if (kindStr.equalsIgnoreCase("src")) //$NON-NLS-1$
			return ILoadpathEntry.CPE_SOURCE;
		if (kindStr.equalsIgnoreCase("lib")) //$NON-NLS-1$
			return ILoadpathEntry.CPE_LIBRARY;
		return -1;
	}
	
	private static String removeAttribute(String nodeName, NamedNodeMap nodeMap) {
		Node node = removeNode(nodeName, nodeMap);
		if (node == null)
			return ""; // //$NON-NLS-1$
		return node.getNodeValue();
	}
	
	private static Node removeNode(String nodeName, NamedNodeMap nodeMap) {
		try {
			return nodeMap.removeNamedItem(nodeName);
		} catch (DOMException e) {
			if (e.code != DOMException.NOT_FOUND_ERR)
				throw e;
			return null;
		}
	}

	public boolean isOptional() {
		for (int i = 0, length = this.extraAttributes.length; i < length; i++) {
			ILoadpathAttribute attribute = this.extraAttributes[i];
			if (ILoadpathAttribute.OPTIONAL.equals(attribute.getName()) && "true".equals(attribute.getValue())) //$NON-NLS-1$
				return true;
		}
		return false;
	}

	public static IRubyModelStatus validateLoadpathEntry(IRubyProject project,
			ILoadpathEntry rawEntry, boolean b, boolean c) {
		// TODO Actually do some checking of the entry
		return RubyModelStatus.VERIFIED_OK;	
	}

	public static IRubyModelStatus validateLoadpath(IRubyProject project,
			ILoadpathEntry[] resolvedPath, IPath projectOutputLocation) {
		// FIXME Remove outputLocation
		// TODO Actually do some checking of the loadpath
		return RubyModelStatus.VERIFIED_OK;	
	}

	/**
	 * Returns the XML encoding of the class path.
	 */
	public void elementEncode(XMLWriter writer, IPath projectPath, boolean indent, boolean newLine, Map unknownElements) {
		HashMap parameters = new HashMap();
		
		parameters.put(TAG_KIND, LoadpathEntry.kindToString(this.entryKind));
		
		IPath xmlPath = this.path;
		if (this.entryKind != ILoadpathEntry.CPE_VARIABLE && this.entryKind != ILoadpathEntry.CPE_CONTAINER) {
			// translate to project relative from absolute (unless a device path)
			if (xmlPath.isAbsolute()) {
				if (projectPath != null && projectPath.isPrefixOf(xmlPath)) {
					if (xmlPath.segment(0).equals(projectPath.segment(0))) {
						xmlPath = xmlPath.removeFirstSegments(1);
						xmlPath = xmlPath.makeRelative();
					} else {
						xmlPath = xmlPath.makeAbsolute();
					}
				}
			}
		}
		parameters.put(TAG_PATH, String.valueOf(xmlPath));
		
		if (this.isExported) {
			parameters.put(TAG_EXPORTED, "true");//$NON-NLS-1$
		}
		encodePatterns(this.inclusionPatterns, TAG_INCLUDING, parameters);
		encodePatterns(this.exclusionPatterns, TAG_EXCLUDING, parameters);
		
		// unknown attributes
		UnknownXmlElements unknownXmlElements = unknownElements == null ? null : (UnknownXmlElements) unknownElements.get(this.path);
		String[] unknownAttributes;
		if (unknownXmlElements != null && (unknownAttributes = unknownXmlElements.attributes) != null)
			for (int i = 0, length = unknownAttributes.length; i < length; i+=2) {
				String tagName = unknownAttributes[i];
				String tagValue = unknownAttributes[i+1];
				parameters.put(tagName, tagValue);
			}
		
		boolean hasExtraAttributes = this.extraAttributes.length != 0;
		ArrayList unknownChildren = unknownXmlElements != null ? unknownXmlElements.children : null;
		boolean hasUnknownChildren = unknownChildren != null;
		writer.printTag(
			TAG_LOADPATHENTRY, 
			parameters, 
			indent, 
			newLine, 
			!hasUnknownChildren/*close tag if no unknown children*/);
		if (hasExtraAttributes)
			encodeExtraAttributes(writer, indent, newLine);
	
		if (hasUnknownChildren) {
			encodeUnknownChildren(writer, indent, newLine, unknownChildren);
		if (hasExtraAttributes || hasUnknownChildren)
			writer.endTag(TAG_LOADPATHENTRY, indent, true/*insert new line*/);
		}
	}
	
	void encodeExtraAttributes(XMLWriter writer, boolean indent, boolean newLine) {
		writer.startTag(TAG_ATTRIBUTES, indent);
		for (int i = 0; i < this.extraAttributes.length; i++) {
			ILoadpathAttribute attribute = this.extraAttributes[i];
			HashMap parameters = new HashMap();
	    	parameters.put(TAG_ATTRIBUTE_NAME, attribute.getName());
			parameters.put(TAG_ATTRIBUTE_VALUE, attribute.getValue());
			writer.printTag(TAG_ATTRIBUTE, parameters, indent, newLine, true);
		}
		writer.endTag(TAG_ATTRIBUTES, indent, true/*insert new line*/);
	}
	
	/**
	 * Encode some patterns into XML parameter tag
	 */
	private static void encodePatterns(IPath[] patterns, String tag, Map parameters) {
		if (patterns != null && patterns.length > 0) {
			StringBuffer rule = new StringBuffer(10);
			for (int i = 0, max = patterns.length; i < max; i++){
				if (i > 0) rule.append('|');
				rule.append(patterns[i]);
			}
			parameters.put(tag, String.valueOf(rule));
		}
	}
	
	private void encodeUnknownChildren(XMLWriter writer, boolean indent, boolean newLine, ArrayList unknownChildren) {
		for (int i = 0, length = unknownChildren.size(); i < length; i++) {
			String child = (String) unknownChildren.get(i);
			writer.printString(child, indent, false/*don't insert new line*/);
		}
	}

	public ILoadpathAttribute[] getExtraAttributes() {
		return extraAttributes;
	}
	
	/**
	 * Returns true if the given object is a classpath entry
	 * with equivalent attributes.
	 */
	public boolean equals(Object object) {
		if (this == object)
			return true;
		if (object instanceof LoadpathEntry) {
			LoadpathEntry otherEntry = (LoadpathEntry) object;

			if (this.entryKind != otherEntry.getEntryKind())
				return false;

			if (this.isExported != otherEntry.isExported())
				return false;

			if (!this.path.equals(otherEntry.getPath()))
				return false;

			if (!equalPatterns(this.inclusionPatterns, otherEntry.getInclusionPatterns()))
				return false;
			if (!equalPatterns(this.exclusionPatterns, otherEntry.getExclusionPatterns()))
				return false;
			if (!equalAttributes(this.extraAttributes, otherEntry.getExtraAttributes()))
				return false;
			return true;
		} else {
			return false;
		}
	}
	
	private static boolean equalAttributes(ILoadpathAttribute[] firstAttributes, ILoadpathAttribute[] secondAttributes) {
		if (firstAttributes != secondAttributes){
		    if (firstAttributes == null) return false;
			int length = firstAttributes.length;
			if (secondAttributes == null || secondAttributes.length != length) 
				return false;
			for (int i = 0; i < length; i++) {
				if (!firstAttributes[i].equals(secondAttributes[i]))
					return false;
			}
		}
		return true;
	}
	
	private static boolean equalPatterns(IPath[] firstPatterns, IPath[] secondPatterns) {
		if (firstPatterns != secondPatterns){
		    if (firstPatterns == null) return false;
			int length = firstPatterns.length;
			if (secondPatterns == null || secondPatterns.length != length) 
				return false;
			for (int i = 0; i < length; i++) {
				// compare toStrings instead of IPaths 
				// since IPath.equals is specified to ignore trailing separators
				if (!firstPatterns[i].toString().equals(secondPatterns[i].toString()))
					return false;
			}
		}
		return true;
	}
	
	/**
	 * Returns the hash code for this classpath entry
	 */
	public int hashCode() {
		return this.path.hashCode();
	}
	
	public String toString() {
		return getPath().toPortableString();
	}
}
