package org.marketcetera.matp.domain;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class Greeting
{
    /**
     * Create a new Greeting instance.
     *
     * @param inId
     * @param inContent
     */
    public Greeting(long inId,
                    String inContent)
    {
        id = inId;
        content = inContent;
    }
    /**
     * Get the id value.
     *
     * @return a <code>long</code> value
     */
    public long getId()
    {
        return id;
    }
    /**
     * Get the content value.
     *
     * @return a <code>String</code> value
     */
    public String getContent()
    {
        return content;
    }
    private final long id;
    private final String content;
}
