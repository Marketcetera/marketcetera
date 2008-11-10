/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Sebastian Davids: sdavids@gmx.de bug 37333, 26653 
 * 	   David Corbin: dcorbin@users.sourceforge.net - editor opening
 *******************************************************************************/
package org.rubypeople.rdt.internal.ui.util;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.rubypeople.rdt.core.IRubyProject;



public class StackTraceLine {
	// for better matching with 1.8, append /:in `(.*)'/ to the regex
	private static Pattern OPEN_TRACE_LINE_PATTERN = Pattern.compile("\\s*(\\S.*?):(\\d+)(:|$)");
	private static Pattern BRACKETED_TRACE_LINE_PATTERN = Pattern.compile("\\[(.*):(\\d+)\\]:");
	private static Pattern OPTIONAL_PREFIX = Pattern.compile("^[ \\t^]*from ");
	private String fFilename;
	private int fLineNumber;
	private int length;
	private int offset;

	public static boolean isTraceLine(String line) {
		Matcher bracketedMatcher = BRACKETED_TRACE_LINE_PATTERN.matcher(line);
		Matcher openMatcher = OPEN_TRACE_LINE_PATTERN.matcher(line);
		return bracketedMatcher.find() || openMatcher.find(); 
	}
	
	public StackTraceLine(String traceLine) {
		this(traceLine, (IProject) null);
	}
	
	public StackTraceLine(String traceLine, IRubyProject launchedProject) {
		this(traceLine, launchedProject.getProject());
	}
    
    public StackTraceLine(String traceLine, IProject launchedProject) {
    	int prefix = 0;
		Matcher matcher = OPTIONAL_PREFIX.matcher(traceLine);
		if (matcher.find()) {
			traceLine = traceLine.substring(matcher.group(0).length());
			prefix = matcher.group(0).length();
		}
		
		matcher = BRACKETED_TRACE_LINE_PATTERN.matcher(traceLine);
		if (!matcher.find()) {
			matcher = OPEN_TRACE_LINE_PATTERN.matcher(traceLine);
			if (!matcher.find())  
				return;
		}
		
		fFilename = matcher.group(1);		
		offset = matcher.start(1) + prefix;
		if (fFilename.startsWith("[")) {
			fFilename = fFilename.substring(1);
			offset++;
		}
		String lineNumber = matcher.group(2);
		fLineNumber = Integer.parseInt(lineNumber);
		length = fFilename.length()+lineNumber.length()+1;
		if (isRelativePath() && launchedProject != null) {
			makeRelativeToWorkspace(launchedProject);
		}		
		
	}

	private void makeRelativeToWorkspace(IProject launchedProject) {
		if (fFilename.startsWith("./")) {
			fFilename = launchedProject.getFullPath().toPortableString() + fFilename.substring(1);
		} else if (fFilename.startsWith("/")) {
			fFilename = launchedProject.getFullPath().toPortableString() + fFilename;
		} else {
			fFilename = launchedProject.getFullPath().toPortableString() + '/' + fFilename;
		}		
	}

	private boolean isRelativePath() {
		if (fFilename.startsWith("./")) return true;
		if (fFilename.startsWith("/")) { // If it starts with '/' it could be relative to workspace or absolute on *-nix!
			File file = new File(fFilename);
			if (file.exists()) return false;
			return true;
		}
		if (fFilename.contains(":")) return false;
		return false;
	}

	public void openEditor() {
        if (fFilename == null)
            return;
        new LineBasedEditorOpener(fFilename, fLineNumber).open();
	}

	public int getLineNumber() {
		return fLineNumber;
	}

	public String getFilename() {
		return fFilename;
	}

	public int offset() {
		return offset;
	}

	public int length() {
		return length;
	}
}
