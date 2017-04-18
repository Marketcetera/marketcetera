package org.marketcetera.tensorflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.modules.headwater.HeadwaterModule;
import org.tensorflow.Graph;

/* $License$ */

/**
 * Test operation of a Tensor Flow model.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class TensorFlowModelTest
        extends TensorFlowTestBase
{
    /**
     * Test creating a graph and reading it back.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGraphPersistence()
            throws Exception
    {
        byte[] graphDef = ImageLabelGraphDefReader.readGraphDef();
        String modelName = "TestModel" + System.nanoTime();
        try(Graph graph = new Graph()) {
            graph.importGraphDef(graphDef);
            tensorFlowService.createContainer(graph,
                                              modelName,
                                              "This is a test model");
        }
        GraphContainer graphContainer = tensorFlowService.findByName(modelName);
        assertNotNull(graphContainer);
    }
    /**
     * Test the baseline effectiveness of an non-persisted model.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testModelExecutionNoPersistence()
            throws Exception
    {
        JpegContainer image = new JpegContainer(Files.readAllBytes(Paths.get("src/test/sample_data",
                                                                             "giant-schnauzer.jpg")));
        // set up a data flow that converts the image to a tensor and sends it to the model
        DataFlowID dataFlow = startModelDataFlow(new TensorFromJpegConverter(),
                                                 new ImageLabelTensorFlowRunner(ImageLabelGraphDefReader.readGraphDef()));
        HeadwaterModule.getInstance(headwaterInstance).emit(image,
                                                            dataFlow);
        Object output = waitForData();
        assertNotNull(output);
        assertTrue(output instanceof String);
        assertEquals("standard schnauzer",
                     output);
    }
    /**
     * Test the effectiveness of a persisted and restored model.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testModelExecutionPersistence()
            throws Exception
    {
        byte[] graphDef = ImageLabelGraphDefReader.readGraphDef();
        String modelName = "TestModel" + System.nanoTime();
        try(Graph graph = new Graph()) {
            graph.importGraphDef(graphDef);
            tensorFlowService.createContainer(graph,
                                              modelName,
                                              "This is a test model");
        }
        GraphContainer graphContainer = tensorFlowService.findByName(modelName);
        assertNotNull(graphContainer);
        JpegContainer image = new JpegContainer(Files.readAllBytes(Paths.get("src/test/sample_data",
                                                                             "giant-schnauzer.jpg")));
        // set up a data flow that converts the image to a tensor and sends it to the model
        DataFlowID dataFlow = startModelDataFlow(new TensorFromJpegConverter(),
                                                 new ImageLabelTensorFlowRunner(graphContainer.readGraph().toGraphDef()));
        HeadwaterModule.getInstance(headwaterInstance).emit(image,
                                                            dataFlow);
        Object output = waitForData();
        assertNotNull(output);
        assertTrue(output instanceof String);
        assertEquals("standard schnauzer",
                     output);
    }
}
