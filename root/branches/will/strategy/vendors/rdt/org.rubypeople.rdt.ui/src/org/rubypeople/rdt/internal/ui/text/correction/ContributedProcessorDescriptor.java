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
package org.rubypeople.rdt.internal.ui.text.correction;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.expressions.EvaluationResult;
import org.eclipse.core.expressions.Expression;
import org.eclipse.core.expressions.ExpressionConverter;
import org.eclipse.core.expressions.ExpressionTagNames;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.rubypeople.rdt.core.IRubyModelMarker;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.dialogs.StatusInfo;

public final class ContributedProcessorDescriptor {

	private final IConfigurationElement fConfigurationElement;
	private Object fProcessorInstance;
	private Boolean fStatus;
	private boolean fLastResult;
	private final Set fHandledMarkerTypes;

	private static final String ID= "id"; //$NON-NLS-1$
	private static final String CLASS= "class"; //$NON-NLS-1$
		
	private static final String HANDLED_MARKER_TYPES= "handledMarkerTypes"; //$NON-NLS-1$
	private static final String MARKER_TYPE= "markerType"; //$NON-NLS-1$
	
	public ContributedProcessorDescriptor(IConfigurationElement element, boolean testMarkerTypes) {
		fConfigurationElement= element;
		fProcessorInstance= null;
		fStatus= null; // undefined
		if (fConfigurationElement.getChildren(ExpressionTagNames.ENABLEMENT).length == 0) {
			fStatus= Boolean.TRUE;
		}
		fHandledMarkerTypes= testMarkerTypes ? getHandledMarkerTypes(element) : null;
	}

	private Set getHandledMarkerTypes(IConfigurationElement element) {
		HashSet map= new HashSet(7);
		IConfigurationElement[] children= element.getChildren(HANDLED_MARKER_TYPES);
		for (int i= 0; i < children.length; i++) {
			IConfigurationElement[] types= children[i].getChildren(MARKER_TYPE);
			for (int k= 0; k < types.length; k++) {
				String attribute= types[k].getAttribute(ID);
				if (attribute != null) {
					map.add(attribute);
				}
			}
		}
		if (map.isEmpty()) {
			map.add(IRubyModelMarker.RUBY_MODEL_PROBLEM_MARKER);
			map.add(IRubyModelMarker.BUILDPATH_PROBLEM_MARKER);
			map.add(IRubyModelMarker.TASK_MARKER);
		}
		return map;
	}

	public IStatus checkSyntax() {
		IConfigurationElement[] children= fConfigurationElement.getChildren(ExpressionTagNames.ENABLEMENT);
		if (children.length > 1) {
			String id= fConfigurationElement.getAttribute(ID);
			return new StatusInfo(IStatus.ERROR, "Only one < enablement > element allowed. Disabling " + id); //$NON-NLS-1$
		}
		return new StatusInfo(IStatus.OK, "Syntactically correct quick assist/fix processor"); //$NON-NLS-1$
	}

	private boolean matches(IRubyScript cunit) {
		if (fStatus != null) {
			return fStatus.booleanValue();
		}

		IConfigurationElement[] children= fConfigurationElement.getChildren(ExpressionTagNames.ENABLEMENT);
		if (children.length == 1) {
			try {
				ExpressionConverter parser= ExpressionConverter.getDefault();
				Expression expression= parser.perform(children[0]);
				EvaluationContext evalContext= new EvaluationContext(null, cunit);
				evalContext.addVariable("rubyScript", cunit); //$NON-NLS-1$
				IRubyProject javaProject= cunit.getRubyProject();
				String[] natures= javaProject.getProject().getDescription().getNatureIds();
				evalContext.addVariable("projectNatures", Arrays.asList(natures)); //$NON-NLS-1$
								fLastResult= !(expression.evaluate(evalContext) != EvaluationResult.TRUE);
				return fLastResult;
			} catch (CoreException e) {
				RubyPlugin.log(e);
			}
		}
		fStatus= Boolean.FALSE;
		return false;
	}
	
	public Object getProcessor(IRubyScript cunit) throws CoreException {
		if (matches(cunit)) {
			if (fProcessorInstance == null) {
				fProcessorInstance= fConfigurationElement.createExecutableExtension(CLASS);
			}
			return fProcessorInstance;
		}
		return null;
	}
	
	public boolean canHandleMarkerType(String markerType) {
		return fHandledMarkerTypes == null || fHandledMarkerTypes.contains(markerType);
	}
	
}
