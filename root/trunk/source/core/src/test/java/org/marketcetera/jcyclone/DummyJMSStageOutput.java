package org.marketcetera.jcyclone;

import org.marketcetera.core.MarketceteraException;
import org.marketcetera.core.ClassVersion;
import quickfix.Message;

/**
 * Shell implementation of the {@link JMSStageOutput}
 * Saves the message internall instead of actually sending it through
 *
 * @author Toli Kuznets
 * @version $Id$
 */
@ClassVersion("$Id$")
public class DummyJMSStageOutput extends JMSStageOutput
{
    public DummyJMSStageOutput(Message msg, JMSOutputInfo inJMSOutputInfo) {
        super(msg, inJMSOutputInfo);
    }

    public void output() throws MarketceteraException {
        // do nothing
    }
}
