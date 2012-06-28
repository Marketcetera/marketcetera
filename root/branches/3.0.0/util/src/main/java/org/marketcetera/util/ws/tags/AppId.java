package org.marketcetera.util.ws.tags;

import org.marketcetera.core.attributes.ClassVersion;

/**
 * An application ID. Each Java application has a unique application
 * ID, which is usually the name of the application. A single JVM
 * typically hosts a single application, but it is also possible to
 * have multiple ones (for example, a JVM that runs a platform
 * application, whose modules are considered separate hosted
 * applications). New IDs should be created using {@link
 * #AppId(String)}.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id: AppId.java 82324 2012-04-09 20:56:08Z colin $
 */

/* $License$ */

@ClassVersion("$Id: AppId.java 82324 2012-04-09 20:56:08Z colin $")
public class AppId
    extends Tag
{

    // CLASS DATA.

    private static final long serialVersionUID=1L;


    // CONSTRUCTORS.

    /**
     * Creates a new application ID with the given ID value.
     *
     * @param value The ID value.
     */

    public AppId
        (String value)
    {
        super(value);
    }

    /**
     * Creates a new application ID. This empty constructor is
     * intended for use by JAXB.
     */

    protected AppId() {}
}
