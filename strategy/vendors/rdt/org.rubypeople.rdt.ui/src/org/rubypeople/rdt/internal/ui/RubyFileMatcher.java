/*
 * Author: Markus Barchfeld
 * 
 * Copyright (c) 2004 RubyPeople.
 * 
 * This file is part of the Ruby Development Tools (RDT) plugin for eclipse. RDT
 * is subject to the "Common Public License (CPL) v 1.0". You may not use RDT
 * except in compliance with the License. For further information see
 * org.rubypeople.rdt/rdt.license.
 */

package org.rubypeople.rdt.internal.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.util.ListenerList;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IFileEditorMapping;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.rubypeople.rdt.ui.RubyUI;

/**
 * RubyViewerFilter uses the Editor mappings for recognising and filtering files
 * Both Editor mappings from plugin.xml and creating using the preferences page
 * are considered
 */
public class RubyFileMatcher {

	private static final String RUBY = "ruby";
	private static final String SHEBANG = "#!";
	public static final int PROP_MATCH_CRITERIA = 1;

	private String[] rubyFileExtensions;
	private String[] rubyFileNames;
	private ListenerList propChangeListeners;

	private IPropertyListener propertyListener = new IPropertyListener() {
		public void propertyChanged(Object source, int property) {
			if (property == IEditorRegistry.PROP_CONTENTS
					&& source instanceof IEditorRegistry) {
				createFileExtensions();
				firePropertyChange(PROP_MATCH_CRITERIA);
			}
		}
	};

	/**
	 * The set of files that are generally associated with Ruby, but aren't
	 * editable by the RubyEditor (they're not ruby code)
	 */
	private static Set RUBY_NON_EDITABLE_EXTENSIONS = new HashSet();
	static {
		RUBY_NON_EDITABLE_EXTENSIONS.add("yaml");
		RUBY_NON_EDITABLE_EXTENSIONS.add("yml");
		RUBY_NON_EDITABLE_EXTENSIONS.add("rhtml");
		RUBY_NON_EDITABLE_EXTENSIONS.add("gem");
		RUBY_NON_EDITABLE_EXTENSIONS.add("gemspec");
	}

	public RubyFileMatcher() {
		propChangeListeners = new ListenerList();
		this.createFileExtensions();
		WorkbenchPlugin.getDefault().getEditorRegistry().addPropertyListener(
				propertyListener);
	}

	public void addPropertyChangeListener(IPropertyListener propListener) {
		propChangeListeners.add(propListener);
	}

	private void firePropertyChange(final int type) {
		Object[] array = propChangeListeners.getListeners();
		for (int nX = 0; nX < array.length; nX++) {
			final IPropertyListener l = (IPropertyListener) array[nX];
			Platform.run(new SafeRunnable() {
				public void run() {
					l.propertyChanged(this, type);
				}
			});
		}
	}

	public void createFileExtensions() {
		List extensions = new ArrayList();
		extensions.addAll(createDefaultExtensions());
		List filenames = new ArrayList();
		filenames.addAll(createDefaultFilenames());
		IFileEditorMapping[] mappings = WorkbenchPlugin.getDefault()
				.getEditorRegistry().getFileEditorMappings();
		for (int i = 0; i < mappings.length; i++) {
			IFileEditorMapping mapping = mappings[i];
			IEditorDescriptor[] editors = mapping.getEditors();
			for (int j = 0; j < editors.length; j++) {
				IEditorDescriptor descriptor = editors[j];
				if (descriptor.getId().equals(RubyUI.ID_RUBY_EDITOR)) {
					// a mapping can also use a filename instead of a suffix
					if (mapping.getExtension() != null
							&& mapping.getExtension().length() != 0) {
						extensions.add(mapping.getExtension());
						break;
					}
					if (mapping.getName() != null
							&& mapping.getName().length() != 0) {
						filenames.add(mapping.getName());
						break;
					}
				}

			}
		}
		this.rubyFileExtensions = (String[]) extensions
				.toArray(new String[extensions.size()]);
		this.rubyFileNames = (String[]) filenames.toArray(new String[filenames
				.size()]);
	}

	/**
	 * The default list of full filenames for ruby related files.
	 * 
	 * @return a Collection of filenames associated with ruby projects.
	 */
	private Collection createDefaultFilenames() {
		Set set = new HashSet();
		set.add("Rakefile");
		return set;
	}

	/**
	 * The default list of file extensions for ruby related files.
	 * 
	 * @return a Collection of file extensions associated with ruby files.
	 */
	private Collection createDefaultExtensions() {
		return RUBY_NON_EDITABLE_EXTENSIONS;
	}

	public boolean hasRubyEditorAssociation(IFile file) {
		String fileExtension = file.getFileExtension();
		for (int i = 0; i < rubyFileExtensions.length; i++) {
			if (rubyFileExtensions[i].equalsIgnoreCase(fileExtension)) {
				return true;
			}
		}
		String fileName = file.getName();
		for (int i = 0; i < rubyFileNames.length; i++) {
			if (rubyFileNames[i].equalsIgnoreCase(fileName)) {
				return true;
			}
		}
		return containsRubyShebang(file);
	}

	/**
	 * Read the first line and check for '#!' and 'ruby' If we find them, assume
	 * this is a ruby file.
	 * 
	 * @param file
	 *            The file to check
	 * @return
	 */
	private boolean containsRubyShebang(IFile file) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(file
					.getContents()));
			String firstLine = reader.readLine();
			if (firstLine == null)
				return false;
			if (firstLine.indexOf(SHEBANG) > -1 && firstLine.indexOf(RUBY) > -1)
				return true;
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
}
