package org.marketcetera.photon.marketdata;

import org.marketcetera.module.Module;
import org.marketcetera.module.ModuleException;
import org.marketcetera.module.ModuleURN;


/* $License$ */

/**
 * Module that claims to be a market data provider, but is not.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
public class DummyModule extends Module {

	protected DummyModule(ModuleURN inURN, boolean inAutoStart) {
		super(inURN, inAutoStart);
	}

	@Override
	protected void preStart() throws ModuleException {
	}

	@Override
	protected void preStop() throws ModuleException {
	}

}