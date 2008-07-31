package org.marketcetera.marketdata;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.CoreException;
import org.marketcetera.util.log.I18NBoundMessage;

/**
 * Indicates an error throw during Market Data Feed operations.
 * 
 * @author Graham Miller
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class FeedException 
    extends CoreException
{
    private static final long serialVersionUID = 1963381129325900265L;

    public FeedException(Throwable nested, I18NBoundMessage message) {
        super(nested, message);
    }

    public FeedException(I18NBoundMessage message) {
        super(message);
    }

    public FeedException(Throwable nested) {
        super(nested);
    }

}
