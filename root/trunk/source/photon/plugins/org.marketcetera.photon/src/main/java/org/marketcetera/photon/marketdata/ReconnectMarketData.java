package org.marketcetera.photon.marketdata;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Reconnects the market data feed.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class ReconnectMarketData extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		PhotonPlugin.getDefault().reconnectMarketDataFeed();
		return null;
	}

}
