package org.marketcetera.photon.marketdata;

import java.util.concurrent.atomic.AtomicLong;

import org.marketcetera.module.Module;
import org.marketcetera.module.ModuleCreationException;
import org.marketcetera.module.ModuleFactory;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.photon.marketdata.MarketDataReceiverModule.IConfigurationProvider;
import org.marketcetera.photon.marketdata.MarketDataReceiverModule.MarketDataSubscriber;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Factory for {@link MarketDataReceiverModule}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")//$NON-NLS-1$
public class MarketDataReceiverFactory extends
		ModuleFactory<MarketDataReceiverModule> {

	/**
	 * Provider URN for this factory.
	 */
	public static final ModuleURN PROVIDER_URN = new ModuleURN(
			"metc:mdatasink:photon"); //$NON-NLS-1$

	private static final AtomicLong counter = new AtomicLong();

	/**
	 * Constructor.
	 */
	public MarketDataReceiverFactory() {
		super(PROVIDER_URN, Messages.MARKET_DATA_RECEIVER_FACTORY_DESCRIPTION, true, false, IConfigurationProvider.class, MarketDataSubscriber.class);
	}

	@Override
	public Module create(Object... inParameters) throws ModuleCreationException {
		ModuleURN urn = new ModuleURN(PROVIDER_URN, "item" + counter.incrementAndGet()); //$NON-NLS-1$
		IConfigurationProvider config = (IConfigurationProvider) inParameters[0];
		MarketDataSubscriber subscriber = (MarketDataSubscriber) inParameters[1];
		return new MarketDataReceiverModule(urn, config, subscriber);
	}

}