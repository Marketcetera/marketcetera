package org.rubypeople.rdt.internal.ti;

import java.util.Collection;

public interface ITypeInferrer {

	/**
	 * Given raw Ruby source code and an offset into that code, does a best
	 * estimate of type information and returns a list of ITypeGuess objects.
	 * 
	 * @param source
	 *            The raw source to be parsed
	 * @param offset
	 *            the position in the source where we want to infer type
	 *            information.
	 * @return A List of ITypeGuess objects giving us the best available
	 *         information for inferred type.
	 */
	public Collection<ITypeGuess> infer(String source, int offset);

}
