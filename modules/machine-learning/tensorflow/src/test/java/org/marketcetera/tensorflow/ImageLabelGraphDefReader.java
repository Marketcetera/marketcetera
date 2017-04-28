package org.marketcetera.tensorflow;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/* $License$ */

/**
 * Provides common behaviors for the Image Label sample.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class ImageLabelGraphDefReader
{
    /**
     * Read the pre-built model graph def.
     *
     * @return a <code>byte[]</code> value
     * @throws IOException if the graph def could not be built
     */
    public static byte[] readGraphDef()
            throws IOException
    {
        return Files.readAllBytes(Paths.get("src/test/sample_data",
                                            "tensorflow_inception_graph.pb"));
    }
}
