package org.marketcetera.client;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.ws.stateless.Node;
import org.marketcetera.trade.*;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.core.LoggerConfiguration;
import static org.marketcetera.client.Messages.*;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;

import java.math.BigDecimal;

/* $License$ */
/**
 * Verifies Order validation failures.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class OrderValidationFailureTest {
    @Test
    public void orderSingle() throws Exception {
        verifySingle(null, NO_ORDER_SUPPLIED);

        OrderSingle order = ClientTest.createOrderSingle();
        //Verify we can send this order
        ClientManager.getInstance().sendOrder(order);
        Validations.validate(order);
        
        //Verify various failures
        order.setOrderID(null);
        verifySingle(order, VALIDATION_ORDERID);

        order = ClientTest.createOrderSingle();
        order.setOrderType(null);
        verifySingle(order, VALIDATION_ORDER_TYPE);

        order = ClientTest.createOrderSingle();
        order.setQuantity(null);
        verifySingle(order, VALIDATION_ORDER_QUANTITY);

        order = ClientTest.createOrderSingle();
        order.setSide(null);
        verifySingle(order, VALIDATION_ORDER_SIDE);

        order = ClientTest.createOrderSingle();
        order.setInstrument(null);
        verifySingle(order, VALIDATION_ORDER_INSTRUMENT);

        order = ClientTest.createOrderSingle();
        order.setInstrument(OPTION_INVALID_EXPIRY);
        verifySingle(order, EXPECTED_EXPIRY_FAIL_MESSAGE);

        order = ClientTest.createOrderSingle();
        order.setInstrument(UNKNOWN_INSTRUMENT);
        verifySingle(order, EXPECTED_UNKNOWN_INSTRUMENT_MESSAGE);
    }
    @Test
    public void orderReplace() throws Exception {
        verifyReplace(null, NO_ORDER_SUPPLIED);

        OrderReplace order = ClientTest.createOrderReplace();
        //Verify we can send this order
        ClientManager.getInstance().sendOrder(order);
        Validations.validate(order);

        //Verify various failures
        order.setOrderID(null);
        verifyReplace(order, VALIDATION_ORDERID);

        order = ClientTest.createOrderReplace();
        order.setOriginalOrderID(null);
        verifyReplace(order, VALIDATION_ORIG_ORDERID);

        order = ClientTest.createOrderReplace();
        order.setOrderType(null);
        verifyReplace(order, VALIDATION_ORDER_TYPE);

        order = ClientTest.createOrderReplace();
        order.setQuantity(null);
        verifyReplace(order, VALIDATION_ORDER_QUANTITY);

        order = ClientTest.createOrderReplace();
        order.setSide(null);
        verifyReplace(order, VALIDATION_ORDER_SIDE);

        order = ClientTest.createOrderReplace();
        order.setInstrument(null);
        verifyReplace(order, VALIDATION_ORDER_INSTRUMENT);

        order = ClientTest.createOrderReplace();
        order.setInstrument(OPTION_INVALID_EXPIRY);
        verifyReplace(order, EXPECTED_EXPIRY_FAIL_MESSAGE);

        order = ClientTest.createOrderReplace();
        order.setInstrument(UNKNOWN_INSTRUMENT);
        verifyReplace(order, EXPECTED_UNKNOWN_INSTRUMENT_MESSAGE);
    }
    @Test
    public void orderCancel() throws Exception {
        verifyCancel(null, NO_ORDER_SUPPLIED);

        OrderCancel order = ClientTest.createOrderCancel();
        //Verify we can send this order
        ClientManager.getInstance().sendOrder(order);
        Validations.validate(order);

        //Verify various failures
        order.setOrderID(null);
        verifyCancel(order, VALIDATION_ORDERID);

        order = ClientTest.createOrderCancel();
        order.setOriginalOrderID(null);
        verifyCancel(order, VALIDATION_ORIG_ORDERID);

        order = ClientTest.createOrderCancel();
        order.setQuantity(null);
        verifyCancel(order, VALIDATION_ORDER_QUANTITY);

        order = ClientTest.createOrderCancel();
        order.setSide(null);
        verifyCancel(order, VALIDATION_ORDER_SIDE);

        order = ClientTest.createOrderCancel();
        order.setInstrument(null);
        verifyCancel(order, VALIDATION_ORDER_INSTRUMENT);

        order = ClientTest.createOrderCancel();
        order.setInstrument(OPTION_INVALID_EXPIRY);
        verifyCancel(order, EXPECTED_EXPIRY_FAIL_MESSAGE);

        order = ClientTest.createOrderCancel();
        order.setInstrument(UNKNOWN_INSTRUMENT);
        verifyCancel(order, EXPECTED_UNKNOWN_INSTRUMENT_MESSAGE);
    }
    @Test
    public void orderFIX() throws Exception {
        verifyRaw(null, NO_ORDER_SUPPLIED);
        Validations.validate(ClientTest.createOrderFIX());
    }
    @Test
    public void suggestions() throws Exception {
        verifySuggestion(null, NO_SUGGEST_SUPPLIED);

        OrderSingleSuggestion suggest = Factory.getInstance().
                createOrderSingleSuggestion();
        suggest.setIdentifier(null);
        suggest.setOrder(ClientTest.createOrderSingle());
        suggest.setScore(BigDecimal.ONE);
        verifySuggestion(suggest, VALIDATION_SUGGEST_IDENTIFIER);
        suggest.setIdentifier("id");
        suggest.setOrder(null);
        verifySuggestion(suggest, VALIDATION_SUGGEST_ORDER);
        suggest.setOrder(ClientTest.createOrderSingle());
        suggest.setScore(null);
        verifySuggestion(suggest, VALIDATION_SUGGEST_SCORE);
        //Verify validation passes
        suggest.setScore(BigDecimal.ONE);
        Validations.validate(suggest);
    }
    @Test
    public void instrument() throws Exception {
        //null instrument
        new ExpectedFailure<OrderValidationException>(Messages.VALIDATION_ORDER_INSTRUMENT){
            @Override
            protected void run() throws Exception {
                Validations.validateInstrument(null);
            }
        };
        //invalid instrument
        new ExpectedFailure<OrderValidationException>(EXPECTED_UNKNOWN_INSTRUMENT_MESSAGE){
            @Override
            protected void run() throws Exception {
                Validations.validateInstrument(UNKNOWN_INSTRUMENT);
            }
        };
        //valid instruments
        Validations.validateInstrument(new Equity("eq"));
        Validations.validateInstrument(new Option("eq","200010",BigDecimal.TEN, OptionType.Call));
    }
    @BeforeClass
    public static void setup() throws Exception {
        LoggerConfiguration.logSetup();
        sServer = new MockServer();
        String u = "u";
        ClientManager.init(new ClientParameters(u, u.toCharArray(),
                MockServer.URL, Node.DEFAULT_HOST, Node.DEFAULT_PORT));
    }
    @AfterClass
    public static void cleanup() throws Exception {
        if(ClientManager.isInitialized()) {
            ClientManager.getInstance().close();
        }
        if (sServer != null) {
            sServer.close();
            sServer = null;
        }
    }

    private static void verifySingle(final OrderSingle inOrder,
                                     I18NBoundMessage inExpectedMessage)
            throws Exception {
        new ExpectedFailure<OrderValidationException>(
                inExpectedMessage){
            protected void run() throws Exception {
                Validations.validate(inOrder);
            }
        };
        new ExpectedFailure<OrderValidationException>(
                inExpectedMessage){
            protected void run() throws Exception {
                ClientManager.getInstance().sendOrder(inOrder);
            }
        };
    }
    private static void verifyReplace(final OrderReplace inOrder,
                                     I18NBoundMessage inExpectedMessage)
            throws Exception {
        new ExpectedFailure<OrderValidationException>(
                inExpectedMessage){
            protected void run() throws Exception {
                Validations.validate(inOrder);
            }
        };
        new ExpectedFailure<OrderValidationException>(
                inExpectedMessage){
            protected void run() throws Exception {
                ClientManager.getInstance().sendOrder(inOrder);
            }
        };
    }
    private static void verifyCancel(final OrderCancel inOrder,
                                     I18NBoundMessage inExpectedMessage)
            throws Exception {
        new ExpectedFailure<OrderValidationException>(
                inExpectedMessage){
            protected void run() throws Exception {
                Validations.validate(inOrder);
            }
        };
        new ExpectedFailure<OrderValidationException>(
                inExpectedMessage){
            protected void run() throws Exception {
                ClientManager.getInstance().sendOrder(inOrder);
            }
        };
    }
    private static void verifyRaw(final FIXOrder inOrder,
                                     I18NMessage0P inExpectedMessage)
            throws Exception {
        new ExpectedFailure<OrderValidationException>(
                inExpectedMessage){
            protected void run() throws Exception {
                Validations.validate(inOrder);
            }
        };
        new ExpectedFailure<OrderValidationException>(
                inExpectedMessage){
            protected void run() throws Exception {
                ClientManager.getInstance().sendOrderRaw(inOrder);
            }
        };
    }
    private static void verifySuggestion(final OrderSingleSuggestion inSuggest,
                                         I18NMessage0P inExpectedMessage)
            throws Exception {
        new ExpectedFailure<OrderValidationException>(inExpectedMessage){
            protected void run() throws Exception {
                Validations.validate(inSuggest);
            }
        };
    }
    private static final Option OPTION_INVALID_EXPIRY = new Option("TEST",
            "20090000", BigDecimal.TEN, OptionType.Call);
    private static final Instrument UNKNOWN_INSTRUMENT = new Instrument() {
        @Override
        public String getSymbol() {
            return null;
        }

        @Override
        public SecurityType getSecurityType() {
            return null;
        }
    };
    private static final I18NBoundMessage1P EXPECTED_UNKNOWN_INSTRUMENT_MESSAGE =
            new I18NBoundMessage1P(Messages.VALIDATION_UNKNOWN_INSTRUMENT,
                    UNKNOWN_INSTRUMENT);
    private static MockServer sServer;
    private static final I18NBoundMessage1P EXPECTED_EXPIRY_FAIL_MESSAGE = new I18NBoundMessage1P(
            org.marketcetera.client.instruments.Messages.INVALID_OPTION_EXPIRY_FORMAT,
            OPTION_INVALID_EXPIRY.getExpiry());
}
