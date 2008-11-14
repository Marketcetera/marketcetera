package org.rubypeople.rdt.core;

public interface ICodeAssist {

	public IRubyElement[] codeSelect(int offset, int length)
			throws RubyModelException;

	public IRubyElement[] codeSelect(int offset, int length,
			WorkingCopyOwner workingCopyOwner) throws RubyModelException;

	/**
	 * Performs code completion at the given offset position in this compilation unit,
	 * reporting results to the given completion requestor. The <code>offset</code>
	 * is the 0-based index of the character, after which code assist is desired.
	 * An <code>offset</code> of -1 indicates to code assist at the beginning of this
	 * compilation unit.
	 * <p>
	 *
	 * @param offset the given offset position
	 * @param requestor the given completion requestor
	 * @exception RubyModelException if code assist could not be performed. Reasons include:<ul>
	 *  <li>This Ruby element does not exist (ELEMENT_DOES_NOT_EXIST)</li>
	 *  <li> The position specified is < -1 or is greater than this compilation unit's
	 *      source length (INDEX_OUT_OF_BOUNDS)
	 * </ul>
	 *
	 * @exception IllegalArgumentException if <code>requestor</code> is <code>null</code>
	 * @since 0.9.0
 	 */
	void codeComplete(int offset, CompletionRequestor requestor)
		throws RubyModelException;
}
