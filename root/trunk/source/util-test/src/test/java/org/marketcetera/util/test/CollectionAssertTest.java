package org.marketcetera.util.test;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.marketcetera.util.test.CollectionAssert.*;

public class CollectionAssertTest
{
    @Test
    public void equal()
    {
        assertArrayPermutation
            (null,null);
        assertArrayPermutation
            (new String[0],new String[0]);
        assertArrayPermutation
            (new Integer[] {1},new Integer[] {1});
        assertArrayPermutation
            (new Integer[] {1,null},new Integer[] {null,1});
        assertArrayPermutation
            (new Integer[] {null,null},new Integer[] {null,null});
        assertArrayPermutation
            (new Long[] {1L,2L,1L},new Long[] {1L,1L,2L});
    }

    @Test
    public void expectedNull()
    {
        try {
            assertArrayPermutation(null,new String[0]);
        } catch (AssertionError ex) {
            assertEquals
                ("expected array is null but actual is not",ex.getMessage());
        }
    }

    @Test
    public void actualNull()
    {
        try {
            assertArrayPermutation(new String[0],null);
        } catch (AssertionError ex) {
            assertEquals
                ("actual array is null but expected is not",ex.getMessage());
        }
    }

    @Test
    public void sameLengthDifferentElements()
    {
        try {
            assertArrayPermutation
                (new Integer[] {null},new Integer[] {2});
        } catch (AssertionError ex) {
            assertEquals
                ("actual is missing 'null'",ex.getMessage());
        }
    }

    @Test
    public void expectedShorter()
    {
        try {
            assertArrayPermutation
                (new Integer[] {1},new Integer[] {1,1});
        } catch (AssertionError ex) {
            assertEquals
                ("actual contains extra elements such as 1",ex.getMessage());
        }
    }

    @Test
    public void actualShorter()
    {
        try {
            assertArrayPermutation
                (new Integer[] {null,null},new Integer[] {null});
        } catch (AssertionError ex) {
            assertEquals
                ("actual is missing 'null'",ex.getMessage());
        }
    }

    @Test
    public void message()
    {
        try {
            assertArrayPermutation
                ("Right now,",new Integer[] {1},new Integer[] {2});
        } catch (AssertionError ex) {
            assertEquals
                ("Right now, actual is missing '1'",ex.getMessage());
        }
    }
}
