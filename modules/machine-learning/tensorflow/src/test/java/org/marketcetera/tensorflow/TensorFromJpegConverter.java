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

import org.marketcetera.tensorflow.converters.AbstractTensorFromObjectConverter;
import org.tensorflow.DataType;
import org.tensorflow.Graph;
import org.tensorflow.Output;
import org.tensorflow.Session;
import org.tensorflow.Tensor;

/* $License$ */

/**
 * Converts a JPG image to a Tensor.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class TensorFromJpegConverter
        extends AbstractTensorFromObjectConverter<JpegContainer>
{
    /* (non-Javadoc)
     * @see org.marketcetera.tensorflow.converters.TensorFromObjectConverter#getType()
     */
    @Override
    public Class<JpegContainer> getType()
    {
        return JpegContainer.class;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.tensorflow.converters.AbstractTensorFromObjectConverter#doConvert(java.lang.Object)
     */
    @Override
    protected Tensor doConvert(JpegContainer inType)
    {
        return constructAndExecuteGraphToNormalizeImage(inType.getRawData());
    }
    /**
     * Construct a tensor from the given image.
     *
     * @param inImageBytes a <code>byte[]</code> value
     * @return a <code>Tensor</code> value
     */
    private Tensor constructAndExecuteGraphToNormalizeImage(byte[] inImageBytes)
    {
        try(Graph graph = new Graph()) {
            GraphBuilder builder = new GraphBuilder(graph);
            // Some constants specific to the pre-trained model at:
            // https://storage.googleapis.com/download.tensorflow.org/models/inception5h.zip
            //
            // - The model was trained with images scaled to 224x224 pixels.
            // - The colors, represented as R, G, B in 1-byte each were converted to
            //   float using (value - Mean)/Scale.
            final int H = 224;
            final int W = 224;
            final float mean = 117f;
            final float scale = 1f;
            // Since the graph is being constructed once per execution here, we can use a constant for the
            // input image. If the graph were to be re-used for multiple input images, a placeholder would
            // have been more appropriate.
            final Output input = builder.constant("input", inImageBytes);
            final Output output = builder.div(builder.sub(builder.resizeBilinear(builder.expandDims(builder.cast(builder.decodeJpeg(input, 3), DataType.FLOAT),
                                                                                                    builder.constant("make_batch", 0)),
                                                                                 builder.constant("size", new int[] {H, W})),
                                                          builder.constant("mean", mean)),
                                              builder.constant("scale", scale));
            try(Session s = new Session(graph)) {
                return s.runner().fetch(output.op().name()).run().get(0);
            }
        }
    }
    /**
     * Builds <code>Graph</code> objects complete with inputs and outputs.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class GraphBuilder
    {
        /**
         * Create a new GraphBuilder instance.
         *
         * @param inGraph a <code>Graph</code> value
         */
        private GraphBuilder(Graph inGraph)
        {
            graph = inGraph;
        }
        /**
         * Provide the divide operation.
         *
         * @param inLeft an <code>Output</code> value
         * @param inRight an <code>Output</code> value
         * @return an <code>Output</code> value
         */
        private Output div(Output inLeft,
                           Output inRight)
        {
            return binaryOp("Div",
                            inLeft,
                            inRight);
        }
        /**
         * Provide the subtract operation.
         *
         * @param inLeft an <code>Output</code> value
         * @param inRight an <code>Output</code> value
         * @return an <code>Output</code> value
         */
        private Output sub(Output inLeft,
                           Output inRight)
        {
            return binaryOp("Sub",
                            inLeft,
                            inRight);
        }
        /**
         * Provide the resize-bilinear operation.
         *
         * @param inImages an <code>Output</code> value
         * @param inSize an <code>Output</code> value
         * @return an <code>Output</code> value
         */
        private Output resizeBilinear(Output inImages,
                                      Output inSize)
        {
            return binaryOp("ResizeBilinear",
                            inImages,
                            inSize);
        }
        /**
         * Provide the expand dimensions operation.
         *
         * @param inInput an <code>Output</code> value
         * @param inNewDimensions an <code>Output</code> value
         * @return an <code>Output</code> value
         */
        private Output expandDims(Output inInput,
                                  Output inNewDimensions)
        {
            return binaryOp("ExpandDims",
                            inInput,
                            inNewDimensions);
        }
        /**
         * Provide the cast operation.
         *
         * @param inValue an <code>Output</code> value
         * @param inDataType a <code>DataType</code> value
         * @return an <code>Output</code> value
         */
        private Output cast(Output inValue,
                            DataType inDataType)
        {
            return graph.opBuilder("Cast", "Cast").addInput(inValue).setAttr("DstT", inDataType).build().output(0);
        }
        /**
         * Provide the decode JPG operation.
         *
         * @param inContents an <code>Output</code> value
         * @param inChannels a <code>long</code> value
         * @return an <code>Output</code> value
         */
        private Output decodeJpeg(Output inContents,
                                  long inChannels)
        {
            return graph.opBuilder("DecodeJpeg", "DecodeJpeg")
                    .addInput(inContents)
                    .setAttr("channels", inChannels)
                    .build()
                    .output(0);
        }
        /**
         * Provide a constant operation.
         *
         * @param inName a <code>String</code> value
         * @param inValue an <code>Object</code> value
         * @return an <code>Output</code> value
         */
        private Output constant(String inName,
                                Object inValue)
        {
            try(Tensor t = Tensor.create(inValue)) {
                return graph.opBuilder("Const", inName)
                        .setAttr("dtype", t.dataType())
                        .setAttr("value", t)
                        .build()
                        .output(0);
            }
        }
        /**
         * Execute the given binary operation on the given operands.
         *
         * @param inType a <code>String</code> value
         * @param inLeft an <code>Output</code> value
         * @param inRight an <code>Output</code> value
         * @return an <code>Output</code> value
         */
        private Output binaryOp(String inType,
                                Output inLeft,
                                Output inRight)
        {
            return graph.opBuilder(inType,inType).addInput(inLeft).addInput(inRight).build().output(0);
        }
        /**
         * graph object to base the build on
         */
        private final Graph graph;
    }
}
