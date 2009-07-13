package org.marketcetera.photon.internal.strategy;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.photon.test.SWTTestUtil;
import org.marketcetera.photon.test.WorkbenchRunner;
import org.marketcetera.photon.test.AbstractUIRunner.UI;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.OrderSingleSuggestion;

/* $License$ */

/**
 * Test {@link TradeSuggestionManager}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id: TradeSuggestionManagerTest.java 10628 2009-06-26 02:20:04Z will
 *          $
 * @since 1.5.0
 */
@RunWith(WorkbenchRunner.class)
public class TradeSuggestionManagerTest {

    private TradeSuggestionManager mFixture;
    private Factory mFactory;

    @Before
    @UI
    public void before() {
        mFixture = new TradeSuggestionManager();
        mFactory = Factory.getInstance();
    }
    
    /**
     * Test incoming data
     */
    @Test
    @UI
    public void testReceivedData() {
        assertThat(mFixture.getTradeSuggestions().size(), is(0));
        // no order is invalid
        OrderSingleSuggestion suggestion = mock(OrderSingleSuggestion.class);
        when(suggestion.getOrder()).thenReturn(null);
        mFixture.receivedData(new DataFlowID("1"), suggestion);
        assertThat(mFixture.getTradeSuggestions().size(), is(0));
        // include an order, make sure it shows up
        suggestion = mock(OrderSingleSuggestion.class);
        OrderSingle order = mFactory.createOrderSingle();
        when(suggestion.getOrder()).thenReturn(order);
        mFixture.receivedData(new DataFlowID("1"), suggestion);
        // the model gets updated in the UI thread
        SWTTestUtil.conditionalDelayUnchecked(1, TimeUnit.SECONDS,
                new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return mFixture.getTradeSuggestions().size() > 0;
                    }
                });
        assertThat(((TradeSuggestion) mFixture.getTradeSuggestions().get(0))
                .getOrder(), sameInstance(order));
    }

}
