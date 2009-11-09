package org.marketcetera.metrics;

import org.marketcetera.util.misc.ClassVersion;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import java.util.Iterator;

/* $License$ */
/**
 * Tests {@link CheckpointInfo}, {@link IterationInfo} & {@link PerThreadInfo}.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class InfoTests {
    /**
     * Tests {@link CheckpointInfo}
     */
    @Test
    public void checkpointInfo() {
        //null values.
        CheckpointInfo info = new CheckpointInfo(null, 0, null);
        assertCheckpointInfo(info, null, 0, null);

        //non-null values.
        String id = "ID";
        long timeStamp = System.currentTimeMillis();
        Object[] data = {"value", 343, 34343l, 343.234d};
        info = new CheckpointInfo(id, timeStamp, data);
        assertCheckpointInfo(info, id, timeStamp, data);
    }

    /**
     * Tests {@link IterationInfo}
     */
    @Test
    public void iterationInfo() {
        IterationInfo info = new IterationInfo();

        //verify initial state
        assertTrue(info.isEmpty());
        assertEquals(0, info.getNumIterations());
        assertFalse(info.iterator().hasNext());

        //verify setters
        info.setNumIterations(3232);
        assertEquals(3232, info.getNumIterations());

        //verify operations
        CheckpointInfo chk1 = new CheckpointInfo("blue", 324324, null);
        CheckpointInfo chk2 = new CheckpointInfo("green", 234998, null);
        info.addCheckpoint(chk1);
        assertFalse(info.isEmpty());
        assertTrue(info.iterator().hasNext());
        assertSame(chk1, info.iterator().next());

        info.addCheckpoint(chk2);
        assertFalse(info.isEmpty());

        Iterator<CheckpointInfo> iter = info.iterator();
        assertTrue(iter.hasNext());
        assertSame(chk1, iter.next());
        assertTrue(iter.hasNext());
        assertSame(chk2, iter.next());
        assertFalse(iter.hasNext());

        info.clear();
        assertTrue(info.isEmpty());
        assertEquals(0, info.getNumIterations());
        assertFalse(info.iterator().hasNext());
    }

    /**
     * Tests {@link PerThreadInfo}
     */
    @Test
    public void perThreadInfo() {
        PerThreadInfo info = new PerThreadInfo();
        //verify initial state
        assertEquals(Thread.currentThread().getName(), info.getName());
        assertTrue(info.isSavedEmpty());
        assertFalse(info.iterator().hasNext());

        //do a save when current is empty
        info.saveCurrent();
        assertFalse(info.isSavedEmpty());
        assertTrue(info.iterator().hasNext());
        IterationInfo iInfo = info.iterator().next();
        assertTrue(iInfo.isEmpty());
        assertEquals(0, iInfo.getNumIterations());
        //increment the iteration counter
        info.addIteration();
        //do another save.
        info.saveCurrent();
        assertFalse(info.isSavedEmpty());

        Iterator<IterationInfo> iter = info.iterator();
        assertTrue(iter.hasNext());
        iInfo = iter.next();
        assertTrue(iInfo.isEmpty());
        assertEquals(0, iInfo.getNumIterations());

        assertTrue(iter.hasNext());
        iInfo = iter.next();
        assertTrue(iInfo.isEmpty());
        assertEquals(1, iInfo.getNumIterations());
        //clear the saved
        info.clearSaved();
        assertTrue(info.isSavedEmpty());
        assertFalse(info.iterator().hasNext());

        //Now test current
        CheckpointInfo chk1 = new CheckpointInfo("a", 12, null);
        info.addCurrent(chk1);
        CheckpointInfo chk2 = new CheckpointInfo("b", 34, null);
        info.addCurrent(chk2);
        assertTrue(info.isSavedEmpty());
        info.saveCurrent();
        assertFalse(info.isSavedEmpty());
        iter = info.iterator();
        assertTrue(iter.hasNext());
        iInfo = iter.next();
        assertFalse(iter.hasNext());
        assertEquals(0, iInfo.getNumIterations());
        assertThat(iInfo, hasItems(chk1,chk2));

        //Add iterations
        info.addIteration();
        info.addIteration();
        //Add stuff to current but clear it before save.
        info.addCurrent(chk2);
        info.clearCurrent();
        info.saveCurrent();
        info.addIteration();
        //Add the two previous checkpoints in reverse order
        info.addCurrent(chk2);
        info.addCurrent(chk1);
        info.saveCurrent();
        //Now verify the info
        iter = info.iterator();
        assertTrue(iter.hasNext());
        iInfo = iter.next();
        assertEquals(0, iInfo.getNumIterations());
        assertThat(iInfo, hasItems(chk1,chk2));
        assertTrue(iter.hasNext());
        iInfo = iter.next();
        assertEquals(2, iInfo.getNumIterations());
        assertTrue(iInfo.isEmpty());
        assertTrue(iter.hasNext());
        iInfo = iter.next();
        assertEquals(3, iInfo.getNumIterations());
        assertThat(iInfo, hasItems(chk2,chk1));
        assertFalse(iter.hasNext());
        assertFalse(info.isSavedEmpty());
        //Now clear all saved data
        info.clearSaved();
        assertTrue(info.isSavedEmpty());
    }

    private static void assertCheckpointInfo(CheckpointInfo inInfo, String inId, long inTimeStamp, Object[] inData) {
        assertEquals(inId, inInfo.getIdentifier());
        assertEquals(inTimeStamp, inInfo.getTimestamp());
        assertArrayEquals(inData, inInfo.getData());
    }
}
