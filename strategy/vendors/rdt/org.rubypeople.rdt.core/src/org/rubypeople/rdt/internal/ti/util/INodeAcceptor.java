package org.rubypeople.rdt.internal.ti.util;

import org.jruby.ast.Node;

public interface INodeAcceptor {
	public boolean doesAccept(Node node);
}
