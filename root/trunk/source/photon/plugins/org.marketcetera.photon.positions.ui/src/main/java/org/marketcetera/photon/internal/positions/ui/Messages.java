package org.marketcetera.photon.internal.positions.ui;

import org.marketcetera.photon.commons.ReflectiveMessages;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * The internationalization constants used by this package.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
final class Messages {

    static I18NMessage0P POSITIONS_VIEW_FILTER__LABEL;
    static I18NMessage0P POSITIONS_TABLE_GROUPING_COLUMN__HEADING;
    static I18NMessage0P POSITIONS_TABLE_UNDERLYING_COLUMN__HEADING;
    static I18NMessage0P POSITIONS_TABLE_ACCOUNT_COLUMN__HEADING;
    static I18NMessage0P POSITIONS_TABLE_TRADER_COLUMN__HEADING;
    static I18NMessage0P POSITIONS_TABLE_INSTRUMENT_COLUMN__HEADING;
    static I18NMessage0P POSITIONS_TABLE_OPTION_ROOT_COLUMN__HEADING;
    static I18NMessage0P POSITIONS_TABLE_OPTION_TYPE_COLUMN__HEADING;
    static I18NMessage0P POSITIONS_TABLE_OPTION_EXPIRY_COLUMN__HEADING;
    static I18NMessage0P POSITIONS_TABLE_OPTION_STRIKE_PRICE_COLUMN__HEADING;
    static I18NMessage0P POSITIONS_TABLE_INCOMING_COLUMN__HEADING;
    static I18NMessage0P POSITIONS_TABLE_POSITION_COLUMN__HEADING;
    static I18NMessage0P POSITIONS_TABLE_POSITION_PL_COLUMN__HEADING;
    static I18NMessage0P POSITIONS_TABLE_TRADING_PL_COLUMN__HEADING;
    static I18NMessage0P POSITIONS_TABLE_REALIZED_PL_COLUMN__HEADING;
    static I18NMessage0P POSITIONS_TABLE_UNREALIZED_PL_COLUMN__HEADING;
    static I18NMessage0P POSITIONS_TABLE_TOTAL_PL_COLUMN__HEADING;
    static I18NMessage0P POSITIONS_TABLE_EMPTY_KEY__LABEL;
    static I18NMessage0P POSITIONS_TABLE_UNKNOWN_VALUE__LABEL;
    static I18NMessage0P POSITIONS_TABLE_EQUITY__LABEL;
    static I18NMessage0P POSITIONS_TABLE_OPTION__LABEL;
    static I18NMessage0P POSITIONS_TABLE_FUTURE__LABEL;
    static I18NMessage0P GROUPING_CONTRIBUTION_ITEM_ACCOUNT__LABEL;
    static I18NMessage0P GROUPING_CONTRIBUTION_ITEM_UNDERLYING__LABEL;
    static I18NMessage0P GROUPING_CONTRIBUTION_ITEM_TRADER__LABEL;
    static I18NMessage0P UNAVAILABLE_PAGE_DESCRIPTION;
    static I18NMessage0P POSITIONS_VIEW_STATE_RESTORE_FAILURE;

    static {
        ReflectiveMessages.init(Messages.class);
    }

    private Messages() {
        throw new AssertionError("non-instantiable"); //$NON-NLS-1$
    }
}
