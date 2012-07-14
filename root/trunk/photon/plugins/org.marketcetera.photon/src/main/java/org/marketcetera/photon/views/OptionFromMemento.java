package org.marketcetera.photon.views;

import java.math.BigDecimal;

import org.eclipse.ui.IMemento;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;
import org.marketcetera.util.misc.ClassVersion;
import static org.marketcetera.photon.views.OptionToMemento.*;

/* $License$ */

/**
 * Facilitates deserialization of an {@link Option} from an Eclipse
 * {@link IMemento} .
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class OptionFromMemento extends InstrumentFromMemento {

    @Override
    protected boolean isHandled(IMemento inValue) {
        return inValue.getChildren(OPTION_TAG).length == 1;
    }

    @Override
    protected Option doRestore(IMemento memento) {
        IMemento option = memento.getChild(OPTION_TAG);
        return new Option(option.getString(SYMBOL_ATTRIBUTE), option
                .getString(EXPIRY_ATTRIBUTE), new BigDecimal(option
                .getString(STRIKE_ATTRIBUTE)), OptionType.valueOf(option
                .getString(TYPE_ATTRIBUTE)));
    }
}
