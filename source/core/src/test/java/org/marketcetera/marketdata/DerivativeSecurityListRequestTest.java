package org.marketcetera.marketdata;

import org.junit.Test;
import static org.junit.Assert.*;

/* $License$ */

/**
 * Tests {@link DerivativeSecurityListRequestTest}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class DerivativeSecurityListRequestTest
{
    @Test
    public void newDerivativeSecurityListRequest()
        throws Exception
    {
        DerivativeSecurityListRequest request1 = DerivativeSecurityListRequest.newDerivativeSecurityListRequest();
        DerivativeSecurityListRequest request2 = DerivativeSecurityListRequest.newDerivativeSecurityListRequest();
        assertNotNull(request1);
        assertTrue(request1.getId() > 0);
        assertNotNull(request2);
        assertTrue(request2.getId() > 0);
        assertTrue(request1.getId() != request2.getId());
        assertFalse(request1.hashCode() == request2.hashCode());
        assertFalse(request1.equals(request2));
        assertTrue(request1.equivalent(request2));
    }
    @Test
    public void newRequestFromString()
        throws Exception
    {
        String requestString = DataRequest.TYPE_KEY + "=" + DerivativeSecurityListRequest.TYPE;
        DerivativeSecurityListRequest request = (DerivativeSecurityListRequest)DataRequest.newRequestFromString(requestString);
        assertNotNull(request);
        assertTrue(request.getId() > 0);
    }
}
