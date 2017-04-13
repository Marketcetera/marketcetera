package org.marketcetera.tensorflow;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.marketcetera.module.DataRequest;
import org.marketcetera.tensorflow.model.TensorFlowRunner;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.tensorflow.Graph;
import org.tensorflow.Session;
import org.tensorflow.Tensor;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class ImageLabelTensorFlowRunner
        implements TensorFlowRunner
{
    /* (non-Javadoc)
     * @see org.marketcetera.tensorflow.model.TensorFlowRunner#fetch(org.marketcetera.module.DataRequest, org.tensorflow.Tensor)
     */
    @Override
    public Object fetch(DataRequest inDataRequest,
                        Tensor inInput)
    {
        try {
            List<String> labels = Files.readAllLines(Paths.get("src/test/sample_data",
                                                               "imagenet_comp_graph_label_strings.txt"),
                                                     Charset.forName("UTF-8"));
            float[] labelProbabilities = executeInceptionGraph(graphDef,
                                                               inInput);
            int bestLabelIdx = maxIndex(labelProbabilities);
            String result = labels.get(bestLabelIdx);
            SLF4JLoggerProxy.trace(this,
                                   "BEST MATCH: {} ({} likely)",
                                   result,
                                   labelProbabilities[bestLabelIdx] * 100f);
            return result;
        } catch (IOException e) {
            SLF4JLoggerProxy.warn(this,
                                  e);
            throw new RuntimeException(e);
        }
    }
    /**
     * Create a new ImageLabelTensorFlowRunner instance.
     *
     * @throws IOException if the runner cannot be constructed
     */
    public ImageLabelTensorFlowRunner()
            throws IOException
    {
        graphDef = ImageLabelGraphDefReader.readGraphDef();
    }
    /**
     * 
     *
     *
     * @param probabilities
     * @return
     */
    private static int maxIndex(float[] probabilities)
    {
        int best = 0;
        for(int i = 1; i < probabilities.length; ++i) {
            if(probabilities[i] > probabilities[best]) {
                best = i;
            }
        }
        return best;
    }
    private static float[] executeInceptionGraph(byte[] graphDef,
                                                 Tensor image)
    {
        try (Graph g = new Graph()) {
          g.importGraphDef(graphDef);
          try (Session s = new Session(g);
              Tensor result = s.runner().feed("input", image).fetch("output").run().get(0)) {
            final long[] rshape = result.shape();
            if (result.numDimensions() != 2 || rshape[0] != 1) {
              throw new RuntimeException(
                  String.format(
                      "Expected model to produce a [1 N] shaped tensor where N is the number of labels, instead it produced one with shape %s",
                      Arrays.toString(rshape)));
            }
            int nlabels = (int) rshape[1];
            return result.copyTo(new float[1][nlabels])[0];
          }
        }
      }
    private final byte[] graphDef;
}
