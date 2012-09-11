package org.marketcetera.dao.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.marketcetera.api.dao.PermissionAttribute.Create;
import static org.marketcetera.api.dao.PermissionAttribute.Delete;
import static org.marketcetera.api.dao.PermissionAttribute.Read;
import static org.marketcetera.api.dao.PermissionAttribute.Update;

import java.util.*;

import org.junit.Before;
import org.junit.Test;
import org.marketcetera.api.dao.PermissionAttribute;

/* $License$ */

/**
 * Tests {@link PermissionAttribute}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class PermissionAttributeTest
{
    /**
     * Run before each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Before
    public void setup()
            throws Exception
    {
        expectedValues.put(Create,
                           1);
        expectedValues.put(Read,
                           2);
        expectedValues.put(Update,
                           4);
        expectedValues.put(Delete,
                           8);
    }
    /**
     * Tests {@link PermissionAttribute#getBitFlagValueFor(PermissionAttribute...)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testBitFlagRoundTrip()
            throws Exception
    {
        assertEquals(0,
                     PermissionAttribute.getBitFlagValueFor(null));
        assertEquals(0,
                     PermissionAttribute.getBitFlagValueFor(new HashSet<PermissionAttribute>()));
        assertTrue(PermissionAttribute.getAttributesFor(0).isEmpty());
        // test single values
        for(PermissionAttribute attribute : PermissionAttribute.values()) {
            int actualValue = PermissionAttribute.getBitFlagValueFor(EnumSet.of(attribute)); 
            assertEquals(expectedValues.get(attribute).intValue(),
                         actualValue);
            Set<PermissionAttribute> actualPermissions = PermissionAttribute.getAttributesFor(actualValue); 
            assertEquals(actualPermissions.size(),
                         1);
            assertTrue(actualPermissions.contains(attribute));
        }
        // test all combined values
        int totalExpectedValue = 0;
        for(int value : expectedValues.values()) {
            totalExpectedValue += value;
        }
        assertEquals(totalExpectedValue,
                     PermissionAttribute.getBitFlagValueFor(EnumSet.allOf(PermissionAttribute.class)));
        assertTrue(PermissionAttribute.getAttributesFor(totalExpectedValue).containsAll(EnumSet.allOf(PermissionAttribute.class)));
        // gradually remove one value at a time
        Iterator<Map.Entry<PermissionAttribute,Integer>> entryIterator = expectedValues.entrySet().iterator();
        while(entryIterator.hasNext()) {
            Map.Entry<PermissionAttribute,Integer> entry = entryIterator.next();
            entryIterator.remove();
            totalExpectedValue -= entry.getValue();
            assertEquals(totalExpectedValue,
                         PermissionAttribute.getBitFlagValueFor(expectedValues.keySet()));
            assertTrue(PermissionAttribute.getAttributesFor(totalExpectedValue).containsAll(expectedValues.keySet()));
        }
    }
    /**
     * holds pre-calculated expected bit flag values for each attribute
     */
    private Map<PermissionAttribute,Integer> expectedValues = new HashMap<PermissionAttribute,Integer>();
}
