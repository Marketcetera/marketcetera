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
package org.rubypeople.rdt.internal.ui.search;

import java.util.Collection;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.jruby.ast.Node;
import org.rubypeople.rdt.core.IRubyElement;

public interface IOccurrencesFinder {

	/**
	 * 
	 * @param root
	 *            the root node
	 * @param offset
	 *            position in source where selection is
	 * @param length
	 *            length of the selection
	 * @return
	 */
	public String initialize(Node root, int offset, int length);

	/**
	 * Returns a lit of AST Nodes back (which contain their associated
	 * positions).
	 * 
	 * @return List of AST Nodes
	 */
	public List<Position> perform();

	public String getJobLabel();

	/**
	 * Returns the plural label for this finder with 3 placeholders:
	 * <ul>
	 * <li>{0} for the {@link #getElementName() element name}</li>
	 * <li>{1} for the number of results found</li>
	 * <li>{2} for the scope (name of the compilation unit)</li>
	 * </ul>
	 * 
	 * @return the unformatted label
	 */
	public String getUnformattedPluralLabel();

	/**
	 * Returns the singular label for this finder with 2 placeholders:
	 * <ul>
	 * <li>{0} for the {@link #getElementName() element name}</li>
	 * <li>{1} for the scope (name of the compilation unit)</li>
	 * </ul>
	 * 
	 * @return the unformatted label
	 */
	public String getUnformattedSingularLabel();

	/**
	 * Returns the name of the element to look for or <code>null</code> if the
	 * finder hasn't been initialized yet.
	 * 
	 * @return the name of the element
	 */
	public String getElementName();

	public void collectOccurrenceMatches(IRubyElement element,
			IDocument document, Collection resultingMatches);

	/* Preferences */
	public void setFMarkConstantOccurrences(boolean markConstantOccurrences);
	public void setFMarkFieldOccurrences(boolean markFieldOccurrences);
	public void setFMarkLocalVariableOccurrences(boolean markLocalVariableOccurrences);
	public void setFMarkMethodExitPoints(boolean markMethodExitPoints);
	public void setFMarkMethodOccurrences(boolean markMethodOccurrences);
	public void setFMarkOccurrenceAnnotations(boolean markOccurrenceAnnotations);
	public void setFMarkTypeOccurrences(boolean markTypeOccurrences);
	public void setFStickyOccurrenceAnnotations(boolean stickyOccurrenceAnnotations);

}
