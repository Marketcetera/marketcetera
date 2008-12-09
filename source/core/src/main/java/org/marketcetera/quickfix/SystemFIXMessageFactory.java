package org.marketcetera.quickfix;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.quickfix.messagefactory.FIXMessageAugmentor;
import org.marketcetera.quickfix.messagefactory.SystemMessageFactory;
import org.marketcetera.quickfix.messagefactory.FIXMessageAugmentor_44;
import quickfix.MessageFactory;
import quickfix.Message;
import quickfix.field.*;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Collections;

/* $License$ */
/**
 * A message factory for creating {@link FIXVersion#FIX_SYSTEM System}
 * FIX Messages. Using this message factory ensures that we do not
 * add any fields on the messages that should not be added by a client
 * application.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class SystemFIXMessageFactory extends FIXMessageFactory {
    /**
     * Creates an instance.
     */
    public SystemFIXMessageFactory() {
        super(FIXDataDictionary.FIX_SYSTEM_BEGIN_STRING,
                new SystemMessageFactory(),
                new FIXMessageAugmentor_44());
    }

    @Override
    public void addTransactionTimeIfNeeded(Message msg) {
        //Do not add transact time field.
    }

    @Override
    protected void addHandlingInst(Message inMessage) {
        //Do not add handling instruction field.
    }

    @Override
    protected void fillFieldsFromExistingMessage(Message oldMessage,
                                                 boolean onlyCopyRequiredFields,
                                                 Message inCancelMessage) {
        Set<Integer> inclusionSet = null;
        if(FIXMessageUtil.isCancelRequest(inCancelMessage)) {
            inclusionSet = ORDER_CANCEL_FIELDS;
        } else if (FIXMessageUtil.isCancelReplaceRequest(inCancelMessage)) {
            inclusionSet = ORDER_REPLACE_FIELDS;
        }
        FIXMessageUtil.fillFieldsFromExistingMessage(inCancelMessage, 
                oldMessage,
                onlyCopyRequiredFields, inclusionSet);
    }

    @Override
    protected void addSendingTime(Message inCancelMessage) {
        //Do not add sending time.
    }

    public static final Set<Integer> ORDER_SINGLE_FIELDS;
    public static final Set<Integer> ORDER_CANCEL_FIELDS;
    public static final Set<Integer> ORDER_REPLACE_FIELDS;
    static {
        Set<Integer> tmp = new HashSet<Integer>();
        tmp.addAll(Arrays.asList(
                ClOrdID.FIELD,
                Account.FIELD,
                OrdType.FIELD,
                Price.FIELD,
                OrderQty.FIELD,
                quickfix.field.Side.FIELD,
                Symbol.FIELD,
                quickfix.field.SecurityType.FIELD,
                quickfix.field.TimeInForce.FIELD,
                quickfix.field.OrderCapacity.FIELD,
                quickfix.field.PositionEffect.FIELD
        ));
        ORDER_SINGLE_FIELDS = Collections.unmodifiableSet(tmp);
        tmp = new HashSet<Integer>();
        tmp.addAll(Arrays.asList(
                ClOrdID.FIELD,
                quickfix.field.OrderID.FIELD,
                Account.FIELD,
                OrigClOrdID.FIELD,
                OrderQty.FIELD,
                quickfix.field.Side.FIELD,
                Symbol.FIELD,
                quickfix.field.SecurityType.FIELD
        ));
        ORDER_CANCEL_FIELDS = Collections.unmodifiableSet(tmp);
        tmp = new HashSet<Integer>();
        tmp.addAll(Arrays.asList(
                ClOrdID.FIELD,
                quickfix.field.OrderID.FIELD,
                Account.FIELD,
                OrdType.FIELD,
                OrigClOrdID.FIELD,
                Price.FIELD,
                OrderQty.FIELD,
                quickfix.field.Side.FIELD,
                Symbol.FIELD,
                quickfix.field.SecurityType.FIELD,
                quickfix.field.TimeInForce.FIELD,
                quickfix.field.OrderCapacity.FIELD,
                quickfix.field.PositionEffect.FIELD
        ));
        ORDER_REPLACE_FIELDS = Collections.unmodifiableSet(tmp);
    }
}
