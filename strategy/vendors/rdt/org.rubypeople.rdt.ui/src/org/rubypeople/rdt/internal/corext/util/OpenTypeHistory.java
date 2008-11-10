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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.rubypeople.rdt.core.ElementChangedEvent;
import org.rubypeople.rdt.core.IElementChangedListener;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyElementDelta;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.search.IRubySearchScope;
import org.rubypeople.rdt.core.search.SearchEngine;
import org.rubypeople.rdt.internal.corext.CorextMessages;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.w3c.dom.Element;

/**
 * History for the open type dialog. Object and keys are both {@link TypeInfo}s.
 */
public class OpenTypeHistory extends History {
	
	private static class TypeHistoryDeltaListener implements IElementChangedListener {
		public void elementChanged(ElementChangedEvent event) {
			if (processDelta(event.getDelta())) {
				OpenTypeHistory.getInstance().markAsInconsistent();
			}
		}
		
		/**
		 * Computes whether the history needs a consistency check or not.
		 * 
		 * @param delta the Ruby element delta
		 * 
		 * @return <code>true</code> if consistency must be checked 
		 *  <code>false</code> otherwise.
		 */
		private boolean processDelta(IRubyElementDelta delta) {
			IRubyElement elem= delta.getElement();
			
			boolean isChanged= delta.getKind() == IRubyElementDelta.CHANGED;
			boolean isRemoved= delta.getKind() == IRubyElementDelta.REMOVED;
						
			switch (elem.getElementType()) {
				case IRubyElement.RUBY_PROJECT:
					if (isRemoved || (isChanged && 
							(delta.getFlags() & IRubyElementDelta.F_CLOSED) != 0)) {
						return true;
					}
					return processChildrenDelta(delta);
				case IRubyElement.SOURCE_FOLDER_ROOT:
					if (isRemoved || (isChanged && (
							(delta.getFlags() & IRubyElementDelta.F_ARCHIVE_CONTENT_CHANGED) != 0 ||
							(delta.getFlags() & IRubyElementDelta.F_REMOVED_FROM_CLASSPATH) != 0))) {
						return true;
					}
					return processChildrenDelta(delta);
				case IRubyElement.TYPE:
					if (isChanged && (delta.getFlags() & IRubyElementDelta.F_MODIFIERS) != 0) {
						return true;
					}
					// type children can be inner classes: fall through
				case IRubyElement.RUBY_MODEL:
				case IRubyElement.SOURCE_FOLDER:
					if (isRemoved) {
						return true;
					}				
					return processChildrenDelta(delta);
				case IRubyElement.SCRIPT:
					// Not the primary compilation unit. Ignore it 
					if (!RubyModelUtil.isPrimary((IRubyScript) elem)) {
						return false;
					}

					if (isRemoved || (isChanged && isUnknownStructuralChange(delta.getFlags()))) {
						return true;
					}
					return processChildrenDelta(delta);
				default:
					// fields, methods, imports ect
					return false;
			}	
		}
		
		private boolean isUnknownStructuralChange(int flags) {
			if ((flags & IRubyElementDelta.F_CONTENT) == 0)
				return false;
			return (flags & IRubyElementDelta.F_FINE_GRAINED) == 0; 
		}

		/*
		private boolean isPossibleStructuralChange(int flags) {
			return (flags & (IRubyElementDelta.F_CONTENT | IRubyElementDelta.F_FINE_GRAINED)) == IRubyElementDelta.F_CONTENT;
		}
		*/		
		
		private boolean processChildrenDelta(IRubyElementDelta delta) {
			IRubyElementDelta[] children= delta.getAffectedChildren();
			for (int i= 0; i < children.length; i++) {
				if (processDelta(children[i])) {
					return true;
				}
			}
			return false;
		}
	}
	
	private static class UpdateJob extends Job {
		public static final String FAMILY= UpdateJob.class.getName();
		public UpdateJob() {
			super(CorextMessages.TypeInfoHistory_consistency_check);
		}
		protected IStatus run(IProgressMonitor monitor) {
			OpenTypeHistory history= OpenTypeHistory.getInstance();
			history.internalCheckConsistency(monitor);
			return new Status(IStatus.OK, RubyPlugin.getPluginId(), IStatus.OK, "", null); //$NON-NLS-1$
		}
		public boolean belongsTo(Object family) {
			return FAMILY.equals(family);
		}
	}
	
	// Needs to be volatile since accesses aren't synchronized.
	private volatile boolean fNeedsConsistencyCheck;
	// Map of cached time stamps
	private Map fTimestampMapping;
	
	private final IElementChangedListener fDeltaListener;
	private final UpdateJob fUpdateJob;
	private final TypeInfoFactory fTypeInfoFactory;
	
	private static final String FILENAME= "OpenTypeHistory.xml"; //$NON-NLS-1$
	private static final String NODE_ROOT= "typeInfoHistroy"; //$NON-NLS-1$
	private static final String NODE_TYPE_INFO= "typeInfo"; //$NON-NLS-1$
	private static final String NODE_NAME= "name"; //$NON-NLS-1$
	private static final String NODE_PACKAGE= "package"; //$NON-NLS-1$
	private static final String NODE_ENCLOSING_NAMES= "enclosingTypes"; //$NON-NLS-1$
	private static final String NODE_PATH= "path"; //$NON-NLS-1$
	private static final String NODE_MODIFIERS= "modifiers";  //$NON-NLS-1$
	private static final String NODE_TIMESTAMP= "timestamp"; //$NON-NLS-1$
	private static final char[][] EMPTY_ENCLOSING_NAMES= new char[0][0];
	
	private static OpenTypeHistory fgInstance;
	
	public static synchronized OpenTypeHistory getInstance() {
		if (fgInstance == null)
			fgInstance= new OpenTypeHistory();
		return fgInstance;
	}
	
	public static synchronized void shutdown() {
		if (fgInstance == null)
			return;
		fgInstance.doShutdown();
	}
	
	private OpenTypeHistory() {
		super(FILENAME, NODE_ROOT, NODE_TYPE_INFO);
		fTypeInfoFactory= new TypeInfoFactory();
		fTimestampMapping= new HashMap();
		fNeedsConsistencyCheck= true;
		load();
		fDeltaListener= new TypeHistoryDeltaListener();
		RubyCore.addElementChangedListener(fDeltaListener);
		fUpdateJob= new UpdateJob();
		// It is not necessary anymore that the update job has a rule since
		// markAsInconsistent isn't synchronized anymore. See bugs
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=128399 and
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=135278
		// for details.
		fUpdateJob.setPriority(Job.SHORT);
	}
	
	public void markAsInconsistent() {
		fNeedsConsistencyCheck= true;
		// cancel the old job. If no job is running this is a NOOP.
		fUpdateJob.cancel();
		fUpdateJob.schedule();
	}
	
	public boolean needConsistencyCheck() {
		return fNeedsConsistencyCheck;
	}

	public void checkConsistency(IProgressMonitor monitor) throws OperationCanceledException {
		if (!fNeedsConsistencyCheck)
			return;
		if (fUpdateJob.getState() == Job.RUNNING) {
			try {
				Platform.getJobManager().join(UpdateJob.FAMILY, monitor);
			} catch (OperationCanceledException e) {
				// Ignore and do the consistency check without
				// waiting for the update job.
			} catch (InterruptedException e) {
				// Ignore and do the consistency check without
				// waiting for the update job.
			}
		}
		if (!fNeedsConsistencyCheck)
			return;
		internalCheckConsistency(monitor);
	}
	
	public synchronized boolean contains(TypeInfo type) {
		return super.contains(type);
	}

	public synchronized void accessed(TypeInfo info) {
		// Fetching the timestamp might not be cheap (remote file system
		// external Jars. So check if we alreay have one.
		if (!fTimestampMapping.containsKey(info)) {
			fTimestampMapping.put(info, new Long(info.getContainerTimestamp()));
		}
		super.accessed(info);
	}

	public synchronized TypeInfo remove(TypeInfo info) {
		fTimestampMapping.remove(info);
		return (TypeInfo)super.remove(info);
	}

	public synchronized TypeInfo[] getTypeInfos() {
		Collection values= getValues();
		int size= values.size();
		TypeInfo[] result= new TypeInfo[size];
		int i= size - 1;
		for (Iterator iter= values.iterator(); iter.hasNext();) {
			result[i]= (TypeInfo)iter.next();
			i--;
		}
		return result;
	}

	public synchronized TypeInfo[] getFilteredTypeInfos(TypeInfoFilter filter) {
		Collection values= getValues();
		List result= new ArrayList();
		for (Iterator iter= values.iterator(); iter.hasNext();) {
			TypeInfo type= (TypeInfo)iter.next();
			if ((filter == null || filter.matchesHistoryElement(type)) && !TypeFilter.isFiltered(type.getFullyQualifiedName()))
				result.add(type);
		}
		Collections.reverse(result);
		return (TypeInfo[])result.toArray(new TypeInfo[result.size()]);
		
	}
	
	protected Object getKey(Object object) {
		return object;
	}

	private synchronized void internalCheckConsistency(IProgressMonitor monitor) throws OperationCanceledException {
		// Setting fNeedsConsistencyCheck is necessary here since 
		// markAsInconsistent isn't synchronized.
		fNeedsConsistencyCheck= true;
		IRubySearchScope scope= SearchEngine.createWorkspaceScope();
		List typesToCheck= new ArrayList(getKeys());
		monitor.beginTask(CorextMessages.TypeInfoHistory_consistency_check, typesToCheck.size());
		monitor.setTaskName(CorextMessages.TypeInfoHistory_consistency_check);
		for (Iterator iter= typesToCheck.iterator(); iter.hasNext();) {
			TypeInfo type= (TypeInfo)iter.next();
			long currentTimestamp= type.getContainerTimestamp();
			Long lastTested= (Long)fTimestampMapping.get(type);
			if (lastTested != null && currentTimestamp != IResource.NULL_STAMP && currentTimestamp == lastTested.longValue() && !type.isContainerDirty())
				continue;
			try {
				IType jType= type.resolveType(scope);
				if (jType == null || !jType.exists()) {
					remove(type);
				} else {
					// copy over the modifiers since they may have changed
					type.setIsModule(jType.isModule());
					fTimestampMapping.put(type, new Long(currentTimestamp));
				}
			} catch (RubyModelException e) {
				remove(type);
			}
			if (monitor.isCanceled())
				throw new OperationCanceledException();
			monitor.worked(1);
		}
		monitor.done();
		fNeedsConsistencyCheck= false;
	}
	
	private void doShutdown() {
		RubyCore.removeElementChangedListener(fDeltaListener);
		save();
	}
	
	protected Object createFromElement(Element type) {
		String name= type.getAttribute(NODE_NAME);
		String pack= type.getAttribute(NODE_PACKAGE);
		char[][] enclosingNames= getEnclosingNames(type);
		String path= type.getAttribute(NODE_PATH);
		boolean isModule = false;
		try {
			isModule= Boolean.parseBoolean(type.getAttribute(NODE_MODIFIERS));
		} catch (NumberFormatException e) {
			// take zero
		}
		TypeInfo info= fTypeInfoFactory.create(
			pack.toCharArray(), name.toCharArray(), enclosingNames, isModule, path);
		long timestamp= IResource.NULL_STAMP;
		String timestampValue= type.getAttribute(NODE_TIMESTAMP);
		if (timestampValue != null && timestampValue.length() > 0) {
			try {
				timestamp= Long.parseLong(timestampValue);
			} catch (NumberFormatException e) {
				// take null stamp
			}
		}
		if (timestamp != IResource.NULL_STAMP) {
			fTimestampMapping.put(info, new Long(timestamp));
		}
		return info;
	}

	protected void setAttributes(Object object, Element typeElement) {
		TypeInfo type= (TypeInfo)object;
		typeElement.setAttribute(NODE_NAME, type.getTypeName());
		typeElement.setAttribute(NODE_PACKAGE, type.getPackageName());
		typeElement.setAttribute(NODE_ENCLOSING_NAMES, type.getEnclosingName());
		typeElement.setAttribute(NODE_PATH, type.getPath());
		typeElement.setAttribute(NODE_MODIFIERS, Boolean.toString(type.isModule()));
		Long timestamp= (Long) fTimestampMapping.get(type);
		if (timestamp == null) {
			typeElement.setAttribute(NODE_TIMESTAMP, Long.toString(IResource.NULL_STAMP));			
		} else {
			typeElement.setAttribute(NODE_TIMESTAMP, timestamp.toString()); 
		}
	}

	private char[][] getEnclosingNames(Element type) {
		String enclosingNames= type.getAttribute(NODE_ENCLOSING_NAMES);
		if (enclosingNames.length() == 0)
			return EMPTY_ENCLOSING_NAMES;
		StringTokenizer tokenizer= new StringTokenizer(enclosingNames, "."); //$NON-NLS-1$
		List names= new ArrayList();
		while(tokenizer.hasMoreTokens()) {
			String name= tokenizer.nextToken();
			names.add(name.toCharArray());
		}
		return (char[][])names.toArray(new char[names.size()][]);
	}
}