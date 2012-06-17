package org.marketcetera.util.ws.types;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id: EnumObject.java 82324 2012-04-09 20:56:08Z colin $
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
