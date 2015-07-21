package com.dynatrace.docker.util;

/**
 * Convenience methods for dealing with {@link String}s
 * 
 * @author Reinhard Pilz
 *
 */
public final class StrUtil {
	
	public static final String EMPTY = "".intern();
	public static final String COLON = ":".intern();
	public static final String SPC = " ".intern();
	public static final String EQ = "=".intern();
	public static final String DOT = ".".intern();
	public static final String SLASH = "/".intern();

	/**
	 * Checks if the given {@link String} is either {@code null} or an empty
	 * {@link String}.
	 * 
	 * @param s the {@link String} to check if it is either {@code null} or an
	 * 		empty {@link String}
	 * 
	 * @return {@code true} if the given {@link String} is either {@code null}
	 * 		or an empty {@link String}, {@code false} otherwise
	 */
	public static final boolean isNullOrEmpty(final String s) {
		if (s == null) {
			return true;
		}
		return s.isEmpty();
	}
	
	public static final boolean equals(final String s1, final String s2) {
		if (s1 == null) {
			return s2 == null;
		}
		if (s2 == null) {
			return s1 == null;
		}
		return s1.equals(s2);
	}
	
	public static final String removeLeadingSlash(final String s) {
		if (s == null) {
			return null;
		}
		if (s.startsWith(SLASH)) {
			return s.substring(1);
		}
		return s;
	}
}
