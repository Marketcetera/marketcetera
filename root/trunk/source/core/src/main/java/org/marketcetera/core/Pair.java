package org.marketcetera.core;

public class Pair<T1, T2> {
	protected T1 firstMember;
	protected T2 secondMember;

	public Pair(T1 o1, T2 o2) {
		firstMember = o1;
		secondMember = o2;
	}
	/**
	 * @return Returns the firstMember.
	 */
	public T1 getFirstMember() {
		return firstMember;
	}


	/**
	 * @return Returns the secondMember.
	 */
	public T2 getSecondMember() {
		return secondMember;
	}

}
