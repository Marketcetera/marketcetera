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
package org.rubypeople.rdt.internal.ui.text.ruby;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rubypeople.rdt.core.IMember;
import org.rubypeople.rdt.core.IMethod;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.corext.util.RDocUtil;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.infoviews.RiUtility;

public class ProposalInfo {

	private boolean fRubydocResolved= false;
	private String fRubydoc= null;

	protected IRubyElement fElement;

	public ProposalInfo(IMember member) {
		fElement= member;
	}
	
	protected ProposalInfo() {
		fElement= null;
	}

	public IRubyElement getRubyElement() throws RubyModelException {
		return fElement;
	}

	/**
	 * Gets the text for this proposal info formatted as HTML, or
	 * <code>null</code> if no text is available.
	 *
	 * @param monitor a progress monitor
	 * @return the additional info text
	 */
	public final String getInfo(IProgressMonitor monitor) {
		if (!fRubydocResolved) {
			fRubydocResolved= true;
			fRubydoc= computeInfo(monitor);
		}
		return fRubydoc;
	}

	/**
	 * Gets the text for this proposal info formatted as HTML, or
	 * <code>null</code> if no text is available.
	 *
	 * @param monitor a progress monitor
	 * @return the additional info text
	 */
	private String computeInfo(IProgressMonitor monitor) {
		try {
			final IRubyElement rubyElement= getRubyElement();
			if (rubyElement instanceof IMember) {
				IMember member= (IMember) rubyElement;
				String inSource = extractRubydoc(member, monitor);
				if (inSource != null && inSource.trim().length() > 0)
					return inSource;
				// Grab from RI
				IType type = member.getDeclaringType();
				List<String> args = new ArrayList<String>();
				String divider = "#"; // instance
				if (member instanceof IMethod) {
					IMethod method = (IMethod) member;
					if (method.isSingleton())
						divider = "::";
				}
				args.add(type.getFullyQualifiedName() + divider + member.getElementName());
				String riResult =  RiUtility.getRIHTMLContents(args);
				// TODO Stick result back into source for core stubs?
				if (riResult.trim().equals("nil")) return null;
				// Replace colors that are hard to read
				riResult = riResult.replace("color: #00ffff", "font-weight: bold");
				riResult = riResult.replace("color: #ffff00", "font-weight: italic");
				return riResult;
			}
		} catch (RubyModelException e) {
			RubyPlugin.log(e);
		} catch (IOException e) {
			RubyPlugin.log(e);
		}
		return null;
	}

	/**
	 * Extracts the RDoc for the given <code>IMember</code> and returns it
	 * as HTML.
	 *
	 * @param member the member to get the documentation for
	 * @param monitor a progress monitor
	 * @return the RDoc for <code>member</code> or <code>null</code> if
	 *         it is not available
	 * @throws RubyModelException if accessing the RDoc fails
	 * @throws IOException if reading the RDoc fails
	 */
	private String extractRubydoc(IMember member, IProgressMonitor monitor) throws RubyModelException, IOException {
		if (member != null && member.getRubyScript() != null) {
			return RDocUtil.getHTMLDocumentation(member);
		}
		return null;
	}
}
