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
 * @version $Id$
 * @since 0.6.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public interface FixMessages
{
    static final I18NMessageProvider PROVIDER = new I18NMessageProvider("photon_fix"); //$NON-NLS-1$
    static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);

    public static I18NMessage0P fix_field_Account = new I18NMessage0P(LOGGER,
                                                                      "fix.field.Account"); //$NON-NLS-1$
    public static I18NMessage0P fix_field_AvgPx = new I18NMessage0P(LOGGER,
                                                                    "fix.field.AvgPx"); //$NON-NLS-1$
    public static I18NMessage0P fix_field_ClOrdID = new I18NMessage0P(LOGGER,
                                                                      "fix.field.ClOrdID"); //$NON-NLS-1$
    public static I18NMessage0P fix_field_CumQty = new I18NMessage0P(LOGGER,
                                                                     "fix.field.CumQty"); //$NON-NLS-1$
    public static I18NMessage0P fix_field_D = new I18NMessage0P(LOGGER,
                                                                "fix.field.D"); //$NON-NLS-1$
    public static I18NMessage0P fix_field_ExecID = new I18NMessage0P(LOGGER,
                                                                     "fix.field.ExecID"); //$NON-NLS-1$
    public static I18NMessage0P fix_field_LastMkt = new I18NMessage0P(LOGGER,
                                                                      "fix.field.LastMkt"); //$NON-NLS-1$
    public static I18NMessage0P fix_field_LastPx = new I18NMessage0P(LOGGER,
                                                                     "fix.field.LastPx"); //$NON-NLS-1$
    public static I18NMessage0P fix_field_LastShares = new I18NMessage0P(LOGGER,
                                                                         "fix.field.LastShares"); //$NON-NLS-1$
    public static I18NMessage0P fix_field_LeavesQty = new I18NMessage0P(LOGGER,
                                                                        "fix.field.LeavesQty"); //$NON-NLS-1$
    public static I18NMessage0P fix_field_MdentryPx = new I18NMessage0P(LOGGER,
                                                                        "fix.field.MdentryPx"); //$NON-NLS-1$
    public static I18NMessage0P fix_field_MdentrySize = new I18NMessage0P(LOGGER,
                                                                          "fix.field.MdentrySize"); //$NON-NLS-1$
    public static I18NMessage0P fix_field_MdentryTime = new I18NMessage0P(LOGGER,
                                                                          "fix.field.MdentryTime"); //$NON-NLS-1$
    public static I18NMessage0P fix_field_MdMkt = new I18NMessage0P(LOGGER,
                                                                    "fix.field.MdMkt"); //$NON-NLS-1$
    public static I18NMessage0P fix_field_MsgType = new I18NMessage0P(LOGGER,
                                                                      "fix.field.MsgType"); //$NON-NLS-1$
    public static I18NMessage0P fix_field_OrderID = new I18NMessage0P(LOGGER,
                                                                      "fix.field.OrderID"); //$NON-NLS-1$
    public static I18NMessage0P fix_field_OrderQty = new I18NMessage0P(LOGGER,
                                                                       "fix.field.OrderQty"); //$NON-NLS-1$
    public static I18NMessage0P fix_field_OrdStatus = new I18NMessage0P(LOGGER,
                                                                        "fix.field.OrdStatus"); //$NON-NLS-1$
    public static I18NMessage0P fix_field_OrdType = new I18NMessage0P(LOGGER,
                                                                      "fix.field.OrdType"); //$NON-NLS-1$
    public static I18NMessage0P fix_field_OrigClOrdID = new I18NMessage0P(LOGGER,
                                                                          "fix.field.OrigClOrdID"); //$NON-NLS-1$
    public static I18NMessage0P fix_field_Price = new I18NMessage0P(LOGGER,
                                                                    "fix.field.Price"); //$NON-NLS-1$
    public static I18NMessage0P fix_field_RefseqNum = new I18NMessage0P(LOGGER,
                                                                        "fix.field.RefseqNum"); //$NON-NLS-1$
    public static I18NMessage0P fix_field_SendingTime = new I18NMessage0P(LOGGER,
                                                                          "fix.field.SendingTime"); //$NON-NLS-1$
    public static I18NMessage0P fix_field_SessionRejectReason = new I18NMessage0P(LOGGER,
                                                                                  "fix.field.SessionRejectReason"); //$NON-NLS-1$
    public static I18NMessage0P fix_field_Side = new I18NMessage0P(LOGGER,
                                                                   "fix.field.Side"); //$NON-NLS-1$
    public static I18NMessage0P fix_field_Symbol = new I18NMessage0P(LOGGER,
                                                                     "fix.field.Symbol"); //$NON-NLS-1$
    public static I18NMessage0P fix_field_TransactTime = new I18NMessage0P(LOGGER,
                                                                           "fix.field.TransactTime"); //$NON-NLS-1$
    public static I18NMessage0P fix_field_value_OrdStatus_PARTIALLY_FILLED = new I18NMessage0P(LOGGER,
                                                                                               "fix.field.value.OrdStatus.PARTIALLY FILLED"); //$NON-NLS-1$
    public static I18NMessage0P fix_field_value_OrdStatus_PENDING_CANCEL = new I18NMessage0P(LOGGER,
                                                                                             "fix.field.value.OrdStatus.PENDING CANCEL"); //$NON-NLS-1$
    public static I18NMessage0P fix_field_value_OrdStatus_PENDING_REPLACE = new I18NMessage0P(LOGGER,
                                                                                              "fix.field.value.OrdStatus.PENDING REPLACE"); //$NON-NLS-1$
    public static I18NMessage0P fix_field_value_OrdStatus_PENDING_NEW = new I18NMessage0P(LOGGER,
                                                                                          "fix.field.value.OrdStatus.PENDING NEW"); //$NON-NLS-1$
    public static I18NMessage0P fix_field_value_OrdType_LIMIT = new I18NMessage0P(LOGGER,
                                                                                  "fix.field.value.OrdType.LIMIT"); //$NON-NLS-1$
    public static I18NMessage0P fix_field_value_OrdType_MARKET = new I18NMessage0P(LOGGER,
                                                                                   "fix.field.value.OrdType.MARKET"); //$NON-NLS-1$
    public static I18NMessage0P fix_field_value_OrdType_LIMIT_ON_CLOSE = new I18NMessage0P(LOGGER,
                                                                                           "fix.field.value.OrdType.LIMIT ON CLOSE"); //$NON-NLS-1$
    public static I18NMessage0P fix_field_value_OrdType_MARKET_ON_CLOSE = new I18NMessage0P(LOGGER,
                                                                                            "fix.field.value.OrdType.MARKET ON CLOSE"); //$NON-NLS-1$
    public static I18NMessage0P fix_field_value_OrdType_FOREX_LIMIT = new I18NMessage0P(LOGGER,
                                                                                        "fix.field.value.OrdType.FOREX_LIMIT"); //$NON-NLS-1$
    public static I18NMessage0P fix_field_value_OrdType_FOREX_MARKET = new I18NMessage0P(LOGGER,
                                                                                         "fix.field.value.OrdType.FOREX_MARKET"); //$NON-NLS-1$
    public static I18NMessage0P fix_field_value_TimeInForce_GOOD_TILL_CANCEL = new I18NMessage0P(LOGGER,
                                                                                                 "fix.field.value.TimeInForce.GOOD TILL CANCEL"); //$NON-NLS-1$
    public static I18NMessage0P fix_field_value_TimeInForce_AT_THE_OPENING = new I18NMessage0P(LOGGER,
                                                                                               "fix.field.value.TimeInForce.AT THE OPENING"); //$NON-NLS-1$
    public static I18NMessage0P fix_field_value_TimeInForce_IMMEDIATE_OR_CANCEL = new I18NMessage0P(LOGGER,
                                                                                                    "fix.field.value.TimeInForce.IMMEDIATE OR CANCEL"); //$NON-NLS-1$
    public static I18NMessage0P fix_field_value_TimeInForce_FILL_OR_KILL = new I18NMessage0P(LOGGER,
                                                                                             "fix.field.value.TimeInForce.FILL OR KILL"); //$NON-NLS-1$
    public static I18NMessage0P fix_field_value_TimeInForce_AT_THE_CLOSE = new I18NMessage0P(LOGGER,
                                                                                             "fix.field.value.TimeInForce.AT THE CLOSE"); //$NON-NLS-1$
    public static I18NMessage0P fix_field_value_Side_BUY = new I18NMessage0P(LOGGER,
                                                                             "fix.field.value.Side.BUY"); //$NON-NLS-1$
    public static I18NMessage0P fix_field_value_Side_SELL = new I18NMessage0P(LOGGER,
                                                                              "fix.field.value.Side.SELL"); //$NON-NLS-1$
    public static I18NMessage0P fix_field_value_Side_SELL_SHORT = new I18NMessage0P(LOGGER,
                                                                                    "fix.field.value.Side.SELL SHORT"); //$NON-NLS-1$
}
