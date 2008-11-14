package org.rubypeople.rdt.internal.ti;

import java.util.List;
import org.jruby.lexer.yacc.ISourcePosition;

public interface IReferenceFinder {
	
	/**
	 * Finds a list of selection offsets that correspond to the
	 * specified element.
	 * @param source Source to search
	 * @param offset Offset into source of the source element
	 * @return List of ISourcePositions
	 */
	public List<ISourcePosition> findReferences( String source, int offset );
}
