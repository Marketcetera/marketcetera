package org.marketcetera.tensorflow;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class ImageLabelGraphDefReader
{
    public static byte[] readGraphDef()
            throws IOException
    {
        return Files.readAllBytes(Paths.get("src/test/sample_data",
                                            "tensorflow_inception_graph.pb"));
    }
}
