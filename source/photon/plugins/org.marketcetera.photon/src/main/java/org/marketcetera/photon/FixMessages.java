package org.marketcetera.photon;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessageProvider;

/* $License$ */

/**
 * External message constants for FIX messages in Photon.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
 * @since $Release$
 */
@ClassVersion("$Id: FIXFieldLocalizer.java 7186 2008-05-01 21:36:09Z tlerios $") //$NON-NLS-1$
public interface FixMessages
{
    static final I18NMessageProvider PROVIDER = new I18NMessageProvider("photon_fix"); //$NON-NLS-1$
    static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);

    public static I18NMessage0P fix_field_Account = new I18NMessage0P(LOGGER,
                                                                      "fix.field.Account");
    public static I18NMessage0P fix_field_AvgPx = new I18NMessage0P(LOGGER,
                                                                    "fix.field.AvgPx");
    public static I18NMessage0P fix_field_ClOrdID = new I18NMessage0P(LOGGER,
                                                                      "fix.field.ClOrdID");
    public static I18NMessage0P fix_field_CumQty = new I18NMessage0P(LOGGER,
                                                                     "fix.field.CumQty");
    public static I18NMessage0P fix_field_D = new I18NMessage0P(LOGGER,
                                                                "fix.field.D");
    public static I18NMessage0P fix_field_ExecID = new I18NMessage0P(LOGGER,
                                                                     "fix.field.ExecID");
    public static I18NMessage0P fix_field_LastMkt = new I18NMessage0P(LOGGER,
                                                                      "fix.field.LastMkt");
    public static I18NMessage0P fix_field_LastPx = new I18NMessage0P(LOGGER,
                                                                     "fix.field.LastPx");
    public static I18NMessage0P fix_field_LastShares = new I18NMessage0P(LOGGER,
                                                                         "fix.field.LastShares");
    public static I18NMessage0P fix_field_LeavesQty = new I18NMessage0P(LOGGER,
                                                                        "fix.field.LeavesQty");
    public static I18NMessage0P fix_field_MdentryPx = new I18NMessage0P(LOGGER,
                                                                        "fix.field.MdentryPx");
    public static I18NMessage0P fix_field_MdentrySize = new I18NMessage0P(LOGGER,
                                                                          "fix.field.MdentrySize");
    public static I18NMessage0P fix_field_MdentryTime = new I18NMessage0P(LOGGER,
                                                                          "fix.field.MdentryTime");
    public static I18NMessage0P fix_field_MdMkt = new I18NMessage0P(LOGGER,
                                                                    "fix.field.MdMkt");
    public static I18NMessage0P fix_field_MsgType = new I18NMessage0P(LOGGER,
                                                                      "fix.field.MsgType");
    public static I18NMessage0P fix_field_OrderID = new I18NMessage0P(LOGGER,
                                                                      "fix.field.OrderID");
    public static I18NMessage0P fix_field_OrderQty = new I18NMessage0P(LOGGER,
                                                                       "fix.field.OrderQty");
    public static I18NMessage0P fix_field_OrdStatus = new I18NMessage0P(LOGGER,
                                                                        "fix.field.OrdStatus");
    public static I18NMessage0P fix_field_OrdType = new I18NMessage0P(LOGGER,
                                                                      "fix.field.OrdType");
    public static I18NMessage0P fix_field_OrigClOrdID = new I18NMessage0P(LOGGER,
                                                                          "fix.field.OrigClOrdID");
    public static I18NMessage0P fix_field_Price = new I18NMessage0P(LOGGER,
                                                                    "fix.field.Price");
    public static I18NMessage0P fix_field_RefseqNum = new I18NMessage0P(LOGGER,
                                                                        "fix.field.RefseqNum");
    public static I18NMessage0P fix_field_SendingTime = new I18NMessage0P(LOGGER,
                                                                          "fix.field.SendingTime");
    public static I18NMessage0P fix_field_SessionRejectReason = new I18NMessage0P(LOGGER,
                                                                                  "fix.field.SessionRejectReason");
    public static I18NMessage0P fix_field_Side = new I18NMessage0P(LOGGER,
                                                                   "fix.field.Side");
    public static I18NMessage0P fix_field_Symbol = new I18NMessage0P(LOGGER,
                                                                     "fix.field.Symbol");
    public static I18NMessage0P fix_field_TransactTime = new I18NMessage0P(LOGGER,
                                                                           "fix.field.TransactTime");
    public static I18NMessage0P fix_field_value_OrdStatus_PARTIALLY_FILLED = new I18NMessage0P(LOGGER,
                                                                                               "fix.field.value.OrdStatus.PARTIALLY FILLED");
    public static I18NMessage0P fix_field_value_OrdStatus_PENDING_CANCEL = new I18NMessage0P(LOGGER,
                                                                                             "fix.field.value.OrdStatus.PENDING CANCEL");
    public static I18NMessage0P fix_field_value_OrdStatus_PENDING_REPLACE = new I18NMessage0P(LOGGER,
                                                                                              "fix.field.value.OrdStatus.PENDING REPLACE");
    public static I18NMessage0P fix_field_value_OrdStatus_PENDING_NEW = new I18NMessage0P(LOGGER,
                                                                                          "fix.field.value.OrdStatus.PENDING NEW");
    public static I18NMessage0P fix_field_value_OrdType_LIMIT = new I18NMessage0P(LOGGER,
                                                                                  "fix.field.value.OrdType.LIMIT");
    public static I18NMessage0P fix_field_value_OrdType_MARKET = new I18NMessage0P(LOGGER,
                                                                                   "fix.field.value.OrdType.MARKET");
    public static I18NMessage0P fix_field_value_OrdType_LIMIT_ON_CLOSE = new I18NMessage0P(LOGGER,
                                                                                           "fix.field.value.OrdType.LIMIT ON CLOSE");
    public static I18NMessage0P fix_field_value_OrdType_MARKET_ON_CLOSE = new I18NMessage0P(LOGGER,
                                                                                            "fix.field.value.OrdType.MARKET ON CLOSE");
    public static I18NMessage0P fix_field_value_OrdType_FOREX_LIMIT = new I18NMessage0P(LOGGER,
                                                                                        "fix.field.value.OrdType.FOREX_LIMIT");
    public static I18NMessage0P fix_field_value_OrdType_FOREX_MARKET = new I18NMessage0P(LOGGER,
                                                                                         "fix.field.value.OrdType.FOREX_MARKET");
    public static I18NMessage0P fix_field_value_TimeInForce_GOOD_TILL_CANCEL = new I18NMessage0P(LOGGER,
                                                                                                 "fix.field.value.TimeInForce.GOOD TILL CANCEL");
    public static I18NMessage0P fix_field_value_TimeInForce_AT_THE_OPENING = new I18NMessage0P(LOGGER,
                                                                                               "fix.field.value.TimeInForce.AT THE OPENING");
    public static I18NMessage0P fix_field_value_TimeInForce_IMMEDIATE_OR_CANCEL = new I18NMessage0P(LOGGER,
                                                                                                    "fix.field.value.TimeInForce.IMMEDIATE OR CANCEL");
    public static I18NMessage0P fix_field_value_TimeInForce_FILL_OR_KILL = new I18NMessage0P(LOGGER,
                                                                                             "fix.field.value.TimeInForce.FILL OR KILL");
    public static I18NMessage0P fix_field_value_TimeInForce_AT_THE_CLOSE = new I18NMessage0P(LOGGER,
                                                                                             "fix.field.value.TimeInForce.AT THE CLOSE");
    public static I18NMessage0P fix_field_value_Side_BUY = new I18NMessage0P(LOGGER,
                                                                             "fix.field.value.Side.BUY");
    public static I18NMessage0P fix_field_value_Side_SELL = new I18NMessage0P(LOGGER,
                                                                              "fix.field.value.Side.SELL");
    public static I18NMessage0P fix_field_value_Side_SELL_SHORT = new I18NMessage0P(LOGGER,
                                                                                    "fix.field.value.Side.SELL SHORT");
}
