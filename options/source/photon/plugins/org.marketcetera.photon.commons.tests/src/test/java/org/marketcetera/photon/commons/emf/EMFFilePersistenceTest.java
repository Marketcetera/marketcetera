package org.marketcetera.photon.commons.emf;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.photon.commons.ValidateTest.ExpectedNullArgumentFailure;
import org.marketcetera.photon.commons.ValidateTest.ExpectedNullElementFailure;
import org.marketcetera.photon.test.EMFTestUtil;
import org.marketcetera.photon.test.ExpectedFailure;

/* $License$ */

/**
 * Test {@link EMFFilePersistence}.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class EMFFilePersistenceTest {

    private EMFFilePersistence mFixture;
    private File mTempFile;
    
    @Before
    public void before() throws Exception {
        mTempFile = File.createTempFile("PersistenceServiceTest", null);
        mFixture = new EMFFilePersistence(mTempFile);
    }

    @After
    public void after() {
        mTempFile.delete();
    }
    
    @Test
    public void testSaveAndRestore() throws Exception {
        EObject obj1 = EMFTestUtil.createDynamicEObject();
        EObject obj2 = EMFTestUtil.createDynamicEObject();
        EMFTestUtil.eSet(obj1, "test", "val1");
        EMFTestUtil.eSet(obj2, "test", "val2");
        mFixture.save(Arrays.asList(obj1, obj2));
        List<EObject> restored = mFixture.restore();
        assertThat(restored.size(), is(2));
        assertThat(EMFTestUtil.eGet(restored.get(0), "test"), is((Object) "val1"));
        assertThat(EMFTestUtil.eGet(restored.get(1), "test"), is((Object) "val2"));
        // do it again with the order swapped
        mFixture.save(Arrays.asList(obj2, obj1));
        restored = mFixture.restore();
        assertThat(restored.size(), is(2));
        assertThat(EMFTestUtil.eGet(restored.get(0), "test"), is((Object) "val2"));
        assertThat(EMFTestUtil.eGet(restored.get(1), "test"), is((Object) "val1"));
    }
    
    @Test
    public void testSaveAndRestoreEmpty() throws Exception {
        mFixture.save(Collections.<EObject>emptyList());
        List<EObject> restored = mFixture.restore();
        assertThat(restored.size(), is(0));
    }
    
    @Test
    public void testUnwritableFile() throws Exception {
        if (!mTempFile.setWritable(false))
            return;
        new ExpectedFailure<IOException>(null) {
            @Override
            protected void run() throws Exception {
                mFixture.save(Collections.<EObject>emptyList());
            }
        };
    }
    
    @Test
    public void testUnreadableFile() throws Exception {
        if (!mTempFile.setReadable(false))
            return;
        new ExpectedFailure<IOException>(null) {
            @Override
            protected void run() throws Exception {
                mFixture.restore();
            }
        };
    }
    
    @Test
    public void testValidation() throws Exception {
        new ExpectedNullArgumentFailure("file") {
            @Override
            protected void run() throws Exception {
                new EMFFilePersistence(null);
            }
        };
        new ExpectedNullArgumentFailure("objects") {
            @Override
            protected void run() throws Exception {
                mFixture.save(null);
            }
        };
        new ExpectedNullElementFailure("objects") {
            @Override
            protected void run() throws Exception {
                mFixture.save(Collections.singletonList((EObject) null));
            }
        };
    }

}
