package org.marketcetera.photon.internal.strategy.engine.embedded;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.marketcetera.photon.strategy.engine.model.core.ConnectionState;
import org.marketcetera.photon.test.PhotonTestBase;


/* $License$ */

/**
 * Test {@link EmbeddedEngineImpl}. 
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
public class EmbeddedStrategyEngineTest  extends PhotonTestBase {
    
    @Test
    public void testAttributes() throws Exception {
        EmbeddedEngineImpl fixture = new EmbeddedEngineImpl();
        assertThat(fixture.getConnectionState(), is(ConnectionState.CONNECTED));
        assertThat(fixture.getName(), is("Embedded Engine"));
        assertThat(fixture.getDescription(), is("The embedded strategy engine"));
        assertThat(fixture.isReadOnly(), is(true));
    }
}
