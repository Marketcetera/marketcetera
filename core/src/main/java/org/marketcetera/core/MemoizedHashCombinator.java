package org.marketcetera.core;

/**
 * @author Graham Miller
 * @version $Id$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class MemoizedHashCombinator<T1, T2> extends Pair<T1, T2>{
	String hashString;
	int hashCode;

	public MemoizedHashCombinator(T1 o1, T2 o2){
		super(o1, o2);
		String s1 = o1==null ? "null" : o1.toString(); //$NON-NLS-1$
		String s2 = o2==null ? "null" : o2.toString(); //$NON-NLS-1$
		int len1 = s1.length();
		int len2 = s2.length();
		hashString = ""+len1+s1+len2+s2; //$NON-NLS-1$
		hashCode = hashString.hashCode();
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return hashString;
	}
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return hashCode;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof MemoizedHashCombinator)) {
            return false;
        }
        MemoizedHashCombinator<?,?> other = (MemoizedHashCombinator<?,?>) obj;
        if (hashString == null) {
            if (other.hashString != null) {
                return false;
            }
        } else if (!hashString.equals(other.hashString)) {
            return false;
        }
        return true;
    }



}
