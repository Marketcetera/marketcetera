package org.marketcetera.client;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.trade.*;
import org.marketcetera.client.instruments.InstrumentValidationHandler;

/* $License$ */
/**
 * Carries out validations for various order types.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public final class Validations {
    /**
     * Validates a new order. Following validations are performed.
     * <ul>
     *  <li>order is not null</li>
     *  <li>has a non-null orderID</li>
     *  <li>has a non-null orderType</li>
     *  <li>has a non-null orderQuantity</li>
     *  <li>has a non-null Side value</li>
     *  <li>has a non-null &amp; valid Instrument</li>
     * </ul>
     *
     * @param inOrderSingle The order that needs to be validated.
     *
     * @throws OrderValidationException if any of the validations fail.
     */
    public static void validate(OrderSingle inOrderSingle)
            throws OrderValidationException {
        if(inOrderSingle == null) {
            throw new OrderValidationException(
                    Messages.NO_ORDER_SUPPLIED);
        }
        //Validate order's fields.
        validate(inOrderSingle.getOrderID() == null,
                Messages.VALIDATION_ORDERID);
        validate(inOrderSingle.getOrderType() == null,
                Messages.VALIDATION_ORDER_TYPE);
        validate(inOrderSingle.getQuantity() == null,
                Messages.VALIDATION_ORDER_QUANTITY);
        validate(inOrderSingle.getSide() == null,
                Messages.VALIDATION_ORDER_SIDE);
        validateInstrument(inOrderSingle.getInstrument());
    }

    /**
     * Validates a new order. Following validations are performed.
     * <ul>
     *  <li>order is not null</li>
     * </ul>
     *
     * @param inFIXOrder The order that needs to be validated.
     * @throws OrderValidationException if any of the validations fail.
     */
    public static void validate(FIXOrder inFIXOrder)
            throws OrderValidationException {
        if(inFIXOrder == null) {
            throw new OrderValidationException(
                    Messages.NO_ORDER_SUPPLIED);
        }
    }

    /**
     * Validates a new order. Following validations are performed.
     * <ul>
     *  <li>order is not null</li>
     *  <li>has a non-null orderID</li>
     *  <li>has a non-null original orderID</li>
     *  <li>has a non-null orderType</li>
     *  <li>has a non-null orderQuantity</li>
     *  <li>has a non-null Side value</li>
     *  <li>has a non-null &amp; valid Instrument</li>
     * </ul>
     *
     * @param inOrderReplace The order that needs to be validated.
     *
     * @throws OrderValidationException if any of the validations fail.
     */
    public static void validate(OrderReplace inOrderReplace)
            throws OrderValidationException {
        if(inOrderReplace == null) {
            throw new OrderValidationException(
                    Messages.NO_ORDER_SUPPLIED);
        }
        //Validate order's fields.
        validate(inOrderReplace.getOrderID() == null,
                Messages.VALIDATION_ORDERID);
        validate(inOrderReplace.getOriginalOrderID() == null,
                Messages.VALIDATION_ORIG_ORDERID);
        validate(inOrderReplace.getOrderType() == null,
                Messages.VALIDATION_ORDER_TYPE);
        validate(inOrderReplace.getQuantity() == null,
                Messages.VALIDATION_ORDER_QUANTITY);
        validate(inOrderReplace.getSide() == null,
                Messages.VALIDATION_ORDER_SIDE);
        validateInstrument(inOrderReplace.getInstrument());
    }

    /**
     * Validates a new order. Following validations are performed.
     * <ul>
     *  <li>order is not null</li>
     *  <li>has a non-null orderID</li>
     *  <li>has a non-null original orderID</li>
     *  <li>has a non-null orderQuantity</li>
     *  <li>has a non-null Side value</li>
     *  <li>has a non-null &amp; valid Instrument</li>
     * </ul>
     *
     * @param inOrderCancel The order that needs to be validated.
     *
     * @throws OrderValidationException if any of the validations fail.
     */
    public static void validate(OrderCancel inOrderCancel)
            throws OrderValidationException {
        if(inOrderCancel == null) {
            throw new OrderValidationException(
                    Messages.NO_ORDER_SUPPLIED);
        }
        //Validate order's fields.
        validate(inOrderCancel.getOrderID() == null,
                Messages.VALIDATION_ORDERID);
        validate(inOrderCancel.getOriginalOrderID() == null,
                Messages.VALIDATION_ORIG_ORDERID);
        validate(inOrderCancel.getQuantity() == null,
                Messages.VALIDATION_ORDER_QUANTITY);
        validate(inOrderCancel.getSide() == null,
                Messages.VALIDATION_ORDER_SIDE);
        validateInstrument(inOrderCancel.getInstrument());
    }

    /**
     * Validates a suggestion. Following validations are performed.
     * <ul>
     *  <li>suggestion is not null</li>
     *  <li>has a non-null identifier</li>
     *  <li>has a non-null score</li>
     *  <li>has a non-null order</li>
     * </ul>
     *
     * @param inSuggestion The suggestion that needs to be validated.
     *
     * @throws OrderValidationException if any of the validations fail.
     *
     */
    public static void validate(OrderSingleSuggestion inSuggestion)
            throws OrderValidationException {
        if(inSuggestion == null) {
            throw new OrderValidationException(
                    Messages.NO_SUGGEST_SUPPLIED);
        }
        validate(inSuggestion.getIdentifier() == null,
                Messages.VALIDATION_SUGGEST_IDENTIFIER);
        validate(inSuggestion.getScore() == null,
                Messages.VALIDATION_SUGGEST_SCORE);
        validate(inSuggestion.getOrder() == null,
                Messages.VALIDATION_SUGGEST_ORDER);
    }

    /**
     * Validates an instrument.
     * <p>
     * Verifies that the instrument is non-null and performs instrument
     * specific validation using {@link InstrumentValidationHandler}.
     *
     * @param inInstrument the instrument
     *
     * @throws OrderValidationException if the instrument fails validation.
     */
    public static void validateInstrument(Instrument inInstrument)
            throws OrderValidationException {
        validate(inInstrument == null, Messages.VALIDATION_ORDER_INSTRUMENT);
        try {
            InstrumentValidationHandler.SELECTOR.forInstrument(inInstrument).
                validate(inInstrument);
        } catch (IllegalArgumentException e) {
            throw new OrderValidationException(e, new I18NBoundMessage1P(
                    Messages.VALIDATION_UNKNOWN_INSTRUMENT, inInstrument));
        }
    }
    private static void validate(boolean inValidationFailed,
                                   I18NMessage0P inMessage)
            throws OrderValidationException {
        if(inValidationFailed) {
            throw new OrderValidationException(inMessage);
        }
    }
    private Validations() {
        //Utility class. Has no instances
    }
}
