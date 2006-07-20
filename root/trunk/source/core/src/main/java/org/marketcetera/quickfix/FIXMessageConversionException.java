package org.marketcetera.quickfix;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MarketceteraException;

/**
 * Helper exception that is thrown when trying to extract a field from a FIX message
 * that doesn't actually exist there
 * @author Toli Kuznets
 * @version $Id$
 */
@ClassVersion("$Id$")
public class FIXMessageConversionException extends MarketceteraException
{
    private String mField;
    public FIXMessageConversionException(String inFieldName)
    {
        super("Field "+ inFieldName + " does not exist in the message");
        mField = inFieldName;
    }

    public String getField()
    {
        return mField;
    }
}
