/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.corext.util;

import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.rubypeople.rdt.core.IRubyModel;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;


public class TypeInfoFactory {
	
	private String[] fProjects;
	private TypeInfo fLast;
	private char[] fBuffer;

//	private static final String CLASS= "class"; //$NON-NLS-1$
	private static final String RUBY= "rb"; //$NON-NLS-1$
	
	public TypeInfoFactory() {
		super();
		fProjects= getProjectList();
		fLast= null;
		fBuffer= new char[512];
	}

	public TypeInfo create(char[] packageName, char[] typeName, char[][] enclosingName, boolean isModule, String path) {
		path = new Path(path).toPortableString();
		String pn= getPackageName(packageName);
		String tn= new String(typeName);
		TypeInfo result= null;
		String project = getProject(path);
		if (project != null) {
			result = createIFileTypeInfo(pn, tn, enclosingName, isModule, path,	getIFileTypeInfo(fLast), project);
		} else {
			result = createExternalFileTypeInfo(pn, tn, enclosingName, isModule, path);
		}
		if (result == null) {
			result= new UnresolvableTypeInfo(pn, tn, enclosingName, isModule, path);
		} else {
			fLast= result;
		}
		return result;
	}
	
	private TypeInfo createExternalFileTypeInfo(String packageName, String typeName, char[][] enclosingName, boolean isModule, String path) {
		return new ExternalFileTypeInfo(packageName, typeName, enclosingName, isModule, path);
	}

	private static IFileTypeInfo getIFileTypeInfo(TypeInfo info) {
		if (info == null || info.getElementType() != TypeInfo.IFILE_TYPE_INFO)
			return null;
		return (IFileTypeInfo)info;
	}
		
	private TypeInfo createIFileTypeInfo(String packageName, String typeName, char[][] enclosingName, boolean isModule, String path, IFileTypeInfo last, String project) {
		String rest= path.substring(project.length() + 1); // the first slashes.
		int index= rest.lastIndexOf(TypeInfo.SEPARATOR);
		if (index == -1)
			return null;
		String middle= rest.substring(0, index);
		rest= rest.substring(index + 1);
		index= rest.lastIndexOf(TypeInfo.EXTENSION_SEPARATOR);
		String file= null;
		String extension= null;
		if (index != -1) {
			file= rest.substring(0, index);
			extension= rest.substring(index + 1);
		} else {
			return null;
		}
		String src= null;
		int ml= middle.length();
		int pl= packageName.length();
		// if we have a source or package then we have to substract the leading '/'
		if (ml > 0 && ml - 1 > pl) {
			 // If we have a package then we have to substract the '/' between src and package
			src= middle.substring(1, ml - pl - (pl > 0 ? 1 : 0));
		}
		if (last != null) {
			if (src != null && src.equals(last.getFolder()))
				src= last.getFolder();
		}
		if (typeName.equals(file)) {
			file= typeName;
		} else {
			file= createString(file);
		}
//		if (CLASS.equals(extension))
//			extension= CLASS;
 	 /* else */ if (RUBY.equals(extension))
			extension= RUBY;
		else
			extension= createString(extension);
		
		return new IFileTypeInfo(packageName, typeName, enclosingName, isModule, project, src, file, extension);
	}
	
	private String getPackageName(char[] packageName) {
		if (fLast == null)
			return new String(packageName);
		String lastPackageName= fLast.getPackageName();
		if (Strings.equals(lastPackageName, packageName))
			return lastPackageName;
		return new String(packageName);
	}
	
	private String getProject(String path) {
		for (int i= 0; i < fProjects.length; i++) {
			String project= fProjects[i];
			if (path.startsWith(project, 1))
				return project;
		}
		return null;
	}
	
	private String createString(String s) {
		if (s == null)
			return null;
		int length= s.length();
		if (length > fBuffer.length)
			fBuffer= new char[length];
		s.getChars(0, length, fBuffer, 0);
		return new String(fBuffer, 0, length);
	}
	
	private static String[] getProjectList() {
		IRubyModel model= RubyCore.create(ResourcesPlugin.getWorkspace().getRoot());
		String[] result;
		try {
			IRubyProject[] projects= model.getRubyProjects();
			result= new String[projects.length];
			for (int i= 0; i < projects.length; i++) {
				result[i]= projects[i].getElementName();
			}
		} catch (RubyModelException e) {
			result= new String[0];
		}
		// We have to sort the list of project names to make sure that we cut of the longest
		// project from the path, if two projects with the same prefix exist. For example
		// org.rubypeople.rdt.ui and org.rubypeople.rdt.ui.tests.
		Arrays.sort(result, new Comparator() {
			public int compare(Object o1, Object o2) {
				int l1= ((String)o1).length();
				int l2= ((String)o2).length();
				if (l1 < l2)
					return 1;
				if (l2 < l1)
					return -1; 
				return  0;
			}
			public boolean equals(Object obj) {
				return super.equals(obj);
			}
		});
		return result;
	}		
}
