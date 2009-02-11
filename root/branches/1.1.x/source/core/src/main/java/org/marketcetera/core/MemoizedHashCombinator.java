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
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object arg0) {
		if (arg0 != null && arg0.getClass().equals(this.getClass())){
			MemoizedHashCombinator<T1, T2> combinator = (MemoizedHashCombinator<T1, T2>) arg0;
			return this.hashString.equals(combinator.hashString);
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return hashCode;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return hashString;
	}



}
