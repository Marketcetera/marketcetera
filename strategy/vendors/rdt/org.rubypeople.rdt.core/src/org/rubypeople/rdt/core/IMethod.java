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
package org.rubypeople.rdt.core;


/**
 * @author Chris
 *  
 */
public interface IMethod extends IRubyElement, IMember {

	public static final int PUBLIC = Flags.AccPublic;
	public static final int PROTECTED = Flags.AccProtected;
	public static final int PRIVATE = Flags.AccPrivate;
	
	public int getVisibility() throws RubyModelException;

    public boolean isConstructor();

    public String[] getParameterNames() throws RubyModelException;

    public boolean isSingleton();

	public int getNumberOfParameters() throws RubyModelException;

	public boolean isPrivate() throws RubyModelException;

	public boolean isPublic() throws RubyModelException;
	
	public boolean isProtected() throws RubyModelException;

	public String[] getBlockParameters() throws RubyModelException;

}