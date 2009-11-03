package org.marketcetera.photon.internal.strategy.ui;

import org.marketcetera.photon.commons.ReflectiveMessages;
import org.marketcetera.photon.commons.ui.LocalizedLabel;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NMessage2P;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * The internationalization constants used by this package.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")//$NON-NLS-1$
final class Messages {
	
    static I18NMessage0P NEW_STRATEGY_WIZARD_PAGE_DESCRIPTION;
    static LocalizedLabel NEW_STRATEGY_WIZARD_PAGE_FOLDER;
    static LocalizedLabel NEW_STRATEGY_WIZARD_PAGE_CLASS_NAME;
    static I18NMessage0P NEW_STRATEGY_WIZARD_PAGE_BROWSE_BUTTON__TEXT;
    static I18NMessage0P NEW_STRATEGY_WIZARD_PAGE_CONTAINER_SELECTION_INSTRUCTIONS;
    static I18NMessage2P ABSTRACT_NEW_STRATEGY_WIZARD_CREATION_FAILED;
    static I18NMessage0P NEW_JAVA_STRATEGY_WIZARD_INVALID_CLASS_NAME;
    static I18NMessage0P NEW_RUBY_STRATEGY_WIZARD_INVALID_CLASS_NAME;
    
    static I18NMessage0P UNDEPLOY_DELETE_PARTICIPANT__NAME;
    static I18NMessage1P UNDEPLOY_DELETE_PARTICIPANT_CHANGE_GROUP__DESCRIPTION;
    static I18NMessage2P UNDEPLOY_DELETE_PARTICIPANT_CHANGE__DESCRIPTION;
    
    static I18NMessage1P ABSTRACT_NEW_STRATEGY_WIZARD_CREATING_FILE__TASK_NAME;
    static I18NMessage1P ABSTRACT_NEW_STRATEGY_WIZARD_MISSING_CONTAINER;
    static I18NMessage1P ABSTRACT_NEW_STRATEGY_WIZARD_FILE_EXISTS;
    static I18NMessage0P ABSTRACT_NEW_STRATEGY_WIZARD_OPENING_FILE__TASK_NAME;
	static I18NMessage0P NEW_RUBY_STRATEGY_WIZARD__TITLE;
    static I18NMessage0P NEW_JAVA_STRATEGY_WIZARD__TITLE;
    
	static I18NMessage0P TRADE_SUGGESTION_IDENTIFIER_LABEL;
	static I18NMessage0P TRADE_SUGGESTION_SIDE_LABEL;
	static I18NMessage0P TRADE_SUGGESTION_SECURITY_TYPE_LABEL;
	static I18NMessage0P TRADE_SUGGESTION_QUANTITY_LABEL;
	static I18NMessage0P TRADE_SUGGESTION_INSTRUMENT_LABEL;
	static I18NMessage0P TRADE_SUGGESTION_PRICE_LABEL;
	static I18NMessage0P TRADE_SUGGESTION_ORDER_TYPE_LABEL;
	static I18NMessage0P TRADE_SUGGESTION_TIME_IN_FORCE_LABEL;
	static I18NMessage0P TRADE_SUGGESTION_ORDER_CAPACITY_LABEL;
	static I18NMessage0P TRADE_SUGGESTION_POSITION_EFFECT_LABEL;
	static I18NMessage0P TRADE_SUGGESTION_SCORE_LABEL;
	static I18NMessage0P TRADE_SUGGESTION_ACCOUNT_LABEL;
	static I18NMessage0P TRADE_SUGGESTION_BROKER_ID_LABEL;
	static I18NMessage0P TRADE_SUGGESTION_TIMESTAMP_LABEL;
	static I18NMessage0P SEND_ORDERS_HANDLER_SERVER_FAILURE;
	static I18NMessage0P SEND_ORDERS_HANDLER_SEND_ORDERS_FAILURE;
	static I18NMessage1P SEND_ORDERS_HANDLER_SEND_ORDER_FAILURE;
	static I18NMessage0P SEND_ORDERS_HANDLER_SEND_ORDERS_FAILURE_SEE_DETAILS;
	static I18NMessage0P OPEN_SUGGESTION_HANDLER_CONVERSION_FAILURE;
	
	static {
        ReflectiveMessages.init(Messages.class);
    }

    private Messages() {
        throw new AssertionError("non-instantiable"); //$NON-NLS-1$
    }
}
