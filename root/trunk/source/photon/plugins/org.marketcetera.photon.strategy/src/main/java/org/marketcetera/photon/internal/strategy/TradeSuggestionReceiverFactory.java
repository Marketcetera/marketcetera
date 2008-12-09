package org.marketcetera.photon.internal.strategy;

import org.marketcetera.module.Module;
import org.marketcetera.module.ModuleCreationException;
import org.marketcetera.module.ModuleFactory;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Module factory for trade suggestion receivers.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class TradeSuggestionReceiverFactory extends ModuleFactory<Module> {

	/**
	 * Provider URN for this factory.
	 */
	static final ModuleURN PROVIDER_URN = new ModuleURN("metc:tssink:photon"); //$NON-NLS-1$

	/**
	 * Singleton instance URN.
	 */
	static final ModuleURN INSTANCE_URN = new ModuleURN(PROVIDER_URN, "single"); //$NON-NLS-1$

	/**
	 * Constructor.
	 */
	public TradeSuggestionReceiverFactory() {
		super(PROVIDER_URN, Messages.TRADE_SUGGESTION_RECEIVER_DESCRIPTION,
				false, false);
	}

	@Override
	public Module create(Object... inParameters) throws ModuleCreationException {
		return new TradeSuggestionReceiverModule(INSTANCE_URN);
	}

}
