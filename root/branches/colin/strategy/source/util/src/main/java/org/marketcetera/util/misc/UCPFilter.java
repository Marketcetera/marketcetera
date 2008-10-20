package org.marketcetera.util.misc;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.HashMap;

/**
 * A filter for Unicode code points. It also maintains a cache of
 * filters associated with {@link Charset} instances.
 * 
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$") //$NON-NLS-1$
public abstract class UCPFilter
{

    // CLASS DATA.

    /**
     * A filter for Unicode characters that can be encoded by a
     * specific charset.
     */

    private static final class UCPCharsetFilter
        extends UCPFilter
    {

        // INSTANCE DATA.

        private CharsetEncoder mEncoder;


        // CONSTRUCTORS.

        /**
         * Creates a filter for the given charset.
         *
         * @param cs The charset.
         */

        public UCPCharsetFilter
            (Charset cs)
        {
            mEncoder=cs.newEncoder();
        }


        // UCPFilter.

        @Override
        public boolean isAcceptable(int ucp)
        {
            return mEncoder.canEncode(StringUtils.fromUCP(ucp));
        }
    }

    /**
     * A filter for Unicode characters deemed valid by {@link
     * StringUtils#isValid(int)}.
     */

    public static final UCPFilter VALID=new UCPFilter()
        {
            @Override
            public boolean isAcceptable(int ucp)
            {
                return StringUtils.isValid(ucp);
            }
        };

    /**
     * A filter for Unicode characters that can be represented by a
     * single char.
     */

    public static final UCPFilter CHAR=new UCPFilter()
        {
            @Override
            public boolean isAcceptable(int ucp)
            {
                return ((0<=ucp) && (ucp<=0xFFFF));
            }
        };

    /**
     * A filter for Unicode code points that are digits.
     */

    public static final UCPFilter DIGIT=new UCPFilter()
        {
            @Override
            public boolean isAcceptable(int ucp)
            {
                return Character.isDigit(ucp);
            }
        };

    /**
     * A filter for Unicode code points that are letters.
     */

    public static final UCPFilter LETTER=new UCPFilter()
        {
            @Override
            public boolean isAcceptable(int ucp)
            {
                return Character.isLetter(ucp);
            }
        };

    /**
     * A filter for Unicode code points that are letters or digits.
     */

    public static final UCPFilter ALNUM=new UCPFilter()
        {
            @Override
            public boolean isAcceptable(int ucp)
            {
                return Character.isLetterOrDigit(ucp);
            }
       };

    private static HashMap<Charset,UCPFilter> mMap=
        new HashMap<Charset,UCPFilter>();


    // CLASS METHODS.

    /**
     * Returns a filter for Unicode code points that can be encoded by
     * the given charset.
     *
     * @param cs The charset.
     *
     * @return The filter.
     */

    public synchronized static UCPFilter forCharset
        (Charset cs)
    {
        UCPFilter filter=mMap.get(cs);
        if (filter!=null) {
            return filter;
        }
        filter=new UCPCharsetFilter(cs);
        mMap.put(cs,filter);
        return filter;
    }

    /**
     * Returns a filter for Unicode code points that can be encoded by
     * the default JVM charset.
     *
     * @return The filter.
     */

    public static final UCPFilter getDefaultCharset()
    {
        return UCPFilter.forCharset(Charset.defaultCharset());
    }

    /**
     * Returns a filter for Unicode code points that can be encoded by
     * the current system file encoding/charset (as specified in the
     * system property <code>file.encoding</code>).
     *
     * @return The filter.
     */

    public static final UCPFilter getFileSystemCharset()
    {
        return UCPFilter.forCharset
            (Charset.forName(System.getProperty
                             ("file.encoding"))); //$NON-NLS-1$
    }


    // INSTANCE METHODS.    
    
    /**
     * Checks whether the given Unicode code point is acceptable to
     * the receiver.
     *
     * @param ucp The code point.
     *
     * @return True if so.
     */
    
    public abstract boolean isAcceptable(int ucp);
}
