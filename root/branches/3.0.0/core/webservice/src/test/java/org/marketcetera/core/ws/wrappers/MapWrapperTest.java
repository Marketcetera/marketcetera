package org.marketcetera.core.ws.wrappers;

import java.util.HashMap;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @since 1.0.0
 * @version $Id: MapWrapperTest.java 82324 2012-04-09 20:56:08Z colin $
 */

/* $License$ */

public class MapWrapperTest
    extends WrapperTestBase
{
    @Test
    public void all()
        throws Exception
    {
        HashMap<Integer,String> map=new HashMap<Integer,String>();
        map.put(0,TEST_VALUE);
        HashMap<Integer,String> mapD=new HashMap<Integer,String>();
        mapD.put(0,TEST_VALUE_D);

        MapWrapper<Integer,String> empty=new MapWrapper<Integer,String>();
        assertNull(empty.getMap());
        MapWrapper<Integer,String> nullArg=new MapWrapper<Integer,String>(null);
        assertNull(nullArg.getMap());
        single(new MapWrapper<Integer,String>(map),
               new MapWrapper<Integer,String>(map),
               empty,nullArg,
               map.toString());

        MapWrapper<Integer,String> wrapper=new MapWrapper<Integer,String>(map);
        assertEquals(map,wrapper.getMap());

        wrapper.setMap(mapD);
        assertEquals(mapD,wrapper.getMap());

        wrapper.setMap(null);
        assertNull(wrapper.getMap());
    }
}
