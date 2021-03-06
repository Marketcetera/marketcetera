package org.marketcetera.core;

import javax.annotation.concurrent.Immutable;

/* $License$ */

/**
 * Strongly-typed object duo.
 * 
 * @author gmiller
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.5.0
 */
@Immutable
@ClassVersion("$Id$")
public class Pair<T1, T2>
{
    /**
     * Creates a new <code>Pair</code> with the given attributes.
     *
     * @param <Clazz1> a <code>Clazz1</code> value
     * @param <Clazz2> a <code>Clazz2</code> value
     * @param inType1 a <code>Clazz1</code> value
     * @param inType2 a <code>Clazz2</code> value
     * @return a <code>Pair&lt;Clazz1,Clazz2&gt;</code> value
     */
    public static <Clazz1,Clazz2> Pair<Clazz1,Clazz2> create(Clazz1 inType1,
                                                             Clazz2 inType2)
    {
        return new Pair<Clazz1,Clazz2>(inType1,
                                        inType2);
    }
	/**
	 * Create a new Pair instance.
	 *
	 * @param inFirst a <code>T1</code> value or null
	 * @param inSecond a <code>T2</code> value or null
	 */
	public Pair(T1 inFirst,
	            T2 inSecond)
	{
		firstMember = inFirst;
		secondMember = inSecond;
	}
    /**
     * Get the firstMember value.
     *
     * @return a <code>T1</code> value
     */
    public T1 getFirstMember()
    {
        return firstMember;
    }
    /**
     * Get the secondMember value.
     *
     * @return a <code>T2</code> value
     */
    public T2 getSecondMember()
    {
        return secondMember;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return String.format("%s - %s", //$NON-NLS-1$
                             firstMember,
                             secondMember);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((firstMember == null) ? 0 : firstMember.hashCode());
        result = prime * result + ((secondMember == null) ? 0 : secondMember.hashCode());
        return result;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Pair<?,?> other = (Pair<?,?>) obj;
        if (firstMember == null) {
            if (other.firstMember != null)
                return false;
        } else if (!firstMember.equals(other.firstMember))
            return false;
        if (secondMember == null) {
            if (other.secondMember != null)
                return false;
        } else if (!secondMember.equals(other.secondMember))
            return false;
        return true;
    }
    /**
     * first member of the pair
     */
    private final T1 firstMember;
    /**
     * second member of the pair
     */
    private final T2 secondMember;
}
