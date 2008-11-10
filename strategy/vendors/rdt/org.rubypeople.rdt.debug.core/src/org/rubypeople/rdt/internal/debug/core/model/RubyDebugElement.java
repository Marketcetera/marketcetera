package org.rubypeople.rdt.internal.debug.core.model;

import org.eclipse.debug.core.model.DebugElement;
import org.eclipse.debug.core.model.IDebugTarget;

public class RubyDebugElement extends DebugElement {

	public RubyDebugElement(IDebugTarget target) {
		super(target);
	}

	public String getModelIdentifier() {
		return IRubyDebugTarget.MODEL_IDENTIFIER;
	}

}
