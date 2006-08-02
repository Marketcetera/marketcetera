package org.marketcetera.core;

/**
 * Marker exception for anything that goes wrong during
 * initial app startup
 * @author Toli Kuznets
 * @version $Id$
 */
@ClassVersion("$Id$")
public class InitializationException extends MarketceteraException {
    public InitializationException(String message) {
        super(message);
    }

    public InitializationException(String msg, Throwable nested) {
        super(msg, nested);
    }

    public InitializationException(Throwable nested) {
        super(nested);
    }
}
