package org.marketcetera.photon.internal.strategy;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.photon.test.SWTTestUtil;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.OrderSingleSuggestion;

/* $License$ */

/**
 * Test {@link TradeSuggestionManager}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
public class TradeSuggestionManagerTest {

	TradeSuggestionManager fixture = new TradeSuggestionManager();
	Factory factory = Factory.getInstance();

	/**
	 * Test incoming data
	 */
	@Test
	public void testReceivedData() {
		assertThat(fixture.getTradeSuggestions().size(), is(0));
		// no order is invalid
		OrderSingleSuggestion suggestion = mock(OrderSingleSuggestion.class);
		stub(suggestion.getOrder()).toReturn(null);
		fixture.receivedData(new DataFlowID("1"), suggestion);
		assertThat(fixture.getTradeSuggestions().size(), is(0));
		// include an order, make sure it shows up
		suggestion = mock(OrderSingleSuggestion.class);
		OrderSingle order = factory.createOrderSingle();
		stub(suggestion.getOrder()).toReturn(order);
		fixture.receivedData(new DataFlowID("1"), suggestion);
		// the model gets updated in the UI thread
		SWTTestUtil.conditionalDelayUnchecked(1, TimeUnit.SECONDS, new Callable<Boolean>() {

			@Override
			public Boolean call() throws Exception {
				return fixture.getTradeSuggestions().size() > 0;
			}
		});
		assertThat(((TradeSuggestion) fixture.getTradeSuggestions().get(0)).getOrder(),
				sameInstance(order));
	}

}
