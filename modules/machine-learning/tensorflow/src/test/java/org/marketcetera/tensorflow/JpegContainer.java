package org.marketcetera.tensorflow;

/* $License$ */

/**
 * Encapsulates the raw data of a JPG image.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class JpegContainer
{
    /**
     * Create a new JpegContainer instance.
     *
     * @param inRawData a <code>byte[]</code> value
     */
    public JpegContainer(byte[] inRawData)
    {
        rawData = inRawData;
    }
    /**
     * Get the rawData value.
     *
     * @return a <code>byte[]</code> value
     */
    public byte[] getRawData()
    {
        return rawData;
    }
    /**
     * raw image data
     */
    private final byte[] rawData;
}
