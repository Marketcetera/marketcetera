/*
 * Created on Feb 20, 2005
 *
 */
package org.rubypeople.rdt.internal.core.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.jruby.lexer.yacc.ISourcePosition;
import org.jruby.lexer.yacc.SyntaxException;
import org.rubypeople.rdt.core.IRubyModelMarker;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.compiler.IProblem;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 * @author Chris
 * 
 */
public class MarkerUtility {
	
	private static Set<IgnoreMarker> toIgnore = new HashSet<IgnoreMarker>();
	
	static {
		loadIgnoredMarkers();
	}

	/**
	 * @param underlyingResource
	 * @param syntaxException
	 * @param contentLength
	 */
	public static void createSyntaxError(IResource underlyingResource, SyntaxException syntaxException) {
		try {
			ISourcePosition pos = syntaxException.getPosition();
			IMarker marker = underlyingResource.createMarker(IRubyModelMarker.RUBY_MODEL_PROBLEM_MARKER);
			Map<String, Comparable> map = new HashMap<String, Comparable>();
			map.put(IMarker.SEVERITY, new Integer(IMarker.SEVERITY_ERROR));
			map.put(IMarker.MESSAGE, "Syntax Error");
			map.put(IMarker.USER_EDITABLE, Boolean.FALSE);
			map.put(IMarker.LINE_NUMBER, new Integer(pos.getStartLine()));
			map.put(IMarker.CHAR_START, new Integer(pos.getStartOffset()));
			map.put(IMarker.CHAR_END, new Integer(pos.getEndOffset()));
			map.put(IRubyModelMarker.ID, IProblem.Syntax);
			marker.setAttributes(map);
		} catch (CoreException e) {
			RubyCore.log(e);
		}
	}

	/**
	 * @param underlyingResource
	 */
	public static void removeMarkers(IResource underlyingResource) {
		try {
			underlyingResource.deleteMarkers(IRubyModelMarker.RUBY_MODEL_PROBLEM_MARKER, true, IResource.DEPTH_INFINITE);
		} catch (CoreException e) {
			RubyCore.log(e);
		}
	}

	/**
	 * @param resource
	 * @param problems
	 */
	public static void createProblemMarkers(IResource resource, List<IProblem> problems) {
		for (Iterator iter = problems.iterator(); iter.hasNext();) {
			createProblemMarker(resource, (IProblem) iter.next());
		}
	}

	public static void createProblemMarker(IResource underlyingResource, IProblem problem) {
		if (problem.isTask()) {
			try {
				createTask(underlyingResource, (TaskTag) problem);
			} catch (CoreException e) {
				RubyCore.log(e);
			}
			return;
		}
		try {
			if (markerExists(underlyingResource, problem.getID(), problem.getSourceStart(), problem.getSourceEnd(), IRubyModelMarker.RUBY_MODEL_PROBLEM_MARKER)) return;
			Map<String, Comparable> map = new HashMap<String, Comparable>();
			int severity;
			if(problem.isWarning()) severity = IMarker.SEVERITY_WARNING;
			else if(problem.isError()) severity = IMarker.SEVERITY_ERROR;
			else severity = IMarker.SEVERITY_INFO;
			IMarker marker = underlyingResource.createMarker(IRubyModelMarker.RUBY_MODEL_PROBLEM_MARKER);
			map.put(IMarker.SEVERITY, new Integer(severity));
			map.put(IMarker.MESSAGE, problem.getMessage());
			map.put(IMarker.USER_EDITABLE, Boolean.FALSE);
			map.put(IMarker.LINE_NUMBER, new Integer(problem.getSourceLineNumber()));
			map.put(IMarker.CHAR_START, new Integer(problem.getSourceStart()));
			map.put(IMarker.CHAR_END, new Integer(problem.getSourceEnd()));
			map.put(IRubyModelMarker.ID, problem.getID());
			marker.setAttributes(map);
		} catch (CoreException e) {
			RubyCore.log(e);
		}
	}

	public static void createTasks(IResource underlyingResource, List<TaskTag> tasks) throws CoreException {
		for (Iterator iter = tasks.iterator(); iter.hasNext();) {
			createTask(underlyingResource, (TaskTag) iter.next());
		}		
	}
	
	/**
	 * @param underlyingResource
	 * @param warning
	 * @throws CoreException 
	 */
	private static void createTask(IResource resource, TaskTag task) throws CoreException {
		int lineNumber = task.getSourceLineNumber();
		if (lineNumber <= 0) lineNumber = 1;
		if (markerExists(resource, task.getID(), task.getSourceStart(), task.getSourceEnd(), IRubyModelMarker.TASK_MARKER)) return;
		HashMap<String, Comparable> map = new HashMap<String, Comparable>();
		map.put(IMarker.PRIORITY, new Integer(task.getPriority()));
		map.put(IMarker.MESSAGE, task.getMessage());
		map.put(IMarker.LINE_NUMBER, new Integer(lineNumber));
		map.put(IMarker.SEVERITY, new Integer(IMarker.SEVERITY_INFO));
		map.put(IMarker.USER_EDITABLE, new Boolean(false));
		map.put(IMarker.TRANSIENT, new Boolean(false));
		map.put(IMarker.CHAR_START, new Integer(task.getSourceStart()));
		map.put(IMarker.CHAR_END, new Integer(task.getSourceEnd()));
		map.put(IRubyModelMarker.ID, task.getID());
		IMarker marker = resource.createMarker(IRubyModelMarker.TASK_MARKER);
		marker.setAttributes(map);
	}
	
	public static boolean markerExists(IResource resource, int id, int offset, int endOffset, String type) throws CoreException {
		// Check against ignore list
		if (ignoring(resource, id, offset, endOffset)) return true;
		IMarker tasks[] = resource.findMarkers(type, true, IResource.DEPTH_ZERO);
		for (int i = 0; i < tasks.length; i++) {
			if (markerMatches(id, offset, endOffset, tasks[i])) return true;
		}
		return false;
	}

	/**
	 * Return a boolean to indicate if we should ignore a potential marker/annotation with the following attributes.
	 * @param resource
	 * @param id
	 * @param offset
	 * @param endOffset
	 * @return
	 */
	public static boolean ignoring(IResource resource, int id, int offset, int endOffset) {
		for (IgnoreMarker marker : toIgnore) {
			if (!marker.getResource().equals(resource)) continue;
			if (markerMatches(id, offset, endOffset, marker)) return true;
		}
		return false;
	}

	private static boolean markerMatches(int id, int offset, int endOffset, IgnoreMarker marker) {
		if (marker.getId() != id) return false;		
		if (marker.getOffset() != offset) return false;		
		if (marker.getEndOffset() != endOffset) return false;				
		return true;
	}

	public static boolean markerMatches(int id, int offset, int endOffset, IMarker marker) throws CoreException {
		Integer markerId = (Integer) marker.getAttribute(IRubyModelMarker.ID);
		if (markerId.intValue() != id) return false;
		
		Integer start = (Integer) marker.getAttribute(IMarker.CHAR_START);
		if (start != offset) return false;
		
		Integer end = (Integer) marker.getAttribute(IMarker.CHAR_END);
		if (end != endOffset) return false;
				
		return true;	
	}
	
	public static void ignore(IResource resource, int problemId, int offset, int length) {
		ignore(new IgnoreMarker(resource, problemId, offset, offset + length));
	}
	
	public static void ignore(IMarker marker) {
		try {
			ignore(new IgnoreMarker(marker));
		} catch (CoreException e) {
			RubyCore.log(e);
		}		
	}

	public synchronized static void ignore(IgnoreMarker marker) {
		if (toIgnore.contains(marker)) return;
		toIgnore.add(marker);		
		saveIgnoredMarkers();
	}
		
	private static void saveIgnoredMarkers() {		
		PrintWriter out = null;
		try {
			out = new PrintWriter(new FileWriter(getConfigFile()));
			writeXML(out);			
		} catch (FileNotFoundException e) {
			RubyCore.log(e);
		} catch (IOException e) {
			RubyCore.log(e);
		} finally {
			if (out != null) 
				out.close();
		}
	}
	
	private static void loadIgnoredMarkers() {		
		Reader fileReader = null;
		try {
			fileReader = new FileReader(getConfigFile());
			XMLReader reader = SAXParserFactory.newInstance().newSAXParser()
					.getXMLReader();
			IgnoreMarkersContentHandler handler = new IgnoreMarkersContentHandler();
			reader.setContentHandler(handler);
			reader.parse(new InputSource(fileReader));

			toIgnore.clear();
			Collection<IgnoreMarker> markers = handler.getIgnoreMarkers();
			for (IgnoreMarker marker : markers) {
				toIgnore.add(marker);
			}
		} catch (FileNotFoundException e) {
			// This is okay, will get thrown if no config exists yet
		} catch (Exception e) {
			RubyCore.log(e);
		} finally {
			try {
				if (fileReader != null) 
					fileReader.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}
	
	private static File getConfigFile() {
		IPath rubyCoreMetadataDir = RubyCore.getPlugin().getStateLocation();
		return rubyCoreMetadataDir.append("ignore_warnings.xml").toFile();
	}

	/**
	 * Writes each server configuration to file in XML format.
	 * 
	 * @param out
	 *            the writer to use
	 */
	private static void writeXML(PrintWriter out) {
		out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		out.println(tag(IgnoreMarkersContentHandler.ROOT));
		Iterator i = toIgnore.iterator();
		while (i.hasNext()) {
			IgnoreMarker s = (IgnoreMarker) i.next();
			out.println(tag(IgnoreMarkersContentHandler.WARNING));
			out.println(tag(IgnoreMarkersContentHandler.RESOURCE, s.getResource().getLocation().toPortableString()));
			out.println(tag(IgnoreMarkersContentHandler.ID, s.getId()));
			out.println(tag(IgnoreMarkersContentHandler.OFFSET, s.getOffset()));
			out.println(tag(IgnoreMarkersContentHandler.END_OFFSET,s.getEndOffset()));
			out.println(endTag(IgnoreMarkersContentHandler.WARNING));
		}
		out.println(endTag(IgnoreMarkersContentHandler.ROOT));
		out.flush();
	}

	private static String tag(String tag) {
		return "<" + tag + ">";
	}
	
	private static String endTag(String tag) {
		return "</" + tag + ">";
	}
	
	private static String tag(String tag, String content) {
		return tag(tag) + content + endTag(tag);
	}
	
	private static String tag(String tag, int intValue) {
		return tag (tag, Integer.toString(intValue));
	}
}
