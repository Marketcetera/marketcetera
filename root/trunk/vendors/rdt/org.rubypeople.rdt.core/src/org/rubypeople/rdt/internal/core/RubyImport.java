/*
 * Author: C.Williams
 * 
 * Copyright (c) 2004 RubyPeople.
 * 
 * This file is part of the Ruby Development Tools (RDT) plugin for eclipse. You
 * can get copy of the GPL along with further information about RubyPeople and
 * third party software bundled with RDT in the file
 * org.rubypeople.rdt.core_x.x.x/RDT.license or otherwise at
 * http://www.rubypeople.org/RDT.license.
 * 
 * RDT is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * RDT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * RDT; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package org.rubypeople.rdt.internal.core;

import org.eclipse.core.runtime.Assert;
import org.rubypeople.rdt.core.IImportDeclaration;
import org.rubypeople.rdt.core.IRubyElement;

/**
 * @author Chris
 * 
 */
public class RubyImport extends SourceRefElement implements IImportDeclaration {

	protected String name;

	/**
	 * @param name
	 */
	public RubyImport(RubyElement parent, String name) {
		super(parent);
		this.name = name;
	}

	public boolean equals(Object o) {
		if (!(o instanceof RubyImport)) return false;
		return super.equals(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rubypeople.rdt.internal.core.parser.RubyElement#getElementType()
	 */
	public int getElementType() {
		return IRubyElement.IMPORT_DECLARATION;
	}

	public String getElementName() {
		return this.name;
	}
	
	/**
	 * @see RubyElement#getHandleMemento(StringBuffer)
	 * For import declarations, the handle delimiter is associated to the import container already
	 */
	protected void getHandleMemento(StringBuffer buff) {
		((RubyElement)getParent()).getHandleMemento(buff);
		escapeMementoName(buff, getElementName());
		if (this.occurrenceCount > 1) {
			buff.append(JEM_COUNT);
			buff.append(this.occurrenceCount);
		}
	}
	/**
	 * @see RubyElement#getHandleMemento()
	 */
	protected char getHandleMementoDelimiter() {
		// For import declarations, the handle delimiter is associated to the import container already
		Assert.isTrue(false, "Should not be called"); //$NON-NLS-1$
		return 0;
	}

}
