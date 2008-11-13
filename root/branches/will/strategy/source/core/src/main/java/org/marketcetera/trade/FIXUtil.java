package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.core.MSymbol;
import quickfix.field.*;
import quickfix.FieldNotFound;
import quickfix.Message;
import java.math.BigDecimal;
import java.util.Date;

/* $License$ */
/**
 * Utility class for FIX.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
class FIXUtil {
    static String getAccount(Message inMessage) {
        try {
            return inMessage.getString(Account.FIELD);
        } catch (FieldNotFound ignore) {
            return null;
        }
    }
    static OrderType getOrderType(Message inMessage) {
        try {
            return OrderType.getInstanceForFIXValue(inMessage.getChar(
                    OrdType.FIELD));
        } catch (FieldNotFound ignore) {
            return null;
        }
    }
    static OrderID getOriginalOrderID(Message inMessage) {
        try {
            String value = inMessage.getString(
                    OrigClOrdID.FIELD);
            return value == null
                    ? null
                    : new OrderID(value);
        } catch (FieldNotFound ignore) {
            return null;
        }
    }
    static OrderStatus getOrderStatus(Message inMessage) {
        try {
            return OrderStatus.getInstanceForFIXValue(inMessage.getChar(
                    OrdStatus.FIELD));
        } catch (FieldNotFound inFieldNotFound) {
            return null;
        }
    }
    static OrderID getOrderID(Message inMessage) {
        try {
            String value = inMessage.getString(
                    ClOrdID.FIELD);
            return value == null
                    ? null
                    : new OrderID(value);
        } catch (FieldNotFound ignore) {
            return null;
        }
    }
    static BigDecimal getPrice(Message inMessage) {
        try {
            return inMessage.getDecimal(Price.FIELD);
        } catch (FieldNotFound ignore) {
            return null;
        }
    }
    static BigDecimal getOrderQuantity(Message inMessage) {
        try {
            return inMessage.getDecimal(OrderQty.FIELD);
        } catch (FieldNotFound ignore) {
            return null;
        }
    }
    static Side getSide(Message inMessage) {
        try {
            return Side.getInstanceForFIXValue(inMessage.getChar(
                    quickfix.field.Side.FIELD));
        } catch (FieldNotFound ignore) {
            return null;
        }
    }
    static TimeInForce getTimeInForce(Message inMessage) {
        try {
            return TimeInForce.getInstanceForFIXValue(inMessage.getChar(
                    quickfix.field.TimeInForce.FIELD));
        } catch (FieldNotFound ignore) {
            return null;
        }
    }
    static MSymbol getSymbol(Message inMessage) {
        try {
            return new MSymbol(inMessage.getString(Symbol.FIELD),
                    SecurityType.getInstanceForFIXValue(
                            inMessage.getString(
                                    quickfix.field.SecurityType.FIELD)));
        } catch (FieldNotFound ignore) {
            return null;
        }
    }
    static Date getTransactTime(Message inMessage) {
        try {
            return inMessage.getUtcTimeStamp(TransactTime.FIELD);
        } catch (FieldNotFound ignore) {
            return null;
        }
    }
    static Date getSendingTime(Message inMessage) {
        try {
            return inMessage.getHeader().getUtcTimeStamp(SendingTime.FIELD);
        } catch (FieldNotFound ignore) {
            return null;
        }
    }
    static String getExecutionID(Message inMessage) {
        try {
            return inMessage.getString(ExecID.FIELD);
        } catch (FieldNotFound ignore) {
            return null;
        }
    }
    static BigDecimal getLastQuantity(Message inMessage) {
        try {
            return inMessage.getDecimal(LastShares.FIELD);
        } catch (FieldNotFound ignore) {
            return null;
        }
    }
    static BigDecimal getLastPrice(Message inMessage) {
        try {
            return inMessage.getDecimal(LastPx.FIELD);
        } catch (FieldNotFound ignore) {
            return null;
        }
    }
    static String getLastMarket(Message inMessage) {
        try {
            return inMessage.getString(LastMkt.FIELD);
        } catch (FieldNotFound ignore) {
            return null;
        }
    }

    static BigDecimal getLeavesQuantity(Message inMessage) {
        try {
            return inMessage.getDecimal(LeavesQty.FIELD);
        } catch (FieldNotFound ignore) {
            return null;
        }
    }

    static BigDecimal getCumulativeQuantity(Message inMessage) {
        try {
            return inMessage.getDecimal(CumQty.FIELD);
        } catch (FieldNotFound ignore) {
            return null;
        }
    }

    static BigDecimal getAveragePrice(Message inMessage) {
        try {
            return inMessage.getDecimal(AvgPx.FIELD);
        } catch (FieldNotFound ignore) {
            return null;
        }
    }
    static ExecutionType getExecOrExecTransType(Message inMessage) {
        try {
            return ExecutionType.getInstanceForFIXValue(
                    inMessage.getChar(ExecType.FIELD));
        } catch (FieldNotFound ignore) {
            //See if ExecTransType is available (FIX v < 4.3)
            try {
                char c = inMessage.getChar(ExecTransType.FIELD);
                switch(c) {
                    case ExecTransType.NEW:
                        return ExecutionType.New;
                    case ExecTransType.CANCEL:
                        return ExecutionType.TradeCancel;
                    case ExecTransType.CORRECT:
                        return ExecutionType.TradeCorrect;
                    case ExecTransType.STATUS:
                        return ExecutionType.OrderStatus;
                    default:
                        return ExecutionType.Unknown;
                }
            } catch (FieldNotFound ignored) {
            }
            return null;
        }
    }
    static SecurityType getSecurityType(Message inMessage) {
        try {
            return SecurityType.getInstanceForFIXValue(
                    inMessage.getString(
                            quickfix.field.SecurityType.FIELD));
        } catch (FieldNotFound inFieldNotFound) {
            return null;
        }
    }
    static String getText(Message inMessage) {
        try {
            return inMessage.getString(Text.FIELD);
        } catch (FieldNotFound ignore) {
            return null;
        }
    }

    private FIXUtil() {
    }
}
