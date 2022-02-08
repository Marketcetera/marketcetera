package org.marketcetera.tensorflow.dao;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.concurrent.GuardedBy;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.marketcetera.core.CloseableLock;
import org.marketcetera.persist.NDEntityBase;
import org.marketcetera.tensorflow.GraphContainer;
import org.tensorflow.Graph;

/* $License$ */

/**
 * Provides a persistent <code>GraphContainer</code> implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Entity
@Table(name="tf_graph_data")
public class PersistentGraphContainer
        extends NDEntityBase
        implements GraphContainer
{
    /* (non-Javadoc)
     * @see org.marketcetera.tensorflow.GraphContainer#readGraph()
     */
    @Override
    public Graph readGraph()
    {
        try(CloseableLock updateLock = CloseableLock.create(graphLock.readLock())) {
            updateLock.lock();
            if(graph != null) {
                return graph;
            }
        }
        try(CloseableLock updateLock = CloseableLock.create(graphLock.writeLock())) {
            updateLock.lock();
            if(graph != null) {
                return graph;
            }
            graph = new Graph();
            graph.importGraphDef(graphData);;
            return graph;
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.tensorflow.GraphContainer#writeGraph(org.tensorflow.Graph)
     */
    @Override
    public void writeGraph(Graph inGraph)
    {
        graphData = inGraph.toGraphDef();
        try(CloseableLock updateLock = CloseableLock.create(graphLock.writeLock())) {
            updateLock.lock();
            graph = null;
        }
    }
    /**
     * guards access to {@link #graph}
     */
    private transient final ReadWriteLock graphLock = new ReentrantReadWriteLock();
    /**
     * non-persistence <code>Graph</code> representation
     */
    @GuardedBy("graphLock")
    private transient Graph graph;
    /**
     * persistence graph data value
     */
    @Column(name="graph_data")
    private byte[] graphData;
    private static final long serialVersionUID = 5408021483861544411L;
}
