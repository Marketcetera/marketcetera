/* Copyright 2016 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/
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
 * Executes a tensor flow operation for the Image Label sample.
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
            float[] labelProbabilities = executeInceptionGraph(inInput);
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
     * @param inGraphDef a <code>byte[]</code> value
     * @throws IOException if the runner cannot be constructed
     */
    public ImageLabelTensorFlowRunner(byte[] inGraphDef)
            throws IOException
    {
        graphDef = inGraphDef;
    }
    /**
     * Get the index of the best fit from the given probabilities.
     *
     * @param inProbabilities a <code>float[]</code> value
     * @return an <code>int</code> value
     */
    private int maxIndex(float[] inProbabilities)
    {
        int best = 0;
        for(int i = 1; i < inProbabilities.length; ++i) {
            if(inProbabilities[i] > inProbabilities[best]) {
                best = i;
            }
        }
        return best;
    }
    /**
     * Derive the results from the given image tensor.
     *
     * @param inImage a <code>Tensor</code> value
     * @return a <code>float[]</code> value
     */
    private float[] executeInceptionGraph(Tensor inImage)
    {
        try(Graph g = new Graph()) {
            g.importGraphDef(graphDef);
            try(Session s = new Session(g);
                Tensor result = s.runner().feed("input",inImage).fetch("output").run().get(0)) {
                final long[] rshape = result.shape();
                if(result.numDimensions() != 2 || rshape[0] != 1) {
                    throw new RuntimeException(String.format("Expected model to produce a [1 N] shaped tensor where N is the number of labels, instead it produced one with shape %s",
                                                             Arrays.toString(rshape)));
                }
                int nlabels = (int)rshape[1];
                return result.copyTo(new float[1][nlabels])[0];
            }
        }
    }
    /**
     * graph def to use for the operations
     */
    private final byte[] graphDef;
}
