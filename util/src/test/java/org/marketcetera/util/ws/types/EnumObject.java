package org.marketcetera.util.ws.types;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id: EnumObject.java 16154 2012-07-14 16:34:05Z colin $
 */

/* $License$ */

public enum EnumObject
{
    ONE("One"),
    TWO("Two");


    private final String mName;


    EnumObject(String name)
    {
        mName=name;
    }


    public String getName()
    {
        return mName;
    }
}
