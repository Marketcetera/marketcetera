package org.marketcetera.photon.commons.ui.workbench;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.junit.Test;
import org.marketcetera.photon.test.ExpectedFailure;
import org.marketcetera.photon.test.PhotonTestBase;


/* $License$ */

/**
 * Tests {@link SafeHandler}.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class SafeHandlerTest extends PhotonTestBase {

    @Test
    public void testExceptionReported() throws Exception {
        final AbstractHandler fixture = new SafeHandler() {  
            @Override
            protected void executeSafely(ExecutionEvent event)
                    throws ExecutionException {
                throw new RuntimeException();
            }
        };
        new ExpectedFailure<ExecutionException>("The command handler threw an unexpected exception.") {
            @Override
            protected void run() throws Exception {
                fixture.execute(new ExecutionEvent());
            }
        };
    }
}
