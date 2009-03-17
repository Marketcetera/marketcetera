package org.marketcetera.util.ws.wrappers;

import java.util.Locale;
import org.apache.commons.lang.ObjectUtils;
import org.marketcetera.util.misc.ClassVersion;

/**
 * The marshalled form for a locale whose raw form is {@link Locale}.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class MarshalledLocale
{

    // INSTANCE DATA.

    private String mLanguage;
    private String mCountry;
    private String mVariant;


    // CONSTRUCTORS.

    /**
     * Creates a new marshalled form for the locale given in its raw
     * form.
     *
     * @param locale The locale, which may be null.
     */

    public MarshalledLocale
        (Locale locale)
    {
        if (locale==null) {
            return;
        }
        setLanguage(locale.getLanguage());
        setCountry(locale.getCountry());
        setVariant(locale.getVariant());
    }

    /**
     * Creates a new marshalled form. This empty constructor is
     * intended for use by JAXB.
     */

    protected MarshalledLocale() {}


    // INSTANCE METHODS.

    /**
     * Set the receiver's language to the given one.
     *
     * @param language The language.
     */

    public void setLanguage
        (String language)
    {
        mLanguage=language;
    }

    /**
     * Returns the receiver's language.
     *
     * @return The language.
     */

    public String getLanguage()
    {
        return mLanguage;
    }

    /**
     * Set the receiver's country to the given one.
     *
     * @param country The country.
     */

    public void setCountry
        (String country)
    {
        mCountry=country;
    }

    /**
     * Returns the receiver's country.
     *
     * @return The country.
     */

    public String getCountry()
    {
        return mCountry;
    }

    /**
     * Set the receiver's variant to the given one.
     *
     * @param variant The variant.
     */

    public void setVariant
        (String variant)
    {
        mVariant=variant;
    }

    /**
     * Returns the receiver's variant.
     *
     * @return The variant.
     */

    public String getVariant()
    {
        return mVariant;
    }

    /**
     * Returns the raw form of the receiver.
     *
     * @return The raw form, which is null if the receiver's language
     * is null (regardless of country or variant).
     */

    public Locale toLocale()
    {
        if (getLanguage()==null) {
            return null;
        }
        if (getCountry()==null) {
            return new Locale(getLanguage());
        }
        if (getVariant()==null) {
            return new Locale(getLanguage(),getCountry());
        }
        return new Locale(getLanguage(),getCountry(),getVariant());
    }


    // Object.

    @Override
    public String toString()
    {
        return ObjectUtils.toString(toLocale());
    }

    @Override
    public int hashCode()
    {
        return ObjectUtils.hashCode(toLocale());
    }

    @Override
    public boolean equals
        (Object other)
    {
        if (this==other) {
            return true;
        }
        if ((other==null) || !getClass().equals(other.getClass())) {
            return false;
        }
        MarshalledLocale o=(MarshalledLocale)other;
        return ObjectUtils.equals(toLocale(),o.toLocale());
    }
}
