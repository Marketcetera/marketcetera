/*
 * Created on Feb 12, 2005
 *
 */
package org.rubypeople.rdt.internal.core;


/**
 * @author Chris
 *
 */
public class RubyConstant extends RubyField {

	/**
	 * @param parent
	 * @param name
	 */
	public RubyConstant(RubyElement parent, String name) {
		super(parent, name);
	}
		
	/* (non-Javadoc)
	 * @see org.rubypeople.rdt.internal.core.RubyField#getElementType()
	 */
	public int getElementType() {
		return CONSTANT;
	}

}
