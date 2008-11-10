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
package org.rubypeople.rdt.internal.ui.browsing;

import java.util.Comparator;

import org.rubypeople.rdt.core.IRubyElement;

public class RubyElementTypeComparator implements Comparator {


	/**
	 * Compares two Ruby element types. A type is considered to be
	 * greater if it may contain the other.
	 *
	 * @return		an int less than 0 if object1 is less than object2,
	 *				0 if they are equal, and > 0 if object1 is greater
	 *
	 * @see Comparator#compare(Object, Object)
	 */
	public int compare(Object o1, Object o2) {
		if (!(o1 instanceof IRubyElement) || !(o2 instanceof IRubyElement))
			throw new ClassCastException();
		return getIdForRubyElement((IRubyElement)o1) - getIdForRubyElement((IRubyElement)o2);
	}

	/**
	 * Compares two Ruby element types. A type is considered to be
	 * greater if it may contain the other.
	 *
	 * @return		an int < 0 if object1 is less than object2,
	 *				0 if they are equal, and > 0 if object1 is greater
	 *
	 * @see Comparator#compare(Object, Object)
	 */
	public int compare(Object o1, int elementType) {
		if (!(o1 instanceof IRubyElement))
			throw new ClassCastException();
		return getIdForRubyElement((IRubyElement)o1) - getIdForRubyElementType(elementType);
	}

	int getIdForRubyElement(IRubyElement element) {
		return getIdForRubyElementType(element.getElementType());
	}

	int getIdForRubyElementType(int elementType) {
		switch (elementType) {
			case IRubyElement.RUBY_MODEL:
				return 130;
			case IRubyElement.RUBY_PROJECT:
				return 120;
			case IRubyElement.SCRIPT:
				return 90;
			case IRubyElement.TYPE:
				return 70;
			case IRubyElement.FIELD:
				return 60;
			case IRubyElement.METHOD:
				return 50;
			case IRubyElement.IMPORT_CONTAINER:
				return 20;
			case IRubyElement.IMPORT_DECLARATION:
				return 10;
			default :
				return 1;
		}
	}
}
