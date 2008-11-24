package org.marketcetera.util.ws.wrappers;

import java.util.Locale;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A dual-form wrapper for marshalling a locale. The raw form is
 * {@link Locale}, and the marshalled form is {@link
 * MarshalledLocale}.
 * 
 * @author tlerios@marketcetera.com
 * @since $Release$
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$") //$NON-NLS-1$
public class LocaleWrapper
    extends DualWrapper<Locale,MarshalledLocale>
{

    // CONSTRUCTORS.

    /**
     * Creates a new wrapper for the given locale. It also sets the
     * internal marshalled form to match.
     *
     * @param locale The locale, which may be null.
     */

    public LocaleWrapper
        (Locale locale)
    {
        super(locale);
    }

    /**
     * Creates a new wrapper. This empty constructor is intended for
     * use by JAXB.
     */

    protected LocaleWrapper() {}


    // DualWrapper.

    @Override
    protected void toRaw()
    {
        setRawOnly(getMarshalled().toLocale());
    }

    @Override
    protected void toMarshalled()
    {
        setMarshalledOnly(new MarshalledLocale(getRaw()));
    }
}
