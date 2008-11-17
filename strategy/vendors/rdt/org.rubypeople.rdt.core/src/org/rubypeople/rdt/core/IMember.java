/*
 * Created on Jan 13, 2005
 *
 */
package org.rubypeople.rdt.core;

/**
 * Common protocol for Ruby elements that can be members of types. This set
 * consists of <code>IRubyType</code>,<code>IRubyMethod</code>,
 * <code>IField</code>.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface IMember extends IRubyElement, ISourceReference, IParent {

	/**
	 * Returns the compilation unit in which this member is declared, or
	 * <code>null</code> if this member is not declared in a compilation unit
	 * (for example, a binary type). This is a handle-only method.
	 * 
	 * @return the compilation unit in which this member is declared, or
	 *         <code>null</code> if this member is not declared in a
	 *         compilation unit (for example, a binary type)
	 */
	IRubyScript getRubyScript();

	/**
	 * Returns the local or anonymous type declared in this source member with
	 * the given simple name and/or with the specified position relative to the
	 * order they are defined in the source. The name is empty if it is an
	 * anonymous type. Numbering starts at 1 (thus the first occurrence is
	 * occurrence 1, not occurrence 0). This is a handle-only method. The type
	 * may or may not exist. Throws a <code>RuntimeException</code> if this
	 * member is not a source member.
	 * 
	 * @param name
	 *            the given simple name
	 * @param occurrenceCount
	 *            the specified position
	 * @return the type with the given name and/or with the specified position
	 *         relative to the order they are defined in the source
	 * @since 3.0
	 */
	IType getType(String name, int occurrenceCount);

	/**
	 * Returns the type in which this member is declared, or <code>null</code>
	 * if this member is not declared in a type (for example, a top-level type).
	 * This is a handle-only method.
	 * 
	 * @return the type in which this member is declared, or <code>null</code>
	 *         if this member is not declared in a type (for example, a
	 *         top-level type)
	 */
	IType getDeclaringType();

	/**
	 * Returns the source range of this member's simple name, or
	 * <code>null</code> if this member does not have a name (for example, an
	 * initializer), or if this member does not have associated source code (for
	 * example, a binary type).
	 * 
	 * @exception RubyModelException
	 *                if this element does not exist or if an exception occurs
	 *                while accessing its corresponding resource.
	 * @return the source range of this member's simple name, or
	 *         <code>null</code> if this member does not have a name (for
	 *         example, an initializer), or if this member does not have
	 *         associated source code (for example, a binary type)
	 * @throws RubyModelException
	 */
	ISourceRange getNameRange() throws RubyModelException;

}
