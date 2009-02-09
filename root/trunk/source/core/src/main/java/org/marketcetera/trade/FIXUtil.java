package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;
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
 * @since 1.0.0
 */
@ClassVersion("$Id$")
class FIXUtil {
    static String getAccount(Message inMessage) {
        if (inMessage.isSetField(Account.FIELD)) {
            try {
                return inMessage.getString(Account.FIELD);
            } catch (FieldNotFound ignore) {
            }
        }
        return null;
    }
    static OrderType getOrderType(Message inMessage) {
        if (inMessage.isSetField(OrdType.FIELD)) {
            try {
                return OrderType.getInstanceForFIXValue(inMessage.getChar(
                        OrdType.FIELD));
            } catch (FieldNotFound ignore) {
            }
        }
        return null;
    }
    static OrderID getOriginalOrderID(Message inMessage) {
        if (inMessage.isSetField(OrigClOrdID.FIELD)) {
            try {
                String value = inMessage.getString(
                        OrigClOrdID.FIELD);
                return value == null
                        ? null
                        : new OrderID(value);
            } catch (FieldNotFound ignore) {
            }
        }
        return null;
    }
    static OrderStatus getOrderStatus(Message inMessage) {
        if (inMessage.isSetField(OrdStatus.FIELD)) {
            try {
                return OrderStatus.getInstanceForFIXValue(inMessage.getChar(
                        OrdStatus.FIELD));
            } catch (FieldNotFound ignore) {
            }
        }
        return null;
    }
    static OrderID getOrderID(Message inMessage) {
        if (inMessage.isSetField(ClOrdID.FIELD)) {
            try {
                String value = inMessage.getString(
                        ClOrdID.FIELD);
                return value == null
                        ? null
                        : new OrderID(value);
            } catch (FieldNotFound ignore) {
            }
        }
        return null;
    }
    static void setOrderID(Message inMessage, String inOrderID) {
        inMessage.setField(new ClOrdID(inOrderID));
    }
    static BigDecimal getPrice(Message inMessage) {
        if (inMessage.isSetField(Price.FIELD)) {
            try {
                return inMessage.getDecimal(Price.FIELD);
            } catch (FieldNotFound ignore) {
            }
        }
        return null;
    }
    static BigDecimal getOrderQuantity(Message inMessage) {
        if (inMessage.isSetField(OrderQty.FIELD)) {
            try {
                return inMessage.getDecimal(OrderQty.FIELD);
            } catch (FieldNotFound ignore) {
            }
        }
        return null;
    }
    static Side getSide(Message inMessage) {
        if (inMessage.isSetField(quickfix.field.Side.FIELD)) {
            try {
                return Side.getInstanceForFIXValue(inMessage.getChar(
                        quickfix.field.Side.FIELD));
            } catch (FieldNotFound ignore) {
            }
        }
        return null;
    }
    static OrderCapacity getOrderCapacity(Message inMessage) {
        if (inMessage.isSetField(quickfix.field.OrderCapacity.FIELD)) {
            try {
                return OrderCapacity.getInstanceForFIXValue(inMessage.getChar(
                        quickfix.field.OrderCapacity.FIELD));
            } catch (FieldNotFound ignore) {
            }
        }
        return null;
    }
    static PositionEffect getPositionEffect(Message inMessage) {
        if (inMessage.isSetField(quickfix.field.PositionEffect.FIELD)) {
            try {
                return PositionEffect.getInstanceForFIXValue(inMessage.getChar(
                        quickfix.field.PositionEffect.FIELD));
            } catch (FieldNotFound ignore) {
            }
        }
        return null;
    }
    static TimeInForce getTimeInForce(Message inMessage) {
        if (inMessage.isSetField(quickfix.field.TimeInForce.FIELD)) {
            try {
                return TimeInForce.getInstanceForFIXValue(inMessage.getChar(
                        quickfix.field.TimeInForce.FIELD));
            } catch (FieldNotFound ignore) {
            }
        }
        return null;
    }
    static MSymbol getSymbol(Message inMessage) {
        String symbol = null;
        String securityType = null;
        if (inMessage.isSetField(Symbol.FIELD)) {
            try {
                symbol = inMessage.getString(Symbol.FIELD);
            } catch (FieldNotFound ignore) {
            }
        }
        if (inMessage.isSetField(quickfix.field.SecurityType.FIELD)) {
            try {
                securityType = inMessage.getString(
                        quickfix.field.SecurityType.FIELD);
            } catch (FieldNotFound ignore) {
            }
        }
        if(symbol != null) {
            if(securityType != null) {
                return new MSymbol(symbol, SecurityType.getInstanceForFIXValue(securityType));
            } else {
                return new MSymbol(symbol);
            }
        }
        return null;
    }
    static Date getTransactTime(Message inMessage) {
        if (inMessage.isSetField(TransactTime.FIELD)) {
            try {
                return inMessage.getUtcTimeStamp(TransactTime.FIELD);
            } catch (FieldNotFound ignore) {
            }
        }
        return null;
    }
    static Date getSendingTime(Message inMessage) {
        if (inMessage.getHeader().isSetField(SendingTime.FIELD)) {
            try {
                return inMessage.getHeader().getUtcTimeStamp(SendingTime.FIELD);
            } catch (FieldNotFound ignore) {
            }
        }
        return null;
    }
    static String getExecutionID(Message inMessage) {
        if (inMessage.isSetField(ExecID.FIELD)) {
            try {
                return inMessage.getString(ExecID.FIELD);
            } catch (FieldNotFound ignore) {
            }
        }
        return null;
    }
    static BigDecimal getLastQuantity(Message inMessage) {
        if (inMessage.isSetField(LastShares.FIELD)) {
            try {
                return inMessage.getDecimal(LastShares.FIELD);
            } catch (FieldNotFound ignore) {
            }
        }
        return null;
    }
    static BigDecimal getLastPrice(Message inMessage) {
        if (inMessage.isSetField(LastPx.FIELD)) {
            try {
                return inMessage.getDecimal(LastPx.FIELD);
            } catch (FieldNotFound ignore) {
            }
        }
        return null;
    }
    static String getLastMarket(Message inMessage) {
        if (inMessage.isSetField(LastMkt.FIELD)) {
            try {
                return inMessage.getString(LastMkt.FIELD);
            } catch (FieldNotFound ignore) {
            }
        }
        return null;
    }

    static BigDecimal getLeavesQuantity(Message inMessage) {
        if (inMessage.isSetField(LeavesQty.FIELD)) {
            try {
                return inMessage.getDecimal(LeavesQty.FIELD);
            } catch (FieldNotFound ignore) {
            }
        }
        return null;
    }

    static BigDecimal getCumulativeQuantity(Message inMessage) {
        if (inMessage.isSetField(CumQty.FIELD)) {
            try {
                return inMessage.getDecimal(CumQty.FIELD);
            } catch (FieldNotFound ignore) {
            }
        }
        return null;
    }

    static BigDecimal getAveragePrice(Message inMessage) {
        if (inMessage.isSetField(AvgPx.FIELD)) {
            try {
                return inMessage.getDecimal(AvgPx.FIELD);
            } catch (FieldNotFound ignore) {
            }
        }
        return null;
    }
    static ExecutionType getExecOrExecTransType(Message inMessage) {
        if (inMessage.isSetField(ExecType.FIELD)) {
            try {
                return ExecutionType.getInstanceForFIXValue(
                        inMessage.getChar(ExecType.FIELD));
            } catch (FieldNotFound ignore) {
            }
        }
        //See if ExecTransType is available (FIX v < 4.3)
        if (inMessage.isSetField(ExecTransType.FIELD)) {
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
        }
        return null;
    }
    static SecurityType getSecurityType(Message inMessage) {
        if (inMessage.isSetField(quickfix.field.SecurityType.FIELD)) {
            try {
                return SecurityType.getInstanceForFIXValue(
                        inMessage.getString(
                                quickfix.field.SecurityType.FIELD));
            } catch (FieldNotFound ignore) {
            }
        }
        return null;
    }
    static String getText(Message inMessage) {
        if (inMessage.isSetField(Text.FIELD)) {
            try {
                return inMessage.getString(Text.FIELD);
            } catch (FieldNotFound ignore) {
            }
        }
        return null;
    }
    static String getBrokerOrderID(Message inMessage) {
        if (inMessage.isSetField(quickfix.field.OrderID.FIELD)) {
            try {
                return inMessage.getString(quickfix.field.OrderID.FIELD);
            } catch (FieldNotFound ignore) {
            }
        }
        return null;
    }

    private FIXUtil() {
    }
}
