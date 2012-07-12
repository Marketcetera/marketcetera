package org.marketcetera.client;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Arrays;

import javax.jms.ConnectionFactory;

import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.client.config.SpringConfig;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.trade.Order;

/* $License$ */

/**
 * Tests the handling of order modifiers on the client side.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.1.4
 */
public class ClientOrderModifierTest
{
    /**
     * Run once before all unit tests.
     *
     * @throws Exception if an unexpected error occurs
     */
    @BeforeClass
    public static void once()
            throws Exception
    {
        LoggerConfiguration.logSetup();
    }
    /**
     * Tests adding and retrieving {@link OrderModifier} objects to {@link SpringConfig} objects.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testSpringConfig()
            throws Exception
    {
        ConnectionFactory mockIncomingFactory = mock(ConnectionFactory.class);
        ConnectionFactory mockOutgoingFactory = mock(ConnectionFactory.class);
        SpringConfig config = new SpringConfig(mockIncomingFactory,
                                               mockOutgoingFactory,
                                               null);
        config.afterPropertiesSet();
        assertTrue(config.getOrderModifiers().isEmpty());
        config = new SpringConfig(mockIncomingFactory,
                                  mockOutgoingFactory,
                                  new ArrayList<OrderModifier>());
        config.afterPropertiesSet();
        assertTrue(config.getOrderModifiers().isEmpty());
        OrderModifier modifier1 = new OrderModifier() {
            @Override
            public boolean modify(Order inOrder)
            {
                throw new UnsupportedOperationException();
            }
        };
        config = new SpringConfig(mockIncomingFactory,
                                  mockOutgoingFactory,
                                  Arrays.asList(new OrderModifier[] { modifier1 } ));
        config.afterPropertiesSet();
        assertTrue(Arrays.equals(new OrderModifier[] { modifier1 },
                                 config.getOrderModifiers().toArray()));
        OrderModifier modifier2 = new OrderModifier() {
            @Override
            public boolean modify(Order inOrder)
            {
                throw new UnsupportedOperationException();
            }
        };
        config = new SpringConfig(mockIncomingFactory,
                                  mockOutgoingFactory,
                                  Arrays.asList(new OrderModifier[] { modifier1, modifier2 } ));
        config.afterPropertiesSet();
        assertTrue(Arrays.equals(new OrderModifier[] { modifier1, modifier2 },
                                 config.getOrderModifiers().toArray()));
        config.setOrderModifiers(null);
        assertTrue(config.getOrderModifiers().isEmpty());
        OrderModifier modifier3 = new OrderModifier() {
            @Override
            public boolean modify(Order inOrder)
            {
                throw new UnsupportedOperationException();
            }
        };
        OrderModifier modifier4 = new OrderModifier() {
            @Override
            public boolean modify(Order inOrder)
            {
                throw new UnsupportedOperationException();
            }
        };
        config.setOrderModifiers(Arrays.asList(new OrderModifier[] { modifier3, modifier4 } ));
        assertTrue(Arrays.equals(new OrderModifier[] { modifier3, modifier4 },
                                 config.getOrderModifiers().toArray()));
        config.setOrderModifiers(new ArrayList<OrderModifier>());
        assertTrue(config.getOrderModifiers().isEmpty());
    }
}
