package org.rubypeople.rdt.core;


public class Flags {
    
	/*
	 * Modifiers
	 */
	public static final int AccPublic       = 0x0001;
	public static final int AccPrivate      = 0x0002;
	public static final int AccProtected    = 0x0004;
	public static final int AccStatic       = 0x0008;
	
	/**
	 * Constant representing the absence of any flag
	 * @since 3.0
	 */
	public static final int AccDefault = 0;
	
	public static final int AccModule = 0x0001;

	/**
	 * Returns whether the given integer includes the <code>private</code> modifier.
	 *
	 * @param flags the flags
	 * @return <code>true</code> if the <code>private</code> modifier is included
	 */
	public static boolean isPrivate(int flags) {
		return (flags & AccPrivate) != 0;
	}
	/**
	 * Returns whether the given integer includes the <code>protected</code> modifier.
	 *
	 * @param flags the flags
	 * @return <code>true</code> if the <code>protected</code> modifier is included
	 */
	public static boolean isProtected(int flags) {
		return (flags & AccProtected) != 0;
	}
	/**
	 * Returns whether the given integer includes the <code>public</code> modifier.
	 *
	 * @param flags the flags
	 * @return <code>true</code> if the <code>public</code> modifier is included
	 */
	public static boolean isPublic(int flags) {
		return (flags & AccPublic) != 0;
	}
	/**
	 * Returns whether the given integer includes the <code>static</code> modifier.
	 *
	 * @param flags the flags
	 * @return <code>true</code> if the <code>static</code> modifier is included
	 */
	public static boolean isStatic(int flags) {
		return (flags & AccStatic) != 0;
	}
	public static boolean isModule(int flags) {
		return (flags & AccModule) != 0;
	}

}
