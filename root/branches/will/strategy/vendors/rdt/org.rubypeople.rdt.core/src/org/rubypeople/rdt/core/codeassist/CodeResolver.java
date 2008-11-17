package org.rubypeople.rdt.core.codeassist;

import org.rubypeople.rdt.core.RubyModelException;

public abstract class CodeResolver {
	
	public abstract void select(ResolveContext context) throws RubyModelException;

}
