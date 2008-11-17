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

import org.rubypeople.rdt.core.IMethod;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.util.Util;

/**
 * @author Chris
 * 
 */
public class RubyMethod extends NamedMember implements IMethod {

	private String[] parameterNames;

    /**
	 * @param name
	 */
	public RubyMethod(RubyElement parent, String name, String[] parameterNames) {
		super(parent, name);
        this.parameterNames = parameterNames;
	}

	public int getElementType() {
		return RubyElement.METHOD;
	}
	
	/**
	 * @see RubyElement#getHandleMemento(StringBuffer)
	 */
	protected void getHandleMemento(StringBuffer buff) {
		((RubyElement) getParent()).getHandleMemento(buff);
		char delimiter = getHandleMementoDelimiter();
		buff.append(delimiter);
		escapeMementoName(buff, getElementName());
		for (int i = 0; i < this.parameterNames.length; i++) {
			buff.append(delimiter);
			escapeMementoName(buff, this.parameterNames[i]);
		}
		if (this.occurrenceCount > 1) {
			buff.append(JEM_COUNT);
			buff.append(this.occurrenceCount);
		}
	}
	
	/**
	 * @see RubyElement#getHandleMemento()
	 */
	protected char getHandleMementoDelimiter() {
		return RubyElement.JEM_METHOD;
	}

	/*
	 * @see RubyElement#getPrimaryElement(boolean)
	 */
	public IRubyElement getPrimaryElement(boolean checkOwner) {
		if (checkOwner) {
			RubyScript cu = (RubyScript) getAncestor(SCRIPT);
			if (cu.isPrimary()) return this;
		}
		IRubyElement primaryParent = this.parent.getPrimaryElement(false);
		// FIXME We need to send more info than the method name. Number of
		// params?
		return ((IType) primaryParent).getMethod(this.name, parameterNames);
	}

	public boolean isConstructor() {
		return getElementName().equals("initialize");
	}

	public boolean equals(Object o) {
		if (!(o instanceof RubyMethod)) return false;
		return super.equals(o);
	}
    
    /**
     * @see org.rubypeople.rdt.internal.core.RubyElement#hashCode()
     */
    public int hashCode() {
       int hash = super.hashCode();
        for (int i = 0, length = parameterNames.length; i < length; i++) {
            hash = Util.combineHashCodes(hash, parameterNames[i].hashCode());
        }
        return hash;
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rubypeople.rdt.core.IMember#getDeclaringType()
	 */
	public IType getDeclaringType() {
        IRubyElement parent = getParent();
        if (parent instanceof IType) return (IType) parent;
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rubypeople.rdt.core.IRubyMethod#getVisibility()
	 */
	public int getVisibility() throws RubyModelException {
		if (isConstructor()) return IMethod.PUBLIC;
		RubyMethodElementInfo info = (RubyMethodElementInfo) getElementInfo();
		return info.getVisibility();
	}

    public String[] getParameterNames() throws RubyModelException {
       return parameterNames;
    }
    
    public int getNumberOfParameters() throws RubyModelException {
    	return getParameterNames().length;
    }

    public boolean isSingleton() {
    	try {
			RubyMethodElementInfo info = (RubyMethodElementInfo) getElementInfo();
			return info.isSingleton();
		} catch (RubyModelException e) {
			RubyCore.log(e);
		}
		return isConstructor();
    }

	public static RubyMethod singleton(RubyElement currentType, String name, String[] parameterNames2) {
		RubyMethod method = new RubyMethod(currentType, name, parameterNames2);
		try {
			RubyMethodElementInfo info = (RubyMethodElementInfo) method.getElementInfo();
			info.setIsSingleton(true);
		} catch (RubyModelException e) {
			RubyCore.log(e);
		}
		return method;
	}
	
	public boolean isPrivate() throws RubyModelException {
		return getVisibility() == PRIVATE;
	}
	
	public boolean isPublic() throws RubyModelException {
		return getVisibility() == PUBLIC;
	}
	
	public boolean isProtected() throws RubyModelException {
		return getVisibility() == PROTECTED;
	}

	public String[] getBlockParameters() throws RubyModelException {
		RubyMethodElementInfo info = (RubyMethodElementInfo) getElementInfo();
		return info.getBlockVars();
	}

}