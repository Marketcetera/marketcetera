package org.rubypeople.rdt.internal.debug.core.commands;

import org.rubypeople.rdt.internal.debug.core.parsing.AbstractReadStrategy;
import org.rubypeople.rdt.internal.debug.core.parsing.XmlStreamReader;

public class GenericCommand extends AbstractCommand {
	
	private AbstractReadStrategy readStrategy;

	public GenericCommand(String command, boolean isControl) {
		super(command, isControl);
	}

	@Override
	protected XmlStreamReader createResultReader(AbstractReadStrategy readStrategy) {
		this.readStrategy = readStrategy;
		return null;
	}

	public AbstractReadStrategy getReadStrategy() {
		return readStrategy;
	}

}
