package org.marketcetera.photon.marketdata;

import java.util.EventListener;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
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
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.misc.ClassVersion;

/**
 * Endpoint for market data flows.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")//$NON-NLS-1$
public class MarketDataReceiverModule extends Module implements DataReceiver,
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

	/**
	 * Interface for objects providing configuration to {@link MarketDataReceiverModule}.
	 *
	 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
	 * @version $Id$
	 * @since 1.0.0
	 */
	@ClassVersion("$Id$")//$NON-NLS-1$
	public interface IConfigurationProvider {

		/**
		 * Returns the {@link ModuleURN} of the market data source module.
		 * 
		 * @return market data source module URN
		 */
		ModuleURN getMarketDataSourceModule();
	}

	/**
	 * Abstract base class of market data receiver module subscribers. 
	 *
	 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
	 * @version $Id$
	 * @since 1.0.0
	 */
	@ClassVersion("$Id$")//$NON-NLS-1$
	public abstract static class MarketDataSubscriber implements EventListener {

		private final String mSymbol;

		/**
		 * Constructor.
		 * 
		 * @param symbol symbol for market data request
		 */
		public MarketDataSubscriber(String symbol) {
			Assert.isLegal(StringUtils.isNotBlank(symbol));
			mSymbol = symbol;
		}

		/**
		 * Returns the symbol for the market data request.
		 * 
		 * @return the symbol for the market data request
		 */
		public final String getSymbol() {
			return mSymbol;
		}

		/**
		 * Callback to provide market data to be processed.
		 * 
		 * @param inData data from the market data flow
		 */
		public abstract void receiveData(Object inData);
	}

}