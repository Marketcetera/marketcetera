package org.marketcetera.tensorflow;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Test;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.modules.headwater.HeadwaterModule;
import org.marketcetera.tensorflow.converter.TensorFlowConverterModule;
import org.marketcetera.tensorflow.converters.TensorFromOrderConverter;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.OrderType;
import org.marketcetera.trade.Side;
import org.tensorflow.Tensor;

/* $License$ */

/**
 * Tests {@link TensorFlowConverterModule}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class TensorFlowConverterTest
        extends TensorFlowTestBase
{
    /**
     * Test behavior of a data flow for which no tensor converter exists.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testNoConverter()
            throws Exception
    {
        DataFlowID dataFlow = startConverterDataFlow(new TensorFromOrderConverter());
        // no converter for type of TensorFlowConverterTest
        HeadwaterModule.getInstance(headwaterInstance).emit(this,
                                                            dataFlow);
        assertTrue(receivedData.isEmpty());
    }
    /**
     * Test creating a conversion data flow of orders.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testOrder()
            throws Exception
    {
        DataFlowID dataFlow = startConverterDataFlow(new TensorFromOrderConverter());
        // test a market data event
        assertTrue(receivedData.isEmpty());
        OrderSingle order = Factory.getInstance().createOrderSingle();
        order.setInstrument(new Equity("METC"));
        order.setQuantity(new BigDecimal(1000));
        order.setPrice(new BigDecimal(100));
        order.setOrderType(OrderType.Limit);
        order.setSide(Side.Buy);
        HeadwaterModule.getInstance(headwaterInstance).emit(order,
                                                            dataFlow);
        Object receivedTensor = waitForData();
        assertNotNull(receivedTensor);
        assertTrue(receivedTensor instanceof Tensor);
    }
}
