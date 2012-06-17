package com.marketcetera.marketdata.reuters;

import org.marketcetera.marketdata.Content;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: ReutersRequest.java 82348 2012-05-03 23:45:18Z colin $
 * @since $Release$
 */
@ClassVersion("$Id: ReutersRequest.java 82348 2012-05-03 23:45:18Z colin $")
public class ReutersRequest
{
    /**
     * Create a new ReutersRequest instance.
     *
     * @param inInstrument
     * @param inContent
     */
    public ReutersRequest(Instrument inInstrument,
                          Content inContent)
    {
        instrument = inInstrument;
        content = inContent;
    }
    /**
     * Get the instrument value.
     *
     * @return a <code>Instrument</code> value
     */
    public Instrument getInstrument()
    {
        return instrument;
    }
    /**
     * Get the content value.
     *
     * @return a <code>Content</code> value
     */
    public Content getContent()
    {
        return content;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("ReutersRequest [instrument=").append(instrument).append(", content=").append(content).append("]");
        return builder.toString();
    }
    /**
     * 
     */
    private final Instrument instrument;
    /**
     * 
     */
    private final Content content;
}
