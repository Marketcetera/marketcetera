package org.marketcetera.photon.internal.marketdata;

import org.apache.commons.lang.StringUtils;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataFlowRequester;
import org.marketcetera.module.DataFlowSupport;
import org.marketcetera.module.DataReceiver;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.Module;
import org.marketcetera.module.ModuleCreationException;
import org.marketcetera.module.ModuleException;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.StopDataFlowException;
import org.marketcetera.module.UnsupportedDataTypeException;
import org.marketcetera.photon.internal.marketdata.MarketDataReceiverFactory.IConfigurationProvider;
import org.marketcetera.photon.marketdata.MarketDataSubscriber;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.misc.ClassVersion;

/**
 * Endpoint for market data flows.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id: MarketDataReceiverModule.java 10267 2008-12-24 16:25:11Z colin $
 * @since 1.0.0
 */
@ClassVersion("$Id: MarketDataReceiverModule.java 10267 2008-12-24 16:25:11Z colin $")
class MarketDataReceiverModule extends Module implements DataReceiver,
		DataFlowRequester {

	private final IConfigurationProvider mConfigurationProvider;

	private final MarketDataSubscriber mSubscriber;

	private DataFlowSupport mDataFlowSupport;

	/**
	 * Constructor.  Should only be called from {@link MarketDataReceiverFactory}.
	 * 
	 * @param inURN
	 *            instance URN for this module
	 * @param configProvider
	 *            data flow configuration provider
	 * @param subscriber
	 *            subscriber that will process incoming market data
	 */
	MarketDataReceiverModule(ModuleURN inURN,
			IConfigurationProvider configProvider,
			MarketDataSubscriber subscriber) throws ModuleCreationException {
		super(inURN, false);
		if (configProvider == null)
			throw new ModuleCreationException(Messages.MARKET_DATA_RECEIVER_NO_CONFIG);
		if (subscriber == null)
			throw new ModuleCreationException(Messages.MARKET_DATA_RECEIVER_NO_SUBSCRIBER);
		mConfigurationProvider = configProvider;
		mSubscriber = subscriber;
	}

	@Override
	protected void preStart() throws ModuleException {
		if (StringUtils.isBlank(mSubscriber.getSymbol())) {
			throw new ModuleException(Messages.MARKET_DATA_RECEIVER_NO_SYMBOL);
		}
		ModuleURN source = mConfigurationProvider.getMarketDataSourceModule();
		if (source == null) {
			throw new ModuleException(new I18NBoundMessage1P(
					Messages.MARKET_DATA_RECEIVER_NO_SOURCE, mSubscriber
							.getSymbol()));
		}
		org.marketcetera.marketdata.DataRequest request = MarketDataRequest
				.newTopOfBookRequest(mSubscriber.getSymbol());
		mDataFlowSupport.createDataFlow(new DataRequest[] {
				new DataRequest(source, request),
				new DataRequest(getURN()) }, false);
	}

	@Override
	protected void preStop() throws ModuleException {

	}

	@Override
	public void receiveData(DataFlowID inFlowID, final Object inData)
			throws UnsupportedDataTypeException, StopDataFlowException {
		mSubscriber.receiveData(inData);
	}

	@Override
	public void setFlowSupport(DataFlowSupport inSupport) {
		mDataFlowSupport = inSupport;
	}

}