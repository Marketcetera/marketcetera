package org.marketcetera.tensorflow;

import static org.junit.Assert.assertNotNull;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;
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
        byte[] graphDef = Files.readAllBytes(Paths.get("src/test/sample_data",
                                                       "tensorflow_inception_graph.pb"));
        String modelName = "TestModel-" + System.nanoTime();
        try(Graph graph = new Graph()) {
            graph.importGraphDef(graphDef);
            tensorFlowService.createContainer(graph,
                                              modelName,
                                              "This is a test model");
        }
        GraphContainer graphContainer = tensorFlowService.findByName(modelName);
        assertNotNull(graphContainer);
    }
}
