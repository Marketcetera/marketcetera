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
package org.rubypeople.rdt.internal.ui.compare;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.IEncodedStreamContentAccessor;
import org.eclipse.compare.IStreamContentAccessor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.swt.graphics.Image;
import org.rubypeople.rdt.core.IMember;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.RubyPluginImages;
import org.rubypeople.rdt.internal.ui.text.IRubyPartitions;
import org.rubypeople.rdt.ui.RubyElementLabels;
import org.rubypeople.rdt.ui.text.RubyTextTools;

class RubyCompareUtilities {
	
	private static final char IMPORTDECLARATION= '#';
	private static final char IMPORT_CONTAINER= '<';
	private static final char FIELD= '^';
	private static final char METHOD= '~';
	private static final char SCRIPT= '{';
	private static final char TYPE= '[';
			
	static String getString(ResourceBundle bundle, String key, String dfltValue) {
		
		if (bundle != null) {
			try {
				return bundle.getString(key);
			} catch (MissingResourceException x) {
				// NeedWork
			}
		}
		return dfltValue;
	}
	
	static String getString(ResourceBundle bundle, String key) {
		return getString(bundle, key, key);
	}
	
	static int getInteger(ResourceBundle bundle, String key, int dfltValue) {
		
		if (bundle != null) {
			try {
				String s= bundle.getString(key);
				if (s != null)
					return Integer.parseInt(s);
			} catch (NumberFormatException x) {
				// NeedWork
			} catch (MissingResourceException x) {
				// NeedWork
			}
		}
		return dfltValue;
	}

	static ImageDescriptor getImageDescriptor(int type) {
		switch (type) {			
		case IRubyElement.METHOD:
			return getImageDescriptor("obj16/compare_method.gif"); //$NON-NLS-1$			
		case IRubyElement.FIELD:
			return getImageDescriptor("obj16/compare_field.gif"); //$NON-NLS-1$
		case IRubyElement.IMPORT_DECLARATION:
			return RubyPluginImages.DESC_OBJS_IMPDECL;
		case IRubyElement.IMPORT_CONTAINER:
			return RubyPluginImages.DESC_OBJS_IMPCONT;
		case IRubyElement.SCRIPT:
			return RubyPluginImages.DESC_OBJS_SCRIPT;
		}
		return ImageDescriptor.getMissingImageDescriptor();
	}
	
	static ImageDescriptor getTypeImageDescriptor(boolean isClass) {
		if (isClass)
			return RubyPluginImages.DESC_OBJS_CLASS;
		return RubyPluginImages.DESC_OBJS_MODULE;
	}

	static ImageDescriptor getImageDescriptor(IMember element) {
		int t= element.getElementType();
		if (t == IRubyElement.TYPE) {
			IType type= (IType) element;
			return getTypeImageDescriptor(type.isClass());
		}
		return getImageDescriptor(t);
	}
	
	/**
	 * Returns a name for the given Ruby element that uses the same conventions
	 * as the RubyNode name of a corresponding element.
	 */
	static String getRubyElementID(IRubyElement je) {
				
		StringBuffer sb= new StringBuffer();
		
		switch (je.getElementType()) {
		case IRubyElement.SCRIPT:
			sb.append(SCRIPT);
			break;
		case IRubyElement.TYPE:
			sb.append(TYPE);
			sb.append(je.getElementName());
			break;
		case IRubyElement.FIELD:
			sb.append(FIELD);
			sb.append(je.getElementName());
			break;
		case IRubyElement.METHOD:
			sb.append(METHOD);
			sb.append(RubyElementLabels.getElementLabel(je, RubyElementLabels.M_PARAMETER_NAMES));
			break;
		case IRubyElement.IMPORT_CONTAINER:
			sb.append(IMPORT_CONTAINER);
			break;
		case IRubyElement.IMPORT_DECLARATION:
			sb.append(IMPORTDECLARATION);
			sb.append(je.getElementName());			
			break;
		default:
			return null;
		}
		return sb.toString();
	}
	
	/**
	 * Returns a name which identifies the given typed name.
	 * The type is encoded as a single character at the beginning of the string.
	 */
	static String buildID(int type, String name) {
		StringBuffer sb= new StringBuffer();
		switch (type) {
		case RubyNode.SCRIPT:
			sb.append(SCRIPT);
			break;
		case RubyNode.CLASS:
		case RubyNode.MODULE:
			sb.append(TYPE);
			sb.append(name);
			break;
		case RubyNode.FIELD:
			sb.append(FIELD);
			sb.append(name);
			break;
		case RubyNode.CONSTRUCTOR:
		case RubyNode.METHOD:
			sb.append(METHOD);
			sb.append(name);
			break;
		case RubyNode.IMPORT:
			sb.append(IMPORTDECLARATION);
			sb.append(name);
			break;
		case RubyNode.IMPORT_CONTAINER:
			sb.append(IMPORT_CONTAINER);
			break;
		default:
			Assert.isTrue(false);
			break;
		}
		return sb.toString();
	}

	static ImageDescriptor getImageDescriptor(String relativePath) {
		IPath path= RubyPluginImages.ICONS_PATH.append(relativePath);
		return RubyPluginImages.createImageDescriptor(RubyPlugin.getDefault().getBundle(), path, true);
	}
	
	static boolean getBoolean(CompareConfiguration cc, String key, boolean dflt) {
		if (cc != null) {
			Object value= cc.getProperty(key);
			if (value instanceof Boolean)
				return ((Boolean) value).booleanValue();
		}
		return dflt;
	}

	static Image getImage(IMember member) {
		ImageDescriptor id= getImageDescriptor(member);
		return id.createImage();
	}

	static RubyTextTools getRubyTextTools() {
		RubyPlugin plugin= RubyPlugin.getDefault();
		if (plugin != null)
			return plugin.getRubyTextTools();
		return null;
	}
	
	static IDocumentPartitioner createRubyPartitioner() {
		RubyTextTools tools= getRubyTextTools();
		if (tools != null)
			return tools.createDocumentPartitioner();
		return null;
	}
	
	static void setupDocument(IDocument document) {
		RubyTextTools tools= getRubyTextTools();
		if (tools != null)
			tools.setupRubyDocumentPartitioner(document, IRubyPartitions.RUBY_PARTITIONING);
	}
	
	/**
	 * Reads the contents of the given input stream into a string.
	 * The function assumes that the input stream uses the platform's default encoding
	 * (<code>ResourcesPlugin.getEncoding()</code>).
	 * Returns null if an error occurred.
	 */
	private static String readString(InputStream is, String encoding) {
		if (is == null)
			return null;
		BufferedReader reader= null;
		try {
			StringBuffer buffer= new StringBuffer();
			char[] part= new char[2048];
			int read= 0;
			reader= new BufferedReader(new InputStreamReader(is, encoding));

			while ((read= reader.read(part)) != -1)
				buffer.append(part, 0, read);
			
			return buffer.toString();
			
		} catch (IOException ex) {
			// NeedWork
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ex) {
					// silently ignored
				}
			}
		}
		return null;
	}
	
	public static String readString(IStreamContentAccessor sa) throws CoreException {
		InputStream is= sa.getContents();
		if (is != null) {
			String encoding= null;
			if (sa instanceof IEncodedStreamContentAccessor) {
				try {
					encoding= ((IEncodedStreamContentAccessor) sa).getCharset();
				} catch (Exception e) {
				}
			}
			if (encoding == null)
				encoding= ResourcesPlugin.getEncoding();
			return readString(is, encoding);
		}
		return null;
	}

	/**
	 * Returns the contents of the given string as an array of bytes 
	 * in the platform's default encoding.
	 */
	static byte[] getBytes(String s, String encoding) {
		try {
			return s.getBytes(encoding);
		} catch (UnsupportedEncodingException e) {
			return s.getBytes();
		}
	}
	
	/**
	 * Breaks the contents of the given input stream into an array of strings.
	 * The function assumes that the input stream uses the platform's default encoding
	 * (<code>ResourcesPlugin.getEncoding()</code>).
	 * Returns null if an error occurred.
	 */
	static String[] readLines(InputStream is2, String encoding) {
		
		BufferedReader reader= null;
		try {
			reader= new BufferedReader(new InputStreamReader(is2, encoding));
			StringBuffer sb= new StringBuffer();
			List list= new ArrayList();
			while (true) {
				int c= reader.read();
				if (c == -1)
					break;
				sb.append((char)c);
				if (c == '\r') {	// single CR or a CR followed by LF
					c= reader.read();
					if (c == -1)
						break;
					sb.append((char)c);
					if (c == '\n') {
						list.add(sb.toString());
						sb= new StringBuffer();
					}
				} else if (c == '\n') {	// a single LF
					list.add(sb.toString());
					sb= new StringBuffer();
				}
			}
			if (sb.length() > 0)
				list.add(sb.toString());
			return (String[]) list.toArray(new String[list.size()]);

		} catch (IOException ex) {
			return null;

		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ex) {
					// silently ignored
				}
			}
		}
	}
	
	/*
	 * Initialize the given Action from a ResourceBundle.
	 */
	static void initAction(IAction a, ResourceBundle bundle, String prefix) {
		
		String labelKey= "label"; //$NON-NLS-1$
		String tooltipKey= "tooltip"; //$NON-NLS-1$
		String imageKey= "image"; //$NON-NLS-1$
		String descriptionKey= "description"; //$NON-NLS-1$
		
		if (prefix != null && prefix.length() > 0) {
			labelKey= prefix + labelKey;
			tooltipKey= prefix + tooltipKey;
			imageKey= prefix + imageKey;
			descriptionKey= prefix + descriptionKey;
		}
		
		a.setText(getString(bundle, labelKey, labelKey));
		a.setToolTipText(getString(bundle, tooltipKey, null));
		a.setDescription(getString(bundle, descriptionKey, null));
		
		String relPath= getString(bundle, imageKey, null);
		if (relPath != null && relPath.trim().length() > 0) {
			
			String dPath;
			String ePath;
			
			if (relPath.indexOf("/") >= 0) { //$NON-NLS-1$
				String path= relPath.substring(1);
				dPath= 'd' + path;
				ePath= 'e' + path;
			} else {
				dPath= "dlcl16/" + relPath; //$NON-NLS-1$
				ePath= "elcl16/" + relPath; //$NON-NLS-1$
			}
			
			ImageDescriptor id= RubyCompareUtilities.getImageDescriptor(dPath);	// we set the disabled image first (see PR 1GDDE87)
			if (id != null)
				a.setDisabledImageDescriptor(id);
			id= RubyCompareUtilities.getImageDescriptor(ePath);
			if (id != null) {
				a.setImageDescriptor(id);
				a.setHoverImageDescriptor(id);
			}
		}
	}
	
	static void initToggleAction(IAction a, ResourceBundle bundle, String prefix, boolean checked) {

		String tooltip= null;
		if (checked)
			tooltip= getString(bundle, prefix + "tooltip.checked", null);	//$NON-NLS-1$
		else
			tooltip= getString(bundle, prefix + "tooltip.unchecked", null);	//$NON-NLS-1$
		if (tooltip == null)
			tooltip= getString(bundle, prefix + "tooltip", null);	//$NON-NLS-1$
		
		if (tooltip != null)
			a.setToolTipText(tooltip);
			
		String description= null;
		if (checked)
			description= getString(bundle, prefix + "description.checked", null);	//$NON-NLS-1$
		else
			description= getString(bundle, prefix + "description.unchecked", null);	//$NON-NLS-1$
		if (description == null)
			description= getString(bundle, prefix + "description", null);	//$NON-NLS-1$
		
		if (description != null)
			a.setDescription(description);
	}
}
