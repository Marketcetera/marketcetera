package org.marketcetera.core.trade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.marketcetera.core.Pair;
import org.marketcetera.core.attributes.ClassVersion;
import quickfix.field.ExecType;

import static org.marketcetera.core.trade.ExecutionType.*;
import static org.marketcetera.core.trade.ExecutionType.OrderStatus;

/* $License$ */
/**
 * Tests {@link org.marketcetera.core.trade.ExecutionType}
 *
 * @author anshul@marketcetera.com
 * @version $Id: ExecutionTypeTest.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.0.0
 */
@ClassVersion("$Id: ExecutionTypeTest.java 16063 2012-01-31 18:21:55Z colin $") //$NON-NLS-1$
public class ExecutionTypeTest extends FIXCharEnumTestBase<ExecutionType> {
    @Override
    protected ExecutionType getInstanceForFIXValue(Character inFIXValue) {
        return ExecutionType.getInstanceForFIXValue(inFIXValue);
    }

    @Override
    protected Character getFIXValue(ExecutionType e) {
        return e.getFIXValue();
    }

    @Override
    protected ExecutionType unknownInstance() {
        return Unknown;
    }

    @Override
    protected List<ExecutionType> getValues() {
        return Arrays.asList(ExecutionType.values());
    }

    @Override
    protected List<Pair<ExecutionType,Character>> knownValues()
    {
        List<Pair<ExecutionType,Character>> values = new ArrayList<Pair<ExecutionType,Character>>();
        values.add(new Pair<ExecutionType, Character>(New, ExecType.NEW));
        values.add(new Pair<ExecutionType, Character>(PartialFill, ExecType.PARTIAL_FILL));
        values.add(new Pair<ExecutionType, Character>(Fill, ExecType.FILL));
        values.add(new Pair<ExecutionType, Character>(DoneForDay, ExecType.DONE_FOR_DAY));
        values.add(new Pair<ExecutionType, Character>(Canceled, ExecType.CANCELED));
        values.add(new Pair<ExecutionType, Character>(Replace, ExecType.REPLACE));
        values.add(new Pair<ExecutionType, Character>(PendingCancel, ExecType.PENDING_CANCEL));
        values.add(new Pair<ExecutionType, Character>(Stopped, ExecType.STOPPED));
        values.add(new Pair<ExecutionType, Character>(Rejected, ExecType.REJECTED));
        values.add(new Pair<ExecutionType, Character>(Suspended, ExecType.SUSPENDED));
        values.add(new Pair<ExecutionType, Character>(PendingNew, ExecType.PENDING_NEW));
        values.add(new Pair<ExecutionType, Character>(Calculated, ExecType.CALCULATED));
        values.add(new Pair<ExecutionType, Character>(Expired, ExecType.EXPIRED));
        values.add(new Pair<ExecutionType, Character>(Restated, ExecType.RESTATED));
        values.add(new Pair<ExecutionType, Character>(PendingReplace, ExecType.PENDING_REPLACE));
        values.add(new Pair<ExecutionType, Character>(Trade, ExecType.TRADE));
        values.add(new Pair<ExecutionType, Character>(TradeCorrect, ExecType.TRADE_CORRECT));
        values.add(new Pair<ExecutionType, Character>(TradeCancel, ExecType.TRADE_CANCEL));
        values.add(new Pair<ExecutionType, Character>(OrderStatus, ExecType.ORDER_STATUS));
        return values;
    }
}
