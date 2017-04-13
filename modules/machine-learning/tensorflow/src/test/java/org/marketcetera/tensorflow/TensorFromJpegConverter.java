package org.marketcetera.tensorflow;

import org.marketcetera.tensorflow.converters.AbstractTensorFromObjectConverter;
import org.tensorflow.DataType;
import org.tensorflow.Graph;
import org.tensorflow.Output;
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
public class TensorFromJpegConverter
        extends AbstractTensorFromObjectConverter<JpegContainer>
{
    /* (non-Javadoc)
     * @see org.marketcetera.tensorflow.converters.TensorFromObjectConverter#convert(java.lang.Object)
     */
    @Override
    public Tensor convert(JpegContainer inType)
    {
        return constructAndExecuteGraphToNormalizeImage(inType.getRawData());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.tensorflow.converters.TensorFromObjectConverter#getType()
     */
    @Override
    public Class<JpegContainer> getType()
    {
        return JpegContainer.class;
    }
    static class GraphBuilder {
        GraphBuilder(Graph g) {
          this.g = g;
        }

        Output div(Output x, Output y) {
          return binaryOp("Div", x, y);
        }

        Output sub(Output x, Output y) {
          return binaryOp("Sub", x, y);
        }

        Output resizeBilinear(Output images, Output size) {
          return binaryOp("ResizeBilinear", images, size);
        }

        Output expandDims(Output input, Output dim) {
          return binaryOp("ExpandDims", input, dim);
        }

        Output cast(Output value, DataType dtype) {
          return g.opBuilder("Cast", "Cast").addInput(value).setAttr("DstT", dtype).build().output(0);
        }

        Output decodeJpeg(Output contents, long channels) {
          return g.opBuilder("DecodeJpeg", "DecodeJpeg")
              .addInput(contents)
              .setAttr("channels", channels)
              .build()
              .output(0);
        }

        Output constant(String name, Object value) {
          try (Tensor t = Tensor.create(value)) {
            return g.opBuilder("Const", name)
                .setAttr("dtype", t.dataType())
                .setAttr("value", t)
                .build()
                .output(0);
          }
        }

        private Output binaryOp(String type, Output in1, Output in2) {
          return g.opBuilder(type, type).addInput(in1).addInput(in2).build().output(0);
        }

        private Graph g;
      }
    private Tensor constructAndExecuteGraphToNormalizeImage(byte[] imageBytes) {
        try (Graph g = new Graph()) {
          GraphBuilder b = new GraphBuilder(g);
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
          final Output input = b.constant("input", imageBytes);
          final Output output =
              b.div(
                  b.sub(
                      b.resizeBilinear(
                          b.expandDims(
                              b.cast(b.decodeJpeg(input, 3), DataType.FLOAT),
                              b.constant("make_batch", 0)),
                          b.constant("size", new int[] {H, W})),
                      b.constant("mean", mean)),
                  b.constant("scale", scale));
          try (Session s = new Session(g)) {
            return s.runner().fetch(output.op().name()).run().get(0);
          }
        }
      }
}
