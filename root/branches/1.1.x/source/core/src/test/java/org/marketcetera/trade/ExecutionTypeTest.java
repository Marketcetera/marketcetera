package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;
import static org.marketcetera.trade.ExecutionType.*;
import org.marketcetera.core.Pair;

import java.util.Arrays;
import java.util.List;

import quickfix.field.ExecType;

/* $License$ */
/**
 * Tests {@link ExecutionType}
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
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
    protected List<Pair<ExecutionType,Character>> knownValues() {
        return Arrays.asList(
                new Pair<ExecutionType, Character>(New, ExecType.NEW),
                new Pair<ExecutionType, Character>(DoneForDay, ExecType.DONE_FOR_DAY),
                new Pair<ExecutionType, Character>(Canceled, ExecType.CANCELED),
                new Pair<ExecutionType, Character>(Replace, ExecType.REPLACE),
                new Pair<ExecutionType, Character>(PendingCancel, ExecType.PENDING_CANCEL),
                new Pair<ExecutionType, Character>(Stopped, ExecType.STOPPED),
                new Pair<ExecutionType, Character>(Rejected, ExecType.REJECTED),
                new Pair<ExecutionType, Character>(Suspended, ExecType.SUSPENDED),
                new Pair<ExecutionType, Character>(PendingNew, ExecType.PENDING_NEW),
                new Pair<ExecutionType, Character>(Calculated, ExecType.CALCULATED),
                new Pair<ExecutionType, Character>(Expired, ExecType.EXPIRED),
                new Pair<ExecutionType, Character>(Restated, ExecType.RESTATED),
                new Pair<ExecutionType, Character>(PendingReplace, ExecType.PENDING_REPLACE),
                new Pair<ExecutionType, Character>(Trade, ExecType.TRADE),
                new Pair<ExecutionType, Character>(TradeCorrect, ExecType.TRADE_CORRECT),
                new Pair<ExecutionType, Character>(TradeCancel, ExecType.TRADE_CANCEL),
                new Pair<ExecutionType, Character>(OrderStatus, ExecType.ORDER_STATUS)
        );
    }
}
