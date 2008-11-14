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

/**
 * @author Chris
 *  
 */
public class RubyModule extends RubyType {

	public RubyModule(RubyElement parent, String name) {
		super(parent, name);
		// Public methods
		// FIXME Make these methods static and share them for all instances
//		addMethod(new RubyMethod(this, "<", Visibility.PUBLIC));
//		addMethod(new RubyMethod(this, "<=", Visibility.PUBLIC));
//		addMethod(new RubyMethod(this, ">", Visibility.PUBLIC));
//		addMethod(new RubyMethod(this, ">=", Visibility.PUBLIC));
//		addMethod(new RubyMethod(this, "<=>", Visibility.PUBLIC));
//		addMethod(new RubyMethod(this, "===", Visibility.PUBLIC));
//		addMethod(new RubyMethod(this, "ancestors", Visibility.PUBLIC));
//		addMethod(new RubyMethod(this, "class_eval", Visibility.PUBLIC));
//		addMethod(new RubyMethod(this, "class_variables", Visibility.PUBLIC));
//		addMethod(new RubyMethod(this, "clone", Visibility.PUBLIC));
//		addMethod(new RubyMethod(this, "const_defined?", Visibility.PUBLIC));
//		addMethod(new RubyMethod(this, "const_get", Visibility.PUBLIC));
//		addMethod(new RubyMethod(this, "const_set", Visibility.PUBLIC));
//		addMethod(new RubyMethod(this, "constants", Visibility.PUBLIC));
//		addMethod(new RubyMethod(this, "included_modules", Visibility.PUBLIC));
//		addMethod(new RubyMethod(this, "instance_methods", Visibility.PUBLIC));
//		addMethod(new RubyMethod(this, "method_defined?", Visibility.PUBLIC));
//		addMethod(new RubyMethod(this, "module_eval", Visibility.PUBLIC));
//		addMethod(new RubyMethod(this, "name", Visibility.PUBLIC));
//		addMethod(new RubyMethod(this, "private_class_method", Visibility.PUBLIC));
//		addMethod(new RubyMethod(this, "private_instance_methods", Visibility.PUBLIC));
//		addMethod(new RubyMethod(this, "protected_instance_methods", Visibility.PUBLIC));
//		addMethod(new RubyMethod(this, "public_class_method", Visibility.PUBLIC));
//		addMethod(new RubyMethod(this, "public_instance_methods", Visibility.PUBLIC));
//		// Private methods
//		addMethod(new RubyMethod(this, "alias_method", Visibility.PRIVATE));
//		addMethod(new RubyMethod(this, "append_features", Visibility.PRIVATE));
//		addMethod(new RubyMethod(this, "attr", Visibility.PRIVATE));
//		addMethod(new RubyMethod(this, "attr_accessor", Visibility.PRIVATE));
//		addMethod(new RubyMethod(this, "attr_reader", Visibility.PRIVATE));
//		addMethod(new RubyMethod(this, "attr_writer", Visibility.PRIVATE));
//		addMethod(new RubyMethod(this, "extend_object", Visibility.PRIVATE));
//		addMethod(new RubyMethod(this, "include", Visibility.PRIVATE));
//		addMethod(new RubyMethod(this, "method_added", Visibility.PRIVATE));
//		addMethod(new RubyMethod(this, "module_function", Visibility.PRIVATE));
//		addMethod(new RubyMethod(this, "private", Visibility.PRIVATE));
//		addMethod(new RubyMethod(this, "protected", Visibility.PRIVATE));
//		addMethod(new RubyMethod(this, "public", Visibility.PRIVATE));
//		addMethod(new RubyMethod(this, "remove_const", Visibility.PRIVATE));
//		addMethod(new RubyMethod(this, "remove_method", Visibility.PRIVATE));
//		addMethod(new RubyMethod(this, "undef_method", Visibility.PRIVATE));
	}
	
	/* (non-Javadoc)
     * @see org.rubypeople.rdt.core.IRubyType#isClass()
     */
    public boolean isClass() {
        return false;
    }

    /* (non-Javadoc)
     * @see org.rubypeople.rdt.core.IRubyType#isModule()
     */
    public boolean isModule() {
        return true;
    }
	
}