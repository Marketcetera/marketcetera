package org.marketcetera.marketdata;

import org.junit.Test;
import static org.junit.Assert.*;

/* $License$ */

/**
 * Tests {@link SecurityListRequestTest}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SecurityListRequestTest
{
    @Test
    public void newDerivativeSecurityListRequest()
        throws Exception
    {
        SecurityListRequest request1 = SecurityListRequest.newSecurityListRequest();
        SecurityListRequest request2 = SecurityListRequest.newSecurityListRequest();
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
        String requestString = DataRequest.TYPE_KEY + "=" + SecurityListRequest.TYPE;
        SecurityListRequest request = (SecurityListRequest)DataRequest.newRequestFromString(requestString);
        assertNotNull(request);
        assertTrue(request.getId() > 0);
    }
}
