package org.marketcetera.photon.internal.strategy;

import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataReceiver;
import org.marketcetera.module.Module;
import org.marketcetera.module.ModuleException;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.StopDataFlowException;
import org.marketcetera.module.UnsupportedDataTypeException;
import org.marketcetera.trade.OrderSingleSuggestion;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Module that receives trade suggestions and forwards them to
 * {@link TradeSuggestionManager}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
class TradeSuggestionReceiverModule extends Module implements DataReceiver {

	/**
	 * Constructor.
	 * 
	 * @param inURN
	 *            URN for this module
	 */
	TradeSuggestionReceiverModule(ModuleURN inURN) {
		super(inURN, true);
	}

	@Override
	protected void preStart() throws ModuleException {
	}

	@Override
	protected void preStop() throws ModuleException {
	}

	@Override
	public void receiveData(DataFlowID inFlowID, Object inData)
			throws UnsupportedDataTypeException, StopDataFlowException {
		if (!(inData instanceof OrderSingleSuggestion)) {
			throw new UnsupportedDataTypeException(new I18NBoundMessage1P(
					Messages.TRADE_SUGGESTION_RECEIVER_INVALID_DATA_TYPE,
					inData.getClass()));
		}
		OrderSingleSuggestion suggestion = (OrderSingleSuggestion) inData;
		if (suggestion.getOrder() == null) {
			throw new UnsupportedDataTypeException(
					Messages.TRADE_SUGGESTION_RECEIVER_INVALID_DATA_NO_ORDER);
		}
		TradeSuggestionManager.getCurrent().addSuggestion(suggestion);
	}

}
